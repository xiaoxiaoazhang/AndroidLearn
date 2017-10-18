package com.jerry.sweetcamera.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.chihun.learn.cameralib.R;
import com.jerry.sweetcamera.CameraLibActivity;
import com.jerry.sweetcamera.CameraManager;
import com.jerry.sweetcamera.IActivityLifiCycle;
import com.jerry.sweetcamera.ICameraOperation;
import com.jerry.sweetcamera.SensorControler;
import com.jerry.sweetcamera.SweetApplication;
import com.jerry.sweetcamera.util.ImageUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 正方形的CamerContainer
 *
 * @author jerry
 * @date 2015-09-16
 */
public class SquareCameraContainer extends FrameLayout implements ICameraOperation, IActivityLifiCycle {
    public static final String TAG = "SquareCameraContainer";

    private Context mContext;

    /**
     * 相机绑定的SurfaceView
     */
    private CameraView mCameraView;

    /**
     * 触摸屏幕时显示的聚焦图案
     */
    private FocusImageView mFocusImageView;
    /**
     * 缩放控件
     */
    private SeekBar mZoomSeekBar;

    private CameraLibActivity mActivity;

//    private SoundPool mSoundPool;

    private boolean mFocusSoundPrepared;

    private int mFocusSoundId;

    private SensorControler mSensorControler;

    public static final int RESETMASK_DELY = 1000; //一段时间后遮罩层一定要隐藏

    public SquareCameraContainer(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SquareCameraContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    void init() {
        inflate(mContext, R.layout.custom_camera_container, this);

        mCameraView = (CameraView) findViewById(R.id.cameraView);
        mFocusImageView = (FocusImageView) findViewById(R.id.focusImageView);
        mZoomSeekBar = (SeekBar) findViewById(R.id.zoomSeekBar);

        mSensorControler = SensorControler.getInstance();

        mSensorControler.setCameraFocusListener(new SensorControler.CameraFocusListener() {
            @Override
            public void onFocus() {
                int screenWidth = SweetApplication.mScreenWidth;
                int screenHeight = SweetApplication.mScreenHeight;
                Point point = new Point(screenWidth / 2, screenHeight / 2);

                onCameraFocus(point);
            }
        });
        mCameraView.setOnCameraPrepareListener(new CameraView.OnCameraPrepareListener() {
            @Override
            public void onPrepare(CameraManager.CameraDirection cameraDirection) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, RESETMASK_DELY);
                //在这里相机已经准备好 可以获取maxZoom
                mZoomSeekBar.setMax(mCameraView.getMaxZoom());

                if (cameraDirection == CameraManager.CameraDirection.CAMERA_BACK) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int screenWidth = SweetApplication.mScreenWidth;
                            int screenHeight = SweetApplication.mScreenHeight;
                            Point point = new Point(screenWidth / 2, screenHeight / 2);
                            onCameraFocus(point);
                        }
                    }, 800);
                }
            }
        });
        mCameraView.setSwitchCameraCallBack(new CameraView.SwitchCameraCallBack() {
            @Override
            public void switchCamera(boolean isSwitchFromFront) {
                if (isSwitchFromFront) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int screenWidth = SweetApplication.mScreenWidth;
                            int screenHeight = SweetApplication.mScreenHeight;
                            Point point = new Point(screenWidth / 2, screenHeight / 2);
                            onCameraFocus(point);
                        }
                    }, 300);
                }
            }
        });
        mCameraView.setPictureCallback(pictureCallback);
        mZoomSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

//        //音效初始化
//        mSoundPool = getSoundPool();
    }

//    private SoundPool getSoundPool(){
//        if(mSoundPool == null) {
//            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//            mFocusSoundId = mSoundPool.load(mContext,R.raw.camera_focus,1);
//            mFocusSoundPrepared = false;
//            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                @Override
//                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                    mFocusSoundPrepared = true;
//                }
//            });
//        }
//        return mSoundPool;
//    }

    public void bindActivity(CameraLibActivity activity, boolean isBack) {
        this.mActivity = activity;
        this.isBack = isBack;
        if (mCameraView != null) {
            mCameraView.bindActivity(activity);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = SweetApplication.mScreenWidth;
        int height = SweetApplication.mScreenHeight;
        //保证View是正方形
        setMeasuredDimension(width, height);
    }

    /**
     * 记录是拖拉照片模式还是放大缩小照片模式
     */

    private static final int MODE_INIT = 0;
    /**
     * 放大缩小照片模式
     */
    private static final int MODE_ZOOM = 1;
    private int mode = MODE_INIT;// 初始状态

    private double startDis;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 手指压下屏幕
            case MotionEvent.ACTION_DOWN:
                mode = MODE_INIT;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //如果mZoomSeekBar为null 表示该设备不支持缩放 直接跳过设置mode Move指令也无法执行
                if (mZoomSeekBar == null) return true;
                //移除token对象为mZoomSeekBar的延时任务
                mHandler.removeCallbacksAndMessages(mZoomSeekBar);
//                mZoomSeekBar.setVisibility(View.VISIBLE);
                mZoomSeekBar.setVisibility(View.GONE);

                mode = MODE_ZOOM;
                /** 计算两个手指间的距离 */
                startDis = spacing(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_ZOOM) {
                    //只有同时触屏两个点的时候才执行
                    if (event.getPointerCount() < 2) return true;
                    double endDis = spacing(event);// 结束距离
                    //每变化10f zoom变1
                    int scale = (int) ((endDis - startDis) / 10f);
                    if (scale >= 1 || scale <= -1) {
                        int zoom = mCameraView.getZoom() + scale;
                        //zoom不能超出范围
                        if (zoom > mCameraView.getMaxZoom()) zoom = mCameraView.getMaxZoom();
                        if (zoom < 0) zoom = 0;
                        mCameraView.setZoom(zoom);
                        mZoomSeekBar.setProgress(zoom);
                        //将最后一次的距离设为当前距离
                        startDis = endDis;
                    }
                }
                break;
            // 手指离开屏幕
            case MotionEvent.ACTION_UP:
                if (mode != MODE_ZOOM) {
                    //设置聚焦
                    Point point = new Point((int) event.getX(), (int) event.getY());
                    onCameraFocus(point);
                } else {
                    //ZOOM模式下 在结束两秒后隐藏seekbar 设置token为mZoomSeekBar用以在连续点击时移除前一个定时任务
                    mHandler.postAtTime(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mZoomSeekBar.setVisibility(View.GONE);
                        }
                    }, mZoomSeekBar, SystemClock.uptimeMillis() + 2000);
                }
                break;
        }
        return true;
    }

    /**
     * 两点的距离
     */
    private double spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
            return Math.sqrt(x * x + y * y);

    }

    /**
     * 相机对焦  默认不需要延时
     *
     * @param point
     */
    private void onCameraFocus(final Point point) {
        onCameraFocus(point, false);
    }

    /**
     * 相机对焦
     *
     * @param point
     * @param needDelay 是否需要延时
     */
    public void onCameraFocus(final Point point, boolean needDelay) {
        long delayDuration = needDelay ? 300 : 0;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mSensorControler.isFocusLocked()) {
                    if (mCameraView.onFocus(point, autoFocusCallback)) {
                        mSensorControler.lockFocus();
                        mFocusImageView.startFocus(point);

                        //播放对焦音效
                        if(mFocusSoundPrepared) {
//                            mSoundPool.play(mFocusSoundId, 1.0f, 0.5f, 1, 0, 1.0f);
                        }
                    }
                }
            }
        }, delayDuration);
    }


    @Override
    public void switchCamera() {
        mCameraView.switchCamera();
    }

    @Override
    public void switchFlashMode() {
        mCameraView.switchFlashMode();
    }

    @Override
    public boolean takePicture() {
        setMaskOn();
        boolean flag = mCameraView.takePicture();
        if (!flag) {
            mSensorControler.unlockFocus();
        }
        setMaskOff();
        return flag;
    }

    @Override
    public int getMaxZoom() {
        return mCameraView.getMaxZoom();
    }

    @Override
    public void setZoom(int zoom) {
        mCameraView.setZoom(zoom);
    }

    @Override
    public int getZoom() {
        return mCameraView.getZoom();
    }

    @Override
    public void releaseCamera() {
        if (mCameraView != null) {
            mCameraView.releaseCamera();
        }
    }


    public static final int REQUEST_CODE = 1000;
    private Thread thread;
    private boolean isBack;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0x1:
                    mActivity.setResult(REQUEST_CODE, (Intent) message.obj);
                    mActivity.finish();
                    break;
            }
        }
    };

    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //聚焦之后根据结果修改图片
            if (success) {
                mFocusImageView.onFocusSuccess();
            } else {
                //聚焦失败显示的图片，由于未找到合适的资源，这里仍显示同一张图片
                mFocusImageView.onFocusFailed();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //一秒之后才能再次对焦
                    mSensorControler.unlockFocus();
                }
            }, 1000);
        }
    };

    private final Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            mActivity.rest();

            Log.i(TAG, "pictureCallback");
            try {
//                camera.startPreview(); // 拍完照后，重新开始预览
                camera.stopPreview();
                saveToSDCard(data); // 保存图片到sd卡中
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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

                ImageUtils.saveImageToFileAndCompress(mActivity, photoData, fileName, 240); //将拍下来的照片存放在SD卡中
                Intent intent = new Intent();
                if (isBack) {
                    intent.putExtra("IDCARD_BACK_NAME", fileName);
                } else {
                    intent.putExtra("IDCARD_FRONT_NAME", fileName);
                }
                Message.obtain(mHandler, 0x1, intent).sendToTarget();
            }
        });
        thread.start();
    }

    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            mCameraView.setZoom(progress);
            mHandler.removeCallbacksAndMessages(mZoomSeekBar);
            //ZOOM模式下 在结束两秒后隐藏seekbar 设置token为mZoomSeekBar用以在连续点击时移除前一个定时任务
            mHandler.postAtTime(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mZoomSeekBar.setVisibility(View.GONE);
                }
            }, mZoomSeekBar, SystemClock.uptimeMillis() + 2000);
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onStart() {
        mSensorControler.onStart();

        if (mCameraView != null) {
            mCameraView.onStart();
        }

//        mSoundPool = getSoundPool();
    }

    @Override
    public void onStop() {
        mSensorControler.onStop();

        if (mCameraView != null) {
            mCameraView.onStop();
        }

//        mSoundPool.release();
//        mSoundPool = null;
    }

    public void setMaskOn() {

    }

    public void setMaskOff() {

    }
}
