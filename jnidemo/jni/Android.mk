# 设置当前的编译目录（Android.mk所在的目录）
LOCAL_PATH := $(call my-dir)

# 清除LOCAL_XX变量（LOCAL_PATH除外）
include $(CLEAR_VARS)

# 指定当前编译模块的名称
LOCAL_MODULE := JNI_FIRST_DEMO

# 编译模块需要的源文件
LOCAL_SRC_FILES := demo/firstdemo.cpp

# 指定编译出的库类型，BUILD_SHARED_LIBRARY：动态库；BUILD_STATIC_LIBRARY：静态库， BUILD_EXECUTEABLE指：可执行文件
include $(BUILD_SHARED_LIBRARY)