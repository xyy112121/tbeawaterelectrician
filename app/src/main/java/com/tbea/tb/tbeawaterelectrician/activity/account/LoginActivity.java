package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;

import java.util.Iterator;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;

import static android.R.attr.key;

/**
 * Created by cy on 2016/12/15.登录页面
 */

public class LoginActivity extends Activity{
    private String[] mPermissions = new String[]{};
    public final int SET_REQEST = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT >= 23){
            mPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION};
            PermissionGen.needPermission(LoginActivity.this,100,mPermissions);
        }
        listener();
        if (ShareConfig.getConfigBoolean(LoginActivity.this,Constants.ONLINE,false) == true){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    @PermissionFail(requestCode = 100)
    private void doFailSomething() {
        for (int i = 0; i < mPermissions.length; i++) {
            boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[i]);
            if(isTip){
                Toast.makeText(LoginActivity.this,"你需要允许访问权限，才可正常使用该功能！",Toast.LENGTH_SHORT).show();
                finish();
                break;
            }else {
                showMissingPermissionDialog();
                break;
            }
        }
    }

    // 显示缺失权限提示
    public void showMissingPermissionDialog() {
		final CustomDialog dialog = new CustomDialog(LoginActivity.this,R.style.MyDialog,R.layout.tip_delete_dialog);
		dialog.setTitle(getResources().getString(R.string.help));
		dialog.setText(getResources().getString(R.string.string_help_text));
		dialog.setConfirmBtnClickListener(null,getResources().getString(R.string.quit));
		dialog.setCancelBtnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();
				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + getPackageName()));
				startActivityForResult(intent,SET_REQEST);
			}
		},getResources().getString(R.string.settings));
		dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this,requestCode, permissions, grantResults);
    }

    public void listener(){
        findViewById(R.id.login_register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.lagin_finish_bth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = ((EditText)findViewById(R.id.login_phone)).getText()+"";
                String pwd = ((EditText)findViewById(R.id.login_pwd)).getText()+"";
                UserAction action = new UserAction();
                try {
                    RspInfo rspInfo = action.login(phone,pwd);
                    if(rspInfo.isSuccess()){//成功
                        UserInfo2 userInfo2 = (UserInfo2) rspInfo.getDateObj("userinfo");
//                        MyApplication.instance.setUserInfo(userInfo2);
                        ShareConfig.setConfig(LoginActivity.this, Constants.ONLINE,true);
                        ShareConfig.setConfig(LoginActivity.this,Constants.USERID,userInfo2.getId());
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        showToast(rspInfo.getMsg());
                    }
                }catch (Exception e){
                    showToast("登录失败,请重试...");

                }
//                action.mobileNumber = ((EditText)findViewById(R.id.login_phone)).getText()+"";
//                action.userPas = ((EditText)findViewById(R.id.login_pwd)).getText()+"";
//                action.execute();


//
            }
        });
    }

//    private void successProcess(String rspContext){
////        JsonObject jobj=new JsonParser().parse(rspContext).getAsJsonObject();
//        Gson gson = new Gson();
//        UserInfo2 userInfo = gson.fromJson(jobj.get("UserInfo"), UserInfo2.class);
//    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
