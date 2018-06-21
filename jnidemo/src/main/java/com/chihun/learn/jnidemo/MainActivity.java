package com.chihun.learn.jnidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JniTest jniTest = new JniTest();
        Log.d("MainActivity", "random: " + jniTest.getRandomNum());
        Log.d("MainActivity", "string: " + jniTest.getNativeString());
    }
}
