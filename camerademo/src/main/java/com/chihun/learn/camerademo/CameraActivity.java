package com.chihun.learn.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";

    private Camera camera;
    private Camera.Parameters parameters = null;

    private ImageView imgCover;
    private ImageView btnCamera;

    private boolean isBack;
    public static final int REQUEST_CODE = 1000;
    private Thread thread;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0x1:
                    setResult(REQUEST_CODE, (Intent) message.obj);
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_camera);
        initViews();
        initData();
        setOnListener();
        Log.d(TAG,  "onCreate:"+this.hashCode());
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
        Log.d(TAG,  "onStop:"+this.hashCode());
    }

    @Override
    protected void onDestroy() {
        if (thread != null) {
            thread.interrupt();
        }
        super.onDestroy();
        Log.d(TAG,  "onDestroy:"+this.hashCode());
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    protected void initData() {
        Intent intent = getIntent();
        isBack = intent.getBooleanExtra("EXTRA_IS_BACK", false);
        if (isBack) {
            imgCover.setImageResource(R.mipmap.shoot2);
        } else {

        }
    }

    protected void initViews() {
        imgCover = (ImageView) findViewById(R.id.iv_cover);
        btnCamera = (ImageView) findViewById(R.id.iv_camera);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(1280, 720);	//设置Surface分辨率
        surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
        surfaceView.getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数
    }

    protected void setOnListener() {
        btnCamera.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                if (camera != null) {
                    camera.takePicture(null, null, new MyPictureCallback());
                }
                break;
        }
    }

    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
//                camera.startPreview(); // 拍完照后，重新开始预览
                camera.stopPreview();
                saveToSDCard(data); // 保存图片到sd卡中
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将拍下来的照片存放在SD卡中
     * @param photoData
     * @throws IOException
     */
    public void saveToSDCard(final byte[] photoData) throws IOException {

        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";

                ImageUtils.saveImageToFileAndCompress(CameraActivity.this, photoData, fileName, 240); //将拍下来的照片存放在SD卡中
                Intent intent = new Intent();
                if (isBack) {
                    intent.putExtra("IDCARD_BACK_NAME", fileName);
                } else {
                    intent.putExtra("IDCARD_FRONT_NAME", fileName);
                }
                Message.obtain(handler, 0x1, intent).sendToTarget();
            }
        });
        thread.start();
    }


    private final class SurfaceCallback implements SurfaceHolder.Callback {

        // 拍照状态变化时调用该方法
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            if (null != camera) {
                parameters = camera.getParameters(); // 获取各项参数
                parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
                parameters.setPreviewSize(width, height); // 设置预览大小
                parameters.setPreviewFrameRate(5);	//设置每秒显示4帧
                parameters.setPictureSize(width, height); // 设置保存的图片尺寸
                parameters.setJpegQuality(100); // 设置照片质量
            }
        }

        // 开始拍照时调用该方法
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                Log.d(TAG, "numbers:"+Camera.getNumberOfCameras()); 
                if(Camera.getNumberOfCameras() == 2){ 
                    camera  =  Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                }else{
                    camera  =  Camera.open();
                }

                Log.d(TAG,  "smoothZoom:"+camera.getParameters().isSmoothZoomSupported()); 
                Log.d(TAG,  "supportzoom:"+camera.getParameters().isZoomSupported()); 
                Log.d(TAG,  "max  zoom:"+camera.getParameters().getMaxZoom());
//                camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
                camera.setDisplayOrientation(setCameraDisplayOrientation(CameraActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK));
                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
                camera.startPreview(); // 开始预览
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }

        // 停止拍照时调用该方法
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮
                if (camera != null && event.getRepeatCount() == 0) {
                    // 拍照
                    //注：调用takePicture()方法进行拍照是传入了一个PictureCallback对象——当程序获取了拍照所得的图片数据之后
                    //，PictureCallback对象将会被回调，该对象可以负责对相片进行保存或传入网络
                    camera.takePicture(null, null, new MyPictureCallback());
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    public static int setCameraDisplayOrientation(Activity activity, int cameraId) {
         android.hardware.Camera.CameraInfo info =  new android.hardware.Camera.CameraInfo();
         android.hardware.Camera.getCameraInfo(cameraId, info); 
         int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
         int degrees = 0; 
         Log.d(TAG, "rotation:"+rotation + " info.orientation:" + info.orientation + " info.facing :" + info.facing);
         switch (rotation) { 
             case Surface.ROTATION_0: degrees = 0; break; 
             case Surface.ROTATION_90: degrees = 90; break; 
             case Surface.ROTATION_180: degrees = 180; break; 
             case Surface.ROTATION_270: degrees = 270; break; 
         }
         
         int result; 
         if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
             result = (info.orientation + degrees) % 360;
             result = (360 - result) % 360; // compensate the mirror
         } else { // back-facing
             result = (info.orientation - degrees + 360) % 360;
         }
         return result;
    }
}