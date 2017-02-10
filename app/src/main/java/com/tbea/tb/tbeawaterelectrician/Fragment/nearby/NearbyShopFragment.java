package com.tbea.tb.tbeawaterelectrician.fragment.nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.DistributorViewAcitivty;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.FranchiserViewActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
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
 * Created by cy on 2016/12/19.附近商家
 */

public class NearbyShopFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private View mView;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private boolean isVisible;
    private ListView mListView;
    private MyAdapter mAdapter;
    private String mCompanyTypeId = "-10000";//公司类型
    private String mCertifiedStatusId = "-10000";//认证类型
    private String mLocationId = "-10000";//区域类型
    private  int mPage = 1;
    private int mPagesiz =10 ;
    private BGARefreshLayout mRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_nearby_franchiser_layout, container, false);
        initUI();//实例化控件
//        isPrepared = true;
        mRefreshLayout.beginRefreshing();
        listener();
        return mView;
    }

    private void listener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NearbyCompany obj = (NearbyCompany)mAdapter.getItem(i);
                if("firstleveldistributor".equals(obj.getCompanytypeid())){
                    //总经销商
                    startActivity(new Intent(getActivity(), FranchiserViewActivity.class));
                }else{
                    //经销商
                    Intent intent = new Intent(getActivity(), DistributorViewAcitivty.class);
                    intent.putExtra("id",obj.getId());
                    startActivity(intent);
                }
            }
        });
        mView.findViewById(R.id.franchiser_search_condition1_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getConditionDate(view,"TBEAENG003001001000");
            }
        });

        mView.findViewById(R.id.franchiser_search_condition3_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getConditionDate(view,"TBEAENG003001003000");
            }
        });

        mView.findViewById(R.id.franchiser_search_condition2_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationList(view);
            }
        });
    }

    /**
     * 刷新数据
     */
    public void refreshDate(){
        mPage = 1;
        mAdapter.removeAll();
        mRefreshLayout.beginRefreshing();
    }

    /**
     * 获取数据
     */
    public void getCompanyList(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<Map<String,String>> list =  (List<Map<String,String>>) re.getDateObj("companylist");
                            List<NearbyCompany> companyList = new ArrayList<>();
                            if(list != null){
                                for (int i = 0;i< list.size();i++){
                                    NearbyCompany obj = new NearbyCompany();
                                    obj.setId(list.get(i).get("id"));
                                    obj.setPicture(list.get(i).get("picture"));
                                    obj.setName(list.get(i).get("name"));
                                    obj.setDistance(list.get(i).get("distance"));
                                    obj.setLatitude(list.get(i).get("latitude"));
                                    obj.setAddress(list.get(i).get("address"));
                                    obj.setCompanytypeid(list.get(i).get("companytypeid"));
                                    obj.setWithcompanyidentified(list.get(i).get("withcompanyidentified"));
                                    obj.setWithcompanylisence(list.get(i).get("withcompanylisence"));
                                    obj.setWithguaranteemoney(list.get(i).get("withguaranteemoney"));
                                    obj.setWithguaranteemoney(list.get(i).get("withidentified"));
                                    companyList.add(obj);
                                }
                                mAdapter.addAll(companyList);
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
                    RspInfo  re = userAction.getShopList(mCompanyTypeId,mLocationId,mCertifiedStatusId,NearbyFragment.mCityname,NearbyFragment.mCityid,mPage++,mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取区域列表
     */
    public  void getLocationList(final  View view){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<Condition> list = (List<Condition>)re.getDateObj("locationlist");
                            if(list != null){
                                showDialog(view,list,"");
                            }
                        }else {
                            UtilAssistants.showToast("操作失败！");
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
                    RspInfo  re = userAction.getLocationList(NearbyFragment.mCityname);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取查询条件列表
     * @param view
     * @param methodName
     */
    public  void getConditionDate(final  View view,final String methodName){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<Condition> list;
                            if("TBEAENG003001003000".equals(methodName)){
                                list = (List<Condition>)re.getDateObj("certifiedstatuslist");
                            }else {
                                list = ( List<Condition>)re.getDateObj("companytypelist");
                            }
                            if(list != null){
                                showDialog(view,list,methodName);
                            }
                        }else {
                            UtilAssistants.showToast("操作失败！");
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
                    RspInfo  re = userAction.getFranchiserType(methodName);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 选择相册Dialog
     */
    protected void showDialog(View view, List<Condition> data, final String methodName) {
        final CustomPopWindow popWindow = new CustomPopWindow(getActivity(),
                R.id.body_bg_view, true, R.style.PopWindowAnimationFade,
                RelativeLayout.LayoutParams.MATCH_PARENT,R.layout.pop_window_scrollview_layout);
        popWindow.addScrollViewForGroup1(data, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                if(methodName.equals("TBEAENG003001001000")){//公司类型
                    mCompanyTypeId = popWindow.mSelectedId;
                    if(popWindow.mSelectedId.equals("")){
                        mCompanyTypeId = "-10000";
                    }

                    ((TextView)mView.findViewById(R.id.franchiser_search_condition1)).setText(popWindow.mSelectedName);
                }else if(methodName.equals("TBEAENG003001003000")){//认证状态
                    mCertifiedStatusId = popWindow.mSelectedId;
                    ((TextView)mView.findViewById(R.id.franchiser_search_condition3)).setText(popWindow.mSelectedName);
                }else {
                    mLocationId = popWindow.mSelectedId;
                    ((TextView)mView.findViewById(R.id.franchiser_search_condition2)).setText(popWindow.mSelectedName);
                }
                mPage = 1;
                mAdapter.removeAll();
                mRefreshLayout.beginRefreshing();
                getCompanyList();
            }
        });
        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 实例化组件
     */
    private void initUI() {
        mListView = (ListView)mView.findViewById(R.id.franchiser_select_list);
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
        getCompanyList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        getCompanyList();
        return true;
    }

    private class MyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;
        private List<NearbyCompany> mList = new ArrayList<>();

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
                    R.layout.fragment_nearby_franchiser_item_layout, null);
            NearbyCompany obj = mList.get(position);
            ImageView imageView = (ImageView) view.findViewById(R.id.nearby_company_item_picture);
            if (!obj.getPicture().equals("")) {
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.getPicture(), imageView);
            }
            TextView nameView = (TextView) view.findViewById(R.id.nearby_company_item_name);
            nameView.setText(obj.getName());
            ((TextView) view.findViewById(R.id.nearby_company_item_distance)).setText(obj.getDistance());
            ((TextView) view.findViewById(R.id.nearby_company_item_addr)).setText(obj.getAddress());
            if (obj.getWithcompanyidentified().equals("1")) {
                Drawable nav_up = ContextCompat.getDrawable(context, R.drawable.icon_attestations);
                nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                nameView.setCompoundDrawables(null, null, nav_up, null);
            }
            if(obj.getWithidentified() != null && obj.getWithcompanylisence().equals("1")){
                (view.findViewById(R.id.nearby_company_item_withcompanylisence)).setVisibility(View.VISIBLE);
            }
            if(obj.getWithidentified() != null && obj.getWithguaranteemoney().equals("1")){
                (view.findViewById(R.id.nearby_company_item_withguaranteemoney)).setVisibility(View.VISIBLE);
            }
            if(obj.getWithidentified() != null && obj.getWithidentified().equals("1")){
                (view.findViewById(R.id.nearby_company_item_withidentified)).setVisibility(View.VISIBLE);
            }
            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }

        public void addAll(List<NearbyCompany> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }


    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
        } else {
            isVisible = false;
        }
    }
}
