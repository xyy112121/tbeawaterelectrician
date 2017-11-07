package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.action.PublicAction;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.model.NetUrlResponseModel;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Distributor;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;

/**
 * 积分提现
 */

public class WalletWithdrawCashActivity extends TopActivity {
    protected String mCanexChangeMoney;
    private WebView mWebView;
    private List<Distributor> mDistributorList = new ArrayList<>();
    private Context mContext;
    private String mdistributorid;

    private String mUrl="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_withdraw_cash);
        initTopbar("我要提现");
        mContext = this;
        getDate();
        findViewById(R.id.wallet_withdraw_cash_finsh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_delete_dialog);
                    dialog.setText("请您确认？");
                    dialog.setCancelBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            String money = ((EditText) findViewById(R.id.wallet_withdraw_cash_money)).getText() + "";
                            if ("".equals(money) || "0".equals(money)) {
                                Toast.makeText(WalletWithdrawCashActivity.this, "请填写提现金额！", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Double mo = Double.parseDouble(money);
                            if (mo > Double.parseDouble(mCanexChangeMoney)) {
                                UtilAssistants.showToast("不能大于最大提现金额");
                                return;
                            }
                            Intent intent = new Intent(WalletWithdrawCashActivity.this, WalletWithdrawCashViewActivity.class);
                            intent.putExtra("money", money);
                            intent.putExtra("distributorid", mdistributorid);
                            startActivity(intent);
                            finish();
                        }
                    }, "确认");
                    dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    }, "取消");
                    dialog.show();
                } catch (Exception e) {
                    Log.d("", "");
                }

            }
        });

        findViewById(R.id.wallet_withdraw_cash_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) findViewById(R.id.wallet_withdraw_cash_money)).setText(mCanexChangeMoney + "");
            }
        });

        findViewById(R.id.wallet_withdraw_cash_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getList();
                } catch (Exception e) {
                    Log.d("", "");
                }

            }
        });
    }

    /**
     * 获取经销商列表
     */
    private void getList() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            mDistributorList = (List<Distributor>) re.getDateObj("distributorlist");
                            if (mDistributorList != null) {
                                String[] dates = new String[mDistributorList.size()];
                                for (int i = 0; i < mDistributorList.size(); i++) {
                                    dates[i] = mDistributorList.get(i).getName() + " " + mDistributorList.get(i).getDistance();
                                }
                                OptionPicker mPicker = new OptionPicker((Activity) mContext, dates);
                                if (mDistributorList.size() > 1) {
                                    mPicker.setSelectedIndex(1);
                                }
                                mPicker.setOffset(1);
                                mPicker.setTextSize(16);
                                mPicker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                                mPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                                    @Override
                                    public void onOptionPicked(String option) {
                                        for (Distributor item : mDistributorList) {
                                            if (option.equals(item.getName() + " " + item.getDistance())) {
                                                mdistributorid = item.getId();
                                                initDistributorView(item);
                                                getUrl(item);
                                            }
                                        }
                                    }
                                });
                                mPicker.setAnimationStyle(R.style.PopWindowAnimationFade);
                                mPicker.show();
                            } else {
                                UtilAssistants.showToast("操作失败！");
                            }
                        } else {
                            UtilAssistants.showToast("操作失败！");
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！");
                        break;
                }
                dialog.dismiss();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getDistributoList();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private void initDistributorView(Distributor obj) {
        ((TextView) findViewById(R.id.wallet_withdraw_cash_name)).setText(obj.getName());
        ((TextView) findViewById(R.id.wallet_withdraw_cash_addrs)).setText(obj.getAddress());
        ((TextView) findViewById(R.id.wallet_withdraw_cash_mobilenumber)).setText(obj.getMobilenumber());
        ((TextView) findViewById(R.id.wallet_withdraw_cash_distance)).setText(obj.getDistance());
        mWebView = (WebView) findViewById(R.id.wallet_withdraw_cash_webwiew);
        ImageView imageView = (ImageView) findViewById(R.id.wallet_withdraw_cash_image);
        if ("-10000".equals(obj.getLongitude()) || "-10000".equals(obj.getLatitude())) {
            imageView.setImageResource(R.drawable.icon_map_no);
            imageView.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }
//        String url = MyApplication.instance.getImgPath() + "enginterface/index.php/Apph5/address?longitude=" + obj.getLongitude() + "&latitude=" + obj.getLatitude();
    }

    /**
     * 获取url
     */
    public void getUrl(final Distributor obj) {
        if (!"".equals(mUrl)) {
            //        String url = MyApplication.instance.getImgPath() + "enginterface/index.php/Apph5/address?longitude=" + obj.getLongitude() + "&latitude=" + obj.getLatitude();
            String url = mUrl + "address?longitude=" + obj.getLongitude() + "&&latitude=" + obj.getLatitude();
            showWebView(url);
            return;
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        NetUrlResponseModel re = (NetUrlResponseModel) msg.obj;
                        if (re.isSuccess() && re.data != null) {
                            mUrl = re.data.url;
                            String url = mUrl + "address?longitude=" + obj.getLongitude() + "&&latitude=" + obj.getLatitude();
                            showWebView(url);
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
                    PublicAction userAction = new PublicAction();
                    NetUrlResponseModel re = userAction.getUrl();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private void showWebView(String url) {
        WebSettings settings = mWebView.getSettings();
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //启用支持javascript
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);//解决图片加载不出来的问题
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);//允许DCOM

        mWebView.loadUrl(url);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
//                    mDialog.dismiss();
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient());
    }


    /**
     * 获取数据
     */
    public void getDate() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data1 = (Map<String, Object>) re.getData();
                            Map<String, String> data = (Map<String, String>) data1.get("mymoneyinfo");
                            if (data != null) {
                                String mMoney = data.get("currentmoney");
                                mCanexChangeMoney = data.get("canexchangemoney");
                                String text = "积分金额￥" + mMoney + ",当前可提现金额￥" + mCanexChangeMoney;
                                ((TextView) findViewById(R.id.wallet_withdraw_cash_info)).setText(text);
                            }

                            Map<String, Object> recommondDistriButorInfo = (Map<String, Object>) data1.get("recommonddistributorinfo");
                            if (recommondDistriButorInfo != null) {
                                Distributor obj = new Distributor();
                                obj.setId(recommondDistriButorInfo.get("id") + "");
                                obj.setName(recommondDistriButorInfo.get("name") + "");
                                obj.setAddress("地址:" + recommondDistriButorInfo.get("address") + "");
                                obj.setMobilenumber("电话:" + recommondDistriButorInfo.get("mobilenumber") + "");
                                obj.setDistance(recommondDistriButorInfo.get("distance") + "");
                                obj.setLatitude(recommondDistriButorInfo.get("latitude") + "");
                                obj.setLongitude(recommondDistriButorInfo.get("longitude") + "");
                                mdistributorid = obj.getId();
                                initDistributorView(obj);
                                getUrl(obj);
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
                    RspInfo1 re = userAction.getCanexChangeMoney();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

}
