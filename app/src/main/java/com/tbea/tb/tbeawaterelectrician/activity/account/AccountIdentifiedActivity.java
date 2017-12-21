package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.PictureShowActivity;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.model.PicturelistBean;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 认证通过和认证中
 */

public class AccountIdentifiedActivity extends TopActivity implements View.OnClickListener {
    Map<String, String> mUserInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_identified);
        initTopbar("实名认证", "重新认证", this);
        getDate();
        findViewById(R.id.identification_personidcard1_iv).setOnClickListener(this);
        findViewById(R.id.identification_personidcard2_iv).setOnClickListener(this);
        findViewById(R.id.identification_personidcard3_iv).setOnClickListener(this);
    }

    private void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            mUserInfo = (Map<String, String>) re.getDateObj("useridentifyinfo");
                            ((TextView) findViewById(R.id.identification_name_tv)).setText(mUserInfo.get("realname"));
                            ((TextView) findViewById(R.id.identification_cardId_tv)).setText(mUserInfo.get("personcardid"));
                            ImageView imageView1 = (ImageView) findViewById(R.id.identification_personidcard1_iv);
                            ImageView imageView2 = (ImageView) findViewById(R.id.identification_personidcard2_iv);
                            ImageView imageView3 = (ImageView) findViewById(R.id.identification_personidcard3_iv);
                            if (!TextUtils.isEmpty(mUserInfo.get("personidcard1")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + mUserInfo.get("personidcard1"), imageView1);
                            if (!TextUtils.isEmpty(mUserInfo.get("personidcard2")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + mUserInfo.get("personidcard2"), imageView2);
                            if (!TextUtils.isEmpty(mUserInfo.get("personidcardwithperson")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + mUserInfo.get("personidcardwithperson"), imageView3);

                        } else {

                            ToastUtil.showMessage(re.getMsg(), mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败!", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getAccountAuthentication();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        List<PicturelistBean> picturelist;
        switch (v.getId()) {
            case R.id.identification_personidcard1_iv:
                picturelist = new ArrayList<>();
                PicturelistBean bean = new PicturelistBean();
                bean.largepicture = mUserInfo.get("personidcard1");
                picturelist.add(bean);
                openImage(picturelist, 0);
                break;
            case R.id.identification_personidcard2_iv:
                picturelist = new ArrayList<>();
                bean = new PicturelistBean();
                bean.largepicture = mUserInfo.get("personidcard2");
                picturelist.add(bean);
                openImage(picturelist, 0);
                break;
            case R.id.identification_personidcard3_iv:
                picturelist = new ArrayList<>();
                bean = new PicturelistBean();
                bean.largepicture = mUserInfo.get("personidcardwithperson");
                picturelist.add(bean);
                openImage(picturelist, 0);
                break;
            case R.id.top_right_text:
                startActivity(new Intent(mContext, AccountAuthenticationActivity.class));
                break;
        }

    }

    private void openImage(List<PicturelistBean> picturelist, int index) {

        Intent intent = new Intent(mContext, PictureShowActivity.class);
        intent.putExtra("images", (Serializable) picturelist);
        intent.putExtra("index", index);
        startActivity(intent);
//
    }
}
