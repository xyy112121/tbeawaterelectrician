package com.tbea.tb.tbeawaterelectrician.fragment.account;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.account.Register2Activity;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;

/**
 * Created by abc on 16/12/15.手机号注册
 */

public class RegisterPhoneFragment extends Fragment {
    private MyCount mc;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_register1,null);
        button = (Button)view.findViewById(R.id.regist_sendcode);
        listener(view);
        return view;
    }

    public void listener(final View view){
        view.findViewById(R.id.register_next_bth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  String mobile = ((EditText)view.findViewById(R.id.regist_phone)).getText()+"";
                final  String code = ((EditText)view.findViewById(R.id.regist_identifying_code)).getText()+"";
                final  String pwd = ((EditText)view.findViewById(R.id.regist_pwd)).getText()+"";

//                if(isMobileNO(mobile) == false){
//                    showToast("请正确输入手机号码");
//                    return;
//                }
//                if(code.equals("")){
//                    showToast("请输入验证码");
//                    return;
//                }
//                if(pwd.equals("")){
//                    showToast("请输入密码");
//                    return;
//                }
                Register2Activity activity = ((Register2Activity)getActivity());
                activity.mObj.setMobile(mobile);
                activity.mObj.setPassword(pwd);
                activity.mObj.setVerifycode(code);
                if(activity.mRealNameVerifyFragment == null){
                    activity.mRealNameVerifyFragment = new RealNameVerifyFragment();
                }
                ((Register2Activity)getActivity()).switchFragment(RegisterPhoneFragment.this,activity.mRealNameVerifyFragment,"");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  String mobile = ((EditText)view.findViewById(R.id.regist_phone)).getText()+"";
                if(isMobileNO(mobile) == false){
                    showToast("请输入正确的手机号码！");
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
                               showToast(re.getMsg());
                               break;
                           case  ThreadState.ERROR:
                               showToast("获取验证失败，请重试！");
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
    public void onDestroy() {
        mc.cancel();
        super.onDestroy();
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

    public void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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


