package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Appeal;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by cy on 2017/2/20.
 */

public class MyAccusationListActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private ListView mListView;
    private MyAdapter mAdapter;
    private  int mPage = 1;
    private int mPagesiz =10 ;
    private BGARefreshLayout mRefreshLayout;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accusation_list);
        initTopbar("我的举报");
        mContext = this;
        initUI();
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
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            List<Map<String,String>> list =  (List<Map<String,String>>) data.get("appeallist");
                            List<Appeal> appeallist = new ArrayList<>();
                            if(list != null){
                                for (int i = 0;i< list.size();i++){
                                    Appeal obj = new Appeal();
                                    obj.setId(list.get(i).get("id"));
                                    obj.setTitle(list.get(i).get("title"));
                                    obj.setAppealtime(list.get(i).get("appealtime"));
                                    obj.setReplycontent(list.get(i).get("replycontent"));
                                    appeallist.add(obj);
                                }
                                mAdapter.addAll(appeallist);
                            }else {
                                if(mPage >1){//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }
                        }else {
                            ToastUtil.showMessage(re.getMsg(), mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败，请重试！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.getMyAccusationList(mPage++,mPagesiz);
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

        private List<Appeal> mList = new ArrayList<>();

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
            FrameLayout view = (FrameLayout) layoutInflater.inflate(
                    R.layout.activity_accusation_list_item, null);
            ((TextView)view.findViewById(R.id.accusation_list_title)).setText(mList.get(position).getTitle());
            ((TextView)view.findViewById(R.id.accusation_list_appealtime)).setText(mList.get(position).getAppealtime());
            if("".equals(mList.get(position).getReplycontent())){
                (view.findViewById(R.id.accusation_list_replycontent)).setVisibility(View.GONE);
            }else {
                ((TextView)view.findViewById(R.id.accusation_list_replycontent)).setText(mList.get(position).getReplycontent());
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext,MyAccusationViewActivity.class);
                    intent.putExtra("id",mList.get(position).getId());
                    startActivity(intent);
                }
            });
            return view;
        }

        public  void addAll(List<Appeal> list){
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
