package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by user on 2017/3/23.
 */

public class PayViewActivity extends TopActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_view);
        initTopbar("收银台");
        String ordertotlefee = getIntent().getStringExtra("ordertotlefee");
        ((TextView)findViewById(R.id.pay_view_ordertotlefee)).setText("￥"+ordertotlefee);

    }
}
