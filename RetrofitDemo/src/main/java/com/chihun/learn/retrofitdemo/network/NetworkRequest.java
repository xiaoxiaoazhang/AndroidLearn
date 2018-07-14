/*
 * Copyright (c) 2008-2018 UBT Corporation.  All rights reserved.  Redistribution,
 * modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *
 */

package com.chihun.learn.retrofitdemo.network;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NetworkRequest {
    private static final String TAG = "NetworkRequest";

    public static void getImage(String col, String tag, int sort, int pn, int rn, String p, int form, Subscriber<ImageResponse> subscriber) {
        Network.imageService().getImage(col, tag, sort, pn, rn, p, form)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static void getImageAsync(String col, String tag, int sort, int pn, int rn, String p, int form, retrofit2.Callback<ImageResponse> callback) {
        Call<ImageResponse> call = Network.imageService().getImageAsync(col, tag, sort, pn, rn, p, form);
        call.enqueue(callback);
    }

    public static ImageResponse getImageSync(String col, String tag, int sort, int pn, int rn, String p, int form) {
        Call<ImageResponse> call = Network.imageService().getImageAsync(col, tag, sort, pn, rn, p, form);
        ImageResponse imageResponse = null;
        try {
            Response<ImageResponse> response = call.execute();
            int code = response.code();
            if (code < 300) {
                imageResponse = response.body();
            } else {
                ResponseBody responseBody = response.errorBody();
                Log.d(TAG, "fail code: " + code + " msg: " + responseBody.string());
            }
            Log.d(TAG, "code: " + response.code() + " msg: " + response.message());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "msg: " + e.getMessage());

            if(e instanceof SocketTimeoutException){//判断超时异常

            }
            if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect to 10.7.5.144

            }
        }
        return imageResponse;
    }

    public static void downloadImage(String imageUrl, Callback<ResponseBody> callback) {
        Network.imageService().downloadImage(imageUrl).enqueue(callback);
    }

//    public static void detect(String accessToken, byte[] image, Callback<DetectResponse> callback) {
//        Map<String, RequestBody> imageMap = new HashMap<>(1);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
//        imageMap.put("image\";filename=\"image_"+ System.currentTimeMillis() +".jpg\"", requestBody);
//        Network.imageService().detect(imageMap, accessToken).enqueue(callback);
//    }
//
//    public static void detect(String accessToken, byte[] image, Callback<DetectResponse> callback) {
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
//        Network.recognitionServer().detect(requestBody, accessToken).enqueue(callback);
//    }
}
