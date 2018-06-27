/*
 * Copyright (c) 2008-2018 UBT Corporation.  All rights reserved.  Redistribution,
 * modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *
 */

package com.chihun.learn.retrofitdemo.network;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NetworkRequest {

    public static void getImage(String col, String tag, int sort, int pn, int rn, String p, int form, Subscriber<ImageResponse> subscriber) {
        Network.imageService().getImage(col, tag, sort, pn, rn, p, form)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
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
