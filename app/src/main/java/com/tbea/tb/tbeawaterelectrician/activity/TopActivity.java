package com.tbea.tb.tbeawaterelectrician.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.util.permissonutil.PermissionActivity;

import java.util.List;


public class TopActivity extends AppCompatActivity {
    protected ImageButton mBackBtn;
    protected ImageButton mRightBtn;
    public final int SET_REQEST = 1000;
    protected Activity mContext;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void initTopbar(String text) {
        initTopbar(text, null);
    }


    protected void initTopbar(String text, OnClickListener listener) {
        TextView tv = (TextView) findViewById(R.id.top_center);
        mBackBtn = (ImageButton) findViewById(R.id.top_left);
        mRightBtn = (ImageButton) findViewById(R.id.top_right);
        if (listener != null) {
            mRightBtn.setVisibility(View.VISIBLE);
            mRightBtn.setOnClickListener(listener);
        }
        tv.setText(text);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    protected void initTopbar(String text, OnClickListener listener,
                              int resource) {
        TextView tv = (TextView) findViewById(R.id.top_center);
        mBackBtn = (ImageButton) findViewById(R.id.top_left);
        mRightBtn = (ImageButton) findViewById(R.id.top_right);
        mRightBtn.setImageResource(resource);
        if (listener != null) {
            mRightBtn.setVisibility(View.VISIBLE);
            mRightBtn.setOnClickListener(listener);
        }
        tv.setText(text);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    protected void initTopbar(String text, OnClickListener listener, String flag) {
        TextView tv = (TextView) findViewById(R.id.top_center);
        mBackBtn = (ImageButton) findViewById(R.id.top_left);
        mRightBtn = (ImageButton) findViewById(R.id.top_right);
        // if(listener != null){
        // mRightBtn.setVisibility(View.VISIBLE);
        // mRightBtn.setOnClickListener(listener);
        // }
        tv.setText(text);
        mBackBtn.setOnClickListener(listener);
    }


    protected void initTopbar(String text, String rightText,
                              OnClickListener listener) {
        TextView tv = (TextView) findViewById(R.id.top_center);
        mBackBtn = (ImageButton) findViewById(R.id.top_left);
        if (!"".equals(rightText)) {
            TextView rightTv = (TextView) findViewById(R.id.top_right_text);
            rightTv.setVisibility(View.VISIBLE);
            rightTv.setText(rightText);
            rightTv.setOnClickListener(listener);
        }
        tv.setText(text);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
