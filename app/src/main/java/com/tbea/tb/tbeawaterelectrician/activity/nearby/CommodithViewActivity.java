package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;

/**
 * Created by cy on 2017/2/8.
 */

public class CommodithViewActivity extends Activity {
    private Context mContext;
    private  WebView mWebView;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodith_view);
        mContext = this;
        initView();
        listener();
    }

    public  void initView(){
        mWebView = (WebView)findViewById(R.id.web_view);
        id = getIntent().getStringExtra("id");
        String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid="+id;
        showWebView(url);
    }

    /**
     * 修改显示的界面
     * @param url
     */
    public  void showWebView(String url){
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

        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

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

    public  void listener(){
        //商品
        findViewById(R.id.text1_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.text1_view).setVisibility(View.VISIBLE);
                findViewById(R.id.text2_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.text3_view).setVisibility(View.INVISIBLE);
                String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid="+id;
                showWebView(url);
            }
        });

        /**
         * 详情
         */
        findViewById(R.id.text2_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.text2_view).setVisibility(View.VISIBLE);
                findViewById(R.id.text1_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.text3_view).setVisibility(View.INVISIBLE);
                String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditydetail?commodityid="+id;
                showWebView(url);
            }
        });

        /**
         * 评价
         */
        findViewById(R.id.text3_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.text3_view).setVisibility(View.VISIBLE);
                findViewById(R.id.text1_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.text2_view).setVisibility(View.INVISIBLE);
            }
        });

        /**
         * 加入购物车
         */
        findViewById(R.id.add_shop_car_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.body_bg_view).setVisibility(View.VISIBLE);
                View view1 = (View)findViewById(R.id.add_shop_car_layout);
                view1.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.in_bottomtotop);
                animation.setFillAfter(true);
                view1.setAnimation(animation);
            }
        });

        /**
         * 加入购物车
         */
        findViewById(R.id.add_shop_car_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.body_bg_view).setVisibility(View.VISIBLE);
                View view1 = (View)findViewById(R.id.add_shop_car_layout);
                view1.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.in_bottomtotop);
                animation.setFillAfter(true);
                view1.setAnimation(animation);
            }
        });

        findViewById(R.id.add_shop_car_layout_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoneAddShopCarView();
            }
        });

        findViewById(R.id.body_bg_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoneAddShopCarView();
            }
        });

        findViewById(R.id.add_shop_car_layout_finish_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoneAddShopCarView();
            }
        });

        findViewById(R.id.commodith_view_SC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ShoppingCartActivity.class);
                startActivity(intent);
            }
        });
    }

    public void GoneAddShopCarView(){
        findViewById(R.id.body_bg_view).setVisibility(View.GONE);
        View view1 = (View)findViewById(R.id.add_shop_car_layout);
        view1.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.out_toptobottom);
//        animation.setFillAfter(true);
        view1.setAnimation(animation);
    }
}
