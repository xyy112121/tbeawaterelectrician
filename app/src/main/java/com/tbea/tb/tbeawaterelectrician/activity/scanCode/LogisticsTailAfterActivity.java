package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.UnderLineLinearLayout;

/**
 * Created by abc on 16/12/28.物流跟踪
 */

public class LogisticsTailAfterActivity extends TopActivity {
    private UnderLineLinearLayout mUnderLineLinearLayout;//物流时间轴


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_tail_after);
        initTopbar("物流跟踪");
        mUnderLineLinearLayout = (UnderLineLinearLayout)
                findViewById(R.id.underline_layout);
        addItem("你的订单商品分拣完毕等待出库","2016-10-27 09:59");
        addItem("你的订单商品当前正在恒大物流中心","2016-10-27 10:59");
        addItem("最新一条物流信息","2016-10-27 09:59");
    }

    private void addItem(String test,String time) {
        View v = LayoutInflater.from(this).inflate(R.layout.underline_item_vertical, mUnderLineLinearLayout, false);
        ((TextView) v.findViewById(R.id.tx_action)).setText(test);
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(time);
        mUnderLineLinearLayout.addView(v,0);
    }
}
