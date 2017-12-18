package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.Map;

/**
 * 认证通过和认证中
 */

public class AccountIdentifiedActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_identified);
        initTopbar("实名认证");
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
                            ImageView imageView1 = (ImageView) findViewById(R.id.identification_personidcard1_tv);
                            ImageView imageView2 = (ImageView) findViewById(R.id.identification_personidcard2_tv);
                            ImageView imageView3 = (ImageView) findViewById(R.id.identification_personidcard3_tv);
                            if (!TextUtils.isEmpty(useridentifyinfo.get("personidcard1")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + useridentifyinfo.get("personidcard1"), imageView1);
                            if (!TextUtils.isEmpty(useridentifyinfo.get("personidcard2")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + useridentifyinfo.get("personidcard2"), imageView2);
                            if (!TextUtils.isEmpty(useridentifyinfo.get("personidcardwithperson")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + useridentifyinfo.get("personidcardwithperson"), imageView3);

                        } else {
                            UtilAssistants.showToast(re.getMsg(), mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！", mContext);
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
