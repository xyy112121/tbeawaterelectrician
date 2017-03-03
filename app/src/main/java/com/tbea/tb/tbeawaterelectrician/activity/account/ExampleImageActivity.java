package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2017/3/2.
 */

public class ExampleImageActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_image);
        initTopbar("示例照片");
    }
}
