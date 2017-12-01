package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.MeetingAction;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model.MeetingViewResponseModel;
import com.tbea.tb.tbeawaterelectrician.component.CircleImageView;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;

/**
 * 会议详情
 */

public class MeetingViewActivity extends TopActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_view);
        initTopbar("会议详情");
        getData();

    }

    private void getData() {
        final String id = getIntent().getStringExtra("id");
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        try {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    dialog.dismiss();
                    switch (msg.what) {
                        case ThreadState.SUCCESS:
                            MeetingViewResponseModel model = (MeetingViewResponseModel) msg.obj;
                            if (model.isSuccess()) {
                                if (model.data != null) {
                                    if (model.data.meetingbaseinfo != null) {
                                        MeetingViewResponseModel.Data.Meetingbaseinfo meetingbaseinfo = model.data.meetingbaseinfo;
                                        ((TextView) findViewById(R.id.meeting_view_code)).setText(meetingbaseinfo.meetingcode);
                                        ((TextView) findViewById(R.id.meeting_view_meetingtime)).setText(meetingbaseinfo.meetingtime);
                                        ((TextView) findViewById(R.id.meeting_view_meetingplace)).setText(meetingbaseinfo.meetingplace);
                                    }
                                    //举办单位
                                    if (model.data.organizecompanylist != null) {
                                        LinearLayout holdMonadView = (LinearLayout) findViewById(R.id.meeting_view_organizecompanylist);
                                        for (MeetingViewResponseModel.Data.OrganizeCompanyModel item : model.data.organizecompanylist) {
                                            View pernsonLayout = getLayoutInflater().inflate(R.layout.activity_person_layout2, null);
                                            CircleImageView headView = (CircleImageView) pernsonLayout.findViewById(R.id.person_info_head);
                                            ImageView typwView = (ImageView) pernsonLayout.findViewById(R.id.person_info_personjobtitle);
                                            ImageView rightView = (ImageView) pernsonLayout.findViewById(R.id.person_info_right);
                                            TextView nameView = (TextView) pernsonLayout.findViewById(R.id.person_info_name);
                                            TextView companyNameView = (TextView) pernsonLayout.findViewById(R.id.person_info_companyname);
                                            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + item.masterthumbpicture, headView);
                                            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + item.companytypeicon, typwView);
                                            nameView.setText(item.mastername);
                                            companyNameView.setText(item.name);
                                            rightView.setVisibility(View.GONE);
                                            holdMonadView.addView(pernsonLayout);
                                        }
                                    }

                                    if (model.data.checkininfo != null) {//签到信息
                                        MeetingViewResponseModel.Data.Checkininfo obj = model.data.checkininfo;
                                        ((TextView) findViewById(R.id.meeting_view_checkintime)).setText(obj.checkintime);
                                        ((TextView) findViewById(R.id.meeting_view_checkinplace)).setText(obj.checkinplace);
                                    }

                                }
                            } else {
                                ToastUtil.showMessage(model.getMsg(),mContext);
                            }
                            break;
                        case ThreadState.ERROR:
                            ToastUtil.showMessage("操作失败！",mContext);
                            break;
                    }
                }
            };


            new

                    Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MeetingAction action = new MeetingAction();
                        MeetingViewResponseModel model = action.getPlumberMeetingView(id);
                        handler.obtainMessage(ThreadState.SUCCESS, model).sendToTarget();
                    } catch (Exception e) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    }
                }
            }).

                    start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
