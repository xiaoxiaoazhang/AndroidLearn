package com.chihun.learn.audiorecorddemo.audio;

import android.media.AudioFormat;

public class AudioConfig {

    public static final int SAMPLE_RATE_IN_HZ = 16000;
    public static final int AUDIO_RECORD_BUFFER_SIZE_IN_BYTE = 32000;
    public static final int CHANNAL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
}
