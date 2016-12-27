package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2016/12/27.
 */

public class SetionActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setion_layout);
        initTopbar("设置");
    }
}
