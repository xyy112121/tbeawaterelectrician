package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by cy on 2017/2/10.
 */

public class AddressCitySelectActivity extends TopActivity implements View.OnClickListener {
    private Context mContext;
    private List<Condition> mProvinceList = new ArrayList<>();
    private List<Condition> mCityList = new ArrayList<>();
    private List<Condition> mLocationList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addr_city_select);
        mContext = this;
        initTopbar("选择地址", "保存", this);
        listener();
    }

    private void listener() {
        String province = getIntent().getStringExtra("province");
        String city = getIntent().getStringExtra("city");
        String zone = getIntent().getStringExtra("zone");
        ((TextView) findViewById(R.id.addr_city_select_province)).setText(province);
        ((TextView) findViewById(R.id.addr_city_select_city)).setText(city);
        ((TextView) findViewById(R.id.addr_city_select_location)).setText(zone);

        findViewById(R.id.addr_city_select_province).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String province = ((TextView) findViewById(R.id.addr_city_select_province)).getText() + "";
                getList("TBEAENG002001002001", province, "");

            }
        });

        findViewById(R.id.addr_city_select_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = ((TextView) findViewById(R.id.addr_city_select_city)).getText() + "";
                String province = ((TextView) findViewById(R.id.addr_city_select_province)).getText() + "";
                getList("TBEAENG002001002000", province, city);
            }
        });

        findViewById(R.id.addr_city_select_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = ((TextView) findViewById(R.id.addr_city_select_city)).getText() + "";
                String location = ((TextView) findViewById(R.id.addr_city_select_location)).getText() + "";
                getList("TBEAENG003001002000", city, location);
            }
        });
    }

    /**
     * 获取省列表
     */
    public void getList(final String methodName, final String name, final String selectName) {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Condition> list = new ArrayList<>();
                            if ("TBEAENG002001002001".equals(methodName)) {
                                list = (List<Condition>) re.getDateObj("provincelist");
                                mProvinceList = list;
                            }
                            if ("TBEAENG002001002000".equals(methodName)) {
                                list = (List<Condition>) re.getDateObj("citylist");
                                mCityList = list;
                            }
                            if ("TBEAENG003001002000".equals(methodName)) {
                                list = (List<Condition>) re.getDateObj("locationlist");
                                mLocationList = list;
                            }
                            String[] dates = new String[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                dates[i] = list.get(i).getName();
                            }
                            OptionPicker picker = new OptionPicker((Activity) mContext, dates);
                            picker.setOffset(1);
                            if (!"".equals(selectName)) {
                                picker.setSelectedItem(selectName);
                            }
                            picker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                            picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                                @Override
                                public void onOptionPicked(String option) {
                                    if ("TBEAENG002001002001".equals(methodName)) {
                                        ((TextView) findViewById(R.id.addr_city_select_province)).setText(option);
                                        ((TextView) findViewById(R.id.addr_city_select_city)).setText("");
                                        ((TextView) findViewById(R.id.addr_city_select_location)).setText("");
                                    } else if ("TBEAENG002001002000".equals(methodName)) {
                                        ((TextView) findViewById(R.id.addr_city_select_city)).setText(option);
                                        ((TextView) findViewById(R.id.addr_city_select_location)).setText("");
                                    } else if ("TBEAENG003001002000".equals(methodName)) {
                                        ((TextView) findViewById(R.id.addr_city_select_location)).setText(option);
                                    }
                                }
                            });
                            picker.setAnimationStyle(R.style.PopWindowAnimationFade);
                            picker.show();
                        } else {
                            ToastUtil.showMessage("操作失败！", mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = null;
                    if ("TBEAENG002001002001".equals(methodName)) {
                        re = userAction.getProvinceList();
                    }
                    if ("TBEAENG002001002000".equals(methodName)) {
                        if(name == null||"".equals(name)){
                            handler.sendEmptyMessage(ThreadState.ERROR);
                            return;
                        }
                        String provinceId = "";
                        for (Condition obj : mProvinceList) {
                            if (obj.getName().equals(name)) {
                                provinceId = obj.getId();
                            }
                        }
                        re = userAction.getCityList2(provinceId);
                    }
                    if ("TBEAENG003001002000".equals(methodName)) {
                        if(name == null ||"".equals(name)){
                            handler.sendEmptyMessage(ThreadState.ERROR);
                            return;
                        }
                        String withall = getIntent().getStringExtra("withall");
                        if (withall == null || "".equals(withall)) {
                            withall = "1";
                        }
                        re = userAction.getLocationList(name, withall);
                    }
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        String province = ((TextView) findViewById(R.id.addr_city_select_province)).getText() + "";
        String city = ((TextView) findViewById(R.id.addr_city_select_city)).getText() + "";
        String location = ((TextView) findViewById(R.id.addr_city_select_location)).getText() + "";

        if ("".equals(province) || "".equals(city) || "".equals(location)) {
            ToastUtil.showMessage("请选择正确的地址！", mContext);
            return;
        }

        //省
        String provinceId = "";
        for (Condition obj : mProvinceList) {
            if (obj.getName().equals(province)) {
                provinceId = obj.getId();
            }
        }

        //市
        String cityId = "";
        for (Condition obj : mCityList) {
            if (obj.getName().equals(city)) {
                cityId = obj.getId();
            }
        }

        //区
        String locationId = "";
        for (Condition obj : mLocationList) {
            if (obj.getName().equals(location)) {
                locationId = obj.getId();
            }
        }

        Intent intent = new Intent();
        intent.putExtra("provinceId", provinceId);
        intent.putExtra("cityId", cityId);
        intent.putExtra("locationId", locationId);
        intent.putExtra("province", province);
        intent.putExtra("city", city);
        intent.putExtra("location", location);
        intent.putExtra("text", province + " " + city + " " + location);
        setResult(RESULT_OK, intent);
        finish();
    }
}
