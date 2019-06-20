package com.chihun.learn.audiorecorddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chihun.learn.audiorecorddemo.audio.CustomAudioCapture;
import com.chihun.learn.audiorecorddemo.audio.CustomAudioTrack;
import com.chihun.learn.audiorecorddemo.audio.DefaultAudioCaptureFactory;
import com.chihun.learn.audiorecorddemo.audio.DefaultAudioTrack;
import com.chihun.learn.audiorecorddemo.audio.DefaultAudioTrackFactory;

public class MainActivity extends AppCompatActivity {

    CustomAudioCapture audioCapture = new DefaultAudioCaptureFactory().create();
    CustomAudioTrack audioTrack = new DefaultAudioTrackFactory().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:

                break;
            case R.id.btn_resume:

                break;
            case R.id.btn_pause:

                break;
            case R.id.btn_stop:

                break;
            case R.id.btn_start_play:

                break;
            case R.id.btn_stop_play:

                break;
        }
    }
}
