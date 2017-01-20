package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2017/1/19.更改绑定手机号(老的号码获取验证码)
 */

public class EditBindingPhoneActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_binding_phone_edit);
        initTopbar("更改绑定手机号");
        mContext = this;
        listener();
    }

    private void  listener(){
        findViewById(R.id.myphone_edit_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,BindingNewPhoneActivity.class);
                startActivity(intent);
            }
        });
    }
}
