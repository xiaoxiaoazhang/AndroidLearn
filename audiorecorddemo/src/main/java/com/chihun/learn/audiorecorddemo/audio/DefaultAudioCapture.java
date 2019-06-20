package com.chihun.learn.audiorecorddemo.audio;

import android.media.AudioDeviceInfo;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.NonNull;

import com.chihun.learn.audiorecorddemo.logger.Log;

public class DefaultAudioCapture implements CustomAudioCapture {

    private AudioRecord audioRecord;
    private boolean isRecording;

    @Override
    public void startRecording() throws Exception {

        int minBufferSize = AudioRecord.getMinBufferSize(AudioConfig.SAMPLE_RATE_IN_HZ, AudioConfig.CHANNAL_CONFIG, AudioConfig.AUDIO_FORMAT);
        int bufferSize = Math.max(AudioConfig.AUDIO_RECORD_BUFFER_SIZE_IN_BYTE, minBufferSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AudioConfig.SAMPLE_RATE_IN_HZ, AudioConfig.CHANNAL_CONFIG, AudioConfig.AUDIO_FORMAT, bufferSize);
        audioRecord.startRecording();
        if (Build.VERSION.SDK_INT >= 23) {
            AudioDeviceInfo routedDevice = this.audioRecord.getRoutedDevice();
            if (routedDevice != null) {
                Log.i("routed device: " + routedDevice.getProductName());
            } else {
                Log.d("routed device is null ");
            }
        }

        Log.d("startRecording audioRecord=" + this.audioRecord.getState() + " minBufferSize=" + minBufferSize + " bufferSize=" + bufferSize + " AUDIO_READ_SIZE_IN_SHORTS=" + 3200);
        this.isRecording = true;
    }

    @Override
    public void finishRecording() throws Exception {
        Log.d("finishRecording audioRecord=" + audioRecord);
        if (isRecording) {
            isRecording = false;
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = null;
        }
    }

    @Override
    public int getAudioBufferSize() {
        return 3200;
    }

    @Override
    public int getAudioIntervalMillis() {
        return 200;
    }

    @Override
    public int record(@NonNull short[] audioBuffer, int audioBufferSize) throws Exception {
        if (!isRecording || audioRecord == null) {
            Log.d("isRecording == false");
            return -1;
        }

        int readed = this.audioRecord.read(audioBuffer, 0, audioBufferSize);
        if (readed < 0) {
            switch(readed) {
                case -3:
                    throw new Exception("AudioRecord.read didn't work : AudioRecord.ERROR_INVALID_OPERATION");
                case -2:
                    throw new Exception("AudioRecord.read didn't work : AudioRecord.ERROR_BAD_VALUE");
                case -1:
                    throw new Exception("AudioRecord.read didn't work : AudioRecord.ERROR");
                default:
                    throw new Exception("AudioRecord.read didn't work : " + readed);
            }
        } else {
            return readed;
        }
    }
}
