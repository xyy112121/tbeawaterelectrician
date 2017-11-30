package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.AddressEditListActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Address;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cy on 2017/2/16.
 */

public class OrderEditActivity extends TopActivity {
    private String mReceiveaddrId;//收货的地址id
    private String mPaytypeId;//支付类型id
    private String mDeliverytypeId;//发货类型id
    private float mActualNeedPayMoney;//实际支付金额
    private float mTotleMoney;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_edit);
        MyApplication.instance.addActivity(this);
        mContext = this;
        initTopbar("填写订单");
        findViewById(R.id.addr_item_isdefault).setVisibility(View.GONE);
        getDate();
        listener();
        initShop();
    }

    /**
     * 商品详情
     */
    private void initShop() {
        String orderdetailidlist = getIntent().getStringExtra("orderdetailidlist");
        Gson gson = new Gson();
        List<OrderDetailid> list = gson.fromJson(orderdetailidlist, new TypeToken<List<OrderDetailid>>() {
        }.getType());
        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.order_edit_shop_layout);
        if (list != null && list.size() > 0) {
            for (OrderDetailid item : list) {
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_order_edit_shopimage_layout, null);
                ImageView imageView = (ImageView) layout.findViewById(R.id.order_edit_shop_imageview);
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + item.url, imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                parentLayout.addView(layout);
            }
        }

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

    private void listener() {
        findViewById(R.id.addr_edit_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddressEditListActivity.class);
                intent.putExtra("flag", "select");
                startActivityForResult(intent, 100);
            }
        });

        findViewById(R.id.order_view_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planOrder();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
            String jsonObj = data.getStringExtra("obj");
            Gson gson = new Gson();
            Address address = gson.fromJson(jsonObj, Address.class);
            initAddrView(address);
        }
    }

    /**
     * 下单
     */
    public void planOrder() {
        if ("".equals(mReceiveaddrId) || mReceiveaddrId == null) {
            UtilAssistants.showToast("请增加收货地址");
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
                            Map<String, Object> date = (Map<String, Object>) re.getData();
                            Map<String, String> map = (Map<String, String>) date.get("userorderinfo");
                            String orderid = map.get("orderid");
                            String ordercode = map.get("ordercode");
                            String deliverytype = map.get("deliverytype");
                            String paytype = map.get("paytype");
                            String actualneedpaymoney = map.get("actualneedpaymoney");
                            Intent intent = new Intent(mContext, OrderScuessViewActivity.class);
                            intent.putExtra("actualneedpaymoney", actualneedpaymoney);
                            intent.putExtra("deliverytype", deliverytype);
                            intent.putExtra("paytype", paytype);
                            intent.putExtra("ordercode", ordercode);
                            intent.putExtra("orderid", orderid);
                            startActivity(intent);
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
                    String orderdetailidlist = getIntent().getStringExtra("orderdetailidlist");
                    String ordernote = ((TextView) findViewById(R.id.order_view_ordernote)).getText() + "";
                    RspInfo1 re = userAction.placeOnOrder(orderdetailidlist, mReceiveaddrId, mPaytypeId, mDeliverytypeId, ordernote, mActualNeedPayMoney + "");
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

                            Map<String, String> receiveaddrinfo = (Map<String, String>) re.getDateObj("receiveaddrinfo");
                            if (receiveaddrinfo != null) {
                                Address address = new Address();
                                address.setId(receiveaddrinfo.get("receiveaddrid"));
                                address.setContactperson(receiveaddrinfo.get("contactperson"));
                                address.setContactmobile(receiveaddrinfo.get("contactmobile"));
                                address.setAddress(receiveaddrinfo.get("address"));
                                initAddrView(address);
                            }

                            List<Map<String, String>> paytypelist = (List<Map<String, String>>) re.getDateObj("paytypelist");
                            List<Condition> payTypeList = new ArrayList<>();
                            if (paytypelist != null) {
                                for (int i = 0; i < paytypelist.size(); i++) {
                                    Condition obj = new Condition();
                                    obj.setId(paytypelist.get(i).get("paytypeid"));
                                    obj.setName(paytypelist.get(i).get("paytypename"));
                                    payTypeList.add(obj);
                                }
                                initPayTypeView(payTypeList);
                            }

                            List<Map<String, String>> deliverytypelist = (List<Map<String, String>>) re.getDateObj("deliverytypelist");
                            mTotleMoney = Float.valueOf(re.getDateObj("totlemoney") + "");
                            List<DeliveryType> deliveryTypeList = new ArrayList<>();
                            if (deliverytypelist != null) {
                                for (int i = 0; i < deliverytypelist.size(); i++) {
                                    DeliveryType obj = new DeliveryType();
                                    obj.setDeliverytypeid(deliverytypelist.get(i).get("deliverytypeid"));
                                    obj.setDeliverytypename(deliverytypelist.get(i).get("deliverytypename"));
                                    obj.setDeliveryfee(Float.valueOf(deliverytypelist.get(i).get("deliveryfee")));
                                    deliveryTypeList.add(obj);
                                }
                                initDeliveryTypeView(deliveryTypeList);
                            }
                            ((TextView) findViewById(R.id.order_view_totlemoney)).setText("￥" + mTotleMoney);
                            ((TextView) findViewById(R.id.order_view_promotioninfo)).setText(re.getDateObj("promotioninfo") + "");


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
                    String orderdetailidlist = getIntent().getStringExtra("orderdetailidlist");
                    Gson gson = new Gson();
                    List<OrderDetailid> list = gson.fromJson(orderdetailidlist, new TypeToken<List<OrderDetailid>>() {
                    }.getType());
                    StringBuilder sb = new StringBuilder();
                    for (OrderDetailid item : list) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(item.getOrderdetailid());
                    }
                    RspInfo re = userAction.getOrderInfo(sb.toString());
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    public void initDeliveryTypeView(final List<DeliveryType> list) {
        RadioGroup rg = (RadioGroup) findViewById(R.id.order_view_delivery_type);
        for (int i = 0; i < list.size(); i++) {
            DeliveryType item = list.get(i);
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.activity_order_view_rb, null);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 10, 10);//设置边距
            radioButton.setLayoutParams(lp);
            radioButton.setText(item.getDeliverytypename());
            radioButton.setTag(item);
            rg.addView(radioButton);
        }
        ((RadioButton) rg.getChildAt(0)).setChecked(true);
        mDeliverytypeId = list.get(0).getDeliverytypeid();
        ((TextView) findViewById(R.id.order_view_deliveryfee)).setText("￥" + list.get(0).getDeliveryfee());
        mActualNeedPayMoney = mTotleMoney + list.get(0).getDeliveryfee();
        ((TextView) findViewById(R.id.order_view_all_totlemoney)).setText("实付款:￥" + mActualNeedPayMoney);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                DeliveryType obj = (DeliveryType) radioButton.getTag();
                mDeliverytypeId = obj.getDeliverytypeid();
                ((TextView) findViewById(R.id.order_view_deliveryfee)).setText("￥" + obj.getDeliveryfee());
                mActualNeedPayMoney = mTotleMoney + obj.getDeliveryfee();
                ((TextView) findViewById(R.id.order_view_all_totlemoney)).setText("￥" + mActualNeedPayMoney);
            }
        });
    }

    public void initPayTypeView(List<Condition> list) {
        RadioGroup rg = (RadioGroup) findViewById(R.id.order_view_pay_type);
        for (int i = 0; i < list.size(); i++) {
            Condition item = list.get(i);
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.activity_order_view_rb, null);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 10, 10);//设置边距
            radioButton.setLayoutParams(lp);
            radioButton.setText(item.getName());
            radioButton.setTag(item.getId());
            rg.addView(radioButton);
        }
        ((RadioButton) rg.getChildAt(0)).setChecked(true);
        mPaytypeId = list.get(0).getId();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                mPaytypeId = radioButton.getTag() + "";
            }
        });
    }

    public void initAddrView(Address obj) {
        mReceiveaddrId = obj.getId();
//        (findViewById(R.id.addr_item_contactperson)).setTag(obj.getId());
        ((TextView) findViewById(R.id.addr_item_contactperson)).setText(obj.getContactperson());
        ((TextView) findViewById(R.id.addr_item_contactmobile)).setText(obj.getContactmobile());
        ((TextView) findViewById(R.id.addr_item_address)).setText(obj.getAddress());
        ((TextView) findViewById(R.id.addr_item_address)).setTextColor(ContextCompat.getColor(mContext, R.color.text_gtay2));
        findViewById(R.id.addr_item_isdefault).setVisibility(View.GONE);
    }

    private class DeliveryType {
        private String deliverytypeid;
        private String deliverytypename;
        private float deliveryfee;

        public float getDeliveryfee() {
            return deliveryfee;
        }

        public void setDeliveryfee(float deliveryfee) {
            this.deliveryfee = deliveryfee;
        }

        public String getDeliverytypeid() {
            return deliverytypeid;
        }

        public void setDeliverytypeid(String deliverytypeid) {
            this.deliverytypeid = deliverytypeid;
        }

        public String getDeliverytypename() {
            return deliverytypename;
        }

        public void setDeliverytypename(String deliverytypename) {
            this.deliverytypename = deliverytypename;
        }
    }
}


