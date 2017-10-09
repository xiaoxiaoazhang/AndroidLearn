package com.chihun.learn.apkupdatedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chihun.learn.apkupdatedemo.services.UpdateService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            startService(new Intent(this, UpdateService.class));
        }
    }
}
