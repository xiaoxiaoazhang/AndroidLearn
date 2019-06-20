package com.chihun.learn.audiorecorddemo.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class DefaultAudioTrack implements CustomAudioTrack {

    private AudioTrack mAudioTrack;

    @Override
    public void initPlayer() {
        int playBufSize = AudioTrack.getMinBufferSize(16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, playBufSize,
                AudioTrack.MODE_STREAM);
    }

    @Override
    public void startPlayer() {
        mAudioTrack.play();
    }

    @Override
    public void stopPlayer() {

    }
}
