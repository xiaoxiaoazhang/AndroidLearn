package com.chihun.learn.jnidemo;

// 参考文章
// https://www.zybuluo.com/cxm-2016/note/564038
// https://blog.csdn.net/xyang81/article/details/45770551
public class JNIExceptionLearn {

    public static native void action(String message);

    public static void exceptionCallback() {
        int a = 20 / 0;
        System.out.println("--->" + a);
    }

    public static void normalCallback() {
        System.out.println("In Java: invoke normalCallback.");
    }

    static {
        System.loadLibrary("jniexceptionlearn");
    }
}
