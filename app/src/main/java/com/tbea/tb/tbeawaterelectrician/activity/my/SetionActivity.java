package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.account.AccountAuthenticationActivity;
import com.tbea.tb.tbeawaterelectrician.activity.account.RealNameAuthenticationFailActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.UpdateResponseModel;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.AppUpdateUtils;
import com.tbea.tb.tbeawaterelectrician.util.AppVersion;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;
import com.tbea.tb.tbeawaterelectrician.util.cache.DataCleanManager;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by cy on 2016/12/27.设置界面
 */

public class SetionActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setion_layout);
        mContext = this;
        MyApplication.instance.addActivity((Activity) mContext);
        initTopbar("设置");
        listener();
        try {
            String size = DataCleanManager.getTotalCacheSize(MyApplication.instance);
            ((TextView) findViewById(R.id.cache_size)).setText(size);
        } catch (Exception e) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String state = ShareConfig.getConfigString(mContext, Constants.WHETHERIDENTIFIEDID, "notidentify");
        TextView tvState = (TextView) findViewById(R.id.authentication_tv);
        if ("notidentify".equals(state)) {
            tvState.setText("未认证");
        } else if ("identifying".equals(state)) {
            tvState.setText("认证中");
        } else if ("identifyfailed".equals(state)) {
            tvState.setText("认证失败");
        } else {
            tvState.setText("已认证");
        }
    }

    private void listener() {
        findViewById(R.id.set_my_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, MyInformationActivity.class));
            }
        });

        findViewById(R.id.set_my_account_safe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AccountSafeActivity.class));
            }
        });

        findViewById(R.id.set_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUpdateInfo();
            }
        });

        findViewById(R.id.cache_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_delete_dialog);
                dialog.setText("清除缓存？");
                dialog.setCancelBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
//                        cleanInternalCache(getApplicationContext());
//                        cleanExternalCache(mContext);
                        DataCleanManager.clearAllCache(MyApplication.instance);
//                        String size = getCacheSize(getApplicationContext().getExternalCacheDir());

                        ((TextView) findViewById(R.id.cache_size)).setText("0KB");
                        UtilAssistants.showToast("清除成功！", mContext);
                    }
                }, "确定");
                dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                }, "取消");
                dialog.show();
            }
        });

        findViewById(R.id.set_my_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_delete_dialog);
                dialog.setText("您确定要退出么？");
                dialog.setCancelBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        ShareConfig.setConfig(mContext, Constants.ONLINE, false);
                        ShareConfig.setConfig(mContext, Constants.USERID, "");
                        finish();
                        MyApplication.instance.exit();
                        startActivity(new Intent(mContext, MainActivity.class));

                    }
                }, "确定");
                dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                }, "取消");
                dialog.show();
            }
        });

        (findViewById(R.id.authentication_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = ShareConfig.getConfigString(mContext, Constants.WHETHERIDENTIFIEDID, "notidentify");
//                String state = "identifyfailed";
                if ("identifyfailed".equals(state)) {//没有通过认证
                    startActivity(new Intent(mContext, RealNameAuthenticationFailActivity.class));

                } else {//已通过和审核中的，就显示认证信息
                    startActivity(new Intent(mContext, AccountAuthenticationActivity.class));
                }
//                Intent intent = new Intent(mContext, AccountAuthenticationActivity.class);
//                intent.putExtra("whetheridentifiedid", getIntent().getStringExtra("whetheridentifiedid"));
//                startActivityForResult(intent, 100);
            }
        });
    }

    /**
     * 从服务器获取是否更新
     */
    private void getUpdateInfo() {
        try {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case ThreadState.SUCCESS:
                            RspInfo1 re = (RspInfo1) msg.obj;
                            if (re.isSuccess()) {
                                Gson gson = new GsonBuilder().serializeNulls().create();
                                String json = gson.toJson(re.getData());
                                UpdateResponseModel model = gson.fromJson(json, UpdateResponseModel.class);
                                if (model.versioninfo != null) {
                                    UpdateResponseModel.VersioninfoBean info = model.versioninfo;
                                    if ("on".equals(info.tipswitch) && info.jumpurl != null && !"".equals(info.jumpurl)) {
                                        AppVersion av = new AppVersion();
                                        av.setApkName("tbeacloudbusiness.apk");
//                                av.setSha1("FCDA0D0E1E7D620A75DA02A131E2FFEDC1742AC8");
//                                        av.setUrl("http://down.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk");
                                        av.setUrl(info.jumpurl);
                                        av.setContent(info.upgradedescription);
                                        av.setVerCode(info.versioncode);
                                        av.setVersionName(info.versionname);
                                        if ("yes".equals(info.mustupgrade)) {
                                            AppUpdateUtils.init(mContext, av, false, false, false);//强制升级
                                            AppUpdateUtils.upDate();
                                        } else {
                                            AppUpdateUtils.init(mContext, av, false, false, true);
                                            AppUpdateUtils.upDate();
                                        }

                                    }
                                }
                            } else {
                                UtilAssistants.showToast(re.getMsg(), mContext);
                            }
                            break;
                        case ThreadState.ERROR:
                            UtilAssistants.showToast("操作失败！", mContext);
                            break;
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        UserAction userAction = new UserAction();
                        RspInfo1 model = userAction.getUpdate();
                        handler.obtainMessage(ThreadState.SUCCESS, model).sendToTarget();
                    } catch (Exception e) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if ("notidentify".equals(data.getStringExtra("whetheridentifiedid"))) {
                ((TextView) findViewById(R.id.authentication_tv)).setText("未认证");
            } else if ("identifying".equals(data.getStringExtra("whetheridentifiedid"))) {
                ((TextView) findViewById(R.id.authentication_tv)).setText("认证中");
            } else {
                ((TextView) findViewById(R.id.authentication_tv)).setText("已认证");
            }
        }
    }

    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getExternalCacheDir());
    }

    public void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     *
     * @param directory
     */
    private void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    public static String getCacheSize(File file) {
        String size;
        try {
            size = getFormatSize(getFolderSize(file));
        } catch (Exception e) {
            size = "";
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            if (kiloByte == 0.0) {
                return "";
            } else {
                return size + "Byte";
            }
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}


