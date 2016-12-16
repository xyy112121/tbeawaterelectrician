package com.tbea.tb.tbeawaterelectrician.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tbea.tb.tbeawaterelectrician.R;

/**
 * Created by cy on 2016/12/15.登录页面
 */

public class LoginActivity extends Activity {
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
    }
}
