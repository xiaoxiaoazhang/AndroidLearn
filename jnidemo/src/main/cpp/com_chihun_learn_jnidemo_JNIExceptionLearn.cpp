#include "com_chihun_learn_jnidemo_JNIExceptionLearn.h"
#include <android/log.h>
// 信号量
#include <signal.h>
#include <setjmp.h>
#include <pthread.h>

#define LOG_TAG "JNILog"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
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
  // 抛出异常工具类 JNU_ThrowByName(env, "java/lang/Exception", "exception from jni: jni exception happened at p0");
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


   /*
   jni捕获异常的方法之二：捕捉系统崩溃信号，适用于代码量大的情况。跳转到指定的异常处理逻辑代码，相当于全局异常处理
   */

   // 定义代码跳转锚点
   sigjmp_buf JUMP_ANCHOR;
   volatile sig_atomic_t error_cnt = 0;

   void exception_handler(int errorCode){
         error_cnt += 1;
         LOGE("JNI_ERROR, error code %d, cnt %d", errorCode, error_cnt);

         // DO SOME CLEAN STAFF HERE...

         // jump to main function to do exception process
         siglongjmp(JUMP_ANCHOR, 1);
   }

   jint process(JNIEnv * env, jobject jobj, jint m, jint n) {
       char* a = NULL;
       int val1 = a[1] - '0';

       char* b = NULL;
       int val2 = b[1] - '0';

       LOGE("val 1 %d", val1);
       return val1/val2;
   }

JNIEXPORT jint JNICALL Java_com_chihun_learn_jnidemo_JNIExceptionLearn_jniDivide
     (JNIEnv * env, jobject jobj, jint m, jint n) {
     // 注册需要捕获的异常信号
           /*
            1    HUP Hangup                        33     33 Signal 33
            2    INT Interrupt                     34     34 Signal 34
            3   QUIT Quit                          35     35 Signal 35
            4    ILL Illegal instruction           36     36 Signal 36
            5   TRAP Trap                          37     37 Signal 37
            6   ABRT Aborted                       38     38 Signal 38
            7    BUS Bus error                     39     39 Signal 39
            8    FPE Floating point exception      40     40 Signal 40
            9   KILL Killed                        41     41 Signal 41
           10   USR1 User signal 1                 42     42 Signal 42
           11   SEGV Segmentation fault            43     43 Signal 43
           12   USR2 User signal 2                 44     44 Signal 44
           13   PIPE Broken pipe                   45     45 Signal 45
           14   ALRM Alarm clock                   46     46 Signal 46
           15   TERM Terminated                    47     47 Signal 47
           16 STKFLT Stack fault                   48     48 Signal 48
           17   CHLD Child exited                  49     49 Signal 49
           18   CONT Continue                      50     50 Signal 50
           19   STOP Stopped (signal)              51     51 Signal 51
           20   TSTP Stopped                       52     52 Signal 52
           21   TTIN Stopped (tty input)           53     53 Signal 53
           22   TTOU Stopped (tty output)          54     54 Signal 54
           23    URG Urgent I/O condition          55     55 Signal 55
           24   XCPU CPU time limit exceeded       56     56 Signal 56
           25   XFSZ File size limit exceeded      57     57 Signal 57
           26 VTALRM Virtual timer expired         58     58 Signal 58
           27   PROF Profiling timer expired       59     59 Signal 59
           28  WINCH Window size changed           60     60 Signal 60
           29     IO I/O possible                  61     61 Signal 61
           30    PWR Power failure                 62     62 Signal 62
           31    SYS Bad system call               63     63 Signal 63
           32     32 Signal 32                     64     64 Signal 64
           */

           // 代码跳转锚点
           if (sigsetjmp(JUMP_ANCHOR, 1) != 0) {
               return -1;
           }

           // 注册要捕捉的系统信号量
           struct sigaction sigact;
           struct sigaction old_action;
           sigaction(SIGABRT, NULL, &old_action);
           if (old_action.sa_handler != SIG_IGN) {
               sigset_t block_mask;
               sigemptyset(&block_mask);
               sigaddset(&block_mask, SIGABRT); // handler处理捕捉到的信号量时，需要阻塞的信号
               sigaddset(&block_mask, SIGSEGV); // handler处理捕捉到的信号量时，需要阻塞的信号

               sigemptyset(&sigact.sa_mask);
               sigact.sa_flags = 0;
               sigact.sa_mask = block_mask;
               sigact.sa_handler = exception_handler;
               sigaction(SIGABRT, &sigact, NULL); // 注册要捕捉的信号
               sigaction(SIGSEGV, &sigact, NULL); // 注册要捕捉的信号
           }

           jint value = process(env, jobj, m, n);
           return value;
   }