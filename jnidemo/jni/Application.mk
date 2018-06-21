LOCAL_PATH:= $(call my-dir)

APP_BUILD_SCRIPT := $(LOCAL_PATH)/Android.mk
APP_ABI := armeabi armeabi-v7a arm64-v8a x86 x86_64
APP_PLATFORM := android-19
APP_STL := c++_static
APP_PIE := true
APP_DEPRECATED_HEADERS := true

ifdef USE_GCC
	NDK_TOOLCHAIN_VERSION := 4.9
endif
