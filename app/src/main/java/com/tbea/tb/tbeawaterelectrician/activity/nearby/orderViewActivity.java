package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Address;

/**
 * Created by cy on 2017/3/9.
 */

public class OrderViewActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        initTopbar("订单详情");
    }

    public void initAddrView(Address obj){
//        mReceiveaddrId = obj.getId();
//        (findViewById(R.id.addr_item_contactperson)).setTag(obj.getId());
        ((TextView)findViewById(R.id.addr_item_contactperson)).setText(obj.getContactperson());
        ((TextView)findViewById(R.id.addr_item_contactmobile)).setText(obj.getContactmobile());
        ((TextView)findViewById(R.id.addr_item_address)).setText(obj.getAddress());
        ((TextView)findViewById(R.id.addr_item_address)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
        findViewById(R.id.addr_item_isdefault).setVisibility(View.GONE);
    }
}
