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
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;

    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;

    /**
     * 下载新版本
     *
     * @param context
     * @param url
     */
    public void downLoadAPK(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            String serviceString = Context.DOWNLOAD_SERVICE;
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(serviceString);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.allowScanningByMediaScanner();
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/";
            Log.i(TAG, "dir: " + dir);
            File file = new File(dir, "juyoubang.apk");
            if (file.exists()) {
                file.delete();
            }
            request.setDestinationInExternalPublicDir(dir, "juyoubang.apk");
            long refernece = downloadManager.enqueue(request);
            SharePreHelper.getInstance(context).setLong("refernece", refernece);
            //注册下载广播
            registerReceiver(receiver, new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception exception) {
            Log.w("UpdateService", "更新失败");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化下载器
     **/
    private void initDownManager(String url) {

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        receiver = new DownloadCompleteReceiver();

        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(
                Uri.parse(url));

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);

        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);

        // 显示下载界面
        down.setVisibleInDownloadsUi(false);
        down.setMimeType("application/vnd.android.package-archive");

        // 设置下载后文件存放的位置
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/";
        Log.i(TAG, "dir: " + dir);
        File file = new File(dir, "baidumusic.apk");
        if (file.exists()) {
            file.delete();
        }
//        down.setDestinationInExternalFilesDir(this,
//                Environment.DIRECTORY_DOWNLOADS, "baidumusic.apk");
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "baidumusic.apk");
        Log.i(TAG, "dir2: " + Environment.DIRECTORY_DOWNLOADS);
        // 将下载请求放入队列
        manager.enqueue(down);

        //注册下载广播
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("UpdateService", "onStartCommand!");
        // 调用下载
        initDownManager("http://gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk");
//        downLoadAPK(this, "http://gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        // 注销下载广播
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        super.onDestroy();
    }

    public class UpdataBroadcastReceiver extends BroadcastReceiver {
        /**
         * 通过downLoadId查询下载的apk，解决6.0以后安装的问题 * @param context * @return
         */
        public File queryDownloadedApk(Context context) {
            File targetApkFile = null;
            DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = SharePreHelper.getInstance(context).getLong("refernece", -1);
            if (downloadId != -1) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                Cursor cur = downloader.query(query);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if (!TextUtils.isEmpty(uriString)) {
                            targetApkFile = new File(Uri.parse(uriString).getPath());
                        }
                    }
                    cur.close();
                }
            }
            return targetApkFile;
        }

        @SuppressLint("NewApi")
        public void onReceive(Context context, Intent intent) {
            long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long refernece = SharePreHelper.getInstance(UpdateService.this).getLong("refernece", 0);
            if (refernece != myDwonloadID) {
                return;
            }
            DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);
            installAPK(context, downloadFileUri);
        }

        private void installAPK(Context context, Uri apk) {
            if (Build.VERSION.SDK_INT < 23) {
                Intent intents = new Intent();
                intents.setAction("android.intent.action.VIEW");
                intents.addCategory("android.intent.category.DEFAULT");
                intents.setType("application/vnd.android.package-archive");
                intents.setData(apk);
                intents.setDataAndType(apk, "application/vnd.android.package-archive");
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intents);
            } else {
                File file = queryDownloadedApk(context);
                if (file.exists()) {
                    openFile(file, context);
                }
            }
        }

        private void openFile(File file, Context context) {
            Intent intent = new Intent();
            intent.addFlags(268435456);
            intent.setAction("android.intent.action.VIEW");
            String type = getMIMEType(file);
            intent.setDataAndType(Uri.fromFile(file), type);
            try {
                context.startActivity(intent);
            } catch (Exception var5) {
                var5.printStackTrace();
                Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
            }
        }

        private String getMIMEType(File var0) {
            String var1 = "";
            String var2 = var0.getName();
            String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
            var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
            return var1;
        }
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                //自动安装apk
                installAPK(manager.getUriForDownloadedFile(downId));

                //停止服务并关闭广播
                UpdateService.this.stopSelf();
            }
        }

        /**
         * 安装apk文件
         */
        private void installAPK(Uri apk) {

            // 通过Intent安装APK文件
            Intent intents = new Intent();

            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            android.os.Process.killProcess(android.os.Process.myPid());
            // 如果不加上这句的话在apk安装完成之后点击单开会崩溃

            startActivity(intents);
        }

        private void installAPK(File apkFile) {
            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= 24) {
                //在AndroidManifest中的android:authorities值
                Uri apkUri = FileProvider.getUriForFile(UpdateService.this, "com.chihun.learn.apkupdatedemo.fileprovider", apkFile);
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                startActivity(install);
            } else {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(install);
            }
        }

    }
}
