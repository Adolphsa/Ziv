//
// Created by walker on 16-7-12.
//
#include <jni.h>
#include <android/log.h>

#include <libavcodec/avcodec.h>
#include <libavdevice/avdevice.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavutil/avutil.h>
#include <libswscale/swscale.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#if 1//DEBUG
#define  ZivPlayer_Debug(x...)  __android_log_print(ANDROID_LOG_INFO, "zivplayer", x)
#else
#define  D(...)  do {} while (0)
#endif

#undef COLOR_FORMAT_YUV420
#define COLOR_FORMAT_YUV420 0L
#undef COLOR_FORMAT_RGB565LE
#define COLOR_FORMAT_RGB565LE 1L
#undef COLOR_FORMAT_BGR32
#define COLOR_FORMAT_BGR32 2L

typedef struct DecoderContext {
    int color_format;
    struct AVCodec *codec;
    struct AVCodecContext *codec_ctx;
    struct AVFrame *src_frame;
    struct AVFrame *dst_frame;
    struct SwsContext *convert_ctx;
    int frame_ready;
} DecoderContext;

static AVFormatContext *i_fmt_ctx;
static AVStream *i_video_stream;

static AVFormatContext *o_fmt_ctx;
static AVStream *o_video_stream;

static int bStop = 0;

static void set_ctx(JNIEnv *env, jobject thiz, void *ctx) {
    jclass cls = (*env)->GetObjectClass(env, thiz);
    jfieldID fid = (*env)->GetFieldID(env, cls, "cdata", "I");
    (*env)->SetIntField(env, thiz, fid, (jint)ctx);
}

static void *get_ctx(JNIEnv *env, jobject thiz) {
    jclass cls = (*env)->GetObjectClass(env, thiz);
    jfieldID fid = (*env)->GetFieldID(env, cls, "cdata", "I");
    return (void*)(*env)->GetIntField(env, thiz, fid);
}

static void av_log_callback(void *ptr, int level, const char *fmt, __va_list vl) {
    static char line[1024] = {0};
    vsnprintf(line, sizeof(line), fmt, vl);
    ZivPlayer_Debug(line);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    ZivPlayer_Debug("Enter JNI_OnLoad\n");
#if 0
    extern AVCodec h264_decoder;
    if(CODEC_ID_H264)
    {
            avcodec_register(&h264_decoder);
    }

    extern AVInputFormat ff_h264_dmuxer;
    if(CONFIG_H264_DEMUXER)
    {
            av_register_input_format(&ff_h264_demuxer);
    }
#else
    avcodec_register_all();
    av_register_all();
#endif
    avformat_network_init();

    //av_log_set_callback(av_log_callback);
    //av_log_set_level(AV_LOG_ERROR);
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    ZivPlayer_Debug("Enter JNI_OnUnload\n");
}


JNIEXPORT jint JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_nativeInit
        (JNIEnv *env, jobject obj, jint color_format)
{
#if 1
    DecoderContext *ctx = (DecoderContext*)calloc(1, sizeof(DecoderContext));
    ZivPlayer_Debug("Creating native H264 decoder context");

    switch (color_format)
    {
        case COLOR_FORMAT_YUV420:
            ctx->color_format = AV_PIX_FMT_YUV420P;
            break;
        case COLOR_FORMAT_RGB565LE:
            ctx->color_format = AV_PIX_FMT_RGB565LE;
            break;
        case COLOR_FORMAT_BGR32:
            ctx->color_format = AV_PIX_FMT_BGR32;
            break;
    }

    ctx->codec = avcodec_find_decoder(AV_CODEC_ID_H264);

    ctx->codec_ctx = avcodec_alloc_context3(ctx->codec);
    ctx->codec_ctx->pix_fmt = AV_PIX_FMT_YUV420P;
    ctx->codec_ctx->flags2 |= CODEC_FLAG2_CHUNKS;

    ctx->src_frame = av_frame_alloc();
    ctx->dst_frame = av_frame_alloc();
    avcodec_open2(ctx->codec_ctx, ctx->codec, NULL);

    set_ctx(env, obj, ctx);
#endif
    return 0;
}


JNIEXPORT jint JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_nativeDestroy
        (JNIEnv *env, jobject obj)
{
    avformat_close_input(&i_fmt_ctx);

    DecoderContext *ctx = get_ctx(env, obj);

    ZivPlayer_Debug("Destroying native H264 decoder context\n");

    avcodec_close(ctx->codec_ctx);
    av_free(ctx->codec_ctx);
    av_free(ctx->src_frame);
    av_free(ctx->dst_frame);

    free(ctx);

    return 0;
}


JNIEXPORT jboolean JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_startStream
        (JNIEnv *env, jobject obj, jstring urlStr)
{
    const char* rtspUrl = (*env)->GetStringUTFChars(env, urlStr, 0);//(env, urlStr, 0);
    ZivPlayer_Debug("rtspUrl:%s", rtspUrl);

    AVDictionary* options = NULL;
    //av_dict_set(&options, "rtsp_transport", "udp_multicast", 0);  //wired LAN OK.
    //av_dict_set(&options, "rtsp_transport", "udp", 0);
    av_dict_set(&options, "rtsp_transport", "tcp", 0);

    if (avformat_open_input(&i_fmt_ctx, rtspUrl, NULL, &options)!=0)
    {
        ZivPlayer_Debug("could not open input stream\n");
        return JNI_FALSE;
    }

    if (avformat_find_stream_info(i_fmt_ctx, NULL)<0)
    {
        ZivPlayer_Debug("could not find stream info\n");
        return JNI_FALSE;
    }

    //find first video stream
    int i = 0;
    for (i=0; i<i_fmt_ctx->nb_streams; i++)
    {
        if (i_fmt_ctx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO)
        {
            i_video_stream = i_fmt_ctx->streams[i];
            break;
        }
    }

    if (i_video_stream == NULL)
    {
        ZivPlayer_Debug("didn't find any video stream\n");
        return JNI_FALSE;
    }

    int last_pts = 0;
    int last_dts = 0;

    int64_t pts, dts;

    AVPacket i_pkt;
    av_init_packet(&i_pkt);

    while(!bStop)
    {
        i_pkt.size = 0;
        i_pkt.data = NULL;
        if (av_read_frame(i_fmt_ctx, &i_pkt) <0 )
        {
            ZivPlayer_Debug("av_read_frame error\n");
            break;
        }

        //pts and dts should increase monotonically pts should be >= dts
        i_pkt.flags |= AV_PKT_FLAG_KEY;
        pts = i_pkt.pts;
        i_pkt.pts += last_pts;
        dts = i_pkt.dts;
        i_pkt.dts += last_dts;
        i_pkt.stream_index = 0;

        static int num = 1;
#if 1
        ZivPlayer_Debug("frame[%d]{pts:%lld,dts:%lld,size:%d,data:%02x%02x%02x%02x%02x}",
                   num++,
                   i_pkt.pts,
                   i_pkt.dts,
                   i_pkt.size,
                   i_pkt.data[0],
                   i_pkt.data[1],
                   i_pkt.data[2],
                   i_pkt.data[3],
                   i_pkt.data[4] );
        if((i_pkt.data[4] & 0x1F) == 1)
        {
            ZivPlayer_Debug("P Frame");
        }
        else if((i_pkt.data[4] & 0x1F) == 5)
        {
            ZivPlayer_Debug("I Frame");
        }
        else if((i_pkt.data[4] & 0x1F) == 7)
        {
            ZivPlayer_Debug("SPS with PPS , SEI and IDR Frame");
        }
#endif//

        if((i_pkt.data[4] & 0x1F) == 7)//SPS with PPS, SEI and IDR.
        {
#if 0
            consumeNalUnitsFromBuffer(env, obj, i_pkt.data, 15, i_pkt.pts);
            consumeNalUnitsFromBuffer(env, obj, i_pkt.data + 15, 8, i_pkt.pts);
            consumeNalUnitsFromBuffer(env, obj, i_pkt.data + 23, 9, i_pkt.pts);
            consumeNalUnitsFromBuffer(env, obj, i_pkt.data + 32, i_pkt.size - 32, i_pkt.pts);
#else
            consumeNalUnitsFromBuffer(env, obj, i_pkt.data, i_pkt.size, i_pkt.pts);
#endif
        }
        else//P Slice.
        {
            //consumeNalUnitsFromBuffer(env, obj, i_pkt.data, i_pkt.size, i_pkt.pts);
        }
    }

    last_dts += dts;
    last_pts += pts;

    return JNI_TRUE;
}


JNIEXPORT jboolean JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_stopStream
        (JNIEnv *env, jobject obj, jstring urlStr)
{
    bStop = 1;
    return JNI_TRUE;
}

int consumeNalUnitsFromBuffer(JNIEnv *env, jobject thiz, char* nalBuf, int nalSize, long pkt_pts)
{
    DecoderContext *ctx = get_ctx(env, thiz);

    AVPacket packet = {
        .data = (uint8_t*)nalBuf,
        .size = nalSize,
        .pts = pkt_pts
    };

    int frameFinished = 0;
    int res = avcodec_decode_video2(ctx->codec_ctx, ctx->src_frame, &frameFinished, &packet);
    if(res < 0)
    {
        ZivPlayer_Debug("avcodec_decode-video2 error.");
    }

    if (frameFinished)
    {
        ctx->frame_ready = 1;
        ZivPlayer_Debug("Frame read");
    }
    return;
}


JNIEXPORT jboolean JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_setRender
        (JNIEnv *env, jobject obj, jstring urlStr)
{
    return JNI_TRUE;
}


JNIEXPORT jboolean JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_isFrameReady
        (JNIEnv *env, jobject obj)
{
    DecoderContext *ctx = get_ctx(env, obj);
    return ctx->frame_ready ? JNI_TRUE : JNI_FALSE;
}


JNIEXPORT jint JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_getVideoWidth
        (JNIEnv *env, jobject obj)
{
    DecoderContext *ctx = get_ctx(env, obj);
    return ctx->codec_ctx->width;
}


JNIEXPORT jint JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_getVideoHeight
        (JNIEnv *env, jobject obj)
{
    DecoderContext *ctx = get_ctx(env, obj);
    return ctx->codec_ctx->height;
}


JNIEXPORT jint JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_getOutputByteSize
        (JNIEnv *env, jobject obj)
{
    DecoderContext *ctx = get_ctx(env, obj);
    return avpicture_get_size(ctx->color_format, ctx->codec_ctx->width, ctx->codec_ctx->height);
}

jlong JNICALL JNICALL Java_com_zivdigi_helloffmpeg_ZivPlayer_decodeFrameToDirectBuffer
        (JNIEnv *env, jobject thiz, jobject out_buffer)
{
    DecoderContext *ctx = get_ctx(env, thiz);

    if (!ctx->frame_ready)
        return -1;

    void *out_buf = (*env)->GetDirectBufferAddress(env, out_buffer);
    if (out_buf == NULL)
    {
        ZivPlayer_Debug("Error getting direct buffer address");
        return -1;
    }

    long out_buf_len = (*env)->GetDirectBufferCapacity(env, out_buffer);

    int pic_buf_size = avpicture_get_size(ctx->color_format, ctx->codec_ctx->width, ctx->codec_ctx->height);

    if (out_buf_len < pic_buf_size)
    {
        ZivPlayer_Debug("Input buffer too small");
        return -1;
    }

    if (ctx->color_format == COLOR_FORMAT_YUV420)
    {
        memcpy(ctx->src_frame->data, out_buffer, pic_buf_size);
    }
    else
    {
        if (ctx->convert_ctx == NULL)
        {
            ctx->convert_ctx = sws_getContext(ctx->codec_ctx->width, ctx->codec_ctx->height, ctx->codec_ctx->pix_fmt,
                                              ctx->codec_ctx->width, ctx->codec_ctx->height, ctx->color_format, SWS_FAST_BILINEAR, NULL, NULL, NULL);
        }

        avpicture_fill((AVPicture*)ctx->dst_frame, (uint8_t*)out_buf, ctx->color_format, ctx->codec_ctx->width,
                       ctx->codec_ctx->height);

        sws_scale(ctx->convert_ctx, (const uint8_t**)ctx->src_frame->data, ctx->src_frame->linesize, 0, ctx->codec_ctx->height,
                  ctx->dst_frame->data, ctx->dst_frame->linesize);
    }

    ctx->frame_ready = 0;

    if (ctx->src_frame->pkt_pts == AV_NOPTS_VALUE)
    {
        ZivPlayer_Debug("No PTS was passed from avcodec_decode!");
    }

    return ctx->src_frame->pkt_pts;
}


