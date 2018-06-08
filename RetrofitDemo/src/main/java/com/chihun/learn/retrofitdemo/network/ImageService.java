package com.chihun.learn.retrofitdemo.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

public interface ImageService {

    @GET(Urls.GET_IMAGE)
    Observable<ImageResponse> getImage(@Query("col") String col, @Query("tag") String tag, @Query("sort") int sort, @Query("pn") int pn, @Query("rn") int rn, @Query("p") String p, @Query("from") int from);

    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadImage(@retrofit2.http.Url String imageUrl);
}
