package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Address;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;

import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by cy on 2017/1/19.新增收货地址
 */

public class AddressEditActivity extends TopActivity implements View.OnClickListener {
    private Context mContext;
    private Address mObj = new Address();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        mContext = this;
        initTopbar("新增收货地址","保存",this);
        listener();
    }

    private  void listener(){
        findViewById(R.id.addr_edit_provincial_city_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,AddressCitySelectActivity.class);
                startActivityForResult(intent,100);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 100 && data != null){
            mObj.setProvinceId(data.getStringExtra("provinceId"));
            mObj.setProvinceId(data.getStringExtra("cityId"));
            mObj.setProvinceId(data.getStringExtra("locationId"));
            String text = data.getStringExtra("text");
            ((TextView)findViewById(R.id.addr_edit_provincial_city)).setText(text);

        }
    }

    @Override
    public void onClick(View view) {
        String name = ((TextView)findViewById(R.id.addr_edit_contactperson)).getText()+"";
        String contactmobile = ((TextView)findViewById(R.id.addr_edit_contactmobile)).getText()+"";
        String address = ((TextView)findViewById(R.id.addr_edit_address)).getText()+"";
        String citys = ((TextView)findViewById(R.id.addr_edit_provincial_city)).getText()+"";

        CheckBox ck = (CheckBox)findViewById(R.id.addr_edit_isdefault);

        if("".equals(name) || "".equals(contactmobile) || "".equals(address) || "".equals(citys)){
            UtilAssistants.showToast("请填写完整的地址信息");
            return;
        }

        if(isMobileNO(contactmobile) == false){
            UtilAssistants.showToast("请输入正确的手机号码！");
            return;
        }

        if(ck.isChecked()){
            mObj.setIsdefault("1");
        }else {
            mObj.setIsdefault("0");
        }
        mObj.setContactperson(name);
        mObj.setContactmobile(contactmobile);
        mObj.setAddress(address);

        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            UtilAssistants.showToast(re.getMsg());
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！");
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.addAddrss(mObj);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    /**
     * 验证手机格式 false不正确
     */
    public  boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (mobiles.equals("")) return false;
        else return mobiles.matches(telRegex);
    }
}
