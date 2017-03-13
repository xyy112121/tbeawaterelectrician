package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.EvaluateListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.OrderListActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Address;
import com.tbea.tb.tbeawaterelectrician.entity.Order;
import com.tbea.tb.tbeawaterelectrician.entity.ProductInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cy on 2017/3/9.
 */

public class OrderViewActivity extends TopActivity {
    private Context mContext;
    private String mPhone;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        initTopbar("订单详情");
        mContext = this;
        getDate();
        listener();

    }

    private void listener(){
        findViewById(R.id.order_view_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
                startActivity(intent);
            }
        });


        findViewById(R.id.order_view_delect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_delete_dialog);
                dialog.show();
                dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                },"否");
                dialog.setCancelBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        delect();

                    }
                },"是");
            }
        });

    }

    //删除订单
    private void delect(){
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
                            UtilAssistants.showToast("删除成功!");
                            finish();
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
                    String id = getIntent().getStringExtra("id");
                    RspInfo1 re = userAction.delectOrder(id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();


    }

    /**
     * 获取数据
     */
    public void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            final List<Map<String, String>> commoditylist = (List<Map<String, String>>) re.getDateObj("commoditylist");
                            if (commoditylist != null) {
                                for (int i = 0; i < commoditylist.size(); i++) {
                                    List<ProductInfo> commoditylist2 = new ArrayList<>();
                                    if (commoditylist != null) {
                                        for (int j = 0; j < commoditylist.size(); j++) {
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
                                        initShopView(commoditylist2);
                                    }
                                }

                            }
                            Map<String, String> orderInfo = (Map<String, String>) re.getDateObj("orderbaseinfo");
                            if (orderInfo != null) {
                                setTextView(R.id.order_view_code, orderInfo.get("ordercode"));
                                setTextView(R.id.order_view_state, orderInfo.get("orderstatus"));
                                setTextView(R.id.order_view_pay_type, orderInfo.get("paytypename"));
                                setTextView(R.id.order_view_time, orderInfo.get("ordertime"));
                                setTextView(R.id.order_view_delivery_type, orderInfo.get("deliverytypename"));
                                setTextView(R.id.order_view_totlemoney, "￥" + orderInfo.get("totlemoney"));
                                setTextView(R.id.order_view_deliveryfee, "￥" + orderInfo.get("deliveryfee"));
                                setTextView(R.id.order_view_actualneedpaymoney, "￥" + orderInfo.get("actualneedpaymoney"));
                                mPhone = orderInfo.get("contacttbea") + "";
                                final String state = orderInfo.get("orderstatusid") + "";
                                Button btn = (Button) findViewById(R.id.order_view_btn);
                                Button btn1 = (Button) findViewById(R.id.order_view_btn1);
                                if ("havepanyed".equals(state)) {//待发货
                                    btn.setVisibility(View.GONE);
                                    btn1.setText("提醒发货");
                                } else if ("havefinished".equals(state)) {//待评价
                                    btn.setVisibility(View.VISIBLE);
                                    btn.setText("再次购买");
                                    btn1.setText("评价晒单");
                                } else if ("orderedwithnomoney".equals(state)) {//待付款
                                    btn.setVisibility(View.GONE);
                                    btn1.setText("去支付");
                                } else {
                                    btn.setVisibility(View.VISIBLE);
                                    btn.setText("再次购买");
                                    btn1.setText("查看物流");
                                }

                                btn1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if ("havepanyed".equals(state)) {//待发货
                                            String id = getIntent().getStringExtra("id");
                                            remindSendOutCommdith(id);

                                        } else if ("havefinished".equals(state)) {//待评价
                                            if (commoditylist != null) {
                                                Gson gson = new Gson();
                                                String objGson = gson.toJson(commoditylist);
                                                Intent intent = new Intent(mContext, EvaluateListActivity.class);
                                                intent.putExtra("obj", objGson);
                                                startActivity(intent);
                                            }

                                        } else if ("orderedwithnomoney".equals(state)) {//待付款

                                        } else {//待收货
                                        }
                                    }
                                });

                            }

                            Map<String, String> receiveaddrinfo = (Map<String, String>) re.getDateObj("receiveaddrinfo");
                            if (receiveaddrinfo != null) {
                                Address obj = new Address();
                                obj.setId(receiveaddrinfo.get("receiveaddrid"));
                                obj.setContactperson(receiveaddrinfo.get("contactperson"));
                                obj.setContactmobile(receiveaddrinfo.get("contactmobile"));
                                obj.setAddress(receiveaddrinfo.get("address") + "");
                                initAddrView(obj);
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
                    String id = getIntent().getStringExtra("id");
                    RspInfo re = userAction.getOrder(id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }

        ).start();
    }

    /**
     * 提醒发货
     */
    private void remindSendOutCommdith(final String id) {
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
                            UtilAssistants.showToast("提醒发货成功");
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
                    RspInfo1 re = userAction.remindSendOutCommdith(id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    private void setTextView(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    private void initShopView(List<ProductInfo> list) {
        LinearLayout parentsLayout = (LinearLayout) findViewById(R.id.order_view_shop_layout);
        parentsLayout.removeAllViews();
        for (ProductInfo item : list) {
            FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.fragment_order_list_item1, null);
            ImageView im = (ImageView) layout.findViewById(R.id.order_commdith_item_picture);
            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + item.getCommoditypicture(), im);
            ((TextView) layout.findViewById(R.id.order_commdith_item_name)).setText(item.getCommodityname());
            String specification = "颜色:" + item.getOrdercolor() + "  规格:" + item.getOrderspecification();
            ((TextView) layout.findViewById(R.id.order_commdith_item_specification)).setText(specification);
            ((TextView) layout.findViewById(R.id.order_commdith_item_ordernumber)).setText("X" + item.getOrdernumber());
            ((TextView) layout.findViewById(R.id.order_commdith_item_price)).setText("￥" + item.getOrderprice());
            parentsLayout.addView(layout);
        }
    }

    public void initAddrView(Address obj) {
        ((TextView) findViewById(R.id.addr_item_contactperson)).setText(obj.getContactperson());
        ((TextView) findViewById(R.id.addr_item_contactmobile)).setText(obj.getContactmobile());
        ((TextView) findViewById(R.id.addr_item_address)).setText(obj.getAddress());
        ((TextView) findViewById(R.id.addr_item_address)).setTextColor(ContextCompat.getColor(mContext, R.color.text_gtay2));
        findViewById(R.id.addr_item_isdefault).setVisibility(View.GONE);
        findViewById(R.id.addr_item_right_view).setVisibility(View.GONE);
    }


}
