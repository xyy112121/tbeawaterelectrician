package com.tbea.tb.tbeawaterelectrician.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by cy on 2017/1/22.
 */

public  class UtilAssistants {

    //根据Uri获取path开始
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
//        java.lang.SecurityException: Permission Denial: reading com.android.providers.media.MediaProvide
        catch (Exception e){
            Log.d("",e.getMessage());
        }  finally{
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    //根据Uri获取path结束

    public static Bitmap revitionImageSize(String path)  {
        Bitmap bitmap = null;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //2.为位图设置10K的缓存
            options.inTempStorage = new byte[10*1024];
            //3.设置位图颜色显示优化方式
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            //4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
            options.inPurgeable = true;
            //6.设置解码位图的尺寸信息
            options.inInputShareable = true;
            //5.设置位图缩放比例
            options.inSampleSize = 4;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;

            while (true) {
                if ((options.outWidth >> i <= 256)
                        && (options.outHeight >> i <= 256)) {
                    in = new BufferedInputStream(
                            new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
            return bitmap;
        } catch (Exception e) {
        }
        return bitmap;
    }

    //根据path获得Bitmpa开始
    public static Bitmap getBitmapFromPath(String filePath, Point point) throws Exception {

        if (!new File(filePath).exists()) {
            System.err.println("getBitmapFromPath: file not exists");
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = computeScale(options, point);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) {
            return null;
        }
        return bm;
    }

    private static int computeScale(BitmapFactory.Options options, Point point) {
        int inSampleSize = 1;
        if (point == null) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > point.x || bitmapHeight > point.y) {
            int widthScale = Math.round((float) bitmapWidth / (float) point.x);
            int heightScale = Math.round((float) bitmapHeight / (float) point.y);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    //根据path获得Bitmpa结束

    public static void showToast(String text){
        Toast.makeText(MyApplication.instance, text, Toast.LENGTH_SHORT).show();
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
