package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.Map;

/**
 * 实名认证状态显示
 */

public class RealNameAuthenticationActivity extends TopActivity {
    String mState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realname_authentication_show);
        initTopbar("实名认证");

        findViewById(R.id.identification_state_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = ShareConfig.getConfigString(mContext, Constants.WHETHERIDENTIFIEDID, "identifyfailed");
                if ("identifyfailed".equals(state)) {//认证失败
                    startActivity(new Intent(mContext, RealNameAuthenticationFailActivity.class));

                } else {//已通过和审核中的，就显示认证信息
                    startActivity(new Intent(mContext, AccountIdentifiedActivity.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDate();
    }

    private void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, String> useridentifyinfo = (Map<String, String>) re.getDateObj("useridentifyinfo");
                            ((TextView) findViewById(R.id.identification_name_tv)).setText(useridentifyinfo.get("realname"));
                            ((TextView) findViewById(R.id.identification_cardId_tv)).setText(useridentifyinfo.get("personcardid"));
                            ((TextView) findViewById(R.id.identification_state_tv)).setText(useridentifyinfo.get("identifystatus"));
                            mState = useridentifyinfo.get("identifystatusid");
                            ImageView iv = (ImageView) findViewById(R.id.identification_image);
                            TextView tittleView = (TextView) findViewById(R.id.identification_title);
                            if ("identifyfailed".equals(mState)) {//认证失败
                                iv.setImageResource(R.drawable.icon_my_relaname_unapprove);
                                tittleView.setText("你未通过实名认证");

                            } else if ("identifying".equals(mState)) {//正在认证
                                iv.setImageResource(R.drawable.icon_my_relaname_audit);
                                tittleView.setText("证件审核中");
                            } else {//通过
                                iv.setImageResource(R.drawable.icon_my_relaname_verified);
                                tittleView.setText("你已通过实名认证");
                            }
                        } else {
                            ToastUtil.showMessage(re.getMsg(), mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败!", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getAccountAuthentication();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
