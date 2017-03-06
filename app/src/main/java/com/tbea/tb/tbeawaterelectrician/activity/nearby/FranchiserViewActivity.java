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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

/**
 * Created by cy on 2016/12/26.总经销商
 */

public class FranchiserViewActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
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
    private  LinearLayout mItemTopView;
    private LinearLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_franchiser_view);
        initTopbar("经销商信息");
        MyApplication.instance.addActivity(this);
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
        LinearLayout layout = initViewInfo();
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

        mCommodityRefreshLayout.beginRefreshing();
        mItemTopView = (LinearLayout)findViewById(R.id.franchiser_view_top_layout);
    }

    private LinearLayout initViewInfo(){
         layout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_franchiser_view_top,null);
        Gson gson = new Gson();
        String objJson = getIntent().getStringExtra("obj");
        if(!"".equals(objJson) && null != objJson){
            NearbyCompany obj = gson.fromJson(objJson,NearbyCompany.class);
            setCompanyView(obj);
        }else {
            mCompanyid = getIntent().getStringExtra("companyId");
        }

        return layout;
    }

    private  void setCompanyView(NearbyCompany obj){
        if(obj != null){
            if(!"".equals(obj.getPicture())){
                String url = MyApplication.instance.getImgPath()+ obj.getPicture();
                ImageView imageView = (ImageView)layout.findViewById(R.id.nearby_company_item_picture);
                ImageLoader.getInstance().displayImage(url,imageView);
            }

            TextView nameView = (TextView) layout.findViewById(R.id.nearby_company_item_name);
            if (obj.getWithcompanyidentified().equals("1")) {
                Drawable nav_up = ContextCompat.getDrawable(mContext, R.drawable.icon_attestations);
                nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                nameView.setCompoundDrawables(null, null, nav_up, null);
            }
            if(obj.getWithcompanylisence().equals("1")){
                (layout.findViewById(R.id.nearby_company_item_withcompanylisence)).setVisibility(View.VISIBLE);
            }
            if(obj.getWithguaranteemoney().equals("1")){
                (layout.findViewById(R.id.nearby_company_item_withguaranteemoney)).setVisibility(View.VISIBLE);
            }
            if(obj.getWithidentified().equals("1")){
                (layout.findViewById(R.id.nearby_company_item_withidentified)).setVisibility(View.VISIBLE);
            }
            ((TextView)layout.findViewById(R.id.nearby_company_item_name)).setText(obj.getName());
            ((TextView)layout.findViewById(R.id.nearby_company_item_distance)).setText(obj.getDistance());
            ((TextView)layout.findViewById(R.id.nearby_company_item_addr)).setText(obj.getAddress());
            mCompanyid = obj.getId();
        }
    }

    private void listener(){
        final View view = getLayoutInflater().inflate(R.layout.activity_franchiser_view_top_com_item_head,null);
        mCommodityListView.addHeaderView(view);
        view.findViewById(R.id.franchiser_item_head_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)view.findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
                ((TextView)view.findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
                ((TextView)view.findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
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
            public void onClick(View v) {
                ((TextView)view.findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
                ((TextView)view.findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
                ((TextView)view.findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
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
            public void onClick(View v) {
                ((TextView)view.findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
                ((TextView)view.findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
                ((TextView)view.findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
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

        ((CheckBox)view.findViewById(R.id.franchiser_item_head_justforPromotion)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mJustforPromotion = "1";
                }else {
                    mJustforPromotion = "0";
                }
                mCommodityAdapter.removeAll();
                mCommodityPage = 1;
                getCommodityListDate();
            }
        });

         findViewById(R.id.franchiser_item_head_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
                ((TextView)findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
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

        findViewById(R.id.franchiser_item_head_price).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
                ((TextView)findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
                ((TextView)findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
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

        findViewById(R.id.franchiser_item_head_salecount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.franchiser_item_head_salecount)).setTextColor(ContextCompat.getColor(mContext,R.color.black));
                ((TextView)findViewById(R.id.franchiser_item_head_price)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
                ((TextView)findViewById(R.id.franchiser_item_head_auto)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
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

        ((CheckBox)findViewById(R.id.franchiser_item_head_justforPromotion)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mJustforPromotion = "1";
                }else {
                    mJustforPromotion = "0";
                }
                mCommodityAdapter.removeAll();
                mCommodityPage = 1;
                getCommodityListDate();
            }
        });

        findViewById(R.id.newtotlenumber_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewRefreshLayout.setVisibility(View.VISIBLE);
                mCommodityRefreshLayout.setVisibility(View.GONE);
                setCompaySearchTextColor(R.id.newtotlenumber_tv,R.id.newtotlenumber);
                mFlag = "CompanyDynamic";
                getNewsListDate();
            }
        });

        findViewById(R.id.commoditytotlenumber_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCompaySearchTextColor(R.id.commoditytotlenumber_tv,R.id.commoditytotlenumber);
                mNewRefreshLayout.setVisibility(View.GONE);
                mCommodityRefreshLayout.setVisibility(View.VISIBLE);
                mFlag = "commodity";
            }
        });

        mCommodityListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
//                if (firstVisibleItem >= 1) {
//                    mItemTopView.setVisibility(View.VISIBLE);
//                    if("0".equals(mJustforPromotion)){
//                        ((CheckBox)(findViewById(R.id.franchiser_item_head_justforPromotion))).setChecked(false);
//                    }else {
//                        ((CheckBox)(findViewById(R.id.franchiser_item_head_justforPromotion))).setChecked(true);
//                    }
//
//                } else {
//                    mItemTopView.setVisibility(View.GONE);
//                }
            }
        });
    }

    private void setCompaySearchTextColor(int id1,int id2){
        setTextColor(R.id.commoditytotlenumber,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(R.id.commoditytotlenumber_tv,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(R.id.porjecttotlenumber,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(R.id.porjecttotlenumber_tv,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(R.id.jobtotlenumber,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(R.id.jobtotlenumber_tv,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(R.id.newtotlenumber,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(R.id.newtotlenumber_tv,ContextCompat.getColor(mContext,R.color.text_gray));
        setTextColor(id1,ContextCompat.getColor(mContext,R.color.blue4));
       setTextColor(id2,ContextCompat.getColor(mContext,R.color.blue4));
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
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCommodityRefreshLayout.endLoadingMore();
                mCommodityRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo re = (RspInfo) msg.obj;
                            if (re.isSuccess()) {
                                List<Map<String, String>> list = (List<Map<String, String>>) re.getDateObj("commoditylist");
                                Map<String, Object> businesstotleinfo = (Map<String, Object>) re.getDateObj("businesstotleinfo");
                                Map<String, String> companybaseinfo = (Map<String, String>) re.getDateObj("companybaseinfo");

                                if(companybaseinfo != null){
                                    NearbyCompany obj = new NearbyCompany();
                                    obj.setId(companybaseinfo.get("id"));
                                    obj.setPicture(companybaseinfo.get("picture"));
                                    obj.setName(companybaseinfo.get("name"));
                                    obj.setDistance(companybaseinfo.get("distance"));
                                    obj.setLatitude(companybaseinfo.get("latitude"));
                                    obj.setAddress(companybaseinfo.get("address"));
                                    obj.setCompanytypeid(companybaseinfo.get("companytypeid"));
                                    obj.setWithcompanyidentified(companybaseinfo.get("withcompanyidentified"));
                                    obj.setWithcompanylisence(companybaseinfo.get("withcompanylisence"));
                                    obj.setWithguaranteemoney(companybaseinfo.get("withguaranteemoney"));
                                    obj.setWithidentified(companybaseinfo.get("withidentified"));
                                    setCompanyView(obj);
                                }


                                if(businesstotleinfo != null){
                                    setViewText(R.id.commoditytotlenumber,businesstotleinfo.get("commoditytotlenumber")+"");
                                    setViewText(R.id.porjecttotlenumber,businesstotleinfo.get("commoditytotlenumber")+"");
                                    setViewText(R.id.jobtotlenumber,businesstotleinfo.get("jobtotlenumber")+"");
                                    setViewText(R.id.newtotlenumber,businesstotleinfo.get("newtotlenumber")+"");
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
                                        obj.setCompanyid(list.get(i).get("companyid"));
                                        obj.setCompanytypeid(list.get(i).get("companytypeid"));
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
                                Map<String, String>advertise = (Map<String, String>) re.getDateObj("advertise");
                                ImageView imageView = (ImageView)findViewById(R.id.franchiser_view_advertisement);
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+ advertise.get("picture"),imageView);
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
       }else if("CompanyDynamic".equals(mFlag)){
            mNewPage = 1;
            mNewAdapter.removeAll();
            getNewsListDate();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        if("commodity".equals(mFlag)){
            //下拉刷新
            getCommodityListDate();//获取商品列表
        }else if("CompanyDynamic".equals(mFlag)){
            getNewsListDate();
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
            final LayoutInflater inflater = (LayoutInflater) context
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
            ((TextView)view.findViewById(R.id.nearby_commdith_item_price)).setText("￥"+obj.getPrice());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CommodithViewActivity.class);
                    intent.putExtra("id",obj.getId());
                    intent.putExtra("distributorid",mCompanyid);
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
        private List<CompanyDynamic> mList = new ArrayList<>();

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
            CompanyDynamic obj = mList.get(position);
            ((TextView)view.findViewById(R.id.item_CompanyDynamic_newstime)).setText(obj.getNewstime());
            ((TextView)view.findViewById(R.id.item_CompanyDynamic_content)).setText(obj.getContent());
            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }
        public void addAll(List<CompanyDynamic> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }

}
