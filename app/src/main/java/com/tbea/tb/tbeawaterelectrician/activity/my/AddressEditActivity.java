package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2017/1/19.新增收货地址
 */

public class AddressEditActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        initTopbar("新增收货地址");
    }
}
