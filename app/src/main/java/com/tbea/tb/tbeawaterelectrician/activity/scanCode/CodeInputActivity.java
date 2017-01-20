package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2017/1/16.条形码输入界面
 */

public class CodeInputActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_input);
        initTopbar("手工输入");
    }
}
