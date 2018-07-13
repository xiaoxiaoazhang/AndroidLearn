/*
 * Copyright (c) 2008-2018 UBT Corporation.  All rights reserved.  Redistribution,
 * modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *
 */

package com.chihun.learn.retrofitdemo.network;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class FileUtil {

    private static final String APP_ROOT_DIR = "recognitionDemo";
    private static final String FACE_DATA_DIR = "face";

    public static String getFaceDir(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_ROOT_DIR + File.separator + FACE_DATA_DIR;
    }

    public static boolean createFolder(String folderPath) {
        if (!TextUtils.isEmpty(folderPath)) {
            File folder = new File(folderPath);
            return createFolder(folder);
        } else {
            return false;
        }
    }

    public static boolean createFolder(File targetFolder) {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory()) {
                return true;
            }

            targetFolder.delete();
        }

        return targetFolder.mkdirs();
    }

    public static boolean createFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return createFile(file);
        } else {
            return false;
        }
    }

    public static boolean createFile(File targetFile) {
        if (targetFile.exists()) {
            if (targetFile.isFile()) {
                return true;
            }

            delFileOrFolder(targetFile);
        }

        try {
            return targetFile.createNewFile();
        } catch (IOException var2) {
            return false;
        }
    }

    public static boolean createNewFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return createNewFile(file);
        } else {
            return false;
        }
    }

    public static boolean createNewFile(File targetFile) {
        if (targetFile.exists()) {
            delFileOrFolder(targetFile);
        }

        try {
            return targetFile.createNewFile();
        } catch (IOException var2) {
            return false;
        }
    }

    public static boolean delFileOrFolder(String path) {
        return TextUtils.isEmpty(path) ? false : delFileOrFolder(new File(path));
    }

    public static boolean delFileOrFolder(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    File[] var2 = files;
                    int var3 = files.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        File sonFile = var2[var4];
                        delFileOrFolder(sonFile);
                    }
                }

                file.delete();
            }
        }

        return true;
    }

    public static File saveFile(Response<ResponseBody> response, String folder, String fileName, LoadListener loadListener) throws Throwable {
        if (TextUtils.isEmpty(folder)) {
            folder = getFaceDir();
        }

        if (TextUtils.isEmpty(fileName)) {
            okhttp3.Response rawResponse = response.raw();
            if (null == rawResponse) {
                fileName = "unknownfile_" + System.currentTimeMillis();
            } else {
                String url = rawResponse.request().url().toString();
                fileName = getNetFileName(rawResponse, url);
            }
        }

        File dir = new File(folder);
        createFolder(dir);
        File file = new File(dir, fileName);
        delFileOrFolder(file);

        InputStream bodyStream = null;
        byte[] buffer = new byte[8192];
        FileOutputStream fileOutputStream = null;
        try {
            ResponseBody body = response.body();
            if (body == null) return null;

            bodyStream = body.byteStream();
            long totalLength = body.contentLength();
            long currentLength = 0;
            int length;
            fileOutputStream = new FileOutputStream(file);
            while ((length = bodyStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
                currentLength += length;
                if (null != loadListener) {
                    loadListener.onLoading(currentLength, totalLength);
                }
            }
            fileOutputStream.flush();
            return file;
        } finally {
            closeQuietly(bodyStream);
            closeQuietly(fileOutputStream);
        }
    }

    public static File saveFile(Response response, String folder, String fileName) throws Throwable {
        return saveFile(response, folder, fileName, null);
    }

    /** 根据响应头或者url获取文件名 */
    public static String getNetFileName(okhttp3.Response response, String url) {
        String fileName = getHeaderFileName(response);
        if (TextUtils.isEmpty(fileName)) fileName = getUrlFileName(url);
        if (TextUtils.isEmpty(fileName)) fileName = "unknownfile_" + System.currentTimeMillis();
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.w(e);
        }
        return fileName;
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(okhttp3.Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replaceAll("\"", "");
            String split = "filename=";
            int indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
            }
            split = "filename*=";
            indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                String fileName = dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
                String encode = "UTF-8''";
                if (fileName.startsWith(encode)) {
                    fileName = fileName.substring(encode.length(), fileName.length());
                }
                return fileName;
            }
        }
        return null;
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    private static String getUrlFileName(String url) {
        String filename = null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex);
                    return filename;
                }
            }
        }
        if (strings.length > 0) {
            filename = strings[strings.length - 1];
        }
        return filename;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Exception e) {
            LogUtil.w(e);
        }
    }

    public static void flushQuietly(Flushable flushable) {
        if (flushable == null) return;
        try {
            flushable.flush();
        } catch (Exception e) {
            LogUtil.w(e);
        }
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件
     * @param filename 文件名
     * @param bytes 数据
     */
    public static void copy(String filename, byte[] bytes) {
        try {
            //如果手机已插入sd卡,且app具有读写sd卡的权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream output = null;
                output = new FileOutputStream(filename);
                output.write(bytes);
                Log.i(TAG, "copy: success，" + filename);
                output.close();
            } else {
                Log.i(TAG, "copy:fail, " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();//注意小米手机必须这样获得public绝对路径
        String fileName = "ningjing";
        File appDir = new File(file ,fileName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        fileName = System.currentTimeMillis() + ".jpg";
        File currentFile = new File(appDir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    currentFile.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(currentFile.getPath()))));
    }


    /**
     * 拍照路径
     */

    private static String FILE_NAME = "userIcon.jpg";
    public static String PATH_PHOTOGRAPH = "/LXT/";

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getAvailableCacheDir(Context context) {
        if (isExternalStorageWritable()) {
            return context.getExternalCacheDir();
        } else {
            return context.getCacheDir();
        }
    }

    public static String getAvatarCropPath(Context context) {
        return new File(getAvailableCacheDir(context), FILE_NAME).getAbsolutePath();
    }

    public static String getPublishPath(Context context, String fileName) {
        return new File(getAvailableCacheDir(context), fileName).getAbsolutePath();
    }


    /**
     * 保存图片
     *
     * @param bitmap
     * @param filePath
     */
    public static void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream bos = null;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            bos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void deleteDir(File directory) {
        if (directory != null){
            if (directory.isFile()) {
                directory.delete();
                return;
            }

            if (directory.isDirectory()) {
                File[] childFiles = directory.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    directory.delete();
                    return;
                }

                for (int i = 0; i < childFiles.length; i++) {
                    deleteDir(childFiles[i]);
                }
                directory.delete();
            }
        }
    }



    public static File getDCIMFile(String filePath, String imageName) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File dirs = new File(Environment.getExternalStorageDirectory(),
                    "DCIM"+filePath);
            if (!dirs.exists())
                dirs.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),
                    "DCIM"+filePath+imageName);
            if (!file.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    file.createNewFile();
                } catch (Exception e) {
                }
            }
            return file;
        } else {
            return null;
        }

    }



    public static File saveBitmap2(Bitmap bitmap, String fileName, File baseFile) {
        FileOutputStream bos = null;
        File imgFile = new File(baseFile, "/" + fileName);
        try {
            bos = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFile;
    }

    public static File getBaseFile(String filePath) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File f = new File(Environment.getExternalStorageDirectory(),
                    filePath);
            if (!f.exists())
                f.mkdirs();
            return f;
        } else {
            return null;
        }
    }

    public static String getFileName() {
        String fileName = FILE_NAME ;
        return fileName;
    }

    /**
     * 由指定的路径和文件名创建文件
     */
    public static File createFile(String path, String name) throws IOException {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
