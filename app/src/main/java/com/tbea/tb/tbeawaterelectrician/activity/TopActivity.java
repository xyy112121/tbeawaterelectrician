package com.tbea.tb.tbeawaterelectrician.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;

import java.util.List;

import kr.co.namee.permissiongen.PermissionGen;

public class TopActivity extends FragmentActivity {
    protected ImageButton mBackBtn;
    protected ImageButton mRightBtn;
    public final int SET_REQEST = 1000;
    protected Context mContext;

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    // 显示缺失权限提示
    public void showMissingPermissionDialog() {
//		final CustomDialog dialog = new CustomDialog(TopActivity.this,R.style.MyDialog,R.layout.tip_delete_dialog);
//		dialog.setTitle(getResources().getString(R.string.help));
//		dialog.setText(getResources().getString(R.string.string_help_text));
//		dialog.setConfirmBtnClickListener(null,getResources().getString(R.string.quit));
//		dialog.setCancelBtnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				dialog.dismiss();
//				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//				intent.setData(Uri.parse("package:" + getPackageName()));
//				startActivityForResult(intent,SET_REQEST);
//			}
//		},getResources().getString(R.string.settings));
//		dialog.show();
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

    /**
     * ���ú��˵��¼�
     */
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

    /**
     * �����ұ�Ϊ����
     */
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
