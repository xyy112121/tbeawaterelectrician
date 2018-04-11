package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * Created by cy on 2017/1/19.修改密码
 */

public class PwdEditActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_edit);
        initTopbar("修改登录密码");
        MyApplication.instance.addActivity(PwdEditActivity.this);
        listener();
    }

    public void listener() {
        findViewById(R.id.pwd_edit_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpwd = ((TextView) findViewById(R.id.pwd_edit_old)).getText() + "";
                String newPwd = ((TextView) findViewById(R.id.pwd_edit_new)).getText() + "";
                String confirmPwd = ((TextView) findViewById(R.id.pwd_edit_confirm)).getText() + "";
                if ("".equals(oldpwd)) {
                    ToastUtil.showMessage("当前密码不能为空！", mContext);
                    return;
                }

                if ("".equals(newPwd)) {
                    ToastUtil.showMessage("新密码不能为空！", mContext);
                    return;
                }

                if (newPwd.length() < 6 || newPwd.length() > 10) {
                    ToastUtil.showMessage("密码长度6到10位！", mContext);
                    return;
                }

                if (!newPwd.equals(confirmPwd)) {
                    ToastUtil.showMessage("两次密码不一致！", mContext);
                    return;
                }
                updatePwd(oldpwd, newPwd);
            }
        });

    }

    public void updatePwd(final String oldPwd, final String newPwd) {
        final CustomDialog dialog = new CustomDialog(PwdEditActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Intent intent = new Intent(PwdEditActivity.this, BindingNewPhoneFinishActivity.class);
                            intent.putExtra("title", "密码修改成功");
                            intent.putExtra("title1", "密码修改成功");
                            startActivity(intent);
                            finish();
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
                    RspInfo1 re = userAction.updateNewPwd(oldPwd, newPwd);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
