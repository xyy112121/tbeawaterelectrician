package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by cy on 2017/2/17.
 */

public class PayViewActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_select);
        initTopbar("下单成功");
        mContext = this;
        initUI();
    }

    private void initUI(){
        String deliverytype = getIntent().getStringExtra("deliverytype");
        String paytype = getIntent().getStringExtra("paytype");
        String actualneedpaymoney = getIntent().getStringExtra("actualneedpaymoney");
        ((TextView)findViewById(R.id.pay_success_order_view_money)).setText(actualneedpaymoney);
        ((TextView)findViewById(R.id.pay_success_order_view_paytype)).setText(paytype);
        ((TextView)findViewById(R.id.pay_success_order_view_delivery_type)).setText(deliverytype);

        findViewById(R.id.go_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.instance.exit();
                startActivity(new Intent(mContext, MainActivity.class));
                finish();

            }
        });

    }
}
