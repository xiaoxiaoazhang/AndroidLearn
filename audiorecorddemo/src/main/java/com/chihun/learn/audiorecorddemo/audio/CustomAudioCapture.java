package com.chihun.learn.audiorecorddemo.audio;

import android.support.annotation.NonNull;

public interface CustomAudioCapture {

    void startRecording() throws Exception;

    void finishRecording() throws Exception;

    int getAudioBufferSize();

    int getAudioIntervalMillis();

    int record(@NonNull short[] var1, int var2) throws Exception;
}
