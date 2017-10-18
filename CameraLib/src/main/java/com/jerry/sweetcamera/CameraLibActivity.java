package com.jerry.sweetcamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.chihun.learn.cameralib.R;
import com.jerry.sweetcamera.widget.SquareCameraContainer;


/**
 * 自定义相机的activity
 *
 * @author jerry
 * @date 2015-09-01
 */
public class CameraLibActivity extends Activity {
    public static final String TAG = "CameraActivity";

    private CameraManager mCameraManager;

    private SquareCameraContainer mCameraContainer;
    private ImageView imgCover;
    private ImageView btnCamera;

    private boolean isBack;

    private int mFinishCount = 2;   //finish计数   当动画和异步任务都结束的时候  再调用finish方法

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_camera);

        mCameraManager = CameraManager.getInstance(this);

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void initView() {
        mCameraContainer = (SquareCameraContainer) findViewById(R.id.cameraContainer);
        imgCover = (ImageView) findViewById(R.id.iv_cover);
        btnCamera = (ImageView) findViewById(R.id.iv_camera);
    }

    void initData() {
        Intent intent = getIntent();
        isBack = intent.getBooleanExtra("EXTRA_IS_BACK", false);
        if (isBack) {
            imgCover.setImageResource(R.mipmap.shoot2);
        } else {

        }
        mCameraManager.setCameraDirection(CameraManager.CameraDirection.CAMERA_BACK);
        mCameraContainer.bindActivity(this, isBack);
    }

    void initListener() {
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePhotoClicked(v);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraContainer != null) {
            mCameraContainer.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraContainer != null) {
            mCameraContainer.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraManager.unbinding();
        mCameraManager.releaseActivityCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //在创建前  释放相机
    }

    /**
     * 对一些参数重置
     */
    public void rest() {
        mFinishCount = 2;
    }

    /**
     * 照相按钮点击
     */
    public void onTakePhotoClicked(View view) {
        mCameraContainer.takePicture();
    }
}
