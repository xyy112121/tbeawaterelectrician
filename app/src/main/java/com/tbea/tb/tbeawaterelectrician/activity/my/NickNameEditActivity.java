package com.tbea.tb.tbeawaterelectrician.activity.my;

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
 * Created by programmer on 2017/9/30.
 */

public class NickNameEditActivity extends TopActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_edit);
        mContext = this;
        initTopbar("更改昵称");
        ((TextView) findViewById(R.id.email_edit_code_tv)).setText("昵称");
        ((TextView) findViewById(R.id.email_edit_code)).setHint("请输入昵称");
        String code = getIntent().getStringExtra("code");
        ((TextView) findViewById(R.id.email_edit_code)).setText(code);
        listener();
    }

    /**
     * 事件
     */
    private void listener() {
        findViewById(R.id.email_edit_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nickName = ((TextView) findViewById(R.id.email_edit_code)).getText() + "";
                if (!"".equals(nickName)) {
                    final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
                    dialog.setText("请等待");
                    dialog.show();
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            dialog.dismiss();
                            switch (msg.what) {
                                case ThreadState.SUCCESS:
                                    RspInfo1 re = (RspInfo1) msg.obj;
                                    if (re.isSuccess()) {
                                        Intent intent = new Intent();
                                        intent.putExtra("code", nickName);
                                        setResult(RESULT_OK, intent);
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
                                RspInfo1 re = userAction.updateInfo(nickName, "", "", "","", "");
                                handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                            } catch (Exception e) {
                                handler.sendEmptyMessage(ThreadState.ERROR);
                            }
                        }
                    }).start();
                } else {
                    UtilAssistants.showToast("请输入昵称！");
                }

            }
        });

    }

}