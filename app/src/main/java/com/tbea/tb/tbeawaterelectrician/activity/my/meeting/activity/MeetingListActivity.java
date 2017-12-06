package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.MeetingAction;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model.MeeingListResponseMode;
import com.tbea.tb.tbeawaterelectrician.activity.scanCode.ScanCodeActivity;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 我参与的会议
 */

public class MeetingListActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {
    ImageView mCodeView;
    ImageView mTimeView;
    private BGARefreshLayout mRefreshLayout;
    private ListView mListView;
    private MyAdapter mAdapter;
    private int mPage = 1;

    private String mOrderItem, mOrder, mCodeOrder, mTimeOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeing_list);
        initTopbar("我的会议", "签到", this);
        intiView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.beginRefreshing();
    }

    public void intiView() {
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter(mContext, 0);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));


        mCodeView = (ImageView) findViewById(R.id.meeting_list_code_iv);
        mTimeView = (ImageView) findViewById(R.id.meeting_list_time_iv);

        findViewById(R.id.meeting_list_code_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ("".equals(mCodeOrder) || "asc".equals(mCodeOrder) || mCodeOrder == null) {//升
                    mCodeOrder = "desc";
                    mCodeView.setImageResource(R.drawable.icon_arraw_grayblue);
                } else {
                    mCodeOrder = "asc";
                    mCodeView.setImageResource(R.drawable.icon_arraw_bluegray);
                }
                mOrder = mCodeOrder;
                mOrderItem = "meetingcode";

                mRefreshLayout.beginRefreshing();

                mTimeOrder = "";
                mTimeView.setImageResource(R.drawable.icon_arraw);
            }
        });


        findViewById(R.id.meeting_list_time_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(mTimeOrder) || "asc".equals(mTimeOrder) || mTimeOrder == null) {//升
                    mTimeOrder = "desc";
                    mTimeView.setImageResource(R.drawable.icon_arraw_grayblue);
                } else {
                    mTimeOrder = "asc";
                    mTimeView.setImageResource(R.drawable.icon_arraw_bluegray);
                }
                mOrder = mTimeOrder;

                mCodeOrder = "";
                mCodeView.setImageResource(R.drawable.icon_arraw);
                mOrderItem = "time";

                mRefreshLayout.beginRefreshing();
            }
        });
    }


    /**
     * 从服务器获取数据
     */
    private void getListData() {
        try {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    mRefreshLayout.endRefreshing();
                    mRefreshLayout.endLoadingMore();
                    switch (msg.what) {
                        case ThreadState.SUCCESS:

                            MeeingListResponseMode model = (MeeingListResponseMode) msg.obj;
                            if (model.isSuccess()) {
                                if (model.data.meetinglist != null) {
                                    mAdapter.addAll(model.data.meetinglist);
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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MeetingAction action = new MeetingAction();
                        MeeingListResponseMode model = action.getMeetinList(mOrderItem, mOrder, mPage++, 10);
                        handler.obtainMessage(ThreadState.SUCCESS, model).sendToTarget();
                    } catch (Exception e) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
        mAdapter.clear();
        mPage = 1;
        getListData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        getListData();
        return true;
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(mContext, ScanCodeActivity.class));
    }

    public class MyAdapter extends ArrayAdapter<MeeingListResponseMode.Data.MeetingModel> {
        public MyAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.activity_meeting_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final MeeingListResponseMode.Data.MeetingModel model = getItem(position);
            holder.mCodeView.setText(model.meetingcode);
            holder.mTimeView.setText(model.checkintime);
            holder.mZoneView.setText(model.zone);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MeetingViewActivity.class);
                    intent.putExtra("id", model.id);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView mCodeView;
            TextView mZoneView;
            TextView mTimeView;


            ViewHolder(View view) {
                mCodeView = (TextView) view.findViewById(R.id.metting_item_code);
                mZoneView = (TextView) view.findViewById(R.id.metting_item_zone);
                mTimeView = (TextView) view.findViewById(R.id.metting_item_time);
            }
        }
    }

}
