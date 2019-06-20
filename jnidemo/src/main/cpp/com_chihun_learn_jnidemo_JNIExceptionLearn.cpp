#include "com_chihun_learn_jnidemo_JNIExceptionLearn.h"
#include <android/log.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , "JNILearn", __VA_ARGS__)

/*
 * Class:     com_chihun_learn_jnidemo_JNIExceptionLearn
 * Method:    action
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIExceptionLearn_action
  (JNIEnv *env, jclass cls, jstring msg) {

    //const jchar *c_str = env->GetStringChars(msg, JNI_FALSE);
    const char *c_str = env->GetStringUTFChars(msg, JNI_FALSE);
    if (c_str == NULL) {
        return;
    }
    LOGD("message: %s", c_str);

    jthrowable exc = NULL;
    jmethodID mid = env->GetStaticMethodID(cls,"exceptionCallback","()V");
    if (mid != NULL) {
        env->CallStaticVoidMethod(cls,mid);
    }
    LOGD("In C: JNICALL Java_com_chihun_learn_jnidemo_JNIExceptionLearn_action-->called!!!!");
    if (env->ExceptionCheck()) {  // 检查JNI调用是否有引发异常,若有异常返回JNI_TRUE，否则返回JNI_FALSE
        env->ExceptionDescribe();
        env->ExceptionClear(); // 清除引发的异常，在Java层不会打印异常的堆栈信息（如果不清除，后面调用ThrowNew抛出的异常堆栈信息会覆盖前面的异常信息）
        env->ThrowNew(env->FindClass("java/lang/Exception"), "JNI抛出的异常！");
        env->ReleaseStringUTFChars(msg, c_str); // 发生异常后释放前面所分配的内存
        return;
    }

    /*
    exc = env->ExceptionOccurred();  // 返回一个指向当前异常对象的引用，和ExceptionCheck类似，只是返回值不一样,若用异常返回该异常的引用，否则返回NULL
    if (exc) {
        env->ExceptionDescribe(); // 打印Java层抛出的异常堆栈信息
        env->ExceptionClear();        // 清除异常信息

        // 抛出我们自己的异常处理
        jclass newExcCls;
        newExcCls = env->FindClass("java/lang/Exception");
        if (newExcCls == NULL) {
            return;
        }
        env->ThrowNew(newExcCls, "throw from C Code.");
    }*/

    mid = env->GetStaticMethodID(cls,"normalCallback","()V");
    if (mid != NULL) {
        env->CallStaticVoidMethod(cls,mid);
    }
    env->ReleaseStringUTFChars(msg, c_str);
  }
  // 抛出异常工具类
  void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg)
   {
       // 查找异常类
       jclass cls = env->FindClass(name);
       /* 如果这个异常类没有找到，VM会抛出一个NowClassDefFoundError异常 */
       if (cls != NULL) {
           env->ThrowNew(cls, msg);  // 抛出指定名字的异常
       }
       /* 释放局部引用 */
       env->DeleteLocalRef(cls);
   }