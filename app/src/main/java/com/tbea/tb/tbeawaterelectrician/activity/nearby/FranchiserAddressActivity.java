package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by Administrator on 2017/3/29.
 */

public class FranchiserAddressActivity extends TopActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_franchiser_addr_webview);
        initTopbar("详细地址");
        mWebView = (WebView) findViewById(R.id.franchiser_addr_webview);
        ImageView imageView = (ImageView) findViewById(R.id.franchiser_addr_image);

        String latitude = getIntent().getStringExtra("latitude");
        String longitude = getIntent().getStringExtra("longitude");
        if ("-10000".equals(latitude) || "-10000".equals(longitude)) {
            imageView.setImageResource(R.drawable.icon_map_no);
            imageView.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        }else {
            imageView.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }
        String url = MyApplication.instance.getImgPath() + "enginterface/index.php/Apph5/address?longitude=" + longitude + "&latitude=" + latitude;
        WebSettings settings = mWebView.getSettings();
        //启用支持javascript
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);//解决图片加载不出来的问题
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);//允许DCOM
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}
