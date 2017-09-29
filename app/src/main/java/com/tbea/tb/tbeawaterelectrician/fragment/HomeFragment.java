package com.tbea.tb.tbeawaterelectrician.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.CityListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.my.MessageListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.CommodithViewActivity;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.HistorySearchActivity;
import com.tbea.tb.tbeawaterelectrician.component.CircleImageView;
import com.tbea.tb.tbeawaterelectrician.entity.Company;
import com.tbea.tb.tbeawaterelectrician.entity.HomeDateSon;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;
import com.xyzlf.vertical.autoscroll.VerticalScrollAdapter;
import com.xyzlf.vertical.autoscroll.VerticalScrollView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by cy on 2016/12/16.首页
 */

public class HomeFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private ListView mListView;
    private CompanyAdapter mAdapter;
    private LayoutInflater mInflater;
    private BGARefreshLayout mRefreshLayout;
    private View mView;
    private View headView;
    private String mCityname = "德阳市";
    private String mCityid = null;
    private int mPage = 1;
    private int mPagesiz = 40;
    private List<List<HomeDateSon.Newmessage2>> mNewmessage2List = new ArrayList<>();//返利列表
    private final int CITY_RESULT = 1000;
//    private BadgeView mBadgeView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (View) inflater.inflate(R.layout.fragment_home, null);
        mInflater = inflater;
        initView(mView);
        mRefreshLayout.beginRefreshing();
        return mView;
    }

    private void initView(View view) {
        mRefreshLayout = getViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
//        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), true);
//        stickinessRefreshViewHolder.setStickinessColor(R.color.custom_stickiness2);
//        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
//        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);

        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));

        mListView = (ListView) view.findViewById(R.id.home_list);
        headView = mInflater.inflate(R.layout.fragment_home_list_head, null);
        mListView.addHeaderView(headView);

        mAdapter = new CompanyAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        view.findViewById(R.id.mian_city_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CityListActivity.class);
                HomeFragment.this.startActivityForResult(intent, CITY_RESULT);
            }
        });

        view.findViewById(R.id.expert_search_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistorySearchActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.open_my_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MessageListActivity.class);
                startActivity(intent);
            }
        });

        getMessageNumber();
        TextView cityView = (TextView) mView.findViewById(R.id.mian_city_text);
        if(!"".endsWith(MyApplication.instance.getCity()) && MyApplication.instance.getCity() != null){
            cityView.setText(MyApplication.instance.getCity());
        }

        mCityname = MyApplication.instance.getCity();

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getMessageNumber();
        TextView cityView = (TextView) mView.findViewById(R.id.mian_city_text);
        if(!"".endsWith(MyApplication.instance.getCity()) && MyApplication.instance.getCity() != null){
            cityView.setText(MyApplication.instance.getCity());
        }
        mCityname = MyApplication.instance.getCity();
    }

    //获取购物车数量
    private void  getMessageNumber(){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo re = (RspInfo) msg.obj;
                            if (re.isSuccess()) {
                                Map<String,String > shortcutinfo = (Map<String, String>) re.getDateObj("shortcutinfo");
                                if(shortcutinfo != null){
                                    String newmessagenumber = shortcutinfo.get("newmessagenumber");
                                    ImageView imageView = (ImageView) mView.findViewById(R.id.open_my_message);
                                    if(newmessagenumber != null && !"".equals(newmessagenumber) && !"0".equals(newmessagenumber)){
                                        imageView.setImageResource(R.drawable.icon_message_redpoint);
                                  }else {
                                        imageView.setImageResource(R.drawable.icon_message);
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
                    RspInfo re = userAction.getMessageNumber();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CITY_RESULT && resultCode == getActivity().RESULT_OK) {
            mCityid = data.getStringExtra("cityId");
            mCityname = data.getStringExtra("cityName");
            ((TextView) mView.findViewById(R.id.mian_city_text)).setText(mCityname);
            mPage = 1;
            mAdapter.removeAll();
            mRefreshLayout.beginRefreshing();
        }
    }

    private void getDate() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            if (mPage == 2) {
                                getAdvertiselist(re);//从返回的结果中获取广告数据列表并操作相对应的View
                                getNewMessage1List(re);//从返回的结果中获取活动通知列表并操作相对应的View
                                getNewMessage2List(re);//从返回的结果中获取返利列表并操作相对应的View
                            }
                            getCompanylist(re);//从返回的结果中获取附近商家列表并操作相对应的View
                        } else {
                            showToast("操作失败，请重试！");
                        }

                        break;
                    case ThreadState.ERROR:
                        showToast("操作失败，请重试！");
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction action = new UserAction();
                    RspInfo1 result = action.getMianDate(mCityname, mCityid, mPage++, mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, result).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 从返回的结果中获取广告数据列表并操作相对应的View
     */
    public void getAdvertiselist(RspInfo1 re) {
//        List<Map<String, String>> advertiselist = (List<Map<String, String>>) re.getDateObj("advertiselist");
        Map<String,Object> date = (Map<String,Object>)re.getData();
        List<Map<String, String>> advertiselist = (List<Map<String, String>>) date.get("advertiselist");
        String[] images = new String[advertiselist.size()];
        if (advertiselist != null) {
            for (int i = 0;i<advertiselist.size();i++) {
                String picture = MyApplication.instance.getImgPath()+advertiselist.get(i).get("picture")+"";
                images[i] = picture;
            }
            Banner banner = (Banner) headView.findViewById(R.id.banner);
            //设置banner样式
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            //设置图片集合
            banner.setImages(images);
            //设置banner动画效果
            banner.setBannerAnimation(Transformer.DepthPage);
            //设置轮播时间
//        banner.setDelayTime(1500);
            //设置指示器位置（当banner模式中有指示器时）
            banner.setIndicatorGravity(BannerConfig.CENTER);


        }
    }

    /**
     * 从返回的结果中获取活动通知列表并操作相对应的View
     */
    public void getNewMessage1List(RspInfo1 re) {
//        List<Map<String, String>> newMessage1List = (List<Map<String, String>>) re.getDateObj("newmessage1");
        Map<String,Object> date = (Map<String,Object>)re.getData();
        List<Map<String, String>> newMessage1List = (List<Map<String, String>>) date.get("newmessage1");
        if (newMessage1List != null) {
            String nerMessage1Name = newMessage1List.get(0).get("name");
            ((TextView) headView.findViewById(R.id.home_newmessage1_text)).setText(nerMessage1Name);
        }
    }

    /**
     * 从返回的结果中获取返利列表并操作相对应的View
     */
    public void getNewMessage2List(RspInfo1 re) {
        //返利列表
//        List<Map<String, String>> newMessage2List = (List<Map<String, String>>) re.getDateObj("newmessage2");
        Map<String,Object> date = (Map<String,Object>)re.getData();
        List<Map<String, String>> newMessage2List = (List<Map<String, String>>) date.get("newmessage2");
        if (newMessage2List != null) {
            for (int i = 0; i < newMessage2List.size(); i++) {
                List<HomeDateSon.Newmessage2> list = new ArrayList<>();
                if (newMessage2List.size() >= 2) {
                    for (int j = 0; j < 2; j++) {
                        if(i < newMessage2List.size()){
                            HomeDateSon.Newmessage2 obj = getNewMessage2(newMessage2List, i);
                            list.add(obj);
                        }
                        i++;
                    }
                    i--;
                } else {
                    HomeDateSon.Newmessage2 obj = getNewMessage2(newMessage2List, i);
                    list.add(obj);
                }
                mNewmessage2List.add(list);
            }
            customViewScrollView(mNewmessage2List);
        }
    }

    /**
     * 从返利列表中获取一个返利对象
     *
     * @param newMessage2List
     * @param i
     * @return
     */
    public HomeDateSon.Newmessage2 getNewMessage2(List<Map<String, String>> newMessage2List, int i) {
        HomeDateSon.Newmessage2 obj = new HomeDateSon().newNewMessage2();
       try {
           obj.setId(newMessage2List.get(i).get("id"));
           obj.setName(newMessage2List.get(i).get("name"));
           obj.setUser_id(newMessage2List.get(i).get("user_id"));
           obj.setMessage(newMessage2List.get(i).get("message"));
           obj.setPicture(newMessage2List.get(i).get("picture"));
           obj.setUsername(newMessage2List.get(i).get("username"));
           obj.setMoney(newMessage2List.get(i).get("money"));
       }catch (Exception e){
           Log.e("HomeFragment",e.getMessage());
       }
        return obj;
    }

    /**
     * 从返回的结果中获取附近商家列表并操作相对应的View
     */
    public void getCompanylist(RspInfo1 re) {
        //附近商家
//        List<Map<String, String>> companylist = (List<Map<String, String>>) re.getDateObj("companylist");
        Map<String,Object> date = (Map<String,Object>)re.getData();
        List<Map<String, String>> companylist = (List<Map<String, String>>) date.get("companylist");
        if (companylist != null) {
            List<Company> companyList2 = new ArrayList<>();
            for (int i = 0; i < companylist.size(); i++) {
                Company obj = new Company();
                obj.setId(companylist.get(i).get("id"));
                obj.setPicture(companylist.get(i).get("picture"));
                obj.setCompanyname(companylist.get(i).get("name"));
                obj.setDistance(companylist.get(i).get("distance"));
                obj.setCommoditydesc(companylist.get(i).get("specification"));
                obj.setPrice(companylist.get(i).get("price"));
                obj.setCompanyid(companylist.get(i).get("companyid"));
                obj.setCompanytypeid(companylist.get(i).get("companytypeid"));
                companyList2.add(obj);
            }
            mAdapter.addAll(companyList2);
        }
    }


    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) mView.findViewById(id);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
        mPage = 1;
        mNewmessage2List = new ArrayList<>();
        mAdapter.removeAll();
        getDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        getDate();
        return true;
    }


    /**
     * 附近商家
     */
    private class CompanyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;
        private List<Company> mList = new ArrayList<>();

        /**
         * 构造函数
         *
         * @param context android上下文环境
         */
        public CompanyAdapter(Context context) {
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
                    R.layout.home_nearby_shop_layout, null);
            final Company obj = mList.get(position);
            ((TextView) view.findViewById(R.id.home_neatby_company_companyname)).setText(obj.getCompanyname());
            ((TextView) view.findViewById(R.id.home_neatby_company_distance)).setText(obj.getDistance());
            ((TextView) view.findViewById(R.id.home_neatby_company_commoditydesc)).setText(obj.getCommoditydesc());
            ((TextView) view.findViewById(R.id.home_neatby_company_price)).setText("￥" + obj.getPrice());
            ImageView imageView = (ImageView) view.findViewById(R.id.home_neatby_company_picture);
            if (!obj.getPicture().equals("")) {
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.getPicture(), imageView);
            }
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

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Company> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }


    private void customViewScrollView(List<List<HomeDateSon.Newmessage2>> datas) {
        final MyScrollAdapter adapter = new MyScrollAdapter(datas);
        final VerticalScrollView tbView = (VerticalScrollView) mView.findViewById(R.id.vertical_scroll_view);
        tbView.setAdapter(adapter);
        //开启线程滚东
        tbView.start();
    }


    /**
     * 返利列表滚动
     */
    private class MyScrollAdapter extends VerticalScrollAdapter<List<HomeDateSon.Newmessage2>> {


        public MyScrollAdapter(List<List<HomeDateSon.Newmessage2>> mDatas) {
            super(mDatas);
        }

        @Override
        public View getView(VerticalScrollView parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.home_rebate_layout, parent, false);
        }

        @Override
        public void setItem(final View view, List<HomeDateSon.Newmessage2> lists) {
            for (int i = 0; i < lists.size(); i++) {
                if (i == 0) {
                    HomeDateSon.Newmessage2 data = (HomeDateSon.Newmessage2) lists.get(i);
                    ((TextView) view.findViewById(R.id.home_rebate_name)).setText(data.getUsername());
                    ((TextView) view.findViewById(R.id.home_rebate_message)).setText(data.getMessage());
                    ((TextView) view.findViewById(R.id.home_rebate_money)).setText("￥" + data.getMoney());
                    CircleImageView imageView = (CircleImageView) view.findViewById(R.id.home_rebate_picture);
                    ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + data.getPicture(), imageView);
                }
                if (i == 1) {
                    view.findViewById(R.id.home_rebate_layout1).setVisibility(View.VISIBLE);
                    HomeDateSon.Newmessage2 data = (HomeDateSon.Newmessage2) lists.get(i);
                    ((TextView) view.findViewById(R.id.home_rebate_name1)).setText(data.getUsername());
                    ((TextView) view.findViewById(R.id.home_rebate_message1)).setText(data.getMessage());
                    ((TextView) view.findViewById(R.id.home_rebate_money1)).setText("￥" + data.getMoney());
                    CircleImageView imageView = (CircleImageView) view.findViewById(R.id.home_rebate_picture1);
                    ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + data.getPicture(), imageView);
                }
            }
        }
    }

    public void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

}
