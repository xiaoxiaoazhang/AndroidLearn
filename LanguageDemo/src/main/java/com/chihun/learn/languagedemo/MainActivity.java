package com.chihun.learn.languagedemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private int language_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice();
            }
        });
    }

    private void choice() {
        final String[] language = getResources().getStringArray(R.array.language_choice);
        final SharedPreferences sharedPreferences = getSharedPreferences("language_choice", MODE_PRIVATE);
        final int id = sharedPreferences.getInt("id", 0);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.language_chioce_title);
        builder.setSingleChoiceItems(language, id, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // default
                        language_id = 0;
                        break;
                    case 1:
                        language_id = 1;
                        // english
                        break;
                    case 2:
                        language_id = 2;
                        // spanish
                        break;
                    case 3:
                        // french
                        language_id = 3;
                        break;
                    default:
                        //default
                        language_id = 0;
                        break;
                }
                sharedPreferences.edit().putInt("id", language_id).commit();
            }
        });
        builder.setPositiveButton(R.string.language_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShowActivity.class);
                startActivity(intent);
                setLanguage();// 必须加上，否者不会更新语言
            }});

        builder.show();
    }

    private void setLanguage() {
        Resources resources = getResources();
        final SharedPreferences sharedPreferences=getSharedPreferences("language_choice", MODE_PRIVATE);
        language_id = sharedPreferences.getInt("id", 0);
        Log.e(TAG,"id == " + language_id);
        // 获取应用内语言
        final Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (language_id){
            case 0:
                configuration.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                configuration.locale = Locale.ENGLISH;
                break;
            case 2:
                configuration.locale = new Locale("vi");
                break;
            case 3:
                configuration.locale = Locale.FRENCH;
                break;
            default:
                configuration.locale = Locale.getDefault();
                break;
        }
        Log.e(TAG,"configuration == " + configuration.locale);
        getResources().updateConfiguration(configuration,displayMetrics);
    }
}
