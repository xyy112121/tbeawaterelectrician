package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * Created by user on 2017/3/13.
 */

public class ForgetPwdEditActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_edit);
        initTopbar("重置密码");
        mContext = this;
        findViewById(R.id.activity_old_pwd_layout).setVisibility(View.GONE);
        findViewById(R.id.activity_old_pwd_view).setVisibility(View.GONE);
        listener();
    }

    public  void listener(){
        findViewById(R.id.pwd_edit_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPwd = ((TextView)findViewById(R.id.pwd_edit_new)).getText()+"";
                String confirmPwd = ((TextView)findViewById(R.id.pwd_edit_confirm)).getText()+"";

                if("".equals(newPwd)){
                    UtilAssistants.showToast("新密码不能为空！",mContext);
                    return;
                }

                if(newPwd.length() < 6 || newPwd.length() > 10){
                    UtilAssistants.showToast("密码长度6到10位！",mContext);
                    return;
                }



                if(!newPwd.equals(confirmPwd)){
                    UtilAssistants.showToast("两次密码不一致！",mContext);
                    return;
                }
                String mobile = getIntent().getStringExtra("mobile");
                updatePwd(mobile,newPwd);
            }
        });

    }

    public  void updatePwd(final String mobile, final String newPwd){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Intent intent = new Intent(mContext,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            UtilAssistants.showToast(re.getMsg(),mContext);
                        }
                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！",mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.resetPwd(mobile,newPwd);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
