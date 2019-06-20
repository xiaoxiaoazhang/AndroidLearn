package com.chihun.learn.audiorecorddemo.audio;

public class DefaultAudioCaptureFactory implements CustomAudioCaptureFactory {

    @Override
    public CustomAudioCapture create() {
        return new DefaultAudioCapture();
    }
}
