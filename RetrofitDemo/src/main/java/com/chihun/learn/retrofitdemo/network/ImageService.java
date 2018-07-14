package com.chihun.learn.retrofitdemo.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

public interface ImageService {
    @Headers("Cache-Control:public,max-age=120") //通过添加 @Headers("Cache-Control: max-age=120") 进行设置。添加了Cache-Control 的请求，retrofit 会默认缓存该请求的返回数据一般来说，这种方法是针对特定的API进行设置。这样我们就通过@Headers快速的为该api添加了缓存控制。120s内，缓存都是生效状态，即无论有网无网都读取缓存。
    @GET(Urls.GET_IMAGE)
    Observable<ImageResponse> getImage(@Query("col") String col, @Query("tag") String tag, @Query("sort") int sort, @Query("pn") int pn, @Query("rn") int rn, @Query("p") String p, @Query("from") int from);

    @GET(Urls.GET_IMAGE)
    Call<ImageResponse> getImageAsync(@Query("col") String col, @Query("tag") String tag, @Query("sort") int sort, @Query("pn") int pn, @Query("rn") int rn, @Query("p") String p, @Query("from") int from);

    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadImage(@retrofit2.http.Url String imageUrl);

//    @Headers("Connection:close")
//    @Multipart
//    @POST(Url.FACE_DETECT)
//    Call<DetectResponse> detect(@PartMap Map<String, RequestBody> imageMap, @Query("accessToken") String accessToken);
//
//    @Headers("Connection:close")
//    @Multipart
//    @POST(Url.FACE_DETECT)
//    Call<DetectResponse> detect(@Part("image\"; filename=\"image.jpg\"") RequestBody image, @Query("accessToken") String accessToken);
}
