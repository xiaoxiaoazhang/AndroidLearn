package com.chihun.learn.camerademo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SweetApplication.onCreate(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(new Intent(MainActivity.this, IdCardActivity.class));
                break;
        }
    }
}
