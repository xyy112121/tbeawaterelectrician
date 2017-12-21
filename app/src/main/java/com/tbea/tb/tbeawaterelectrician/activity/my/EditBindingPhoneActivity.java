package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * Created by cy on 2017/1/19.更改绑定手机号(老的号码获取验证码)
 */

public class EditBindingPhoneActivity extends TopActivity {
    private Context mContext;
    private Button button;
    private MyCount mc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_binding_phone_edit);
        initTopbar("更改绑定手机号");
        String phone = getIntent().getStringExtra("phone");
        ((EditText)findViewById(R.id.myphone_edit_old_phone)).setText(phone);
        button = (Button)findViewById(R.id.send_code);
        mContext = this;
        listener();
    }

    private void  listener(){
        findViewById(R.id.myphone_edit_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = ((EditText)findViewById(R.id.myphone_edit_old_phone)).getText()+"";
                String code = ((EditText)findViewById(R.id.myphone_edit_code)).getText()+"";
                updateOldPhone(mobile,code);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  String mobile = ((EditText)findViewById(R.id.myphone_edit_old_phone)).getText()+"";
                if(isMobileNO(mobile) == false){
                    ToastUtil.showMessage("请输入正确的手机号码", mContext);
                    return;
                }
                mc = new MyCount(60000, 1000);//倒计时60秒
                mc.start();

                @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what){
                            case ThreadState.SUCCESS:
                                RspInfo1 re = (RspInfo1) msg.obj;
                                if(re.isSuccess() == false){
                                    mc.cancel();
                                    button.setText("获取验证码");
                                }
                                ToastUtil.showMessage(re.getMsg(), mContext);
                                break;
                            case  ThreadState.ERROR:
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
                            RspInfo1 result = action.sendCode(mobile,"TBEAENG005001005000");
                            handler.obtainMessage(ThreadState.SUCCESS,result).sendToTarget();
                        } catch (Exception e) {
                            handler.sendEmptyMessage(ThreadState.ERROR);
                        }
                    }
                }).start();
            }
        });
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
        String telRegex = "[1][73458]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
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

    public  void updateOldPhone(final String mobile, final String verifycode){
        if(isMobileNO(mobile) == false){
            ToastUtil.showMessage("请输入正确的手机号码！", mContext);
            return;
        }
        if("".equals(verifycode)){
            ToastUtil.showMessage("验证码不能为空！", mContext);
            return;
        }
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();

        @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Intent intent = new Intent(mContext,BindingNewPhoneActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
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
                    RspInfo1 re = userAction.updateOldPhone(mobile,verifycode);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
