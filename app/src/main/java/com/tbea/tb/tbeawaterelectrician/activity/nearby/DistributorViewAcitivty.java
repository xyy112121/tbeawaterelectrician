package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.AddressEditListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.action.PublicAction;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.model.NetUrlResponseModel;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * 经销商
 */

public class DistributorViewAcitivty extends TopActivity {
    private WebView mWebView;
    private String id;
    private Context mContext;

    CustomDialog mDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_take_view);
        mContext = this;
        initTopbar("经销商详情");
        id = getIntent().getStringExtra("id");
        mWebView = (WebView) findViewById(R.id.web_view);
        mDialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        mDialog.setText("加载中...");
        mDialog.show();
        getDate();

        //   String url = "http://www.u-shang.net/enginterface/index.php/Apph5/business?companyid="+id
//        +"&&userid="+ MyApplication.instance.getUserId()+"&&longitude="+MyApplication.instance.getLongitude()
//                +"&&latitude="+MyApplication.instance.getLatitude();
    }

    /**
     * 获取url
     */
    public void getDate() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        NetUrlResponseModel re = (NetUrlResponseModel) msg.obj;
                        if (re.isSuccess() && re.data != null) {
                            String url = re.data.url + "business?companyid=" + id + "&&latitude=" + MyApplication.instance.getLatitude() + "&&longitude=" + MyApplication.instance.getLongitude();
                            showWebView(url);
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

        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new MyWebViewClient());

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    mDialog.dismiss();
                }
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("distributorphone")) {
                String phone = url.substring(url.indexOf("_") + 1, url.length());
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);
                return true;
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
}
