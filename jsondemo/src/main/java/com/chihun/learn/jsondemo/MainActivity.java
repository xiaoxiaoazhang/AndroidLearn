package com.chihun.learn.jsondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.chihun.learn.jsondemo.pojo.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    String json = "{ \"directive\": { \"header\": { \"namespace\": \"DeviceControl\", \"name\": \"ExpectReportState\", \"messageId\": \"573fe46c-572e-417b-9e2f-42b8fff638c4\" }, \"payload\": {  } } }";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        example();
    }

    private void example() {
        Gson gson = (new GsonBuilder()).registerTypeAdapterFactory(MyTypeAdapterFactory.create()).create();
        Type type = TypeToken.get(Response.class).getType();
        Response response = (Response)gson.fromJson(json, type);
        Log.d(MainActivity.class.getSimpleName(), "response: " + response.toString());
    }
}
