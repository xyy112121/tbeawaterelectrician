package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.AddressCitySelectActivity;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.activity.NetWebViewActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by cy on 2017/3/2.
 */

public class RegisterActivity extends TopActivity {
    private Context mContext;
    private List<Condition> mDistributorList = new ArrayList<>();
    private String mProvinceId;
    private String mCityId;
    private String mLocationId;

    private String mProvince;
    private String mCity;
    private String mLocation;
    private MyCount mc;
    private Button button;
    private String mPhone;

    private final int ADDR_SELECT = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        getPhoneDate();
        initTopbar("注册");
        mContext = this;
        button = (Button) findViewById(R.id.regist_sendcode);
        mProvince = MyApplication.instance.getProvince();
        mCity = MyApplication.instance.getCity();
        mLocation = MyApplication.instance.getDistrict();
        ((TextView) findViewById(R.id.register_zone)).setText(mProvince + mCity + mLocation);
        listener();
    }

    public void getPhoneDate() {
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String, String> pageinfo = (Map<String, String>) data.get("pageinfo");
                            mPhone = pageinfo.get("contactmobile");
                            ((TextView) findViewById(R.id.register_cm_phone)).setText("热线电话: " + mPhone);

                        } else {
                            ToastUtil.showMessage(re.getMsg(), mContext);
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
                    RspInfo1 re = userAction.getPhone();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private void listener() {

        findViewById(R.id.register_cm_phone).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
                startActivity(intent);
            }
        });

        findViewById(R.id.register_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddressCitySelectActivity.class);
                intent.putExtra("withall", "0");//不显示全部
                intent.putExtra("province", mProvince);
                intent.putExtra("city", mCity);
                intent.putExtra("zone", mLocation);
                startActivityForResult(intent, ADDR_SELECT);
            }
        });

        findViewById(R.id.register_distributor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String zone = ((TextView) findViewById(R.id.register_zone)).getText() + "";
                if (!"".equals(zone)) {
                    getDistributorList();
                } else {
                    ToastUtil.showMessage("请选择所在地区！", mContext);
                }
            }
        });

        findViewById(R.id.register_argee_tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, NetWebViewActivity.class);
                intent.putExtra("title", "用户注册协议");
                intent.putExtra("parameter", "userregisteragreement");//URL后缀
                startActivity(intent);

            }
        });

        findViewById(R.id.register_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile = ((EditText) findViewById(R.id.register_phone)).getText() + "";
                final String code = ((EditText) findViewById(R.id.register_identifying_code)).getText() + "";
                final String pwd = ((EditText) findViewById(R.id.register_pwd)).getText() + "";
                final String zone = ((TextView) findViewById(R.id.register_zone)).getText() + "";
                final String distributor = ((TextView) findViewById(R.id.register_distributor)).getText() + "";
                String pwd2 = ((TextView) findViewById(R.id.register_pwd2)).getText() + "";

                if (isMobileNO(mobile) == false) {
                    ToastUtil.showMessage("请正确输入手机号码！", mContext);
                    return;
                }
                if (code.equals("") || "".equals(pwd) || "".equals(zone) || "".equals(distributor) || "".equals(pwd2)) {
                    ToastUtil.showMessage("请填写全部信息！", mContext);
                    return;
                }

                if (pwd.length() < 6 || pwd.length() > 10) {
                    ToastUtil.showMessage("密码长度6到10位！", mContext);
                    return;
                }

                if (!pwd.equals(pwd2)) {
                    ToastUtil.showMessage("两次密码不一致！", mContext);
                    return;
                }

                final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
                dialog.setText("加载中");
                dialog.show();

                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        dialog.dismiss();
                        switch (msg.what) {
                            case ThreadState.SUCCESS:
                                RspInfo1 re = (RspInfo1) msg.obj;
                                if (re.isSuccess()) {
                                    startActivity(new Intent(mContext, RegisterSuccessActivity.class));
                                    finish();
                                } else {
                                    ToastUtil.showMessage(re.getMsg(), mContext);
                                }

                                break;
                            case ThreadState.ERROR:
                                ToastUtil.showMessage("注册失败！", mContext);
                                break;
                        }
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserAction action = new UserAction();
                        try {
                            String distributorId = "";
                            if (mDistributorList != null) {
                                for (Condition item : mDistributorList
                                        ) {
                                    if (item.getName().equals(distributor)) {
                                        distributorId = item.getId();
                                    }
                                }
                            }
                            RspInfo1 result = action.register(mobile, pwd, code, mProvinceId, mCityId, mLocationId, distributorId, mProvince, mCity, mLocation);
                            handler.obtainMessage(ThreadState.SUCCESS, result).sendToTarget();
                        } catch (Exception e) {
                            handler.sendEmptyMessage(ThreadState.ERROR);
                        }
                    }
                }).start();

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile = ((EditText) findViewById(R.id.register_phone)).getText() + "";
                if (isMobileNO(mobile) == false) {
                    ToastUtil.showMessage("请输入正确的手机号码！", mContext);
                    return;
                }
                mc = new MyCount(60000, 1000);//倒计时60秒
                mc.start();

                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case ThreadState.SUCCESS:
                                RspInfo1 re = (RspInfo1) msg.obj;
                                if (re.isSuccess() == false) {
                                    mc.cancel();
                                    button.setText("获取验证码");
                                }
                                ToastUtil.showMessage(re.getMsg(), mContext);
                                break;
                            case ThreadState.ERROR:
                                ToastUtil.showMessage("获取验证失败，请重试！", mContext);
                                mc.cancel();
                                button.setText("获取验证码");
                                break;
                        }
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserAction action = new UserAction();
                        try {
                            RspInfo1 result = action.sendCode(mobile, "TBEAENG001001002000");
                            handler.obtainMessage(ThreadState.SUCCESS, result).sendToTarget();
                        } catch (Exception e) {
                            handler.sendEmptyMessage(ThreadState.ERROR);
                        }
                    }
                }).start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ADDR_SELECT && data != null) {
            String text = data.getStringExtra("text");
            ((TextView) findViewById(R.id.register_zone)).setText(text);
            ((TextView) findViewById(R.id.register_distributor)).setText("");
            mProvinceId = data.getStringExtra("provinceId");
            mCityId = data.getStringExtra("cityId");
            mLocationId = data.getStringExtra("locationId");
            mProvince = data.getStringExtra("province");
            mCity = data.getStringExtra("city");
            mLocation = data.getStringExtra("location");
        }
    }

    private void getDistributorList() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            mDistributorList = (List<Condition>) re.getDateObj("distributorlist");
                            if (mDistributorList != null) {
                                String[] dates = new String[mDistributorList.size()];
                                for (int i = 0; i < mDistributorList.size(); i++) {
                                    dates[i] = mDistributorList.get(i).getName();
                                }
                                OptionPicker mPicker = new OptionPicker((Activity) mContext, dates);
                                mPicker.setOffset(1);
                                mPicker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                                mPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                                    @Override
                                    public void onOptionPicked(String option) {
                                        ((TextView) findViewById(R.id.register_distributor)).setText(option);
                                    }
                                });
                                mPicker.setAnimationStyle(R.style.PopWindowAnimationFade);
                                mPicker.show();
                            } else {
                                ToastUtil.showMessage("操作失败！", mContext);
                            }
                        } else {
                            ToastUtil.showMessage("操作失败！", mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", mContext);
                        break;
                }
                dialog.dismiss();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getDistributorList(mProvinceId, mCityId, mLocationId, mProvince, mCity, mLocation);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        if (mc != null) {
            mc.cancel();
        }
        super.onDestroy();
    }

    /**
     * 验证手机格式 false不正确
     */
    public boolean isMobileNO(String mobiles) {
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (mobiles.equals("")) return false;
        else return mobiles.matches(telRegex);
    }


    private class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            button.setText("获取验证码");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            button.setText("重新发送" + millisUntilFinished / 1000 + "秒");

        }
    }
}
