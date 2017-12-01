package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.component.FlexRadioGroup;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 我的举报填写页
 */

public class MyAccusationViewActivity extends TopActivity{
    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accusation_view);
        initTopbar("我要举报");
        mContext = this;
        initUI();
    }

    private void initUI(){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String,Object> appealinfo =  (Map<String,Object>) data.get("appealinfo");
                            Map<String,String> replyinfo =  (Map<String,String>) data.get("replyinfo");
                            if(appealinfo != null){
                                ((TextView)findViewById(R.id.accusation_view_appealcategory)).setText(appealinfo.get("appealcategory")+"");
                                ((TextView)findViewById(R.id.accusation_view_appealtime)).setText(appealinfo.get("appealtime")+"");
                                ((TextView)findViewById(R.id.accusation_view_scanaddress)).setText(appealinfo.get("scanaddress")+"");
                                ((TextView)findViewById(R.id.accusation_view_provincecity)).setText(appealinfo.get("provincecity")+"");
                                ((TextView)findViewById(R.id.accusation_view_distributor)).setText(appealinfo.get("distributor")+"");
                                ((TextView)findViewById(R.id.accusation_view_commodity)).setText(appealinfo.get("commodity")+"");
                                ((TextView)findViewById(R.id.accusation_view_appealcontent)).setText(appealinfo.get("appealcontent")+"");
                                final List <Map<String,String>> picturelist = (List<Map<String,String>>)appealinfo.get("picturelist");
                                if(picturelist != null){
                                    try {
                                        FlexRadioGroup group = (FlexRadioGroup)findViewById(R.id.accusation_view_image_rg);
                                        group.removeAllViews();
                                        for (int i= 0;i<picturelist.size();i++){
                                            ImageView imageView = new ImageView(mContext);
                                            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(UtilAssistants.dp2px(mContext,80),UtilAssistants.dp2px(mContext,80));
                                            lp.setMargins(15,0,0,0);
                                            imageView.setLayoutParams(lp);
                                            imageView.setTag(picturelist.get(i).get("largepicture"));
                                            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+picturelist.get(i).get("picture"),imageView);
                                            imageView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    showGiveTypicalExamplesImage(MyApplication.instance.getImgPath()+view.getTag());
                                                }
                                            });
                                            group.addView(imageView);
                                        }
                                    }catch (Exception e){
                                        Log.e("","");
                                    }

                                }
                            }
                            if(replyinfo != null){
                                ((TextView)findViewById(R.id.accusation_view_replytime)).setText(replyinfo.get("replytime"));
                                ((TextView)findViewById(R.id.accusation_view_replycontent)).setText(replyinfo.get("replycontent"));
                            }
                        }else {
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
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.getAccusationInfo(getIntent().getStringExtra("id"));
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

        Typeface iconfont = Typeface.createFromAsset(getAssets(),
                "iconfont/iconfont.ttf");
        ((TextView)findViewById(R.id.register_shili_image_cancel)).setTypeface(iconfont);

        findViewById(R.id.register_shili_image_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.body_bg_view2).setVisibility(View.GONE);
                findViewById(R.id.regist_shili_image_2).setVisibility(View.GONE);
                findViewById(R.id.register_shili_image_cancel).setVisibility(View.GONE);
            }
        });
    }

    //显示示例图片
    public void showGiveTypicalExamplesImage(String url){
        findViewById(R.id.body_bg_view2).setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView)findViewById(R.id.regist_shili_image_2);
        imageView.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(url,imageView);
        findViewById(R.id.register_shili_image_cancel).setVisibility(View.VISIBLE);
    }
}
