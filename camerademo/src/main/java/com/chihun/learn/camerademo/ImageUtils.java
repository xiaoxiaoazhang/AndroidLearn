package com.chihun.learn.camerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File Description
 * <p>
 * 作者：wzd on 2017年08月30日 17:30
 * 邮箱：wangzhenduo@yunnex.com
 */

public class ImageUtils {

    private final static String TAG = "ImageUtils";

    public static File readImageFromLocal(Context context, String fileName) {
        //本地文件
        String dir = getDir(context);
        File file = new File(dir, fileName);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    public static Bitmap readImageFromLocal2(Context context, String fileName) {
        File file = readImageFromLocal(context, fileName);
        if (file == null) {
            return null;
        }
        String filePath = file.getAbsolutePath();
        return BitmapFactory.decodeFile(filePath);
    }

    public static void saveImageToFile(Context context, Bitmap mBitmap, String fileName) {
        try {
            String dir = getDir(context);
            File file = new File(dir, fileName);
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveImageToFile(Context context, byte[] data, String fileName) {
        try {
            String dirName = getDir(context);
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dirName, fileName);
            FileOutputStream out = new FileOutputStream(file); // 文件输出流
            out.write(data); // 写入sd卡中
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveImageToFileAndCompress(Context context, byte[] data, String fileName, float size) {
        try {
            Log.i(TAG, "origin size: " + data.length / 1024 + "k");
            // 尺寸压缩
            byte[] compress1 = getSmallBitmap(data, 640, 360);
            Log.i(TAG, "compress1 size: " + compress1.length / 1024 + "k");
            // 质量压缩
            byte[] compress2 = compressBitmap(data, size);
            Log.i(TAG, "compress2 size: " + compress2.length / 1024 + "k");
            // 写入sd卡中
            String dirName = getDir(context);
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dirName, fileName);
            FileOutputStream out = new FileOutputStream(file); // 文件输出流
            out.write(compress2);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSdcardEnable() {
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static String getDir(Context context) {
        if (isSdcardEnable()) {
            String dir = context.getExternalFilesDir(null).getAbsolutePath();
            return dir + "/image/";
//            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/files/image/";
        } else {
            return context.getFilesDir() + "/image/";
        }
    }
    /**
     * 压缩图片，处理某些手机拍照角度旋转的问题
    */
    public static String compressImage(Context context, String filePath, String fileName, int q, int reqWidth, int reqHeight) throws FileNotFoundException {

        Bitmap bm = getSmallBitmap(filePath, reqWidth, reqHeight);

        int degree = readPictureDegree(filePath);

        if(degree!=0){//旋转照片角度
            bm=rotateBitmap(bm,degree);
        }

        String imageDir = getDir(context);

        File outputFile = new File(imageDir,fileName);

        FileOutputStream out = new FileOutputStream(outputFile);

        bm.compress(Bitmap.CompressFormat.JPEG, q, out);

        return outputFile.getPath();
    }

    /**
     * 获取图片长宽
     * @param filePath
     * @return 宽、高
     */
    public static int[] getImageSize(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        return new int[]{width, height};
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static byte[] getSmallBitmap(byte[] data, int reqWidth, int reqHeight) {
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, reqWidth, reqHeight);
        canvas.drawBitmap(bmp, null, rect, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 判断照片角度
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    /**
     * 旋转图片
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public static byte[] compressBitmapBySize(byte[] data, int ratio) {
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
    public static byte[] compressBitmap(byte[] data, float size) { /* 取得相片 */
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap == null || getSizeOfBitmap(bitmap) <= size) {
            return data;// 如果图片本身的大小已经小于指定大小，就没必要进行压缩
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 如果签名是png的话，则不管quality是多少，都不会进行质量的压缩
        int quality = 100;
        while ((baos.toByteArray().length / 1024f) > size) {
            quality -= 5; // 每次都减少5(如果这里-=10，有时候循环次数会提前结束)
            baos.reset(); // 重置baos即清空baos
            if (quality <= 0) {
                break;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        return baos.toByteArray();
    }

    private static long getSizeOfBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //这里100的话表示不压缩质量
        long length = baos.toByteArray().length / 1024; //读出图片的kb大小
        return length;
    }

    /**
     *
     * @param bmp
     * @param file
     * @param ratio 尺寸压缩倍数,值越大，图片尺寸越小
     */
    public static void compressBitmapToFile(Bitmap bmp, File file, int ratio) {
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param filePath
     * @param file
     * @param inSampleSize 数值越高，图片像素越低
     */
    public static void compressBitmap(String filePath, File file, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //采样率
        options.inSampleSize = inSampleSize;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 图片文件转换为指定编码的字符串
//     *
//     * @param imgFile  图片文件
//     */
//    public static String file2String(File imgFile) {
//        InputStream in = null;
//        byte[] data = null;
//        //读取图片字节数组
//        try{
//            in = new FileInputStream(imgFile);
//            data = new byte[in.available()];
//            in.read(data);
//            in.close();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        //对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        String result = encoder.encode(data);
//        return result;//返回Base64编码过的字节数组字符串
//    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
