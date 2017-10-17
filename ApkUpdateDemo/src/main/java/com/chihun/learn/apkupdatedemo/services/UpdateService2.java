package com.chihun.learn.apkupdatedemo.services;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class UpdateService2 extends Service {
    private static final String TAG = "UpdateService2";
//    DownloadController downloadController;
//    public UpdateService2() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String url = "http://gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk";
//        downloadController = DownloadController.getInstance(this);
//        File apkFile = downloadController.isExistFile(url);
//        if (apkFile != null) {
//            downloadController.installApkByFile(apkFile, this);
//        } else {
//            downloadController.startLauncherDownLoader(url, -1);
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }

    /** 安卓系统下载类 **/
    private DownloadManager manager;

    /** 接收下载完的广播 **/
    private UpdataBroadcastReceiver receiver;

    private String filePath;
    private String apkVersion;

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new UpdataBroadcastReceiver();
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private void initDownManager(String url) {
        Uri parse = Uri.parse(url);
        String apkName = parse.getLastPathSegment();
        // 设置文件存放目录
        try {
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydemo/";
            Log.i(TAG, "dir: " + dirPath);
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dirPath, apkName);
            Log.i(TAG, "file: " + file.getPath());
            if (file != null && file.exists()) {
                installAPK(this, file);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request down = new DownloadManager.Request(parse);
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 下载时，通知栏显示途中
        down.setTitle("mydemo");
        down.setDescription("mydemo " + apkVersion);
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        down.setMimeType("application/vnd.android.package-archive");

        down.setDestinationInExternalPublicDir("mydemo", apkName);
        // 将下载请求放入队列
        long refernece = manager.enqueue(down);
        SharePreHelper.getInstance(this).setLong("refernece", refernece);
        SharePreHelper.getInstance(this).setString("fileName", apkName);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 调用下载
        if (intent != null) {
            filePath = "http://gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk";
            apkVersion = "1.2.3";
            Log.d(TAG, "filePath:" + filePath);
            initDownManager(filePath);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 接受下载完成后的intent
    public class UpdataBroadcastReceiver extends BroadcastReceiver {

        @SuppressLint("NewApi")
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                long refernece = SharePreHelper.getInstance(UpdateService2.this).getLong("refernece", 0);
                Log.i(TAG, "myDwonloadID: " + myDwonloadID + " refernece: " + refernece);
                if (refernece != myDwonloadID) {
                    return;
                }
                DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);
                try {
                    installAPK(context, downloadFileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过downLoadId查询下载的apk，解决6.0以后安装的问题 * @param context * @return
     */
    public static File queryDownloadedApk(Context context) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = SharePreHelper.getInstance(context).getLong("refernece", -1);
        Log.i(TAG, "downloadId:" + downloadId);
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Log.i(TAG, "uriString:" + uriString);
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }

    private static void installAPK(Context context, Uri uri) {
        File apkFile = queryDownloadedApk(context);
        Log.i(TAG, "installAPK: sdk:" + Build.VERSION.SDK_INT + " apkfile:" + apkFile.getPath() + " uri:" + uri.toString() + " " + uri.getPath());
        if (apkFile != null && apkFile.exists()) {
            if (Build.VERSION.SDK_INT <= 23) {
                Uri downloadFileUri = Uri.fromFile(apkFile);
                Intent intents = new Intent();
                intents.setAction("android.intent.action.VIEW");
                intents.addCategory("android.intent.category.DEFAULT");
                intents.setType("application/vnd.android.package-archive");
                intents.setData(downloadFileUri);
                intents.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intents);
            } else {
                Log.i(TAG, "apkfile:" + apkFile.getPath());
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri apkUri = FileProvider.getUriForFile(context, "com.chihun.learn.apkupdatedemo.fileprovider", apkFile);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            }
        }
    }

    private static void installAPK(Context context, File apkFile) {
        Uri uri = FileProvider.getUriForFile(context, "com.chihun.learn.apkupdatedemo.fileprovider", apkFile);
        Log.i(TAG, "installAPK2: sdk:" + Build.VERSION.SDK_INT + " uri:" + uri.getPath() + " " + uri.toString());
        if (Build.VERSION.SDK_INT <= 23) {
            Uri downloadFileUri = Uri.fromFile(apkFile);
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(downloadFileUri);
            intents.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);
        } else {
            Log.i(TAG, "apkfile:" + apkFile.getPath());
            Intent install = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(context, "com.chihun.learn.apkupdatedemo.fileprovider", apkFile);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(install);
        }
    }
}
