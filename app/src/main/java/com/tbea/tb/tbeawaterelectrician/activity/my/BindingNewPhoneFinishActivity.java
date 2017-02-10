package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.account.LoginActivity;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;

/**
 * Created by cy on 2017/1/19.
 */

public class BindingNewPhoneFinishActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_new_phone_finish);
        String title  = getIntent().getStringExtra("title");
        String title2  = getIntent().getStringExtra("title1");
        initTopbar(title);
        ((TextView)findViewById(R.id.title)).setText(title2);
        mContext = this;
        ShareConfig.setConfig(mContext, Constants.ONLINE,false);
        listener();
    }

    private void  listener(){
        findViewById(R.id.binding_finish_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
