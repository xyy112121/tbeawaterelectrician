package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;

import java.util.Iterator;

import static android.R.attr.key;

/**
 * Created by cy on 2016/12/15.登录页面
 */

public class LoginActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        listener();
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
                    if(rspInfo.success){//成功
                        UserInfo2 userInfo2 = (UserInfo2) rspInfo.getDateObj("userinfo");
                        MyApplication.instance.setUserInfo(userInfo2);
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        showToast(rspInfo.msg);
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
