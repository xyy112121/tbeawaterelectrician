package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    ImageView mTitlePic;
    TextView mTitleView;
    TextView mTimeView;
    TextView mAddrView;
    private String mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_signin_result);
        mTitlePic = (ImageView) findViewById(R.id.meeting_signin_result_picture);
        mTitleView = (TextView) findViewById(R.id.meeting_signin_result_title);
        mTimeView = (TextView) findViewById(R.id.meeting_signin_result_time);
        mAddrView = (TextView) findViewById(R.id.meeting_signin_result_addr);
        getDate();

        findViewById(R.id.meeting_signin_result_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.meeting_signin_result_addr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MeetingViewActivity.class);
                intent.putExtra("id", mId);
                startActivity(intent);
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
                            RspInfo1 re = (RspInfo1) msg.obj;
                            mTitleView.setText(re.getMsg());
                            if (re.isSuccess()) {
                                Gson gson = new GsonBuilder().serializeNulls().create();
                                String json = gson.toJson(re.getData());
                                MeetingSignInResponseModel model = (gson.fromJson(json, MeetingSignInResponseModel.class));
                                if (model != null) {
                                    MeetingSignInResponseModel.Meetingcheckininfo info = model.meetingcheckininfo;
                                    if ("1".equals(info.checkstatus)) {
                                        initTopbar("签到成功");
                                    } else {
                                        mTitlePic.setImageResource(R.drawable.icon_failure);
                                        initTopbar("签到失败");
                                    }
                                    mId = info.meetingid;
                                    mTimeView.setVisibility(View.VISIBLE);
                                    mAddrView.setVisibility(View.VISIBLE);
                                    mTimeView.setText("签到时间：" + info.checkintime);
                                    mAddrView.setText("签到地点：" + info.checkinplace);
                                }
                            } else {
                                initTopbar("签到失败");
                                mTitlePic.setImageResource(R.drawable.icon_failure);
                            }

                        } catch (Exception e) {
                            initTopbar("签到失败");
                            mTitlePic.setImageResource(R.drawable.icon_failure);
                            mTitleView.setText("签到失败");
                        }

                        break;
                    case ThreadState.ERROR:
                        initTopbar("签到失败");
                        mTitlePic.setImageResource(R.drawable.icon_failure);
                        mTitleView.setText("签到失败");
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
                    RspInfo1 re = userAction.getSignInResult(scanCode, MyApplication.instance.getAddrsss());
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
