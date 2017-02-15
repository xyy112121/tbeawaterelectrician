package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Commodith;
import com.tbea.tb.tbeawaterelectrician.entity.CompanyDynamic;
import com.tbea.tb.tbeawaterelectrician.entity.NearbyCompany;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by cy on 2016/12/26.总经销商
 */

public class FranchiserViewActivity2 extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private ListView mCommodityListView;//商品列表的listView
    private MyCommodityAdapter mCommodityAdapter;
    private Context mContext;
    private int mCommodityPage = 1;//商品列表的当前页
    private int mCommodityPagesiz = 10;//商品列表
    private int mNewPage = 1;//商品列表的当前页
    private int mNewPagesiz = 10;//商品列表
    private BGARefreshLayout mCommodityRefreshLayout;
    private String mFlag = "commodity";//判断当期获取的时商品还是公司动态或者其他,默认是获取商品
    private ListView mNewListView;//显示公司动态的listView
    private MyNewAdapter mNewAdapter;
    private BGARefreshLayout mNewRefreshLayout;

//    /**
//     * ListView数据加载动画
//     */
//    private View loadingView;

    /**
     * companyid(经销商ID)
     orderitemid(排序类型id，推荐(auto)(默认),价格(price),销量,(salecount))
     order(排序类型id，desc（默认）,asc)
     justforpromotion(只显示促销商品 0不显示(默认)，1显示)
     */
    private String mCompanyid;
    private String mOrderitemid = "auto";
    private String mOrder = "desc";
    private String mJustforPromotion = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_franchiser_view2);
        initTopbar("经销商信息");
        mContext= this;
        initView();
        listener();
    }

    private void initView(){

        mCommodityListView = (ListView) findViewById(R.id.listview);
        mNewListView = (ListView)findViewById(R.id.listview2);
        mNewAdapter  = new MyNewAdapter(mContext);
        mNewListView.setAdapter(mNewAdapter);
        mCommodityAdapter = new MyCommodityAdapter(mContext);
        mCommodityListView.setAdapter(mCommodityAdapter);
        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_franchiser_view_top,null);
        mCommodityListView.addHeaderView(layout);
        mNewListView.addHeaderView(layout);
        mCommodityRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh);
        mNewRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh2);

        mCommodityRefreshLayout.setDelegate(this);
        mNewRefreshLayout.setDelegate(this);
        mNewRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));
//        if("commodity".equals(mFlag)){
//            mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, false));
//        }else {
        mCommodityRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));
//        }
        Gson gson = new Gson();
        String objJson = getIntent().getStringExtra("obj");
        NearbyCompany obj = gson.fromJson(objJson,NearbyCompany.class);
        if(!"".equals(obj.getPicture())){
            String url = MyApplication.instance.getImgPath()+ obj.getPicture();
            ImageView imageView = (ImageView)findViewById(R.id.nearby_company_item_picture);
            ImageLoader.getInstance().displayImage(url,imageView);
        }

        TextView nameView = (TextView) findViewById(R.id.nearby_company_item_name);
        if (obj.getWithcompanyidentified().equals("1")) {
            Drawable nav_up = ContextCompat.getDrawable(mContext, R.drawable.icon_attestations);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            nameView.setCompoundDrawables(null, null, nav_up, null);
        }
        if(obj.getWithcompanylisence().equals("1")){
            (findViewById(R.id.nearby_company_item_withcompanylisence)).setVisibility(View.VISIBLE);
        }
        if(obj.getWithguaranteemoney().equals("1")){
            (findViewById(R.id.nearby_company_item_withguaranteemoney)).setVisibility(View.VISIBLE);
        }
        if(obj.getWithidentified().equals("1")){
            (findViewById(R.id.nearby_company_item_withidentified)).setVisibility(View.VISIBLE);
        }
        setViewText(R.id.nearby_company_item_name,obj.getName());
        setViewText(R.id.nearby_company_item_distance,obj.getDistance());
        setViewText(R.id.nearby_company_item_addr,obj.getAddress());
        mCompanyid = obj.getId();
//        loadingView = getLayoutInflater().inflate(R.layout.list_loading_view,
//                null);
//        mCommodityListView.addFooterView(loadingView,null,false);

        mCommodityRefreshLayout.beginRefreshing();
    }

    private void listener(){
        View view = getLayoutInflater().inflate(R.layout.activity_franchiser_view_top_com_item_head,null);
        mCommodityListView.addHeaderView(view);
        view.findViewById(R.id.franchiser_item_head_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                ((TextView)findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                ((TextView)findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
                // orderitemid(排序类型id，推荐(auto)(默认),价格(price),销量,(salecount))
                if(mOrderitemid.equals("auto")){
                    if("desc".equals(mOrder)){
                        mOrder = "asc";
                    }else {
                        mOrder = "desc";
                    }
                }
                mOrderitemid  = "auto";
                mCommodityAdapter.removeAll();
                mCommodityPage = 1;
                getCommodityListDate();
            }
        });

        view.findViewById(R.id.franchiser_item_head_price).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                ((TextView)findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
                ((TextView)findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                // orderitemid(排序类型id，推荐(auto)(默认),价格(price),销量,(salecount))
                if(mOrderitemid.equals("price")){
                    if("desc".equals(mOrder)){
                        mOrder = "asc";
                    }else {
                        mOrder = "desc";
                    }
                }
                mOrderitemid  = "price";
                mCommodityAdapter.removeAll();
                mCommodityPage = 1;
                getCommodityListDate();
            }
        });

        view.findViewById(R.id.franchiser_item_head_salecount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
                ((TextView)findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                ((TextView)findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                // orderitemid(排序类型id，推荐(auto)(默认),价格(price),销量,(salecount))
                if(mOrderitemid.equals("salecount")){
                    if("desc".equals(mOrder)){
                        mOrder = "asc";
                    }else {
                        mOrder = "desc";
                    }
                }
                mOrderitemid  = "salecount";
                mCommodityAdapter.removeAll();
                mCommodityPage = 1;
                getCommodityListDate();
            }
        });

        findViewById(R.id.newtotlenumber_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCompaySearchTextColor(R.id.newtotlenumber_tv,R.id.newtotlenumber);
                mCommodityListView.setVisibility(View.GONE);
                mNewListView.setVisibility(View.VISIBLE);
                getNewsListDate();
            }
        });

        findViewById(R.id.commoditytotlenumber_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCompaySearchTextColor(R.id.commoditytotlenumber_tv,R.id.commoditytotlenumber);
                mCommodityListView.setVisibility(View.VISIBLE);
                mNewListView.setVisibility(View.GONE);
            }
        });

    }

    private void setCompaySearchTextColor(int id1,int id2){
        setTextColor(R.id.commoditytotlenumber,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.commoditytotlenumber_tv,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.porjecttotlenumber,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.porjecttotlenumber_tv,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.jobtotlenumber,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.jobtotlenumber_tv,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.newtotlenumber,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.newtotlenumber_tv,getResources().getColor(R.color.text_gray));
        setTextColor(id1,getResources().getColor(R.color.blue4));
       setTextColor(id2,getResources().getColor(R.color.blue4));
    }

    public void setViewText(int id,String text){
        ((TextView)findViewById(id)).setText(text);
    }

    private void setTextColor(int ids,int color){
        ((TextView)findViewById(ids)).setTextColor(color);
    }

     /**
     * 获取商品列表
     */
    public void getCommodityListDate(){
//        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
//        dialog.setText("加载中...");
//        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                dialog.dismiss();
//                mCommodityListView.removeFooterView(loadingView);
//                mCommodityListView.removeHeaderView(loadingView);
                mCommodityRefreshLayout.endLoadingMore();
                mCommodityRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo re = (RspInfo) msg.obj;
                            if (re.isSuccess()) {
                                List<Map<String, String>> list = (List<Map<String, String>>) re.getDateObj("commoditylist");
                                Map<String, Object> businesstotleinfo = (Map<String, Object>) re.getDateObj("businesstotleinfo");
                                if(businesstotleinfo != null){
                                    double commoditytotlenumber = (double)businesstotleinfo.get("commoditytotlenumber");
                                    double porjecttotlenumber= (double)businesstotleinfo.get("commoditytotlenumber");
                                    double jobtotlenumber= (double)businesstotleinfo.get("jobtotlenumber");
                                    double newtotlenumber= (double)businesstotleinfo.get("newtotlenumber");
                                    int c = (int)commoditytotlenumber;
                                    int p  = (int)porjecttotlenumber;
                                    int j = (int)jobtotlenumber;
                                    int n = (int)newtotlenumber;

                                    setViewText(R.id.commoditytotlenumber,c+"");
                                    setViewText(R.id.porjecttotlenumber,p+"");
                                    setViewText(R.id.jobtotlenumber,j+"");
                                    setViewText(R.id.newtotlenumber,n+"");
                                }

                                List<Commodith> companyList = new ArrayList<>();
                                if (list != null) {
                                    for (int i = 0; i < list.size(); i++) {
                                        Commodith obj = new Commodith();
                                        obj.setId(list.get(i).get("id"));
                                        obj.setPicture(list.get(i).get("picture"));
                                        obj.setName(list.get(i).get("name"));
                                        obj.setDistance(list.get(i).get("distance"));
                                        obj.setPrice(list.get(i).get("price"));
                                        obj.setSpecification(list.get(i).get("specification"));
                                        companyList.add(obj);
                                    }
                                    mCommodityAdapter.addAll(companyList);
                                } else {
                                    if (mCommodityPage> 1) {//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                        mCommodityPage--;
                                    }
                                }
                            } else {
                                UtilAssistants.showToast(re.getMsg());
                            }

                        }catch (Exception e){
                            Log.e("","");
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
                    RspInfo re = userAction.getCommodithList(mCompanyid,mOrderitemid,mOrder,mJustforPromotion,mCommodityPage++,mCommodityPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取公司动态
     */
    public void getNewsListDate(){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mNewRefreshLayout.endLoadingMore();
                mNewRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo re = (RspInfo) msg.obj;
                            if (re.isSuccess()) {
                                List<Map<String, String>> list = (List<Map<String, String>>) re.getDateObj("newslist");
//                                Map<String, Object> businesstotleinfo = (Map<String, Object>) re.getDateObj("businesstotleinfo");
//                                if(businesstotleinfo != null){
//                                    double commoditytotlenumber = (double)businesstotleinfo.get("commoditytotlenumber");
//                                    double porjecttotlenumber= (double)businesstotleinfo.get("commoditytotlenumber");
//                                    double jobtotlenumber= (double)businesstotleinfo.get("jobtotlenumber");
//                                    double newtotlenumber= (double)businesstotleinfo.get("newtotlenumber");
//                                    int c = (int)commoditytotlenumber;
//                                    int p  = (int)porjecttotlenumber;
//                                    int j = (int)jobtotlenumber;
//                                    int n = (int)newtotlenumber;
//
//                                    setViewText(R.id.commoditytotlenumber,c+"");
//                                    setViewText(R.id.porjecttotlenumber,p+"");
//                                    setViewText(R.id.jobtotlenumber,j+"");
//                                    setViewText(R.id.newtotlenumber,n+"");
//                                }

                                List<CompanyDynamic> companyList = new ArrayList<>();
                                if (list != null) {
                                    for (int i = 0; i < list.size(); i++) {
                                        CompanyDynamic obj = new CompanyDynamic();
                                        obj.setId(list.get(i).get("id"));
                                        obj.setContent(list.get(i).get("content"));
                                        obj.setNewstime(list.get(i).get("newstime"));
                                        companyList.add(obj);
                                    }
                                    mNewAdapter.addAll(companyList);
                                } else {
                                    if (mNewPage> 1) {//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                        mNewPage--;
                                    }
                                }
                            } else {
                                UtilAssistants.showToast(re.getMsg());
                            }

                        }catch (Exception e){
                            Log.e("","");
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
                    RspInfo re = userAction.getNewList(mCompanyid,mNewPage++,mNewPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }



    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
       if("commodity".equals(mFlag)){
           //下拉刷新
           mCommodityPage = 1;
           mCommodityAdapter.removeAll();
           getCommodityListDate();//获取商品列表
       }

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        if("commodity".equals(mFlag)){
            //下拉刷新
            getCommodityListDate();//获取商品列表
        }
        return false;
    }

    /**
     * 用于显示商品列表的适配器
     */
    private class MyCommodityAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;
        private List<Commodith> mList = new ArrayList<>();

        /**
         * 构造函数
         *
         * @param context android上下文环境
         */
        public MyCommodityAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            FrameLayout view = (FrameLayout) inflater.inflate(
                    R.layout.fragment_nearby_purchase_item_layout, null);
            final Commodith obj = mList.get(position);
            ImageView imageView = (ImageView)view.findViewById(R.id.nearby_commdith_item_picture);
            if(!obj.getPicture().equals("")){
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getPicture(),imageView);
            }
            ((TextView)view.findViewById(R.id.nearby_commdith_item_name)).setText(obj.getName());
            ((TextView)view.findViewById(R.id.nearby_commdith_item_specification)).setText(obj.getSpecification());
            (view.findViewById(R.id.nearby_commdith_item_distance)).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.nearby_commdith_item_price)).setText("￥："+obj.getPrice());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CommodithViewActivity.class);
                    intent.putExtra("id",obj.getId());
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
        public void addAll(List<Commodith> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 用于显示公司动态的适配器
     */
    private class MyNewAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;
        private List<Commodith> mList = new ArrayList<>();

        /**
         * 构造函数
         *
         * @param context android上下文环境
         */
        public MyNewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
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
                    R.layout.fragment_company_dynamics_item, null);
            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }
        public void addAll(List<Commodith> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }

}