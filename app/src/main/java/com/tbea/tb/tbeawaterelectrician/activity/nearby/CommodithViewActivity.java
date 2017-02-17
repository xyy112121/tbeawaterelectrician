package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.component.FlexRadioGroup;
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
 * Created by cy on 2017/2/8.
 */

public class CommodithViewActivity extends Activity {
    private Context mContext;
    private  WebView mWebView;
    private String id;
    private FlexRadioGroup mColorRG;
    private FlexRadioGroup mSpecificationsRG;
    private float mWidth;
    private String mSpecificationId;
    private String mColorId ;
    private String mDistributorid;

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
        mColorRG = (FlexRadioGroup)findViewById(R.id.commodith_view_color_rg);
        mSpecificationsRG = (FlexRadioGroup)findViewById(R.id.commodith_view_specifications_rg);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDistributorid = getIntent().getStringExtra("distributorid");
        mWidth = metrics.density;
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

        findViewById(R.id.add_shop_car_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /**
         * 加入购物车
         */
        findViewById(R.id.add_shop_car_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
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

                                    if(commodityinfo != null){
                                        ImageView imageView = (ImageView)findViewById(R.id.add_shop_car_layout_picture);
                                        if(!"".equals(commodityinfo.get("picture")))
                                        ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+commodityinfo.get("picture"),imageView);
                                        ((TextView)findViewById(R.id.add_shop_car_layout_price)).setText("￥"+commodityinfo.get("price"));
                                        double stockNumber = (double)commodityinfo.get("stock");
                                        int stock = (int)stockNumber;
                                        ((TextView)findViewById(R.id.add_shop_car_layout_stock)).setText("库存"+stock+"件");
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
                                        for (int i = 0;i<colorList.size();i++){
                                            final RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.activity_commodith_view_rb, null);
                                            rb.setText(colorList.get(i).getName());
                                            rb.setTag(colorList.get(i).getId());
                                            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams((int) (mWidth - margin) / 4, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            lp.setMargins(8,8,8,8);
                                            rb.setLayoutParams(lp);
                                            mColorRG.addView(rb);
                                            if(i == 0){
                                                rb.setChecked(true);
                                                mColorId = colorList.get(i).getId();
                                            }

                                            mColorRG.setOnCheckedChangeListener(new FlexRadioGroup.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(@IdRes int checkedId) {
                                                    if(((RadioButton)findViewById(checkedId)).isChecked()){
                                                        mColorId = rb.getTag()+"";
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    if(specificationlist != null){
                                        mSpecificationsRG.removeAllViews();
                                        List<Condition> specificationList = new ArrayList<>();
                                        for (int i =0; i<specificationlist.size();i++){
                                            Condition condition = new Condition();
                                            condition.setId(specificationlist.get(i).get("id"));
                                            condition.setName(specificationlist.get(i).get("name"));
                                            specificationList.add(condition);
                                        }
                                        float margin = UtilAssistants.dp2px(mContext, 85);
                                        for (int i = 0;i<specificationList.size();i++){
                                            final  RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.activity_commodith_view_rb, null);
                                            rb.setText(specificationList.get(i).getName());
                                            rb.setTag(specificationList.get(i).getId());
                                            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams((int) (mWidth - margin) / 4, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            lp.setMargins(8,8,8,8);
                                            rb.setLayoutParams(lp);
                                            mSpecificationsRG.addView(rb);
                                            if(i == 0){
                                                rb.setChecked(true);
                                                mSpecificationId = specificationList.get(i).getId();
                                            }
                                            mSpecificationsRG.setOnCheckedChangeListener(new FlexRadioGroup.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(@IdRes int checkedId) {
                                                    if(((RadioButton)findViewById(checkedId)).isChecked()){
                                                        mSpecificationId = rb.getTag()+"";
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    findViewById(R.id.body_bg_view).setVisibility(View.VISIBLE);
                                    View view1 = (View)findViewById(R.id.add_shop_car_layout);
                                    view1.setVisibility(View.VISIBLE);
                                    Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.in_bottomtotop);
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
        });


        findViewById(R.id.add_shop_car_layout_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoneAddShopCarView();
                addShopCar();//添加购物车
            }
        });

        findViewById(R.id.body_bg_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoneAddShopCarView();
            }
        });

        findViewById(R.id.add_shop_car_layout_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoneAddShopCarView();
            }
        });

        findViewById(R.id.commodith_view_SC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ShoppingCartListActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView numView = (TextView)findViewById(R.id.tv_num);
                int count = Integer.parseInt(numView.getText()+"");
                numView.setText(++count+"");
            }
        });

        findViewById(R.id.tv_reduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView numView = (TextView)findViewById(R.id.tv_num);
                int count = Integer.parseInt(numView.getText()+"");
                if(count > 1){
                    numView.setText(--count+"");
                }
            }
        });
    }

    /**
     * 添加购物车
     */
    public  void addShopCar(){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
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
                    TextView numView = (TextView)findViewById(R.id.tv_num);
                    String number = numView.getText()+"";
                    RspInfo1 re = userAction.addShopCar(mDistributorid,id,mSpecificationId,mColorId,number);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

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
