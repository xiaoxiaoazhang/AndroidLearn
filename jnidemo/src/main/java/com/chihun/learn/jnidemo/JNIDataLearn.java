package com.chihun.learn.jnidemo;


import android.util.Log;

import com.chihun.learn.jnidemo.entity.Person;

import java.util.Arrays;

/**
 * 先make module，然后在命令行中切换到build\intermediates\javac\debug\compileDebugJavaWithJavac\classes目录
 * 输入javah com.chihun.learn.jnidemo.JNIDataLearn
 * 将会在此目录下生成com_chihun_learn_jnidemo_JNIDataLearn.h及com_chihun_learn_jnidemo_JNIDataLearn_InstanceHolder.h文件
 * 将这两个文件复制到src\main\cpp目录下即可
 *
 * jni语法可以参考文献
 * https://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/jniTOC.html
 * https://www.zybuluo.com/cxm-2016/note/566595
 */
public class JNIDataLearn {
    private static final String TAG = JNIDataLearn.class.getSimpleName();
    static {
        System.loadLibrary("jnidatalearn");
    }

    private JNIDataLearn() {

    }
    private static class InstanceHolder{
        private final static JNIDataLearn INSTANCE = new JNIDataLearn();
    }
    public static JNIDataLearn getInstance(){
        return InstanceHolder.INSTANCE;
    }

    public void learn() {
        Log.i(TAG, "getString: " + getString());
        setString("hello word!!");
        String[] strings = getStringArray();
        if (null != strings) {
            for (String s : strings) {
                Log.i(TAG, "strArray: " + s);
            }
        }
        setStringArray(new String[]{"你好", "呀", "Hello", "world"});
        int[] intArray = getIntArray();
        if (null != intArray) {
            Log.i(TAG, Arrays.toString(intArray));
        }
        setIntArray(new int[]{1, 2, 3});

        long[] longArray = getLongArray();
        if (null != longArray) {
            Log.i(TAG, Arrays.toString(longArray));
        }
        setLongArray(new long[]{100L, 200L, 300L});

        String[][] string2d = getString2DArray();
        if (null != string2d) {
            for (int i = 0; i < string2d.length; i++) {
                for (int j = 0; j < string2d[i].length; j++) {
                    Log.i(TAG, "str2dArray: " + string2d[i][j]);
                }
            }
        }

        Person person = getPerson();
        Log.i(TAG, person.toString());

        Person[] people = getPersonArray();
        if (null != people && people.length > 0) {
            for (Person p : people) {
                Log.i(TAG, p.toString());
            }
        }
    }

    private native String getString();
    private native int getInt();
    private native long getLong();
    private native float getFloat();
    private native double getDouble();
    private native char getChar();
    private native byte getByte();
    private native boolean getBoolean();
    private native short getShort();

    private native void setString(String s);
    private native void setInt(int i);
    private native void setLong(long l);
    private native void setFloat(float f);
    private native void setDouble(double d);
    private native void setChar(char c);
    private native void setByte(byte b);
    private native void setBoolean(boolean b);
    private native void setShort(short s);

    private native void setStringArray(String[] stringArray);
    private native String[] getStringArray();

    private native void setIntArray(int[] intArray);
    private native int[] getIntArray();

    private native void setLongArray(long[] longArray);
    private native long[] getLongArray();

    private native String[][] getString2DArray();

    private native Person[] getPersonArray();
    private native Person getPerson();
}
