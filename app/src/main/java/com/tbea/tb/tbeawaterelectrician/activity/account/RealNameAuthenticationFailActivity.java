package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.account.model.RealNameAuthenticationFailResponseModel;
import com.tbea.tb.tbeawaterelectrician.activity.account.model.ResponseInfo;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;

/**
 * 审核失败
 */

public class RealNameAuthenticationFailActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realname_authentication_fail);
        initTopbar("审核拒绝");
        getDate();

        findViewById(R.id.identification_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AccountAuthenticationActivity.class));
                finish();
            }
        });
    }

    /**
     * 获取数据
     */
    public void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        ResponseInfo re = (ResponseInfo) msg.obj;
                        if (re.isSuccess()) {
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            String json = gson.toJson(re.data);
                            RealNameAuthenticationFailResponseModel model = gson.fromJson(json, RealNameAuthenticationFailResponseModel.class);
                            LinearLayout parentLayout = (LinearLayout) findViewById(R.id.realname_authentication_fail_layout);
                            if (model.failedreasonlist != null && model.failedreasonlist.size() > 0) {
                                for (int i = 0; i < model.failedreasonlist.size(); i++) {
                                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_realname_authentication_fail_item, null);
                                    ((TextView) layout.findViewById(R.id.fail_item_index)).setText(i + 1 + "");
                                    ((TextView) layout.findViewById(R.id.fail_item_info)).setText(model.failedreasonlist.get(i).reason);
                                    parentLayout.addView(layout);
                                }
                            }


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
                    ResponseInfo re = userAction.getDentifiedFailInfo();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }


}
