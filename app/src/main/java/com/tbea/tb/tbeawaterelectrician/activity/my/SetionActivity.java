package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2016/12/27.设置界面
 */

public class SetionActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setion_layout);
        initTopbar("设置");
        listener();
    }
    private  void listener(){
        findViewById(R.id.set_my_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetionActivity.this,MyInformationActivity.class));
            }
        });

        findViewById(R.id.set_my_account_safe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetionActivity.this,AccountSafeActivity.class));
            }
        });
    }
}
