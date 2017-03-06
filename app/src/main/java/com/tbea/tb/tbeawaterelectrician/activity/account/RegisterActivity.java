package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.AddressCitySelectActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.fragment.account.RealNameVerifyFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.account.RegisterPhoneFragment;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

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
    private MyCount mc;
    private Button button;
    private String mPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        getPhoneDate();
        initTopbar("注册");
        mContext = this;
        button = (Button)findViewById(R.id.regist_sendcode);
        listener();
    }

    public void getPhoneDate(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String, String> pageinfo = (Map<String, String>) data.get("pageinfo");
                            mPhone = pageinfo.get("contactmobile");
                            ((TextView)findViewById(R.id.register_cm_phone)).setText("热线电话: "+mPhone);

                        }else {
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
                    RspInfo1 re = userAction.getPhone();
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private void listener(){

        findViewById(R.id.register_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,AddressCitySelectActivity.class);
                startActivityForResult(intent,100);
            }
        });

        findViewById(R.id.register_distributor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String zone = ((TextView)findViewById(R.id.register_zone)).getText()+"";
                if(!"".equals(zone)){
                    getDistributorList();
                }else {
                    UtilAssistants.showToast("请选择所在地区");
                }
            }
        });

        findViewById(R.id.register_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  String mobile = ((EditText)findViewById(R.id.register_phone)).getText()+"";
                final  String code = ((EditText)findViewById(R.id.register_identifying_code)).getText()+"";
                final  String pwd = ((EditText)findViewById(R.id.register_pwd)).getText()+"";
                final  String zone = ((TextView)findViewById(R.id.register_zone)).getText()+"";
                final  String distributor = ((TextView)findViewById(R.id.register_distributor)).getText()+"";
                if(isMobileNO(mobile) == false){
                    UtilAssistants.showToast("请正确输入手机号码");
                    return;
                }
                if(code.equals("") || "".equals(pwd)||"".equals(zone)||"".equals(distributor)){
                    UtilAssistants.showToast("请填写全部信息");
                    return;
                }

                final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
                dialog.setText("加载中");
                dialog.show();

                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        dialog.dismiss();
                        switch (msg.what){
                            case ThreadState.SUCCESS:
                                RspInfo1 re = (RspInfo1) msg.obj;

                                if(re.isSuccess()){
                                    startActivity(new Intent(mContext,RegisterSuccessActivity.class));
                                    finish();
                                }else {
                                    UtilAssistants.showToast(re.getMsg());
                                }

                                break;
                            case  ThreadState.ERROR:
                                UtilAssistants. showToast("注册失败！");
                                break;
                        }
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserAction action = new UserAction();
                        try {
                            String distributorId ="";
                            if(mDistributorList != null){
                                for (Condition item:mDistributorList
                                        ) {
                                    if(item.getName().equals(distributor)){
                                        distributorId = item.getId();
                                    }
                                }
                            }
                            RspInfo1 result = action.register(mobile,pwd,code,mProvinceId,mCityId,mLocationId,distributorId);
                            handler.obtainMessage(ThreadState.SUCCESS,result).sendToTarget();
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
                final  String mobile = ((EditText)findViewById(R.id.register_phone)).getText()+"";
                if(isMobileNO(mobile) == false){
                    UtilAssistants.showToast("请输入正确的手机号码！");
                    return;
                }
                mc = new MyCount(60000, 1000);//倒计时60秒
                mc.start();

                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what){
                            case ThreadState.SUCCESS:
                                RspInfo1 re = (RspInfo1) msg.obj;
                                if(re.isSuccess() == false){
                                    mc.cancel();
                                    button.setText("获取验证码");
                                }
                                UtilAssistants.showToast(re.getMsg());
                                break;
                            case  ThreadState.ERROR:
                               UtilAssistants. showToast("获取验证失败，请重试！");
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
                            RspInfo1 result = action.sendCode(mobile,"TBEAENG001001002000");
                            handler.obtainMessage(ThreadState.SUCCESS,result).sendToTarget();
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
        if(resultCode == RESULT_OK && requestCode == 100 && data != null){
            String text = data.getStringExtra("text");
            ((TextView)findViewById(R.id.register_zone)).setText(text);
            ((TextView)findViewById(R.id.register_distributor)).setText("");
            mProvinceId = data.getStringExtra("provinceId");
            mCityId = data.getStringExtra("cityId");
            mLocationId = data.getStringExtra("locationId");
        }
    }

    private void  getDistributorList(){
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                           mDistributorList = (List<Condition>) re.getDateObj("distributorlist");
                            if(mDistributorList != null){
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
                                        ((TextView)findViewById(R.id.register_distributor)).setText(option);
                                    }
                                });
                                mPicker.setAnimationStyle(R.style.PopWindowAnimationFade);
                                mPicker.show();
                            }else {
                                UtilAssistants.showToast("操作失败！");
                            }
                        } else {
                            UtilAssistants.showToast("操作失败！");
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！");
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
                    RspInfo re = userAction.getDistributorList(mProvinceId, mCityId, mLocationId);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        if (mc != null){
            mc.cancel();
        }
        super.onDestroy();
    }

    /**
     * 验证手机格式 false不正确
     */
    public  boolean isMobileNO(String mobiles) {
        String telRegex = "[1][3584]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
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
            button.setText("重新发送"+millisUntilFinished / 1000+"秒");

        }
    }
}
