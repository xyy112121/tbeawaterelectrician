package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.activity.MeetingSignInResultActivity;
import com.tbea.tb.tbeawaterelectrician.activity.scanCode.model.ProvingScanCodeResponseModel;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cy on 2017/1/16.条形码输入界面
 */

public class CodeInputActivity extends TopActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_input);
        initTopbar("输入编码");

        findViewById(R.id.scan_code_input_comfire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = ((TextView) findViewById(R.id.scan_code_input_text)).getText() + "";
                if ("".equals(result)) {
                    UtilAssistants.showToast("请输入编码！",mContext);
                    return;
                }

                provingScanCode("tbscrfl_" + result);
            }
        });
    }

    public void provingScanCode(final String result) {
        final CustomDialog dialog = new CustomDialog(CodeInputActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Object date = re.getData();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            String json = gson.toJson(date);
                            ProvingScanCodeResponseModel model = gson.fromJson(json, ProvingScanCodeResponseModel.class);
                            if (model.qrtypeinfo != null) {
                                Intent intent = new Intent();
                                String type = model.qrtypeinfo.qrtype;
                                if (type.equals("meetingcheckin")) {//会议签到成功页面
                                    intent.setClass(mContext, MeetingSignInResultActivity.class);
                                } else if (type.equals("scanrebate")) {//扫码返利信息页面
                                    intent.setClass(mContext, ScanCodeViewActivity.class);
                                    intent.putExtra("type", "net");
                                } else if (type.equals("verifytbeaproduct")) {//扫码溯源页面
                                    intent.setClass(mContext, SuYuanViewActivity.class);
                                }
                                intent.putExtra("scanCode", result);
                                startActivity(intent);
                                finish();
                            }

                        } else {
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
                    RspInfo1 re = userAction.provingScanCode(result);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
