#include <jni.h>
#include <string.h>
#include <stdlib.h>

#define __USE_GNU
#include <sched.h>
#include <ctype.h>
#include <pthread.h>

#include "libavformat/avformat.h"
#include "libavutil/avutil.h"
#include "libavdevice/avdevice.h"
#include "libavfilter/avfilter.h"
#include "libswresample/swresample.h"
#include "libavcodec/avcodec.h"
#include "libswscale/swscale.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvFormatVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvFormatVersion
        (JNIEnv *env, jobject obj)
{
        //return (*env)->NewStringUTF(env, "Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvFormatVersion");
        static char avformatVertion[64] = {0};
        sprintf(avformatVertion, "AvFormatVersion:%d", avformat_version());
        return (*env)->NewStringUTF(env, avformatVertion);
}

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvCodecVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvCodecVersion
        (JNIEnv *env, jobject obj)
{
        //return (*env)->NewStringUTF(env, "Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvCodecVersion");

        static char avcodecVersionStr[64] = {0};
        sprintf(avcodecVersionStr, "AvCodecVersion:%d", avcodec_version());
        return (*env)->NewStringUTF(env, avcodecVersionStr);
}

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvDeviceVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvDeviceVersion
        (JNIEnv *env, jobject obj)
{
        //return (*env)->NewStringUTF(env, "Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvDeviceVersion");

        static char avdeviceVertion[64] = {0};
        sprintf(avdeviceVertion, "AvDeviceVersion:%d", avdevice_version());
        return (*env)->NewStringUTF(env, avdeviceVertion);
}

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvFilterVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvFilterVersion
        (JNIEnv *env, jobject obj)
{
        //return (*env)->NewStringUTF(env, "Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvFilterVersion");
        static char avfilterVersion[64] = {0};
        sprintf(avfilterVersion, "AvFilterVersion:%d", avfilter_version());
        return (*env)->NewStringUTF(env, avfilterVersion);
}

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getSwResampleVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getSwResampleVersion
        (JNIEnv *env, jobject obj)
{
        //return (*env)->NewStringUTF(env, "Java_com_zivdigi_helloffmpeg_FfmpegUtils_getSwResampleVersion");
        static char swresampleVersion[64] = {0};
        sprintf(swresampleVersion, "SwResampleVersion:%d", swresample_version());
        return (*env)->NewStringUTF(env, swresampleVersion);
}

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvUtilVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvUtilVersion
        (JNIEnv *env, jobject obj)
{
        //return (*env)->NewStringUTF(env, "Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvUtilVersion");
        static char avutilVersion[64] = {0};
        sprintf(avutilVersion, "AvUtilVersion:%d", avutil_version());
        return (*env)->NewStringUTF(env, avutilVersion);
}

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getSwScaleVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getSwScaleVersion
        (JNIEnv *env, jobject obj)
{
        //return (*env)->NewStringUTF(env, "Java_com_zivdigi_helloffmpeg_FfmpegUtils_getSwScaleVersion");
        static char swscaleVersion[64] = {0};
        sprintf(swscaleVersion, "SwScaleVersion:%d", swscale_version());
        return (*env)->NewStringUTF(env, swscaleVersion);
}


#ifdef __cplusplus
}
#endif

//TODO: change the fixed string with the real version string by call the API of the Library.