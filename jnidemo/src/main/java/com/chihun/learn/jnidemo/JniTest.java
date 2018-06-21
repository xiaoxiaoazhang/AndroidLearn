package com.chihun.learn.jnidemo;

public class JniTest {
    public native int getRandomNum();
    public native String getNativeString();

    static {
        System.loadLibrary("JNI_FIRST_DEMO");
    }
}
