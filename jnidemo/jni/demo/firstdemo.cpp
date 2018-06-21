//
// Created by zhenduowang on 2018/6/21.
//

// jni头文件
#include <jni.h>

#include <cassert>
#include <cstdlib>
#include <iostream>
using namespace std;
static jclass myClass;
static const char* const kClassName = "com/chihun/learn/jnidemo/JniTest";
//native 方法实现
jint get_random_num(){
    return rand();
}

jstring get_native_string(JNIEnv *env, jclass clz) {
    return env->NewStringUTF("Hello java, this is C++. ---jni");
}
/*需要注册的函数列表，放在JNINativeMethod 类型的数组中，
以后如果需要增加函数，只需在这里添加就行了
参数：
1.java代码中用native关键字声明的函数名字符串
2.签名（传进来参数类型和返回值类型的说明）
3.C/C++中对应函数的函数名（地址）
*/
static JNINativeMethod getMethods[] = {
        {"getRandomNum","()I",(void*)get_random_num},
        {"getNativeString","()Ljava/lang/String;",(void*)get_native_string}
};
//此函数通过调用JNI中 RegisterNatives 方法来注册我们的函数
static int registerNativeMethods(JNIEnv* env, const char* className,JNINativeMethod* getMethods,int methodsNum){
    jclass clazz;
    //找到声明native方法的类
    clazz = env->FindClass(className);
    if(clazz == NULL){
        return JNI_FALSE;
    }
    //注册函数 参数：java类 所要注册的函数数组 注册函数的个数
    if(env->RegisterNatives(clazz,getMethods,methodsNum) < 0){
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv* env){
    //指定类的路径，通过FindClass 方法来找到对应的类
    return registerNativeMethods(env, kClassName, getMethods, sizeof(getMethods) / sizeof(getMethods[0]));
}
//回调函数 在这里面注册函数
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* env = NULL;
    //判断虚拟机状态是否有问题
    if(vm->GetEnv((void**)&env,JNI_VERSION_1_6)!= JNI_OK){
        return -1;
    }
    assert(env != NULL);
//    myClass = (*env)->FindClass(env, kClassName);
//    if(myClass == NULL)
//    {
//        printf("cannot get class:%s\n", kClassName);
//        return -1;
//    }
//
//    //开始注册函数 registerNatives -》registerNativeMethods -》env->RegisterNatives
//    if((*env)->RegisterNatives(env,myClass,gMethods,sizeof(gMethods)/sizeof(gMethods[0]))<0)
//    {
//        printf("register native method failed!\n");
//        return -1;
//    }

    if(!registerNatives(env)){
        return -1;
    }
    //返回jni 的版本
    return JNI_VERSION_1_6;
}