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
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Commodith;
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

public class FranchiserViewActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private StickyListHeadersListView mListView;
    private MyAdapter mAdapter;
    private Context mContext;
    private int mCommodityPage = 1;//商品的当前页
    private int mCommodityPagesiz = 10;//商品
    private BGARefreshLayout mRefreshLayout;
    private String mFlag = "commodity";//判断当期获取的时商品还是公司动态或者其他,默认是获取商品
    /**
     * ListView数据加载动画
     */
    private View loadingView;

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
        setContentView(R.layout.activity_franchiser_view);
        initTopbar("经销商信息");
        mContext= this;
        initView();
        listener();
    }

    private void initView(){
        mListView = (StickyListHeadersListView)findViewById(R.id.listview);
        mAdapter = new MyAdapter(mContext);
        mListView.setAdapter(mAdapter);
        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_franchiser_view_top,null);
        mListView.addHeaderView(layout);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh);

        mRefreshLayout.setDelegate(this);
        if("commodity".equals(mFlag)){
            mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, false));
        }else {
            mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));
        }
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
        loadingView = getLayoutInflater().inflate(R.layout.list_loading_view,
                null);
        mListView.addFooterView(loadingView,null,false);

        getCommodityListDate();
//        mRefreshLayout.beginRefreshing();
    }

    private void listener(){

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    int dd = view.getLastVisiblePosition();
//                    int d = mAdapter.getCount();
                    if (view.getLastVisiblePosition() == (mAdapter.getCount())) {
//                        ((TextView)loadingView.findViewById(R.id.list_load_text)).setText("正在努力加载");
                        mListView.removeFooterView(loadingView);
                        mListView.addFooterView(loadingView);
                        getCommodityListDate();
                    }

//                        UtilAssistants.showToast("底部");
//                    }else if(view.getFirstVisiblePosition() == 0){
//                        UtilAssistants.showToast("顶部");
////                        ((TextView)loadingView.findViewById(R.id.list_load_text)).setText("下拉加载");
//                        mListView.addHeaderView(loadingView);
//                        //下拉刷新
//                        mCommodityPage = 1;
//                        mAdapter.removeAll();
//                        getCommodityListDate();//获取商品列表
//                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

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
                mListView.removeFooterView(loadingView);
//                mListView.removeHeaderView(loadingView);
                mRefreshLayout.endRefreshing();
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
                                    mAdapter.addAll(companyList);
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



    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
       if("commodity".equals(mFlag)){
           //下拉刷新
           mCommodityPage = 1;
           mAdapter.removeAll();
           getCommodityListDate();//获取商品列表
       }

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {


//        //上拉加载更多
//        if("commodity".equals(mFlag)){
//            //下拉刷新
//            getCommodityListDate();//获取商品列表
//        }
        return false;
    }



    public boolean isTop() {
        if (mListView.getFirstVisiblePosition() == 0
                && (mListView == null || mListView.getTop() == 0)) {
            return true;
        }
        return false;
    }


    public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private List<Commodith> mList = new ArrayList<>();
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void addAll(List<Commodith> list){
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public  void removeAll(){
            mList.clear();
            notifyDataSetChanged();
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

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.activity_franchiser_view_top_com_item_head, parent, false);
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
                    mAdapter.removeAll();
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
                    mAdapter.removeAll();
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
                    mAdapter.removeAll();
                    mCommodityPage = 1;
                    getCommodityListDate();
                }
            });
            return view;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return 0;
        }


    }

}
