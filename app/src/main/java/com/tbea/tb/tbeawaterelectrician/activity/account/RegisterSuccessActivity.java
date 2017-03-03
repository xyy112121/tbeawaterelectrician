package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2017/3/2.
 */

public class RegisterSuccessActivity extends TopActivity {
    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_success);
        initTopbar("");
        mContext = this;
        listener();
    }

    private void listener(){
        findViewById(R.id.register_authentucation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,AccountAuthenticationActivity.class));
            }
        });
    }
}
