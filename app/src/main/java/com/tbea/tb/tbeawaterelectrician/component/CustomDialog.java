package com.tbea.tb.tbeawaterelectrician.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;


/**
 * CustomDialog 自定义Dialog
 */
public class CustomDialog extends Dialog {
	/**
	 * 提示文本
	 */
	private TextView tipText;
	/**
	 * 标题
	 */
	private TextView tvTitle;
	/**
	 * 自定义Dialog
	 * @param context
	 * @param theme 主题样式
	 * @param contentView 布局文件
	 */
	public CustomDialog(Context context, int theme, int contentView){
		super(context,theme);
		setContentView(contentView);
		DisplayMetrics metric = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
		LayoutParams lp = getWindow().getAttributes();
		lp.width = (int) (metric.widthPixels * 0.9);
		setCanceledOnTouchOutside(false);
		findView(contentView);
	}
	
	/**
	 * 
	 * @param layoutResID  R.layout.main_loading_view:加载中样式;
	 * R.layout.tip_delete_dialog:有确定、取消按钮
	 */
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		findView(layoutResID);
	}
	
	
	private void findView(int contentView){
		switch (contentView) {
		case R.layout.tip_wait_dialog:
			tipText = (TextView) findViewById(R.id.tip_wait_dialog_text);
			tipText.setSingleLine();
			tipText.setEllipsize(TruncateAt.MIDDLE);
			break;
		case R.layout.tip_delete_dialog:
			tipText = (TextView) findViewById(R.id.tip_message);
			tvTitle = (TextView)findViewById(R.id.tip_title);
			break;
		}
	}
	
	/**
	 * 设置dialog提示文本
	 * @param text
	 */
	public void setText(String text){
		if(tipText != null){
			tipText.setText(text);
		}
	}
	
	public void setTitle(String text){
		if(tvTitle != null){
			tvTitle.setText(text);
		}
	}
	
	/**
	 * 设置确定按钮事件（仅只有一个按钮时）
	 * @param listener
	 */
	public void setConfirmBtnIsCloseWindow(android.view.View.OnClickListener listener){
		try {
			findViewById(R.id.cancel_btn).setVisibility(View.GONE);
			if(listener == null){
				findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
			} else {
				findViewById(R.id.confirm_btn).setOnClickListener(listener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置确定按钮事件
	 * @param listener
	 */
	public void setConfirmBtnClickListener(android.view.View.OnClickListener listener,String text){
		try {
			if(!"".equals(text)){
				((Button)findViewById(R.id.confirm_btn)).setText(text);
			}

			if(listener == null){
				findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
			} else {
				findViewById(R.id.confirm_btn).setOnClickListener(listener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置取消按钮事件（仅有两个按钮时）
	 * @param listener
	 */
	public void setCancelBtnClickListener(android.view.View.OnClickListener listener,String text){
		try {
			if(!"".equals(text)){
				((Button)findViewById(R.id.cancel_btn)).setText(text);
			}
			if(listener == null){
				findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();					
					}
				});
			} else {
				findViewById(R.id.cancel_btn).setOnClickListener(listener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public Dialog createDialog(){
//		Dialog dialog = new Dialog(context, theme);
//		dialog.setContentView(contentView);
//		DisplayMetrics metric = new DisplayMetrics();
//		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
//		LayoutParams lp = dialog.getWindow().getAttributes();
//		lp.width = (int) (metric.widthPixels * 0.9);
//		dialog.setCanceledOnTouchOutside(false);
//		return dialog;
//	}
}
