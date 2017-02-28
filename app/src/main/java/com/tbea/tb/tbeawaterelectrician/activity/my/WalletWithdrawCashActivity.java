package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;
import java.util.Map;

/**
 *积分提现
 */

public class WalletWithdrawCashActivity extends TopActivity {
    protected Double mCanexChangeMoney;
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_withdraw_cash);
        initTopbar("我要提现");
        getDate();
        findViewById(R.id.wallet_withdraw_cash_finsh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = ((EditText)findViewById(R.id.wallet_withdraw_cash_money)).getText()+"";
                if("".equals(money)){
                    Toast.makeText(WalletWithdrawCashActivity.this,"请填写提现金额！",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(WalletWithdrawCashActivity.this,WalletWithdrawCashViewActivity.class);
                intent.putExtra("money",money);
                startActivity(intent);
            }
        });

        findViewById(R.id.wallet_withdraw_cash_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)findViewById(R.id.wallet_withdraw_cash_money)).setText(mCanexChangeMoney+"");
            }
        });
    }

    /**
     * 获取数据
     */
    public void getDate(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Map<String, Object> data1 = (Map<String, Object>) re.getData();
                            Map<String, Object> data = (Map<String, Object>) data1.get("mymoneyinfo");
                            if(data != null){
                                String mMoney = data.get("currentmoney")+"";
                                mCanexChangeMoney = (Double) data.get("canexchangemoney");
                                String text = "积分金额￥"+mMoney+",当前可提现金额￥"+ mCanexChangeMoney;
                                ((TextView)findViewById(R.id.wallet_withdraw_cash_info)).setText(text);
                            }

                            Map<String, Object> recommondDistriButorInfo = (Map<String, Object>) data1.get("recommonddistributorinfo");
                            if(recommondDistriButorInfo != null){
                                ((TextView)findViewById(R.id.wallet_withdraw_cash_name)).setText(recommondDistriButorInfo.get("name")+"");
                                ((TextView)findViewById(R.id.wallet_withdraw_cash_addrs)).setText("地址:"+recommondDistriButorInfo.get("address")+"");
                                ((TextView)findViewById(R.id.wallet_withdraw_cash_mobilenumber)).setText("电话:"+recommondDistriButorInfo.get("mobilenumber")+"");
                                ((TextView)findViewById(R.id.wallet_withdraw_cash_distance)).setText(recommondDistriButorInfo.get("distance")+"");
                                String longitude = recommondDistriButorInfo.get("longitude")+"";
                                String latitude = recommondDistriButorInfo.get("latitude")+"";

                                String url = MyApplication.instance.getImgPath()+ "enginterface/index.php/Apph5/address?longitude="+longitude+"&latitude="+latitude;
                                mWebView = (WebView)findViewById(R.id.wallet_withdraw_cash_webwiew);
                                WebSettings settings = mWebView.getSettings();
                                //自适应屏幕
//                                settings.setUseWideViewPort(true);
//                                settings.setLoadWithOverviewMode(true);
                                //启用支持javascript
                                settings.setJavaScriptEnabled(true);
                                settings.setBlockNetworkImage(false);//解决图片加载不出来的问题
                                settings.setJavaScriptEnabled(true);
                                settings.setAllowFileAccess(true);
                                settings.setDomStorageEnabled(true);//允许DCOM

//                                webSettings.setAllowFileAccess(true);
//                                webSettings.setDomStorageEnabled(true);
//                                webSettings.setJavaScriptEnabled(true);
//                                webSettings.setBlockNetworkImage(false);//解决图片加载不出来的问题
//                                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//                                webSettings.setLoadsImagesAutomatically(true);
//                                webSettings.setDatabaseEnabled(true);
//                                webSettings.setGeolocationEnabled(true);
//                                webSettings.setSupportZoom(true);
//                                webSettings.setBuiltInZoomControls(true);
//                                webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//                                webSettings.setUseWideViewPort(true);// 设置是当前html界面自适应屏幕
//                                webView.setWebViewClient(new MyWebViewClient());
//                                webView.loadUrl("http://baidu.com");
                                mWebView.loadUrl(url);
                                mWebView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        view.loadUrl(url);
                                        return super.shouldOverrideUrlLoading(view, url);
                                    }
                                });

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
                    RspInfo1 re = userAction.getCanexChangeMoney();
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private class MyWebViewClient extends WebViewClient {
        //重写父类方法，让新打开的网页在当前的WebView中显示
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }//扩充缓存的容量
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
            //handler.cancel(); 默认的处理方式，WebView变成空白页
            //          //接受证书
            handler.proceed();
        }

    }
}
