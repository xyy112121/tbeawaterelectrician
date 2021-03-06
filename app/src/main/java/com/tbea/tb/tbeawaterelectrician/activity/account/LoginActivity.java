package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.my.EditBindingPhoneActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.Map;


/**
 * Created by cy on 2016/12/15.登录页面
 */

public class LoginActivity extends Activity {
    private String[] mPermissions = new String[]{};
    public final int SET_REQEST = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        if (Build.VERSION.SDK_INT >= 23) {
//            mPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.CALL_PHONE,
//                    Manifest.permission.ACCESS_FINE_LOCATION};
//            PermissionGen.needPermission(LoginActivity.this, 100, mPermissions);
//        }
        listener();
//        if (ShareConfig.getConfigBoolean(LoginActivity.this, Constants.ONLINE, false) == true) {
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }


//    @PermissionFail(requestCode = 100)
//    private void doFailSomething() {
//        try {
//            for (int i = 0; i < mPermissions.length; i++) {
//                boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[i]);
//                if (isTip) {
//                    Toast.makeText(LoginActivity.this, "你需要允许访问权限，才可正常使用该功能！", Toast.LENGTH_SHORT).show();
////                finish();
//                    break;
//                } else {
//                    showMissingPermissionDialog();
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            Log.e("Login,doFailSomething", e.getMessage());
//        }
//
//    }

    // 显示缺失权限提示
    public void showMissingPermissionDialog() {
        try {
            final CustomDialog dialog = new CustomDialog(LoginActivity.this, R.style.MyDialog, R.layout.tip_delete_dialog);
            dialog.setTitle(getResources().getString(R.string.help));
            dialog.setText(getResources().getString(R.string.string_help_text));
            dialog.setConfirmBtnClickListener(null, getResources().getString(R.string.quit));
            dialog.setCancelBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, SET_REQEST);
                }
            }, getResources().getString(R.string.settings));
            dialog.show();
        } catch (Exception e) {
            Log.e("Login,showMiss", e.getMessage());
        }

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        try {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        } catch (Exception e) {
//            Log.e("Login,onRequest", e.getMessage());
//        }
//    }

    public void listener() {
        findViewById(R.id.login_register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.lagin_finish_bth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = ((EditText) findViewById(R.id.login_phone)).getText() + "";
                String pwd = ((EditText) findViewById(R.id.login_pwd)).getText() + "";
                login(phone, pwd);
            }
        });

        findViewById(R.id.login_forget_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPwdPhoneActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(final String mobile, final String pwd) {
        if (isMobileNO(mobile) == false) {
            ToastUtil.showMessage("请输入正确的手机号码", LoginActivity.this);
            return;
        }
        if ("".equals(pwd)) {
            ToastUtil.showMessage("请输入密码", LoginActivity.this);
            return;

        }
        final CustomDialog dialog = new CustomDialog(LoginActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
//                            UserInfo2 userInfo2 = (UserInfo2) re.getDateObj("userinfo");
                            Map<String,Object> data = (Map<String,Object>)re.getData();
                            Map<String,String> userinfo = (Map<String,String>)data.get("userinfo");
//                        MyApplication.instance.setUserInfo(userInfo2);
                            ShareConfig.setConfig(LoginActivity.this, Constants.ONLINE, true);
                            ShareConfig.setConfig(LoginActivity.this, Constants.USERID, userinfo.get("id"));
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            MyApplication.instance.exit();
                            finish();
                        } else {
                            ToastUtil.showMessage(re.getMsg(), LoginActivity.this);
                        }
                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败", LoginActivity.this);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.login(mobile, pwd);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 验证手机格式 false不正确
     */
    public boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][73458]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (mobiles.equals("")) return false;
        else return mobiles.matches(telRegex);
    }
}
