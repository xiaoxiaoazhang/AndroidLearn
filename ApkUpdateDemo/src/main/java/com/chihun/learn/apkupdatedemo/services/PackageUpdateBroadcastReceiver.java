package com.chihun.learn.apkupdatedemo.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 作者：wzd on 2017年03月01日 17:52
 * 邮箱：wangzhenduo@yunnex.com
 */

public class PackageUpdateBroadcastReceiver extends BroadcastReceiver {
    DownloadController downloadController;

    public PackageUpdateBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("Receiver", "receiver action: " + action);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {

        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            String data = intent.getDataString();
            Log.d("Receiver", "package replaced! " + data);
        }else if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            downloadController = DownloadController.getInstance(context);
            downloadController.installAPK(context);
        }

    }

}
