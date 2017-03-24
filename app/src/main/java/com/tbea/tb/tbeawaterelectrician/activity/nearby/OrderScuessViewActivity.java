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

public class OrderScuessViewActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sucess);
        initTopbar("下单成功");
        mContext = this;
        initUI();
    }

    private void initUI(){
        String deliverytype = getIntent().getStringExtra("deliverytype");
        String paytype = getIntent().getStringExtra("paytype");
        String actualneedpaymoney = getIntent().getStringExtra("actualneedpaymoney");
        final String ordercode = getIntent().getStringExtra("ordercode");
        final String orderid = getIntent().getStringExtra("orderid");

        ((TextView)findViewById(R.id.pay_success_order_view_money)).setText("￥"+actualneedpaymoney);
        ((TextView)findViewById(R.id.pay_success_order_view_paytype)).setText(paytype);
        ((TextView)findViewById(R.id.pay_success_order_view_delivery_type)).setText(deliverytype);
        ((TextView)findViewById(R.id.pay_seccess_orderId)).setText("订单编号："+ordercode);

        findViewById(R.id.go_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.instance.exit();
                startActivity(new Intent(mContext, MainActivity.class));
                finish();

            }
        });

        findViewById(R.id.pay_success_order_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrderViewActivity.class);
                intent.putExtra("id",orderid);
                startActivity(intent);
            }
        });

    }
}
