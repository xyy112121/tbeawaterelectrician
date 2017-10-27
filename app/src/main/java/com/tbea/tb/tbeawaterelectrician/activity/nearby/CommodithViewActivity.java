package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.account.LoginActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.AddressEditListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.action.PublicAction;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.model.NetUrlResponseModel;
import com.tbea.tb.tbeawaterelectrician.component.BadgeView;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.component.FlexRadioGroup;
import com.tbea.tb.tbeawaterelectrician.entity.Address;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.entity.Evaluate;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 商品详细信息
 */

public class CommodithViewActivity extends Activity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private Context mContext;
    private WebView mWebView;
    private String id;
    private FlexRadioGroup mColorRG;
    private FlexRadioGroup mSpecificationsRG;
    private float mWidth;
    private String mSpecificationId;
    private String mColorId;
    private String mDistributorid;//经销商id
    private ListView mListView;
    private EvaluateAdapter mAdapter;
    private int mPage = 1;
    private int mPagesiz = 10;
    private BGARefreshLayout mRefreshLayout;
    private List<OrderDetailid> mSelectIds = new ArrayList<>();
    private String mFlag = "pay";//pay立即购买，add加入购物车
    private BadgeView mBadgeView;
    private String mUrl;

    private CustomDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.instance.addActivity(this);
        setContentView(R.layout.activity_commodith_view);
        mContext = this;
        mDialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        mDialog.setText("加载中...");
        mDialog.show();
        initView();
        listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getShopCarNumber();
    }

    public void initView() {
        mWebView = (WebView) findViewById(R.id.web_view);
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new EvaluateAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));

        id = getIntent().getStringExtra("id");
//        String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid="+id
//                + "&&userid="+ MyApplication.instance.getUserId()+"&&longitude="+MyApplication.instance.getLongitude()
//                +"&&latitude="+MyApplication.instance.getLatitude();;
//        showWebView(url);
        getUrl();
        mColorRG = (FlexRadioGroup) findViewById(R.id.commodith_view_color_rg);
        mSpecificationsRG = (FlexRadioGroup) findViewById(R.id.commodith_view_specifications_rg);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDistributorid = getIntent().getStringExtra("distributorid");
        mWidth = metrics.density;

    }

    /**
     * 获取url
     */
    public void getUrl() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        NetUrlResponseModel re = (NetUrlResponseModel) msg.obj;
                        if (re.isSuccess() && re.data != null) {
                            mUrl = re.data.url;
//                            String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid=" + id
//                                    + "&&userid=" + MyApplication.instance.getUserId() + "&&longitude=" + MyApplication.instance.getLongitude()
//                                    + "&&latitude=" + MyApplication.instance.getLatitude();
                            String url = mUrl + "commoditysaleinfo?commodityid" + id + "&&userid=" + MyApplication.instance.getUserId();
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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("selectspecification.com")) {
                mFlag = "add";
                showShopInfo();
                return true;
            }
            if (url.contains("selectaddr.com")) {
                Intent intent = new Intent(mContext, AddressEditListActivity.class);
                intent.putExtra("flag", "select");
                startActivityForResult(intent, 100);
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

    /**
     * 修改显示的界面
     *
     * @param url
     */
    public void showWebView(String url) {
        mRefreshLayout.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
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
        mWebView.setWebViewClient(new MyWebViewClient());
//        mWebView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//        });

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

    public void listener() {
        //商品
        findViewById(R.id.text1_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.text1_view).setVisibility(View.VISIBLE);
                findViewById(R.id.text2_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.text3_view).setVisibility(View.INVISIBLE);
//                String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid="+id
//                        + "&&userid="+ MyApplication.instance.getUserId()+"&&longitude="+MyApplication.instance.getLongitude()
//                        +"&&latitude="+MyApplication.instance.getLatitude();
                mDialog.show();
                String url = mUrl + "commoditysaleinfo?commodityid" + id + "&&userid=" + MyApplication.instance.getUserId();
                showWebView(url);
            }
        });

        findViewById(R.id.top_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                String url = mUrl + "commoditydetail?commodityid=" + id;
                mDialog.show();
//                String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditydetail?commodityid=" + id;
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
                mWebView.setVisibility(View.GONE);
                mRefreshLayout.setVisibility(View.VISIBLE);
                mRefreshLayout.beginRefreshing();
            }
        });

        findViewById(R.id.commodith_view_company).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String companyTypeId = getIntent().getStringExtra("companytypeid");
                String companyId = getIntent().getStringExtra("companyid");
                if ("firstleveldistributor".equals(companyTypeId)) {
                    //经销商
                    Intent intent = new Intent(mContext, FranchiserViewActivity.class);
//                    Gson gson = new Gson();
//                    String objGson = gson.toJson(obj);
                    intent.putExtra("companyId", companyId);
                    //总经销商
                    startActivity(intent);
                } else if ("distributor".equals(companyTypeId)) {
                    //经销商
                    Intent intent = new Intent(mContext, DistributorViewAcitivty.class);
                    intent.putExtra("id", companyId);
                    startActivity(intent);
                }
            }
        });


        //收藏
        findViewById(R.id.commodith_view_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
                    dialog.setText("加载中");
                    dialog.show();
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            dialog.dismiss();
                            switch (msg.what) {
                                case ThreadState.SUCCESS:
                                    RspInfo1 re = (RspInfo1) msg.obj;
                                    if (re.isSuccess()) {
                                        UtilAssistants.showToast(re.getMsg());
                                        Map<String, Object> map = (Map<String, Object>) re.getData();
                                        TextView collectView = (TextView) findViewById(R.id.commodith_view_collect);
                                        if ("0".equals(map.get("commoditysavestatus"))) {
                                            Drawable top = getResources().getDrawable(R.drawable.icon_collect);
                                            collectView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                                        }
                                        if ("1".equals(map.get("commoditysavestatus"))) {
                                            Drawable top = getResources().getDrawable(R.drawable.icon_collect_select);
                                            collectView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
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
                                RspInfo1 re = userAction.collectCommodity(id, mDistributorid);
                                handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                            } catch (Exception e) {
                                handler.sendEmptyMessage(ThreadState.ERROR);
                            }
                        }
                    }).start();
                }
            }
        });

        //立即购买
        findViewById(R.id.pay_shop_car_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    mFlag = "pay";
                    showShopInfo();
                }

            }
        });

        /**
         * 加入购物车
         */
        findViewById(R.id.add_shop_car_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    mFlag = "add";
                    showShopInfo();
                }

            }
        });


        findViewById(R.id.add_shop_car_layout_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    GoneAddShopCarView();
                    if ("pay".equals(mFlag)) {
                        pay();//立即购买
                    } else {
                        addShopCar();//添加购物车
                    }
                }
            }
        });

        findViewById(R.id.body_bg_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    GoneAddShopCarView();
                }
            }
        });

        findViewById(R.id.add_shop_car_layout_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    GoneAddShopCarView();
                }
            }
        });

        findViewById(R.id.commodith_view_SC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ShoppingCartListActivity.class);
                    startActivity(intent);
                }
            }
        });


        findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    TextView numView = (TextView) findViewById(R.id.tv_num);
                    int count = Integer.parseInt(numView.getText() + "");
                    numView.setText(++count + "");
                }
            }
        });

        findViewById(R.id.tv_reduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareConfig.getConfigBoolean(CommodithViewActivity.this, Constants.ONLINE, false) == false) {
                    Intent intent = new Intent(CommodithViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    TextView numView = (TextView) findViewById(R.id.tv_num);
                    int count = Integer.parseInt(numView.getText() + "");
                    if (count > 1) {
                        numView.setText(--count + "");
                    }
                }
            }
        });

        findViewById(R.id.commodith_view_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShareInfo(id);
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
            UtilAssistants.showToast("分享成功啦");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            UtilAssistants.showToast("分享失败啦" + t.getMessage());
            if (t != null) {
                com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            UtilAssistants.showToast("分享取消啦");
        }
    };

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

                            //                    String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid="+mSelectIds.get(0).getOrderdetailid();
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
                    UtilAssistants.showToast("分享失败，请重试！");
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

    //获取购物车数量
    private void getShopCarNumber() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo re = (RspInfo) msg.obj;
                            if (re.isSuccess()) {
                                String commoditynumber = (String) re.getDateObj("commoditynumber");

                                if (commoditynumber != null && !"".equals(commoditynumber) && !"0".equals(commoditynumber)) {
                                    TextView textView = (TextView) findViewById(R.id.commodith_view_SC);
                                    if (mBadgeView == null) {
                                        mBadgeView = new BadgeView(mContext, textView);
                                        mBadgeView.setText(commoditynumber);
                                        mBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                                        mBadgeView.setBadgeMargin(0, 0); // 水平和竖直方向的间距
                                        mBadgeView.show();
                                    } else {
                                        mBadgeView.setText(commoditynumber);
                                    }
                                }
                                String commoditysavestatus = (String) re.getDateObj("commoditysavestatus");
                                TextView collectView = (TextView) findViewById(R.id.commodith_view_collect);
                                if ("1".equals(commoditysavestatus)) {//收藏
                                    Drawable top = getResources().getDrawable(R.drawable.icon_collect_select);
                                    collectView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                                } else {
                                    Drawable top = getResources().getDrawable(R.drawable.icon_collect);
                                    collectView.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                                }


                            } else {
                                UtilAssistants.showToast(re.getMsg());
                            }

                        } catch (Exception e) {
                            Log.e("", "");
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
                    RspInfo re = userAction.getShopCarNumber(id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    //显示规格
    private void showShopInfo() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {

                            Map<String, Object> commodityinfo = (Map<String, Object>) re.getDateObj("commodityinfo");
                            List<Map<String, String>> colorlist = (List<Map<String, String>>) re.getDateObj("colorlist");
                            List<Map<String, String>> specificationlist = (List<Map<String, String>>) re.getDateObj("specificationlist");

                            if (commodityinfo != null) {
                                ImageView imageView = (ImageView) findViewById(R.id.add_shop_car_layout_picture);
                                if (!"".equals(commodityinfo.get("picture")))
                                    ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + commodityinfo.get("picture"), imageView);
                                ((TextView) findViewById(R.id.add_shop_car_layout_price)).setText("￥" + commodityinfo.get("price"));
                                double stockNumber = (double) commodityinfo.get("stock");
                                int stock = (int) stockNumber;
                                ((TextView) findViewById(R.id.add_shop_car_layout_stock)).setText("库存" + stock + "件");
                            }

                            if (colorlist != null) {
                                mColorRG.removeAllViews();
                                List<Condition> colorList = new ArrayList<>();
                                for (int i = 0; i < colorlist.size(); i++) {
                                    Condition condition = new Condition();
                                    condition.setId(colorlist.get(i).get("id"));
                                    condition.setName(colorlist.get(i).get("name"));
                                    colorList.add(condition);
                                }
                                float margin = UtilAssistants.dp2px(mContext, 85);
                                for (int i = 0; i < colorList.size(); i++) {
                                    final RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.activity_commodith_view_rb, null);
                                    rb.setText(colorList.get(i).getName());
                                    rb.setTag(colorList.get(i).getId());
                                    FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams((int) (mWidth - margin) / 4, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(8, 8, 8, 8);
                                    rb.setLayoutParams(lp);
                                    mColorRG.addView(rb);
                                    if (i == 0) {
                                        rb.setChecked(true);
                                        mColorId = colorList.get(i).getId();
                                    }

                                    mColorRG.setOnCheckedChangeListener(new FlexRadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(@IdRes int checkedId) {
                                            if (((RadioButton) findViewById(checkedId)).isChecked()) {
                                                mColorId = rb.getTag() + "";
                                            }
                                        }
                                    });
                                }
                            }
                            if (specificationlist != null) {
                                mSpecificationsRG.removeAllViews();
                                List<Condition> specificationList = new ArrayList<>();
                                for (int i = 0; i < specificationlist.size(); i++) {
                                    Condition condition = new Condition();
                                    condition.setId(specificationlist.get(i).get("id"));
                                    condition.setName(specificationlist.get(i).get("name"));
                                    specificationList.add(condition);
                                }
                                float margin = UtilAssistants.dp2px(mContext, 85);
                                for (int i = 0; i < specificationList.size(); i++) {
                                    final RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.activity_commodith_view_rb, null);
                                    rb.setText(specificationList.get(i).getName());
                                    rb.setTag(specificationList.get(i).getId());
                                    FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams((int) (mWidth - margin) / 4, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(8, 8, 8, 8);
                                    rb.setLayoutParams(lp);
                                    mSpecificationsRG.addView(rb);
                                    if (i == 0) {
                                        rb.setChecked(true);
                                        mSpecificationId = specificationList.get(i).getId();
                                    }
                                    mSpecificationsRG.setOnCheckedChangeListener(new FlexRadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(@IdRes int checkedId) {
                                            if (((RadioButton) findViewById(checkedId)).isChecked()) {
                                                mSpecificationId = rb.getTag() + "";
                                            }
                                        }
                                    });
                                }
                            }
                            findViewById(R.id.body_bg_view).setVisibility(View.VISIBLE);
                            View view1 = (View) findViewById(R.id.add_shop_car_layout);
                            view1.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.in_bottomtotop);
                            animation.setFillAfter(true);
                            view1.setAnimation(animation);
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
                    RspInfo re = userAction.getAddSCInfo(id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    //显示评价
    private void showEvaluate() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            List<Map<String, String>> list = (List<Map<String, String>>) data.get("appraiselist");
                            List<Evaluate> receiveList = new ArrayList<>();
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    Evaluate obj = new Evaluate();
                                    obj.setAppraise(list.get(i).get("appraise"));
                                    obj.setAppraiselevel(list.get(i).get("appraiselevel"));
                                    obj.setAppraisetime(list.get(i).get("appraisetime"));
                                    obj.setUserpicture(list.get(i).get("userpicture"));
                                    obj.setUsername(list.get(i).get("username"));
                                    obj.setUsermobile(list.get(i).get("usermobile"));
                                    receiveList.add(obj);
                                }
                                mAdapter.addAll(receiveList);
                            } else {
                                if (mPage > 1) {//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
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
                    RspInfo1 re = userAction.getEvaluateList(id, mPage++, mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mAdapter.removeAll();
        mPage = 1;
        showEvaluate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        showEvaluate();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String jsonObj = data.getStringExtra("obj");
            Gson gson = new Gson();
            Address address = gson.fromJson(jsonObj, Address.class);
            String url = "http://www.u-shang.net/enginterface/index.php/Apph5/commoditysaleinfo?commodityid=" + id + "&&recvaddressid=" + address.getId()
                    + "&&userid=" + MyApplication.instance.getUserId() + "&&longitude=" + MyApplication.instance.getLongitude()
                    + "&&latitude=" + MyApplication.instance.getLatitude();
            showWebView(url);
        }
    }

    private class EvaluateAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;

        private List<Evaluate> mList = new ArrayList<>();

        /**
         * 构造函数
         *
         * @param context android上下文环境
         */
        public EvaluateAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = (View) layoutInflater.inflate(
                    R.layout.activity_evaluate_list_item, null);

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.evaluate_item_appraiselevel_layout);
            String level = mList.get(position).getAppraiselevel();
            if (!"".equals(level)) {
                int size;
                if (level.contains(".")) {
                    String level1 = level.substring(0, level.indexOf("."));
                    size = Integer.parseInt(level1);
                } else {
                    size = Integer.parseInt(level);
                }
                for (int i = 0; i < size; i++) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageResource(R.drawable.icon_bit);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(5, 0, 0, 0);
                    imageView.setLayoutParams(lp);
                    layout.addView(imageView);
                }
            }

            ((TextView) view.findViewById(R.id.evaluate_item_appraise)).setText(mList.get(position).getAppraise());
            ((TextView) view.findViewById(R.id.evaluate_item_name)).setText(mList.get(position).getUsername());
            ((TextView) view.findViewById(R.id.evaluate_item_usermobile)).setText(mList.get(position).getUsermobile());
            ((TextView) view.findViewById(R.id.evaluate_item_appraisetime)).setText(mList.get(position).getAppraisetime());
            ImageView imageView = (ImageView) view.findViewById(R.id.evaluate_item_picture);
            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + mList.get(position).getUserpicture(), imageView);
            return view;
        }

        public void addAll(List<Evaluate> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 添加购物车
     */
    public void addShopCar() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            getShopCarNumber();
                            UtilAssistants.showToast("成功加入购物车！");

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
                    TextView numView = (TextView) findViewById(R.id.tv_num);
                    String number = numView.getText() + "";
                    RspInfo1 re = userAction.addShopCar(mDistributorid, id, mSpecificationId, mColorId, number);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    /**
     * 购买
     */
    public void pay() {
        TextView numView = (TextView) findViewById(R.id.tv_num);
        final String number = numView.getText() + "";
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String, String> orderdetailinfo = (Map<String, String>) data.get("orderdetailinfo");
                            String orderdetailid = orderdetailinfo.get("orderdetailid");
                            OrderDetailid orderDetailid = new OrderDetailid(orderdetailid, number);
                            mSelectIds.add(orderDetailid);
                            Gson gson = new Gson();
                            String objJson = gson.toJson(mSelectIds);
                            Intent intent = new Intent(mContext, OrderEditActivity.class);
                            intent.putExtra("orderdetailidlist", objJson);
                            startActivity(intent);
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
                    RspInfo1 re = userAction.getOrderDetailId(mDistributorid, id, mSpecificationId, mColorId, number);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    public void GoneAddShopCarView() {
        findViewById(R.id.body_bg_view).setVisibility(View.GONE);
        View view1 = (View) findViewById(R.id.add_shop_car_layout);
        view1.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.out_toptobottom);
//        animation.setFillAfter(true);
        view1.setAnimation(animation);
    }

    /**
     * 选择的产品
     */
    private class OrderDetailid {
        private String orderdetailid;
        private String ordernumber;

        public OrderDetailid(String id, String number) {
            this.orderdetailid = id;
            this.ordernumber = number;
        }
    }
}
