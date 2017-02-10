package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;

import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by cy on 2017/1/19.新增收货地址
 */

public class AddressEditActivity extends TopActivity {
    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        mContext = this;
        initTopbar("新增收货地址");
        listener();
    }

    private  void listener(){
        findViewById(R.id.addr_edit_provincial_city_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,AddressCitySelectActivity.class);
                startActivity(intent);
//                ArrayList<AddressPicker.Province> data = new ArrayList<AddressPicker.Province>();
//                String json = UtilAssistants.readText(mContext, "city.json");
//                data.addAll(JSON.parseArray(json, AddressPicker.Province.class));
//                AddressPicker picker = new AddressPicker((Activity) mContext, data);
//                picker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
////                picker.setSelectedItem("贵州", "贵阳", "花溪");
//                //picker.setHideProvince(true);//加上此句举将只显示地级及县级
//                //picker.setHideCounty(true);//加上此句举将只显示省级及地级
//                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
//                    @Override
//                    public void onAddressPicked(String province, String city, String county) {
//                        ((TextView)findViewById(R.id.addr_edit_provincial_city)).setText(province + city + county);
////                        showToast(province + city + county);
//                    }
//                });
//                picker.setAnimationStyle(R.style.PopWindowAnimationFade);
//                picker.show();

            }
        });
    }
}
