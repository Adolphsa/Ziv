//
// Created by walker on 16-3-29.
//

#ifndef HELLOFFMPEG_SOURCE_FILE_H
#define HELLOFFMPEG_SOURCE_FILE_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvFormatVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvFormatVersion
        (JNIEnv *, jobject);

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvCodecVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvCodecVersion
        (JNIEnv *, jobject);

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvDeviceVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvDeviceVersion
        (JNIEnv *, jobject);

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvFilterVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvFilterVersion
        (JNIEnv *, jobject);

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getSwResampleVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getSwResampleVersion
        (JNIEnv *, jobject);

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getAvUtilVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getAvUtilVersion
        (JNIEnv *, jobject);

/*
 * Class:     com_zivdigi_helloffmpeg_FfmpegUtils
 * Method:    getSwScaleVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_zivdigi_helloffmpeg_FfmpegUtils_getSwScaleVersion
        (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif //HELLOFFMPEG_SOURCE_FILE_H
