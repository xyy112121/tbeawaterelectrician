package com.tbea.tb.tbeawaterelectrician.util;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;


import com.tbea.tb.tbeawaterelectrician.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

/**
 * 更新
 */
public class AppUpdateUtils {
    private static Context mContext;
    private static int mVersionCode;
    private static AppVersion mAppVersion;
    private static String mVersionName;
    private static String mFileName;
    private static ProgressDialog mProgressDialog;
    private static String mLocalFilepath;
    private static DownloadFileAsyncTask mDownloadFileAsyncTask;
    private static boolean hasCancel = false;
    public static final String TAG = "AppUpDate";
    // 如果是自动更新，那么就不跳出已经是最新版本的对话框。否则用户点击更新应用按钮，弹出已经是最新版本的提示框
    private static boolean mIsAuto = false;
    private static boolean mShowProgressDialog = false;
    private static boolean mShowCancelableDialog = true;
    private static final int NOTIFY_ID = 54;
    private static NotificationManager mNotificationManager;
    private static Notification mNotification;

    /**
     * 初始化
     *
     * @param context            执行上下文
     * @param newAv              对比版本
     * @param isauto             指示是否是自动升级，当版本号没变时，true无响应，false弹出已是最新版的对话框。
     * @param showProgressDialog true弹出下载进度对话框，false为通知消息提示进度
     * @param showCancelableDialog true对话框点击其他位置消失，false对话框点击其他位置不消失
     */
    public static void init(Context context, AppVersion newAv, boolean isauto,
                            boolean showProgressDialog,boolean showCancelableDialog) {
        mIsAuto = isauto;
        mShowProgressDialog = showProgressDialog;
        mContext = context;
        mShowCancelableDialog = showCancelableDialog;
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mVersionCode = getVerCode(mContext);
        mVersionName = getVerName(mContext);
        mAppVersion = newAv;
        mFileName = mAppVersion.getApkName();
        File sdDir = null;

        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 获取根目录
            sdDir = Environment.getExternalStorageDirectory();
        }

        // 注意FileOutputStream不会自动创建路径，所以初始化的时候要主动创建路径。
        String dirpath;
        if (sdDir != null) {
            // AppName为你想保存的路径，一般为应用目录
            dirpath = sdDir.toString() + "/AppName/";
        } else {
            dirpath = "/AppName/";
        }
        File dir = new File(dirpath);
        if (!dir.exists()) {
            dir.mkdir();// 如果路径不存在就先创建路径
        }
        mLocalFilepath = dirpath + mFileName;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    private static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    private static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verName;
    }

    /**
     * 更新应用
     */
    public static void upDate() {
        Log.e("更新",mVersionCode+"");
        Log.e("更新版本",mAppVersion.getVerCode()+"");
        Log.e("是否更新",mIsAuto+"");
        if (mVersionCode < mAppVersion.getVerCode()) {
            doNewVersionUpdate();
        } else {
            notNewVersionShow();
        }
    }

    /**
     * 不执行更新
     */
    private static void notNewVersionShow() {
        // 如果不是自动升级
        if (!mIsAuto) {
            StringBuffer sb = new StringBuffer();
            sb.append("当前版本,");
//            sb.append(mVersionName);
            sb.append("已是最新版,无需更新。");
            // 这里的提示框是我自定义的
            AlertDialog ad = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage(sb.toString())
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 取消
                                    dialog.dismiss();
                                }
                            }).create();
            ad.show();
        }
    }

    /**
     * 更新应用提示
     */
    private static void doNewVersionUpdate() {
        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(mVersionName);
        sb.append(", 发现新版本:");
        sb.append(mAppVersion.getVersionName()).append("\n");
        sb.append("更新内容：\n");
        // 这里我们使用;作为分隔符，显示多条跟新内容信息。
        if (mAppVersion.getContent().contains(";")) {
            String[] up = mAppVersion.getContent().split(";");
            for (String s : up) {
                sb.append(s).append("\n");
            }
        } else {
            sb.append(mAppVersion.getContent()).append("\n");
        }
//        sb.append("是否更新?");
        if(mShowCancelableDialog){//不强制更新
            AlertDialog ad = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage(sb.toString())
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            updateFile(mAppVersion.getUrl());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("暂不更新",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // 点击"取消"按钮之后退出程序
                                    dialog.dismiss();
                                }
                            }).create();// 创建
            // 显示对话框
            ad.show();
        }else {//强制更新
            AlertDialog ad = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage(sb.toString())
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            updateFile(mAppVersion.getUrl());
                            dialog.dismiss();
                        }
                    }).create();// 创建
            // 显示对话框
            ad.setCancelable(false);
            ad.show();
        }

    }

    // 执行应用更新
    private static void updateFile(final String path) {
        // pBar.show();

        File file = new File(mLocalFilepath);

        // 根据服务器传回的更新包sha1进行比对,判断是否下载完成，如果不匹配，则重新下载，否则直接打开。
        if (mAppVersion.getSha1() != null) {
            if (getFileSHA1(file).toUpperCase().equals(mAppVersion.getSha1())) {
                openFile(file);
            } else {
                try {
                    // downloadFile(path, localFilepath);
                    mDownloadFileAsyncTask = new DownloadFileAsyncTask();
                    mDownloadFileAsyncTask.execute(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                mDownloadFileAsyncTask = new DownloadFileAsyncTask();
                mDownloadFileAsyncTask.execute(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消更新
     */
    public void cancelUpDate() {
        if (mDownloadFileAsyncTask != null) {
            if (!mDownloadFileAsyncTask.isCancelled()) {
                mDownloadFileAsyncTask.cancel(true);
            }
        }
    }

    /**
     * 应用下载Task
     */
    private static class DownloadFileAsyncTask extends
            AsyncTask<String, Integer, String> {

        // 后台下载任务
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                URLConnection connection;
                connection = url.openConnection();
                // 2.2以上默认用“gzip”，这里要取消使用，否则大小永远为-1.
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.connect();
                int length = connection.getContentLength();
                InputStream input = new BufferedInputStream(
                        connection.getInputStream());
                FileOutputStream output = new FileOutputStream(mLocalFilepath);
                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (!hasCancel) {
                        total += count;
                        publishProgress((int) (total * 100 / length));
                        output.write(data, 0, count);
                    } else {
                        cancel(true);
                        break;
                    }
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        // 进度更新
        private int tempv;

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            // 如果需要用到进度提示框，则执行
            if (mShowProgressDialog) {
                if (values[0] != tempv) {
                    mProgressDialog.setProgress(values[0]);
                }
            } else {
                // 防止多次重复提示，影响性能
                if (values[0] != tempv) {
                    RemoteViews contentView = mNotification.contentView;
                    contentView.setTextViewText(R.id.rate,
                            String.valueOf(values[0]) + "%");
                    contentView.setProgressBar(R.id.progress, 100, values[0],
                            false);
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    tempv = values[0];
                }
            }
        }

        // 下载完成后执行
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mShowProgressDialog) {
                openFile(new File(mLocalFilepath));
            } else {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                // 设定intent的file与MimeType
                intent.setDataAndType(Uri.fromFile(new File(mLocalFilepath)),
                        "application/vnd.android.package-archive");

                // 下载完毕后变换通知形式
                mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                mNotification.contentView = null;
                // Intent intent = new Intent(mContext, FileMgrActivity.class);
                // // 告知已完成
                // intent.putExtra("completed", "yes");
                // //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                PendingIntent contentIntent = PendingIntent.getActivity(
                        mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                mNotification.setLatestEventInfo(mContext, "下载完成",
//                        "文件已下载完毕，点击进行安装。", contentIntent);
                if (Build.VERSION.SDK_INT < 16) {
                    Class clazz = mNotification.getClass();
                    try {
                        Method m2 = clazz.getDeclaredMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                        m2.invoke(mNotification, mContext, "下载完成",
                                "文件已下载完毕，点击进行安装。", contentIntent);
                        mNotificationManager.notify(NOTIFY_ID, mNotification);
                    } catch (Exception e) {
                        Log.e("","");
                    }

                } else {
                    Notification.Builder builder1 = new Notification.Builder(mContext);
                    builder1.setSmallIcon(R.mipmap.icon_main); //设置图标
//                    builder1.setTicker("显示第二个通知");
                    builder1.setContentTitle("下载完成"); //设置标题
                    builder1.setContentText("文件已下载完毕，点击进行安装。"); //消息内容
                    builder1.setWhen(System.currentTimeMillis()); //发送时间
//                    builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
                    builder1.setAutoCancel(true);//打开程序后图标消失
                    builder1.setContentIntent(contentIntent);
                    Notification notification1 = builder1.build();
                    mNotificationManager.notify(NOTIFY_ID, notification1); // 通过通知管理器发送通知
                }
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            hasCancel = false;
            if (mShowProgressDialog) {
                showProgressDialog();
            } else {
                tempv = 0;
                int icon = R.mipmap.icon_main;
                CharSequence tickerText = "开始下载";
                long when = System.currentTimeMillis();
                mNotification = new Notification(icon, tickerText, when);

                // 在通知栏上点击此通知后自动清除此通知
                mNotification.flags = Notification.FLAG_AUTO_CANCEL;

                RemoteViews contentView = new RemoteViews(
                        mContext.getPackageName(), R.layout.notification_update);
                contentView.setTextViewText(R.id.fileName, "更新文件下载中……");
                // 指定个性化视图
                mNotification.contentView = contentView;
                Intent intent = new Intent();
                PendingIntent contentIntent = PendingIntent.getActivity(
                        mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // 指定内容意图
                mNotification.contentIntent = contentIntent;
                mNotificationManager.notify(NOTIFY_ID, mNotification);
            }
        }
    }

    // 在手机上打开文件
    private static void openFile(File f) {
        // mProgressDialog.dismiss();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        // 设定intent的file与MimeType，安装文件
        intent.setDataAndType(Uri.fromFile(f),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    // 使用自带算法 获取文件的SHA1
    private static String getFileSHA1(File file) {
        if (file.exists()) {
            MessageDigest digest = null;
            byte buffer[] = new byte[1024];
            int len;
            try {
                digest = MessageDigest.getInstance("SHA-1");// ("SHA-1");
                FileInputStream in = new FileInputStream(file);
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

            // 直接用这玩意转换成16进制，碉堡了、
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);

        } else {
            return "";
        }
    }

    /**
     * 显示下载进度对话框，可以取消下载任务。
     */
    private static void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (mDownloadFileAsyncTask != null) {
                    if (!mDownloadFileAsyncTask.isCancelled()) {
                        mDownloadFileAsyncTask.cancel(true);
                        hasCancel = true;
                    }
                }
            }
        });
        mProgressDialog.setTitle("正在下载");
        mProgressDialog.setMax(100);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("请稍候...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }
}
