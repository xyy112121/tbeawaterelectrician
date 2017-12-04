package com.tbea.tb.tbeawaterelectrician.fragment.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.CommodithViewActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.entity.Commodith;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by cy on 2016/12/19.附近采购
 */

public class NearbyCommodithFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private View mView;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private boolean isVisible;
    private ListView mListView;
    private MyAdapter mAdapter;
    private String mCategoryId = "-10000";//商品类型
    private String mBrandId = "-10000";//品牌类型
    private String mLocationId = "-10000";//区域类型
    private  int mPage = 1;
    private int mPagesiz =10 ;
    private BGARefreshLayout mRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_nearby_franchiser_layout, container, false);
        initUI();//实例化控件
        mRefreshLayout.beginRefreshing();
        listener();
        return mView;
    }

    /**
     * 刷新数据
     */
    public void refreshDate(){
        mPage = 1;
        mAdapter.removeAll();
        mRefreshLayout.beginRefreshing();
    }

    private void listener(){
        mView.findViewById(R.id.franchiser_search_condition1_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //类型
                getConditionDate(view,"TBEAENG003001009001");
            }
        });

        mView.findViewById(R.id.franchiser_search_condition3_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //品牌
                getConditionDate(view,"TBEAENG003001009002");
            }
        });

        mView.findViewById(R.id.franchiser_search_condition2_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //区域
                getLocationList(view);
            }
        });
    }

    /**
     * 获取数据
     */
    public void getCommodithList(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<Commodith> commoditylist = ( List<Commodith>)re.getDateObj("commoditylist");
                            if(commoditylist != null){
                                mAdapter.addAll(commoditylist);
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
                    RspInfo  re = userAction.getCommodithList(mCategoryId,mBrandId,mLocationId,mPage++,mPagesiz);
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
                            UtilAssistants.showToast("操作失败！",getActivity());
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
                    RspInfo  re = userAction.getLocationList(NearbyFragment.mCityname,"1");
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取查询条件列表(类型和品牌)
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
                            if("TBEAENG003001009001".equals(methodName)){
                                list = (List<Condition>)re.getDateObj("commoditycategorylist");
                            }else {
                                list = ( List<Condition>)re.getDateObj("commoditybrandlist");
                            }
                            if(list != null){
                                showDialog(view,list,methodName);
                            }
                        }else {
                            UtilAssistants.showToast("操作失败！",getActivity());
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
                if(methodName.equals("TBEAENG003001009001")){//采购类型
                    mCategoryId = popWindow.mSelectedId;
                    if(popWindow.mSelectedId.equals("")){
                        mCategoryId = "-10000";
                    }

                    ((TextView)mView.findViewById(R.id.franchiser_search_condition1)).setText(popWindow.mSelectedName);
                }else if(methodName.equals("TBEAENG003001009002")){//商品品牌
                    mBrandId = popWindow.mSelectedId;
                    ((TextView)mView.findViewById(R.id.franchiser_search_condition3)).setText(popWindow.mSelectedName);
                }else {
                    mLocationId = popWindow.mSelectedId;
                    ((TextView)mView.findViewById(R.id.franchiser_search_condition2)).setText(popWindow.mSelectedName);
                }
                mPage = 1;
                mAdapter.removeAll();
                mRefreshLayout.beginRefreshing();
                getCommodithList();
            }
        });
        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 实例化组件
     */
    private void initUI() {
        ((TextView)mView.findViewById(R.id.franchiser_search_condition1)).setText("全部类型");
        ((TextView)mView.findViewById(R.id.franchiser_search_condition2)).setText("全部区域");
        ((TextView)mView.findViewById(R.id.franchiser_search_condition3)).setText("全部品牌");
        mListView = (ListView)mView.findViewById(R.id.franchiser_select_list);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout)mView.findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));
//        try {
//            mView.findViewById(R.id.franchiser_search_condition_layout).setVisibility(View.GONE);
//            mView.findViewById(R.id.franchiser_search_condition_view).setVisibility(View.GONE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
       //下拉刷新
        mPage = 1;
        mAdapter.removeAll();
        getCommodithList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        getCommodithList();
        return true;
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
            final Commodith obj = mList.get(position);
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
