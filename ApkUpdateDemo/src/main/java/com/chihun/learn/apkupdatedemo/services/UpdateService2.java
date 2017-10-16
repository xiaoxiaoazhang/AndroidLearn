package com.chihun.learn.apkupdatedemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;

public class UpdateService2 extends Service {
    DownloadController downloadController;
    public UpdateService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = "http://gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk";
        downloadController = DownloadController.getInstance(this);
        File apkFile = downloadController.isExistFile(url);
        if (apkFile != null) {
            downloadController.installApkByFile(apkFile, this);
        } else {
            downloadController.startLauncherDownLoader(url, -1);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
