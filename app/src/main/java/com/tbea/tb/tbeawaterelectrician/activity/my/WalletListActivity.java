package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Receive;
import com.tbea.tb.tbeawaterelectrician.fragment.my.WalletPayFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.my.WalletRevenueFragment;
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
 * 我的钱包
 */

public class WalletListActivity extends TopActivity implements View.OnClickListener,BGARefreshLayout.BGARefreshLayoutDelegate{
    private Context mContext;
    private ListView mListView;
    private MyAdapter mAdapter;
    private  int mPage = 1;
    private int mPagesiz =10 ;
    private BGARefreshLayout mRefreshLayout;
    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_list);
        initTopbar("我的钱包","收支明细",this);
        mContext = this;
        initUI();
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
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            if(isFirst == true){
                                LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_wallet_list_head,null);
                                mListView.addHeaderView(layout);
                                FrameLayout layout1 = (FrameLayout)getLayoutInflater().inflate(R.layout.activity_wallet_list_item_head,null);
                                mListView.addHeaderView(layout1);
                                findViewById(R.id.my_wallet_list_withdraw_cash).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(WalletListActivity.this,WalletWithdrawCashActivity.class));
                                    }
                                });
                            }
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String,String> mymoneyinfo =  (Map<String,String>) data.get("mymoneyinfo");
                            if(mymoneyinfo != null){
                                ((TextView)findViewById(R.id.my_wallet_list_currentmoney)).setText("￥ "+mymoneyinfo.get("currentmoney"));
                            }
                            List<Map<String,String>> list =  (List<Map<String,String>>) data.get("nottakemoneylist");
                            List<Receive> receiveList = new ArrayList<>();
                            if(list != null){
                                for (int i = 0;i< list.size();i++){
                                    Receive obj = new Receive();
                                    obj.setId(list.get(i).get("id"));
                                    obj.setEvent(list.get(i).get("takemoneycode"));
                                    obj.setMoney(list.get(i).get("money"));
                                    obj.setTime(list.get(i).get("validexpiredtime"));
                                    receiveList.add(obj);
                                }
                                mAdapter.addAll(receiveList);
                            }else {
                                if(mPage >1){//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }
                            isFirst = false;
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
                    RspInfo1 re = userAction.getWalletRevenueList(mPage++,mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
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

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPage = 1;
        mAdapter.removeAll();
        getDate();

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getDate();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.beginRefreshing();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = (View) layoutInflater.inflate(
                    R.layout.activity_wallet_list_item, null);
            ((TextView)view.findViewById(R.id.wallet_item_takemoneycode)).setText(mList.get(position).getEvent());
            ((TextView)view.findViewById(R.id.wallet_item_takemoneycode)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((TextView)view.findViewById(R.id.wallet_item_validexpiredtime)).setText(mList.get(position).getTime());
            ((TextView)view.findViewById(R.id.wallet_item_money)).setText(mList.get(position).getMoney());

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_delete_dialog);
                    dialog.setText("删除后提现二维码将会失效");
                    dialog.setText2("如需提现请重新生成二维码");
                    dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    },"取消");
                    dialog.setCancelBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            delect(mList.get(position).getId());
                        }
                    },"删除");
                    dialog.show();
                    return  false;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext,WalletWithdrawCashViewActivity.class);
                    intent.putExtra("money","");
                    intent.putExtra("takemoneycodeid",mList.get(position).getId());
                    startActivity(intent);
                }
            });
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

    /**
     * 删除数据
     */
    public void delect(final String id){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            UtilAssistants.showToast(re.getMsg());
                            mRefreshLayout.beginRefreshing();
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
                    RspInfo1 re = userAction.delectTakeMoneyCodeId(id);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(mContext,WalletIncomeAndExpensesActivity.class));

    }
}
