package com.tbea.tb.tbeawaterelectrician.activity.publicUse.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.action.PublicAction;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.model.NetUrlResponseModel;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * webView公用界面（需要先去服务器获取url）
 */

public class NetWebViewActivity extends TopActivity {
    WebView mWebView;

    CustomDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plumber_meeting_view_plan);
        mDialog = new CustomDialog(this, R.style.MyDialog, R.layout.tip_wait_dialog);
        mDialog.setText("加载中...");
        mDialog.show();
        mWebView = (WebView) findViewById(R.id.web_view);
        initTopbar(getIntent().getStringExtra("title"));
        getDate();
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
                            String parameter = getIntent().getStringExtra("parameter");
                            String url = re.data.url+parameter;
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
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);//允许DCOM

        mWebView.loadUrl(url);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    mDialog.dismiss();
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient());
    }
}
