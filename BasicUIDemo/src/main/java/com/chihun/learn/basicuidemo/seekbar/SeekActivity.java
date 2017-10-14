package com.chihun.learn.basicuidemo.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chihun.learn.basicuidemo.R;

import java.util.ArrayList;

public class SeekActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, ResponseOnTouch {
    SeekBar mSeekBar;
    TextView mProgressText;
    TextView mTrackingText;
    CustomSeekbar customSeekBar;
    Custom2SeekBar custom2SeekBar;
    Custom2SeekBar custom2SeekBar2;

    private ArrayList<String> volume_sections = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);

        mSeekBar = (SeekBar) findViewById(R.id.seek);
        mSeekBar.setOnSeekBarChangeListener(this);
        mProgressText = (TextView) findViewById(R.id.progress);
        mTrackingText = (TextView) findViewById(R.id.tracking);
        customSeekBar = (CustomSeekbar) findViewById(R.id.myCustomSeekBar);
        custom2SeekBar = (Custom2SeekBar) findViewById(R.id.myCustomSeekBar2);
        custom2SeekBar2 = (Custom2SeekBar) findViewById(R.id.myCustomSeekBar3);
        volume_sections.add("1M");
        volume_sections.add("2M");
        volume_sections.add("3M");
        volume_sections.add("4M");
        volume_sections.add("5M");
        volume_sections.add("6M");
        volume_sections.add("7M");
        volume_sections.add("8M");
        customSeekBar.initData(volume_sections);
        customSeekBar.setProgress(0);
//        customSeekBar.setResponseOnTouch(this);//activity实现了下面的接口ResponseOnTouch，每次touch会回调onTouchResponse

        custom2SeekBar.setData(volume_sections);
        custom2SeekBar.setResponseOnTouch(this);

        custom2SeekBar2.setData(volume_sections);
        custom2SeekBar2.setResponseOnTouch(this);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        mProgressText.setText(progress + " " +
                getString(R.string.seekbar_from_touch) + "=" + fromTouch);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        mTrackingText.setText(getString(R.string.seekbar_tracking_on));
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        mTrackingText.setText(getString(R.string.seekbar_tracking_off));
    }

    @Override
    public void onTouchResponse(int volume) {
        Log.i("===", "volume: " + volume);
    }
}
