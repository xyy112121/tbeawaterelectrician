package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.MeetingAction;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model.MeetingSignInResponseModel;
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

        findViewById(R.id.meeting_signin_result_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                            MeetingSignInResponseModel re = (MeetingSignInResponseModel) msg.obj;
                            ImageView pic = (ImageView) findViewById(R.id.meeting_signin_result_picture);
                            ((TextView) findViewById(R.id.meeting_signin_result_title)).setText(re.getMsg());
                            if (re.isSuccess()) {

                                if (re.data != null && re.data.meetingcheckininfo != null) {
                                    MeetingSignInResponseModel.Data.Meetingcheckininfo info = re.data.meetingcheckininfo;
                                    if("1".equals(info.checkstatus)){
                                        initTopbar("签到成功");
                                    }else {
                                        pic.setImageResource(R.drawable.icon_failure);
                                        initTopbar("签到失败");
                                    }
                                    ((TextView) findViewById(R.id.meeting_signin_result_time)).setText("签到时间：" + re.data.meetingcheckininfo.checkintime);
                                    ((TextView) findViewById(R.id.meeting_signin_result_addr)).setText("签到地点：" + re.data.meetingcheckininfo.checkinplace);
                                }
                            } else {
                                initTopbar("签到失败");
                                pic.setImageResource(R.drawable.icon_failure);
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
                    MeetingSignInResponseModel
                            re = userAction.getSignInResult(scanCode, MyApplication.instance.getAddrsss());
                    if (re == null) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    } else {
                        handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
