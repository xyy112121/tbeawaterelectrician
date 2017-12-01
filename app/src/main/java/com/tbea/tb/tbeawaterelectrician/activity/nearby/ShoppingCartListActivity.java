package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.ProductInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 购物车
 */

public class ShoppingCartListActivity extends TopActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {
    private ListView mListView;
    private MyAdapter mAdapter;
    private Context mContext;
    private BGARefreshLayout mRefreshLayout;
    private List<OrderDetailid> mSelectIds = new ArrayList<>();
    private int mPage = 1;
    private int mPagesiz = 40;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_list);
        MyApplication.instance.addActivity(this);
        mContext = this;
        initTopbar("购物车", "编辑", this);
        initUI();
        listener();
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
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Map<String, String>> list = (List<Map<String, String>>) re.getDateObj("commoditylist");
                            List<ProductInfo> companyList = new ArrayList<>();
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    ProductInfo obj = new ProductInfo();
                                    obj.setOrderdetailid(list.get(i).get("orderdetailid"));
                                    obj.setCommodityid(list.get(i).get("commodityid"));
                                    obj.setCommodityname(list.get(i).get("commodityname"));
                                    obj.setCommoditypicture(list.get(i).get("commoditypicture"));
                                    obj.setOrdercolorid(list.get(i).get("ordercolorid"));
                                    obj.setOrdercolor(list.get(i).get("ordercolor"));
                                    obj.setOrderspecificationid(list.get(i).get("orderspecificationid"));
                                    obj.setOrderspecification(list.get(i).get("orderspecification"));
                                    obj.setOrderprice(Float.parseFloat(list.get(i).get("orderprice")));
                                    obj.setOrdernumber(Integer.parseInt(list.get(i).get("ordernumber")));
                                    obj.setOrdertime(list.get(i).get("ordertime"));
                                    companyList.add(obj);
                                }
                                mAdapter.addAll(companyList);
                            } else {
                                if (mPage > 1) {//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }
                        } else {
                            UtilAssistants.showToast(re.getMsg(),mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！",mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getShopCarList(mPage++, mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    public void initUI() {
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));
        mRefreshLayout.beginRefreshing();
    }

    public void listener() {
        ((CheckBox) findViewById(R.id.all_chekbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mAdapter.selectAll();
                    ((CheckBox) findViewById(R.id.delect_all_chekbox)).setChecked(true);
                    ((CheckBox) findViewById(R.id.all_chekbox)).setText("全不选");
                    ((CheckBox) findViewById(R.id.delect_all_chekbox)).setText("全不选");
                } else {
                    mAdapter.selectAllNo();
                    ((CheckBox) findViewById(R.id.delect_all_chekbox)).setChecked(false);
                    ((CheckBox) findViewById(R.id.all_chekbox)).setText("全选");
                    ((CheckBox) findViewById(R.id.delect_all_chekbox)).setText("全选");
                }
            }
        });

        ((CheckBox) findViewById(R.id.delect_all_chekbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mAdapter.selectAll();
                    ((CheckBox) findViewById(R.id.all_chekbox)).setChecked(true);
                    ((CheckBox) findViewById(R.id.all_chekbox)).setText("全不选");
                    ((CheckBox) findViewById(R.id.delect_all_chekbox)).setText("全不选");

                } else {
                    mAdapter.selectAllNo();
                    ((CheckBox) findViewById(R.id.all_chekbox)).setChecked(false);
                    ((CheckBox) findViewById(R.id.all_chekbox)).setText("全选");
                    ((CheckBox) findViewById(R.id.delect_all_chekbox)).setText("全选");
                }
            }
        });

        findViewById(R.id.delect_tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_delete_dialog);
                dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                }, "取消");
                dialog.setCancelBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        delect();
                    }
                }, "确定");
                dialog.show();
            }
        });

        findViewById(R.id.tv_go_to_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectIds.size() > 0) {
                    Gson gson = new Gson();
                    String objJson = gson.toJson(mSelectIds);
                    Intent intent = new Intent(mContext, OrderEditActivity.class);
                    intent.putExtra("orderdetailidlist", objJson);
                    startActivity(intent);
                } else {
                    UtilAssistants.showToast("您至少需要选择一个产品！",mContext);
                }

            }
        });

        //分享
        findViewById(R.id.delete_tv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectIds.size() > 0 && mSelectIds.size() < 2) {
                    getShareInfo(mSelectIds.get(0).getOrderdetailid());

//                    String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid="+mSelectIds.get(0).getOrderdetailid();
//                    UMWeb  web = new UMWeb(url);
//                    web.setTitle("This is web title");
//                    web.setThumb(new UMImage(mContext, R.drawable.icon_about));
//                    web.setDescription("my description");
//
//                    new ShareAction((Activity) mContext)
//                            .withMedia(web)
//                            .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
//                            .setCallback(umShareListener).open();

                } else {
                    UtilAssistants.showToast("您需要选择一个产品！",mContext);
                }


//                new ShareAction((Activity) mContext).withText("hello")
//                        .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
//                        .setCallback(umShareListener).open();
            }
        });
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            UtilAssistants.showToast("分享成功啦",mContext);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            UtilAssistants.showToast("分享失败啦" + t.getMessage(),mContext);
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            UtilAssistants.showToast("分享取消啦",mContext);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 分享
     *
     * @return
     */
    private void getShareInfo(final String id) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == ThreadState.SUCCESS) {
                    RspInfo1 rsp = (RspInfo1) msg.obj;
                    if (rsp.isSuccess()) {
                        Map<String, Object> map = (Map<String, Object>) rsp.getData();
                        Map<String, String> list = (Map<String, String>) map.get("shareinfo");
                        if (list != null) {
                            String description = list.get("description");
                            String picture = list.get("picture");
                            String title = list.get("title");
                            String url = list.get("url");
                            UMWeb web = new UMWeb(url);
                            web.setTitle(title);
                            web.setThumb(new UMImage(mContext, MyApplication.instance.getImgPath() + picture));
                            web.setDescription(description);

                            new ShareAction((Activity) mContext)
                                    .withMedia(web)
                                    .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                                    .setCallback(umShareListener).open();

                        }

                    }


                } else if (msg.what == ThreadState.ERROR) {
                    UtilAssistants.showToast("分享失败，请重试！",mContext);
                }

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction action = new UserAction();
                    RspInfo1 re = action.getShareInfo("commodity", id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);

                }


            }
        }).start();

    }

    /**
     * 选择的产品
     */
    private class OrderDetailid {
        private String orderdetailid;
        private int ordernumber;
        private String url;

        public OrderDetailid(String id, int number, String url) {
            this.orderdetailid = id;
            this.ordernumber = number;
            this.url = url;
        }

        public String getOrderdetailid() {
            return orderdetailid;
        }
    }

    private void delect() {
        if (mSelectIds.size() < 1) {
            UtilAssistants.showToast("您至少需要选择一个产品！",mContext);
            return;
        }
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            mRefreshLayout.beginRefreshing();
                        } else {
                            UtilAssistants.showToast(re.getMsg(),mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！",mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    StringBuilder sb = new StringBuilder();
                    for (OrderDetailid item : mSelectIds) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(item.getOrderdetailid());
                    }
                    RspInfo1 re = userAction.delectShopCar(sb.toString());
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();


    }

    /**
     * 设置结算数量和金额
     *
     * @param count
     * @param price
     */
    public void setPayCount(int count, Double price) {
        ((TextView) findViewById(R.id.tv_go_to_pay)).setText("去结算(" + count + ")");
        String result = String.format("%.2f", price);
        ((TextView) findViewById(R.id.tv_total_price)).setText(result);
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        if ("编辑".equals(textView.getText())) {
            textView.setText("完成");
            findViewById(R.id.tv_go_to_pay_layout).setVisibility(View.GONE);
            findViewById(R.id.shop_car_delect_layout).setVisibility(View.VISIBLE);
        } else {
            textView.setText("编辑");
            findViewById(R.id.tv_go_to_pay_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.shop_car_delect_layout).setVisibility(View.GONE);
        }

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
        mPage = 1;
        mAdapter.removeAll();
        getListDate();

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        getListDate();
        return false;
    }

    private class MyAdapter extends BaseAdapter {

        private Context mContext;
        private List<ProductInfo> mList = new ArrayList<>();

        public MyAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            final ChildHolder cholder;
            if (convertView == null) {
                cholder = new ChildHolder();
                convertView = (View) inflater.inflate(R.layout.activity_shoping_cart_list_item, null);
                cholder.cb_check = (CheckBox) convertView.findViewById(R.id.check_box);
                cholder.tv_count = (TextView) convertView.findViewById(R.id.tv_num);
                cholder.iv_increase = (TextView) convertView.findViewById(R.id.tv_add);
                cholder.iv_decrease = (TextView) convertView.findViewById(R.id.tv_reduce);
                cholder.tv_product_name = (TextView) convertView.findViewById(R.id.shop_car_item_commodityname);
                cholder.tv_product_orderspecification_color = (TextView) convertView.findViewById(R.id.shop_car_item_orderspecification_color);
                cholder.tv_product_price = (TextView) convertView.findViewById(R.id.shop_car_item_orderprice);
                cholder.iv_product_picture = (ImageView) convertView.findViewById(R.id.shop_car_item_picture);
                convertView.setTag(cholder);
            } else {
                // convertView = childrenMap.get(groupPosition);
                cholder = (ChildHolder) convertView.getTag();
            }

            final ProductInfo obj = mList.get(i);
            cholder.tv_product_name.setText(obj.getCommodityname());
            cholder.tv_product_orderspecification_color.setText("颜色:" + obj.getOrdercolor() + "  规格:" + obj.getOrderspecification());
            cholder.tv_product_price.setText("￥ " + obj.getOrderprice());
            cholder.tv_count.setText(obj.getOrdernumber() + "");
            if (!"".equals(obj.getCommoditypicture())) {
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.getCommoditypicture(), cholder.iv_product_picture);
            }

            cholder.cb_check.setChecked(obj.isChoosed());
            cholder.cb_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obj.setChoosed(((CheckBox) v).isChecked());
                    cholder.cb_check.setChecked(((CheckBox) v).isChecked());

                    String price = ((TextView) findViewById(R.id.tv_total_price)).getText() + "";
                    Double price2 = Double.valueOf(price);
                    if (((CheckBox) v).isChecked()) {
                        OrderDetailid orderDetailid = new OrderDetailid(obj.getOrderdetailid(), obj.getOrdernumber(), obj.getCommoditypicture());
                        mSelectIds.add(orderDetailid);
                        price2 = price2 + (obj.getOrdernumber() * obj.getOrderprice());
                    } else {
                        for (int i = 0; i < mSelectIds.size(); i++) {
                            if (mSelectIds.get(i).getOrderdetailid().equals(obj.getOrderdetailid())) {
//                              list.add(i);
                                mSelectIds.remove(i);
                                i--;
                            }
                        }
                        price2 = price2 - (obj.getOrdernumber() * obj.getOrderprice());
                    }

//                    for (int i = 0; i < mSelectIds.size(); i++) {
//                        price = price + (mSelectIds.get(i).getOrdernumber() * obj.getOrderprice());
//                    }
                    setPayCount(mSelectIds.size(), price2);
                }
            });
            cholder.iv_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = obj.getOrdernumber();
                    cholder.tv_count.setText(++count + "");
                    obj.setOrdernumber(count);
                    if (obj.isChoosed()) {
                        Double price = 0.0;
                        for (int i = 0; i < mSelectIds.size(); i++) {
                            price = price + (obj.getOrdernumber() * obj.getOrderprice());
                        }
                        setPayCount(mSelectIds.size(), price);
                    }
                }
            });

            cholder.tv_count.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (!"".equals(s) && s != null) {
                            obj.setOrdernumber(Integer.parseInt(s + ""));
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
            cholder.iv_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = obj.getOrdernumber();
                    if (count > 1) {
                        cholder.tv_count.setText(--count + "");
                        obj.setOrdernumber(count);
                        if (obj.isChoosed()) {
                            Double price = 0.0;
                            for (int i = 0; i < mSelectIds.size(); i++) {
                                price = price + (obj.getOrdernumber() * obj.getOrderprice());
                            }
                            setPayCount(mSelectIds.size(), price);
                        }
                    }
                }
            });

            return convertView;
        }

        private void addAll(List<ProductInfo> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void selectAll() {
            mSelectIds.clear();
            if (mList != null && mList.size() > 0) {
                Double price = 0.0;
                for (ProductInfo item : mList) {
                    item.setChoosed(true);
                    OrderDetailid orderDetailid = new OrderDetailid(item.getOrderdetailid(), item.getOrdernumber(), item.getCommoditypicture());
                    mSelectIds.add(orderDetailid);
                    int count = item.getOrdernumber();
                    price = price + (count * item.getOrderprice()
                    );
                }
                setPayCount(mSelectIds.size(), price);
                notifyDataSetChanged();
            }
        }

        public void selectAllNo() {
            if (mList != null && mList.size() > 0) {
                for (ProductInfo item : mList) {
                    item.setChoosed(false);
                    mSelectIds.clear();
                }
                setPayCount(mSelectIds.size(), 0.0);
                notifyDataSetChanged();
            }

        }

        public void removeAll() {
            mSelectIds.clear();
            mList.clear();
            notifyDataSetChanged();
        }

        /**
         * 子元素绑定器
         */
        private class ChildHolder {
            CheckBox cb_check;
            TextView tv_product_name;
            TextView tv_product_orderspecification_color;
            TextView tv_product_price;
            ImageView iv_product_picture;
            TextView iv_increase;
            TextView tv_count;
            TextView iv_decrease;
            TextView tv_delete;
        }
    }
}
