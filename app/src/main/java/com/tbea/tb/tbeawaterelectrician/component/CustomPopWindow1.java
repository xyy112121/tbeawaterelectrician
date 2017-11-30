package com.tbea.tb.tbeawaterelectrician.component;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.zhouwei.library.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.R;

import java.util.List;


public class CustomPopWindow1 {
    private ItemClick mItemClick;//内容点击事件
    private ItemClickClose mItemClickClose;//内容点击事件
    private Context mContext;
    CustomPopWindow mPopWindow;

    public CustomPopWindow1(Context context) {
        mContext = context;
    }

    /**
     * 警告框
     *
     * @param parentLayout 父布局
     * @param headerRes    头部布局
     * @param contentRes   内容
     * @param title        标题
     * @param content      内容
     * @param btnText      btton显示的文本
     */
    public void init(View parentLayout, int headerRes, int contentRes, String title, String content, String btnText) {
        try {
            LinearLayout parentView = (LinearLayout) ((Activity) mContext).getLayoutInflater().inflate(R.layout.pop_window_layout, null);
            View headerView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.pop_window_header, null);
            ((TextView) headerView.findViewById(R.id.picker_header_tv)).setText(title);
            ImageView closeBtn = (ImageView) headerView.findViewById(R.id.picker_header_close);
            closeBtn.setVisibility(View.GONE);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickClose == null) {
                        mPopWindow.dissmiss();
                    } else {
                        mPopWindow.dissmiss();
                        mItemClickClose.close();
                    }

                }
            });

            parentView.addView(headerView);

            View contentView = ((Activity) mContext).getLayoutInflater().inflate(contentRes, null);
            TextView textView = (TextView) contentView.findViewById(R.id.pop_window_tv);
            Button buttonColse = (Button) contentView.findViewById(R.id.pop_window_close);
            buttonColse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickClose == null) {
                        mPopWindow.dissmiss();
                    } else {
                        mPopWindow.dissmiss();
                        mItemClickClose.close();
                    }
                }
            });


            Button button = (Button) contentView.findViewById(R.id.pop_window_btn);
            button.setText(btnText);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopWindow.dissmiss();
                    if (mItemClick != null) {
                        mItemClick.onItemClick("");
                    }
                }
            });
            textView.setText(content);
            parentView.addView(contentView);

//            ColorDrawable dw = new ColorDrawable(00000);popupwindow.setBackgroundDrawable(dw);
//            mPopWindow = new CustomPopWindow.PopupWindowBuilder((mContext)).setOnDissmissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    if (mItemClickClose == null) {
//                        mPopWindow.dissmiss();
//                    } else {
//                        mItemClickClose.close();
//                    }
//
//                }
//            })
            mPopWindow = new CustomPopWindow.PopupWindowBuilder((mContext)).setView(parentView)
                    .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                    .setBgDarkAlpha(0.4f) // 控制亮度
                    .setAnimationStyle(R.style.PopWindowAnimationFade)
                    .setTouchable(true)
                    .enableOutsideTouchableDissmiss(false)
                    .create()
                    .showAtLocation(parentLayout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface ItemClick {
        void onItemClick(String text);
    }

    public interface ItemClickClose {
        void close();
    }

    public void setItemClickClose(ItemClickClose click) {
        this.mItemClickClose = click;
    }

    public void setItemClick(ItemClick click) {
        this.mItemClick = click;
    }

}
