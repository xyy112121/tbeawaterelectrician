package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * 举报成功
 */

public class MyAccusationSuccessActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accusation_success);
        initTopbar("举报成功");
    }
}
