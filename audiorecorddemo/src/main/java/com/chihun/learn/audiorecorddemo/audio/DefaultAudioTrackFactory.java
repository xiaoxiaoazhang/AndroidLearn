package com.chihun.learn.audiorecorddemo.audio;

public class DefaultAudioTrackFactory implements CustomAudioTrackFactory {

    @Override
    public CustomAudioTrack create() {
        return new DefaultAudioTrack();
    }
}
