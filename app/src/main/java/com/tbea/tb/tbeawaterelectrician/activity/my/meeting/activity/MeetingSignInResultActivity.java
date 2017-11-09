package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.MeetingAction;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * 会议签到结果
 */

public class MeetingSignInResultActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_signin_result);
        getDate();
    }

    public void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo1 re = (RspInfo1) msg.obj;
                            if (re.isSuccess()) {
//                                ((TextView) findViewById(R.id.suyuan_view_name)).setText(suYuan.getName());
//                                ((TextView) findViewById(R.id.suyuan_view_specifications)).setText(suYuan.getSpecifications());
//                                ((TextView) findViewById(R.id.suyuan_view_manudate)).setText(suYuan.getManudate());
//                                ((TextView) findViewById(R.id.suyuan_view_deliverdate)).setText(suYuan.getDeliverdate());
//                                ((TextView) findViewById(R.id.suyuan_view_manufacture)).setText(suYuan.getManufacture());
                            } else {
                                UtilAssistants.showToast(re.getMsg());
                            }
                        } catch (Exception e) {
                            Log.d(e.getMessage(), "");
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
                    MeetingAction userAction = new MeetingAction();
                    String scanCode = getIntent().getStringExtra("scanCode");
                    RspInfo1
                            re = userAction.getSignInResult(scanCode, MyApplication.instance.getAddrsss());
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
