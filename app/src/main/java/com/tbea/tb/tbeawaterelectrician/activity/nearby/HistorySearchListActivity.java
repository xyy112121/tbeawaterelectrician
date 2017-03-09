package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Commodith;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 商品搜索结果
 */

public class HistorySearchListActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private BGARefreshLayout mRefreshLayout;
    private ListView mListView;
    private MyAdapter mAdapter;
    private String mKeyword ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_search_list);
        initTopbar("搜索结果");
        mKeyword = getIntent().getStringExtra("keyword");
        mListView = (ListView)findViewById(R.id.listview);
        mAdapter = new MyAdapter(HistorySearchListActivity.this);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(HistorySearchListActivity.this, true));
        mRefreshLayout.beginRefreshing();
    }

    /**
     * 获取数据
     */
    public void getListDate(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<Map<String,String>> list =  (List<Map<String,String>>) re.getDateObj("commoditylist");
                            List<Commodith> companyList = new ArrayList<>();
                            if(list != null){
                                for (int i = 0;i< list.size();i++){
                                    Commodith obj = new Commodith();
                                    obj.setId(list.get(i).get("id"));
                                    obj.setPicture(list.get(i).get("picture"));
                                    obj.setName(list.get(i).get("name"));
                                    obj.setPrice(list.get(i).get("price"));
                                    obj.setDistance(list.get(i).get("distance"));
                                    obj.setSpecification(list.get(i).get("specification"));
                                    obj.setCompanyid(list.get(i).get("companyid"));
                                    obj.setCompanytypeid(list.get(i).get("companytypeid"));
                                    companyList.add(obj);
                                }
                                mAdapter.addAll(companyList);
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
                    RspInfo re = userAction.getSearchList("1",mKeyword);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
        mAdapter.removeAll();
        getListDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        return false;
    }
    private class MyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;

        public List<Commodith> mList = new ArrayList<>();

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
            FrameLayout view = (FrameLayout) layoutInflater.inflate(
                    R.layout.fragment_nearby_purchase_item_layout, null);
            final  Commodith obj = mList.get(position);
            ImageView imageView = (ImageView)view.findViewById(R.id.nearby_commdith_item_picture);
            if(!obj.getPicture().equals("")){
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getPicture(),imageView);
            }
            ((TextView)view.findViewById(R.id.nearby_commdith_item_name)).setText(obj.getName());
            ((TextView)view.findViewById(R.id.nearby_commdith_item_specification)).setText(obj.getSpecification());
            ((TextView)view.findViewById(R.id.nearby_commdith_item_distance)).setText(obj.getDistance());
            ((TextView)view.findViewById(R.id.nearby_commdith_item_price)).setText("￥"+obj.getPrice());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommodithViewActivity.class);
                    intent.putExtra("id",obj.getId());
                    intent.putExtra("companytypeid",obj.getCompanytypeid());
                    intent.putExtra("companyid",obj.getCompanyid());
                    startActivity(intent);
                }
            });
            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }

        public void addAll(List<Commodith> list){
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }


    }
}
