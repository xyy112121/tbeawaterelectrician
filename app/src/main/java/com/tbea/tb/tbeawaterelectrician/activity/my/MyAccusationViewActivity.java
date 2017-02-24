package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * 我的举报填写页
 */

public class MyAccusationViewActivity extends TopActivity{
    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accusation_view);
        initTopbar("我要举报");
        mContext = this;
        listener();
    }

    /**
     * 事件
     */
    private void  listener(){

    }
}
