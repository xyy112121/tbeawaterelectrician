package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 *Created by cy on 2017/1/19.更改绑定手机号(新的号码获取验证码)
 */

public class BindingNewPhoneActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_new_phone_edit);
        initTopbar("更改绑定手机号");
        mContext = this;
        listener();
    }

    private void  listener(){
        findViewById(R.id.myphone_edit_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,BindingNewPhoneFinishActivity.class);
                startActivity(intent);
            }
        });
    }
}
