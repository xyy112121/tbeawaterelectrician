package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Commodith;
import com.tbea.tb.tbeawaterelectrician.entity.NearbyCompany;
import com.tbea.tb.tbeawaterelectrician.entity.SearchResponseModuel;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 商品搜索结果
 */

public class HistorySearchListActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private BGARefreshLayout mRefreshLayout;
    private ListView mListView;
    private MyAdapter mAdapter;
    private String mKeyword = "";
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_search_list);
        initTopbar("搜索结果");
        mKeyword = getIntent().getStringExtra("keyword");
        mType = getIntent().getStringExtra("type");
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter(HistorySearchListActivity.this);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(HistorySearchListActivity.this, true));
        mRefreshLayout.beginRefreshing();
    }

    /**
     * 获取数据
     */
    public void getListDate() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            if (re.getData() != null) {
                                Gson gson = new GsonBuilder().serializeNulls().create();
                                String json = gson.toJson(re.getData());
                                SearchResponseModuel moduel = gson.fromJson(json, SearchResponseModuel.class);
                                if (moduel.list != null) {
                                    mAdapter.addAll(moduel.list);
                                }
                            }

                        } else {
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
                    RspInfo1 re = userAction.getSearchList(mType, mKeyword);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
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

        public List<SearchResponseModuel.SearchModel> mList = new ArrayList<>();

        /**
         * 构造函数
         *
         * @param context android上下文环境
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
            View view;
            if ("commodity".equals(mType)) {
                view = layoutInflater.inflate(
                        R.layout.fragment_nearby_purchase_item_layout, null);
                final SearchResponseModuel.SearchModel obj = mList.get(position);
                ImageView imageView = (ImageView) view.findViewById(R.id.nearby_commdith_item_picture);
                if (!TextUtils.isEmpty(obj.picture)) {
                    ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.picture, imageView);
                }
                ((TextView) view.findViewById(R.id.nearby_commdith_item_name)).setText(obj.name);
                ((TextView) view.findViewById(R.id.nearby_commdith_item_specification)).setText(obj.specification);
                ((TextView) view.findViewById(R.id.nearby_commdith_item_distance)).setText(obj.distance);
                ((TextView) view.findViewById(R.id.nearby_commdith_item_price)).setText("￥" + obj.price);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CommodithViewActivity.class);
                        intent.putExtra("id", obj.id);
                        intent.putExtra("companytypeid", obj.companytypeid);
                        intent.putExtra("companyid", obj.companyid);
                        startActivity(intent);
                    }
                });
            } else {
                view = layoutInflater.inflate(
                        R.layout.fragment_nearby_franchiser_item_layout, null);
                final SearchResponseModuel.SearchModel obj = mList.get(position);
                ImageView imageView = (ImageView) view.findViewById(R.id.nearby_company_item_picture);
                if (!TextUtils.isEmpty(obj.picture)) {
                    ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.picture, imageView);
                }
                TextView nameView = (TextView) view.findViewById(R.id.nearby_company_item_name);
                nameView.setText(obj.name);
                ((TextView) view.findViewById(R.id.nearby_company_item_distance)).setText(obj.distance);
                ((TextView) view.findViewById(R.id.nearby_company_item_addr)).setText(obj.address);
                if (obj.withcompanyidentified.equals("1")) {
                    Drawable nav_up = ContextCompat.getDrawable(context, R.drawable.icon_attestations);
                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                    nameView.setCompoundDrawables(null, null, nav_up, null);
                }
                if (obj.withcompanylisence.equals("1")) {
                    (view.findViewById(R.id.nearby_company_item_withcompanylisence)).setVisibility(View.VISIBLE);
                }
                if (obj.withguaranteemoney.equals("1")) {
                    (view.findViewById(R.id.nearby_company_item_withguaranteemoney)).setVisibility(View.VISIBLE);
                }
                if (obj.withidentified.equals("1")) {
                    (view.findViewById(R.id.nearby_company_item_withidentified)).setVisibility(View.VISIBLE);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("firstleveldistributor".equals(obj.companytypeid)) {
                            Intent intent = new Intent(mContext, FranchiserViewActivity.class);
                            if ("distributor".equals(mType)) { //经销商
                                NearbyCompany company = new NearbyCompany();
                                company.setId(obj.id);
                                company.setPicture(obj.picture);
                                company.setName(obj.name);
                                company.setDistance(obj.distance);
                                company.setDistance(obj.distance);
                                company.setLatitude(obj.latitude);
                                company.setLongitude(obj.longitude);
                                company.setAddress(obj.address);
                                company.setCompanytypeid(obj.companytypeid);
                                company.setWithcompanyidentified(obj.withcompanyidentified);
                                company.setWithcompanylisence(obj.withcompanylisence);
                                company.setWithguaranteemoney(obj.withguaranteemoney);
                                company.setWithidentified(obj.withidentified);
                                Gson gson = new Gson();
                                String objGson = gson.toJson(company);
                                intent.putExtra("obj", objGson);
                            } else {
                                intent.putExtra("companyId", obj.id);//商家
                            }
                            //总经销商
                            startActivity(intent);
                        } else {
                            //经销商
                            Intent intent = new Intent(mContext, DistributorViewAcitivty.class);
                            intent.putExtra("id", obj.id);
                            startActivity(intent);
                        }
                    }
                });

            }

            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }

        public void addAll(List<SearchResponseModuel.SearchModel> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }


    }
}
