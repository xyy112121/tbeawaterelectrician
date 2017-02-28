package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Receive;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 收支记录
 */

public class WalletIncomeAndExpensesActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private Context mContext;
    private ListView mListView;
    private MyAdapter mAdapter;
    private  int mPage = 1;
    private int mPagesiz =10 ;
    private BGARefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_list);
        mContext = this;
        initTopbar("收支明细");
        initUI();
    }

    /**
     * 实例化组件
     */
    private void initUI() {
        mListView = (ListView)findViewById(R.id.my_wallet_listview);
        mAdapter = new MyAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));
        mRefreshLayout.beginRefreshing();
    }

    /**
     * 获取数据
     */
    public void getDate(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<Receive> receiveList = (List<Receive>)re.getDateObj("receivelist");
                            if(receiveList != null){
                                mAdapter.addAll(receiveList);
                            }else {
                                if(mPage >1){//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }
                        }else {
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
                    RspInfo re = userAction.getPayRevenue(mPage++,mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mAdapter.removeAll();
        mPage = 1;
        getDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getDate();
        return true;
    }

    private class MyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;

        private List<Receive> mList = new ArrayList<>();

        /**
         * 构造函数
         *
         * @param context
         *            android上下文环境
         */
        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = (View) layoutInflater.inflate(
                    R.layout.activity_wallet_income_expenses_list_item, null);
            ((TextView)view.findViewById(R.id.wallet_income_expences_event)).setText(mList.get(position).getEvent());
            if("exchangecommodity".equals(mList.get(position).getEventid()) || "ordercommodity".equals(mList.get(position).getEventid())){
                ((TextView)view.findViewById(R.id.wallet_income_expences_event)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
            }else if("takemoney".equals(mList.get(position).getEventid()) || "givemoneyforscore".equals(mList.get(position).getEventid())){
                ((TextView)view.findViewById(R.id.wallet_income_expences_event)).setTextColor(ContextCompat.getColor(mContext,R.color.head_color));
            }if("appealaward".equals(mList.get(position).getEventid())){
                ((TextView)view.findViewById(R.id.wallet_income_expences_event)).setTextColor(ContextCompat.getColor(mContext,R.color.orange));
            }

            if("+".equals(mList.get(position).getOptype())){
                ((TextView)view.findViewById(R.id.wallet_income_expences_thisvalue)).setTextColor(ContextCompat.getColor(mContext,R.color.red));
            }else {
                ((TextView)view.findViewById(R.id.wallet_income_expences_thisvalue)).setTextColor(ContextCompat.getColor(mContext,R.color.green));
            }
            ((TextView)view.findViewById(R.id.wallet_income_expences_thisvalue)).setText(mList.get(position).getThisvalue());
            ((TextView)view.findViewById(R.id.wallet_income_expences_time)).setText(mList.get(position).getTime());
            ((TextView)view.findViewById(R.id.wallet_income_expences_currenttotlemoney)).setText("￥"+mList.get(position).getCurrenttotlemoney());
            return view;
        }

        public  void addAll(List<Receive> list){
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }
}
