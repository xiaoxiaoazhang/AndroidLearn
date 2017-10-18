package com.chihun.learn.camerademo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

/**
 * File description
 * <p>
 * Author: wzd
 * Date: 2017/9/2.
 */

public class IdCardActivity extends Activity implements View.OnClickListener {

    private static String TAG = "IdCardActivity";

    private ImageView idcardFront;
    private ImageView idcardFrontCamera;
    private ImageView idcardBack;
    private ImageView idcardBackCamera;
    private boolean isBackClick;
    private String frontPhotoName;
    private String backPhotoName;

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        initViews();
        initData();
        setOnListener();
    }
    protected void initViews() {
        idcardFront = (ImageView) findViewById(R.id.iv_idcard_front);
        idcardFrontCamera = (ImageView) findViewById(R.id.iv_idcard_front_camera);
        idcardBack = (ImageView) findViewById(R.id.iv_idcard_back);
        idcardBackCamera = (ImageView) findViewById(R.id.iv_idcard_back_camera);
    }

    protected void initData() {
    }

    protected void setOnListener() {
        idcardBackCamera.setOnClickListener(this);
        idcardFrontCamera.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_idcard_front_camera:
                isBackClick = false;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermission();
                    return;
                }
                if (Build.VERSION_CODES.M < Build.VERSION.SDK_INT) {
//                    startActivityForResult(new Intent(this, Camera2Activity.class), CameraActivity.REQUEST_CODE);
                } else {
                    startActivityForResult(new Intent(this, CameraActivity.class), CameraActivity.REQUEST_CODE);
                }
                break;
            case R.id.iv_idcard_back_camera:
                isBackClick = true;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermission();
                    return;
                }
                if (Build.VERSION_CODES.M < Build.VERSION.SDK_INT) {
//                    Intent intent = new Intent(this, Camera2Activity.class);
//                    intent.putExtra("EXTRA_IS_BACK", true);
//                    startActivityForResult(intent, CameraActivity.REQUEST_CODE);
                } else {
                    Intent intent = new Intent(this, CameraActivity.class);
                    intent.putExtra("EXTRA_IS_BACK", true);
                    startActivityForResult(intent, CameraActivity.REQUEST_CODE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CameraActivity.REQUEST_CODE) {
            if (intent != null) {
                if (isBackClick) {
                    backPhotoName = intent.getStringExtra("IDCARD_BACK_NAME");
                    idcardBack.setImageBitmap(ImageUtils.readImageFromLocal2(this, backPhotoName));
                } else {
                    frontPhotoName = intent.getStringExtra("IDCARD_FRONT_NAME");
                    idcardFront.setImageBitmap(ImageUtils.readImageFromLocal2(this, frontPhotoName));
                }
            }
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setMessage("请求权限")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(IdCardActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .create();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setMessage("请求权限")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .create();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
