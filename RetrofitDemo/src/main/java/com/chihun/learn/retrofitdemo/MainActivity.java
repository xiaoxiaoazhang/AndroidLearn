package com.chihun.learn.retrofitdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.chihun.learn.retrofitdemo.network.FileUtil;
import com.chihun.learn.retrofitdemo.network.ImageResponse;
import com.chihun.learn.retrofitdemo.network.LoadListener;
import com.chihun.learn.retrofitdemo.network.NetworkRequest;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.et_col)
    EditText et_col;
    @BindView(R.id.et_tag)
    EditText et_tag;
    @BindView(R.id.et_sort)
    EditText et_sort;
    @BindView(R.id.et_pn)
    EditText et_pn;
    @BindView(R.id.et_rn)
    EditText et_rn;
    @BindView(R.id.et_p)
    EditText et_p;
    @BindView(R.id.et_form)
    EditText et_form;
    @BindView(R.id.et_url)
    EditText et_url;

    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getImageBtn:
                getImage();
                break;
            case R.id.downImageBtn:
                downImage();
                break;
            case R.id.downImageBtn2:
//                downImage2();
                break;
        }
    }

    private void getImage() {
        String col = et_col.getText().toString();
        String tag = et_tag.getText().toString();
        String sort = et_sort.getText().toString();
        String pn = et_pn.getText().toString();
        String rn = et_rn.getText().toString();
        String p = et_p.getText().toString();
        String form = et_form.getText().toString();
        NetworkRequest.getImage(col, tag, Integer.parseInt(sort), Integer.parseInt(pn), Integer.parseInt(rn), p, Integer.parseInt(form), new Subscriber<ImageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ImageResponse imageResponse) {

            }
        });
    }

    private void downImage() {
        String url = et_url.getText().toString();
        if (TextUtils.isEmpty(url)) {
            url = "http://open-api-images.ubtrobot.com/face/201803/4/f473262f-c356-427d-9f61-e38ccdbc6e1f.jpg";
        }
        NetworkRequest.downloadImage(url, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    File file = FileUtil.saveFile(response, FileUtil.getFaceDir(), "", new LoadListener() {
                        @Override
                        public void onLoading(long current, long total) {
                            Log.i("MainActivity", "total:" + total + " | current:" + current);
                        }
                    });
                    if (null != file) {
                        imageView.setImageBitmap(decodeImage(file.getAbsolutePath()));
                    } else {
                        Log.i("MainActivity", "download fail!");
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

//    private void downImage2() {
//        String url = et_url.getText().toString();
//        if (TextUtils.isEmpty(url)) {
//            url = "http://pic.sc.chinaz.com/files/pic/pic9/201508/apic14052.jpg";
//        }
//
//        try {
//            File file = null;
//            Bitmap bitmap = null;
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    File file = Glide.with(MainActivity.this)
//                            .load(url)
////                    .diskCacheStrategy(DiskCacheStrategy.NONE)
////                    .skipMemoryCache( true )
//                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
////                    .downloadOnly(width, height)
//                            .get();
//                }
//            });
//
////            bitmap = Glide.with(this)
////                    .load(url)
////                    .asBitmap().into(200, 200)
////                    .get();
//
////            byte[] bytes = Glide.with(this)
////                    .load(url)
////                    .asBitmap()
////                    .toBytes()
////                    .into(width, height)
////                    .get();
////            FileUtil.copy(target, bytes);
////            Glide.with(this)
////                    .load(url)
////                    .asBitmap()
////                    .toBytes()
////                    .into(new SimpleTarget<byte[]>(width, height) {
////                        @Override
////                        public void onResourceReady(byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
////                            // 下载成功回调函数
////                            // 数据处理方法，保存bytes到文件
////                            FileUtil.copy(file, bytes);
////                        }
////
////                        @Override
////                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
////                            // 下载失败回调
////                        }
////                    });
//            if (bitmap != null){
//                // 在这里执行图片保存方法
//                FileUtil.saveImageToGallery(this,bitmap);
//            }
//            if (file != null) {
//                imageView.setImageBitmap(decodeImage(file.getAbsolutePath()));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op);
            //rotate and scale.
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("MainActivity", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
