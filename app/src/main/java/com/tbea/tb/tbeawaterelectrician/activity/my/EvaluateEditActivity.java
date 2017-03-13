package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.component.StarBar;
import com.tbea.tb.tbeawaterelectrician.entity.ProductInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * Created by abc on 17/3/5.
 */

public class EvaluateEditActivity extends TopActivity implements View.OnClickListener {
   private String mStarlevel ="";
    private String mCommodityid ="";
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_edit);
        initTopbar("评价","保存",this);
        mContext = this;
//        initUI();
    }

    private void initUI(){
        Gson gson = new Gson();
        String objGson = getIntent().getStringExtra("obj");
        ProductInfo obj = gson.fromJson(objGson,ProductInfo.class);
        mCommodityid = obj.getCommodityid();
        final ImageView imageView = (ImageView)findViewById(R.id.order_evaluate_commdith_edit_picture);
        ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getCommoditypicture(),imageView);
        ((TextView)findViewById(R.id.order_evaluate_commdith_edit_name)).setText(obj.getCommodityname());

       StarBar starbar = (StarBar)findViewById(R.id.order_evaluate_edit_startbar);
        starbar.setIntegerMark(true);
        starbar.setOnStarChangeListener(new StarBar.OnStarChangeListener() {
            @Override
            public void onStarChange(float mark) {
                mStarlevel = mark+"";
            }
        });
    }

    @Override
    public void onClick(View view) {
        if("".equals(mStarlevel)){
            UtilAssistants.showToast("请评价...");
            return;
        }

        final String appraise = ((EditText)findViewById(R.id.order_evaluate_edit_appraise)).getText()+"";
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            finish();
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
                    RspInfo1 re = userAction.saveEvaluate(mCommodityid,mStarlevel,appraise);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
