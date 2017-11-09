package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * 扫码签到结果
 */

public class ScanCodeSignInActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_signin);
    }
}
