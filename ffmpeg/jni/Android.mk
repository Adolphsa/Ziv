LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := FfmpegUtils
LOCAL_SRC_FILES := ffmpegUtils.c

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86_64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/lib
endif


LOCAL_C_INCLUDES := $(FFMPEG_INC_DIR)/
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavdevice.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavformat.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavcodec.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavutil.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavfilter.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswresample.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libpostproc.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswscale.a


LOCAL_LDFLAGS += -lz
include $(BUILD_SHARED_LIBRARY)

####################################################################################################
include $(CLEAR_VARS)
LOCAL_MODULE := H264Decoder
LOCAL_SRC_FILES := H264Decoder.c

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86_64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/lib
endif


LOCAL_C_INCLUDES := $(FFMPEG_INC_DIR)/
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavdevice.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavformat.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavcodec.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavutil.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavfilter.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswresample.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libpostproc.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswscale.a

LOCAL_LDFLAGS += -lz
LOCAL_LDFLAGS += -llog
include $(BUILD_SHARED_LIBRARY)


####################################################################################################
include $(CLEAR_VARS)
LOCAL_MODULE := RTSPClient
LOCAL_SRC_FILES := RTSPClient.c

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86_64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/lib
endif


LOCAL_C_INCLUDES := $(FFMPEG_INC_DIR)/
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavdevice.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavformat.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavcodec.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavutil.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavfilter.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswresample.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libpostproc.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswscale.a

LOCAL_LDFLAGS += -lz
LOCAL_LDFLAGS += -llog
include $(BUILD_SHARED_LIBRARY)


####################################################################################################
include $(CLEAR_VARS)
LOCAL_MODULE := ZivPlayer
LOCAL_SRC_FILES := ZivPlayer.c

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/arm64-v8a/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/armeabi-v7a/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips/lib
endif

ifeq ($(TARGET_ARCH_ABI),mips64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/mips64/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86_64)
FFMPEG_INC_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/include
FFMPEG_LIB_DIR := $(LOCAL_PATH)/ffmpeg-android-lib/x86_64/lib
endif


LOCAL_C_INCLUDES := $(FFMPEG_INC_DIR)/
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavdevice.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavformat.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavcodec.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavutil.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libavfilter.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswresample.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libpostproc.a
LOCAL_LDLIBS += $(FFMPEG_LIB_DIR)/libswscale.a

LOCAL_LDFLAGS += -lz
LOCAL_LDFLAGS += -llog
include $(BUILD_SHARED_LIBRARY)
