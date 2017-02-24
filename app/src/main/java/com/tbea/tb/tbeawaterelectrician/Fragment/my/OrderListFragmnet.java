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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.my.OrderListActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Order;
import com.tbea.tb.tbeawaterelectrician.entity.ProductInfo;
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
 * Created by abc on 16/12/24.
 */

public class OrderListFragmnet extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private View mView;
    private ListView mListView;
    private MyAdapter mAdapter;
    private int mPage = 1;
    private int mPagesiz = 10;
    private BGARefreshLayout mRefreshLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order_list, container, false);
        initUI();//实例化控件
        mRefreshLayout.beginRefreshing();
        return mView;
    }

    /**
     * 获取数据
     */
    public void getDate() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Map<String, Object>> list = (List<Map<String, Object>>) re.getDateObj("userorderlist");
                            List<Order> orderList = new ArrayList<>();
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    Order order = new Order();
                                    order.setOrderid(list.get(i).get("orderid")+"");
                                    order.setOrdercompany(list.get(i).get("ordercompany")+"");
                                    order.setOrderstatusid(list.get(i).get("orderstatusid")+"");
                                    order.setOrderstatus(list.get(i).get("orderstatus")+"");
                                    order.setOrdercommoditynumber(list.get(i).get("ordercommoditynumber")+"");
                                    order.setOrdertotlefee(list.get(i).get("ordertotlefee")+"");
                                    order.setDeliveryfee(list.get(i).get("deliveryfee")+"");
                                    order.setPromotioninfo(list.get(i).get("promotioninfo")+"");
                                    List<Map<String, String>> commoditylist = (List<Map<String, String>>)list.get(i).get("commoditylist");
                                    List<ProductInfo> commoditylist2 = new ArrayList<>();
                                    if(commoditylist != null){
                                        for (int j = 0;j<commoditylist.size();j++){
                                            ProductInfo obj = new ProductInfo();
                                            obj.setOrderdetailid(commoditylist.get(j).get("orderdetailid"));
                                            obj.setCommodityid(commoditylist.get(j).get("commodityid"));
                                            obj.setCommodityname(commoditylist.get(j).get("commodityname"));
                                            obj.setCommoditypicture(commoditylist.get(j).get("commoditypicture"));
                                            obj.setOrdercolorid(commoditylist.get(j).get("ordercolorid"));
                                            obj.setOrdercolor(commoditylist.get(j).get("ordercolor"));
                                            obj.setOrderspecificationid(commoditylist.get(j).get("orderspecificationid"));
                                            obj.setOrderspecification(commoditylist.get(j).get("orderspecification"));
                                            obj.setOrderprice(Float.parseFloat(commoditylist.get(j).get("orderprice")));
                                            obj.setOrdernumber(Integer.parseInt(commoditylist.get(j).get("ordernumber")));
                                            obj.setOrdertime(commoditylist.get(j).get("ordertime"));
                                            commoditylist2.add(obj);
                                        }
                                        order.setCommoditylist(commoditylist2);
                                    }
                                    orderList.add(order);
                                }
                                mAdapter.addAll(orderList);
                            } else {
                                if (mPage > 1) {//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
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
                    String orderstatusid = ((OrderListActivity)getActivity()).stateId;
                    if("-10000".equals(orderstatusid)){
                        orderstatusid = "";
                    }
                    RspInfo re = userAction.getOrderList(orderstatusid,mPage++, mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
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
        mListView = (ListView)mView.findViewById(R.id.order_listview);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout) mView.findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉
        mAdapter.removeAll();
        mPage = 1;
        getDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    private class MyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;

        /**
         * 构造函数
         *
         * @param context
         *            android上下文环境
         */
        public MyAdapter(Context context) {
            this.context = context;
        }
        private List<Order> mList = new ArrayList<>();

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
                    R.layout.fragment_order_list_item, null);
            Order obj = mList.get(position);
            ((TextView)view.findViewById(R.id.order_item_ordercompany)).setText(obj.getOrdercompany());
            ((TextView)view.findViewById(R.id.order_item_orderstatus)).setText(obj.getOrderstatus());
            int size = 0;
            if(obj.getCommoditylist() != null){
                size = obj.getCommoditylist().size();
                LinearLayout parentsLayout = (LinearLayout)view.findViewById(R.id.item_goods_layout);
                parentsLayout.removeAllViews();
                for (ProductInfo item:obj.getCommoditylist()) {
                    FrameLayout layout = (FrameLayout)layoutInflater.inflate(R.layout.fragment_order_list_item1,null);
                    ImageView im = (ImageView) layout.findViewById(R.id.order_commdith_item_picture);
                    ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+item.getCommoditypicture(),im);
                    ((TextView)layout.findViewById(R.id.order_commdith_item_name)).setText(item.getCommodityname());
                    String specification = "颜色:"+item.getOrdercolor()+"  规格:"+item.getOrderspecification();
                    ((TextView)layout.findViewById(R.id.order_commdith_item_specification)).setText(specification);
                    ((TextView)layout.findViewById(R.id.order_commdith_item_ordernumber)).setText("X"+item.getOrdernumber());
                    ((TextView)layout.findViewById(R.id.order_commdith_item_price)).setText("￥"+item.getOrderprice());
                    parentsLayout.addView(layout);
                }
            }
            String price =  "共"+size+"件商品"+" 合计:"+obj.getOrdertotlefee()+"(含运费:"+obj.getDeliveryfee()+")";
           ((TextView)view.findViewById(R.id.order_item_price)).setText(price);
            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                notifyDataSetChanged();
            }
        }

        private void addAll(List<Order> list){
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }
}
