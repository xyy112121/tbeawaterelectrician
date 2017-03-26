package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.AddressEditListActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;

/**
 * 经销商
 */

public class DistributorViewAcitivty extends TopActivity{
    private WebView mWebView;
    private String id;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_take_view);
        mContext = this;
        initTopbar("经销商详情");
        id = getIntent().getStringExtra("id");
        mWebView = (WebView)findViewById(R.id.web_view);
        final CustomDialog mDialog= new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        mDialog.setText("加载中...");
        mDialog.show();
        WebSettings settings = mWebView.getSettings();
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //启用支持javascript
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);//解决图片加载不出来的问题
        String url = "http://www.u-shang.net/enginterface/index.php/Apph5/business?companyid="+id
                +"&&userid="+ MyApplication.instance.getUserId()+"&&longitude="+MyApplication.instance.getLongitude()
                +"&&latitude="+MyApplication.instance.getLatitude();
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
            if(url.contains("distributorphone")){
                String phone = url.substring(url.indexOf("_",url.length()));
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ phone));
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
