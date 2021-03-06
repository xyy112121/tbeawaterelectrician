package com.tbea.tb.tbeawaterelectrician.fragment.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.entity.Receive;
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
 * Created by cy on 2016/12/19.钱包支出列表
 */

public class WalletPayFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private View mView;
    private ListView mListView;
    private MyAdapter mAdapter;
    private  int mPage = 1;
    private int mPagesiz =10 ;
    private BGARefreshLayout mRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wallet_list, container, false);
        initUI();//实例化控件
//        isPrepared = true;
        mRefreshLayout.beginRefreshing();
        return mView;
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
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            List<Map<String,String>> list =  (List<Map<String,String>>) data.get("spentlist");
                            List<Receive> receiveList = new ArrayList<>();
                            if(list != null){
                                for (int i = 0;i< list.size();i++){
                                    Receive obj = new Receive();
                                    obj.setId(list.get(i).get("id"));
                                    obj.setEvent(list.get(i).get("event"));
                                    obj.setMoney(list.get(i).get("money"));
                                    obj.setTime(list.get(i).get("time"));
                                    receiveList.add(obj);
                                }
                                mAdapter.addAll(receiveList);
                            }else {
                                if(mPage >1){//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }
                        }else {
                            UtilAssistants.showToast(re.getMsg(),getActivity());
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！",getActivity());
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.getWalletPayList(mPage++,mPagesiz);
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
        mListView = (ListView)mView.findViewById(R.id.my_wallet_listview);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout)mView.findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
        mPage = 1;
        mAdapter.removeAll();
        getDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
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
                    R.layout.fragment_wallet_list_item, null);
            ((TextView)view.findViewById(R.id.wallet_list_item_event)).setText(mList.get(position).getEvent());
            ((TextView)view.findViewById(R.id.wallet_list_item_time)).setText(mList.get(position).getTime());
            ((TextView)view.findViewById(R.id.wallet_list_item_money)).setText(mList.get(position).getMoney());
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

