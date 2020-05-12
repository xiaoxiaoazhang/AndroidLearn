package com.ubtechinc.cruzr.javaassistdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ubtechinc.cruzr.common.MyClass;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyClass object = new MyClass();
    }
}
