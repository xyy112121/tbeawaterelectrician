package com.tbea.tb.tbeawaterelectrician.component;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.entity.HomeDateSon;

import java.util.List;


public class CustomPopWindow extends PopupWindow {
	private Activity mActivity;
	private View mBackGroundView;
	private int mResource;
	public String mSelectedId;
	public String mSelectedName;
	
	/**
	 * 创建一个popupwindow
	 * @param activity
	 * @param canceledOnTouchOutside 点击其他区域是否关闭
	 * @param resource 变暗背景的id
	 */
	public CustomPopWindow(final Activity activity, int resource, boolean canceledOnTouchOutside, int width) {
		super();
		this.mActivity = activity;
		init(canceledOnTouchOutside, resource, 0,width);
	}
	
	/**
	 * 创建一个popupwindow
	 * @param activity
	 * @param canceledOnTouchOutside 点击其他区域是否关闭
	 * @param resource 变暗背景的id
	 * @param anim 动画id
	 */
	public CustomPopWindow(final Activity activity, int resource, boolean canceledOnTouchOutside, int anim, int width) {
		super();
		this.mActivity = activity;
		init(canceledOnTouchOutside, resource, anim,width);
	}

	/**
	 * 创建一个滚动的popupwindow
	 * @param activity
	 * @param canceledOnTouchOutside 点击其他区域是否关闭
	 * @param resource 变暗背景的id
	 * @param anim 动画id
	 */
	public CustomPopWindow(final Activity activity, int resource, boolean canceledOnTouchOutside, int anim, int width,int layout) {
		super();
		this.mActivity = activity;
		init(canceledOnTouchOutside, resource, anim,width,layout);
	}
	
	private void init(boolean canceledOnTouchOutside,int resource, int anim,int width){
		// 获取自定义布局文件activity_popupwindow_left.xml的视图  
		mResource = resource;
        View contentView = mActivity.getLayoutInflater().inflate(R.layout.pop_window, null,
                false);
        if(canceledOnTouchOutside){
        	contentView.setOnTouchListener(new OnTouchListener() {
        		@Override
        		public boolean onTouch(View v, MotionEvent event) {
        			if (isShowing()) {
        				dismiss();
        			}
        			return false; 
        		}
        	});
        }
        if(resource != 0){
        	// 变暗的背景
            mBackGroundView = mActivity.findViewById(resource);
            setOnDismissListener(new OnDismissListener() {
    			@Override
    			public void onDismiss() {
    				mBackGroundView.setVisibility(View.GONE);
    			}
    		});
        }else{
        	contentView.setBackgroundColor(0xe0A09D9D);
        }
        
        setContentView(contentView);
        setFocusable(true);
//        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        if(width == 0){
        	 setWidth(LayoutParams.MATCH_PARENT);
        }else{
        	setWidth(width);
        }
        
//        setHeight(height);
        if(anim != 0){
        	setAnimationStyle(anim);
        }
	}

	private void init(boolean canceledOnTouchOutside,int resource, int anim,int width,int layout){
		// 获取自定义布局文件activity_popupwindow_left.xml的视图
		mResource = resource;
		View contentView = mActivity.getLayoutInflater().inflate(layout, null,
				false);
		if(canceledOnTouchOutside){
			contentView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (isShowing()) {
						dismiss();
					}
					return false;
				}
			});
		}
		if(resource != 0){
			// 变暗的背景
			mBackGroundView = mActivity.findViewById(resource);
			setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					mBackGroundView.setVisibility(View.GONE);
				}
			});
		}else{
			contentView.setBackgroundColor(0xe0A09D9D);
		}

		setContentView(contentView);
		setFocusable(true);
//        setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		if(width == 0){
			setWidth(LayoutParams.MATCH_PARENT);
		}else{
			setWidth(width);
		}

//        setHeight(height);
		if(anim != 0){
			setAnimationStyle(anim);
		}
	}
	
	/**
	 * 添加按钮
	 * @param text 按钮文字
	 * @param color 文字颜色 （0：默认颜色）
	 * @param listener 事件
	 */
	public void addButtonForGroup1(String text, int color, OnClickListener listener){
		addButton(text, color, listener, R.id.pop_window_content_layout);
	}

	/**
	 * 添加滚动View
	 * @param text 按钮文字
	 * @param listener 事件
	 */
	public void addScrollViewForGroup1(List<Condition> text, OnClickListener listener){
		addScrollView(text,listener, R.id.pop_window_content_layout);
	}
	
	/**
	 * 添加按钮
	 * @param text 按钮文字
	 * @param color 文字颜色 （0：默认颜色）
	 * @param listener 事件
	 */
	public void addButtonForGroup2(String text, int color, OnClickListener listener){
		addButton(text, color, listener, R.id.pop_window_content_layout1);
	}
	
	private void addButton(String text, int color, OnClickListener listener, int resource){
		Button btn = (Button) mActivity.getLayoutInflater().inflate(R.layout.pop_window_btn, null,
                false);
		if(mResource == 0){//背景 
			 btn = (Button) mActivity.getLayoutInflater().inflate(R.layout.pop_window_btn_home_page, null,
		                false);
			 btn.setBackgroundColor(0x00ffffff);//00为全透明
		}
		btn.setText(text);
		if(color != 0){
			btn.setTextColor(color);
		}
		if(listener == null){
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		} else {
			btn.setOnClickListener(listener);
		}
		LinearLayout ll = ((LinearLayout)getContentView().findViewById(resource));
		if(mResource == 0){//背景 
//			ll.setBackgroundColor(0xe0A09D9D);
			ll.setBackgroundColor(0x00000000);
		}
		if(ll != null && ll.getChildCount() > 0){
			View view = new View(mActivity);
			if(mResource == 0){//背景 
				view.setBackgroundColor(0xFFFFffff);
			}else{
				view.setBackgroundColor(0xFFBCBCBC);
			}
			android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1);
			view.setLayoutParams(lp);
			ll.addView(view);
		}
		ll.addView(btn);
	}

	private void addScrollView(List<Condition> obj, OnClickListener listener, int resource){
		LinearLayout linearLayout = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.pop_window_scrollview, null,
				false);
		CustomScrollView customScrollView = (CustomScrollView)linearLayout.findViewById(R.id.CustomScrollView);
		customScrollView.setOnSelectListener(new CustomScrollView.onSelectListener()
		{

			@Override
			public void onSelect(String selected,String selectName)
			{
				mSelectedId = selected;
				mSelectedName = selectName;
			}
		});
		customScrollView.setData(obj);
		TextView cancelView = (TextView)linearLayout.findViewById(R.id.cancel);
		cancelView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		final TextView comfirmView = (TextView)linearLayout.findViewById(R.id.comfirm);
		if(listener == null){
			comfirmView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		} else {
			comfirmView.setOnClickListener(listener);
		}
		LinearLayout ll = ((LinearLayout)getContentView().findViewById(resource));
		if(mResource == 0){//背景
//			ll.setBackgroundColor(0xe0A09D9D);
			ll.setBackgroundColor(0x00000000);
		}
		if(ll != null && ll.getChildCount() > 0){
			View view = new View(mActivity);
			if(mResource == 0){//背景
				view.setBackgroundColor(0xFFFFffff);
			}else{
				view.setBackgroundColor(0xFFBCBCBC);
			}
			android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1);
			view.setLayoutParams(lp);
			ll.addView(view);
		}
		ll.addView(linearLayout);
	}

	/**
	 * 添加文字内容
	 * @param text
	 * @param color
	 */
	public void addText(String text, int color){
		if(!"".equals(text)){
			TextView tv = (TextView) mActivity.getLayoutInflater().inflate(R.layout.pop_window_title_text, null,
	                false);
			tv.setText(text);
			tv.setTextColor(color);
			LinearLayout ll = ((LinearLayout)getContentView().findViewById(R.id.pop_window_content_layout));
			if(ll != null && ll.getChildCount() > 0){
				View view = new View(mActivity);
				view.setBackgroundColor(0xFFBCBCBC);
				android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1);
				view.setLayoutParams(lp);
				ll.addView(view);
			}
			ll.addView(tv);
		}
		
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		if(mActivity != null){
			try {
				InputMethodManager inputMethodManager = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.showAtLocation(parent, gravity, x, y);
		if(mBackGroundView != null){
			mBackGroundView.setVisibility(View.VISIBLE);
		}
	}
	
	/** 
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {  
//            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 0); 
            this.showAsDropDown(parent,0,  parent.getLayoutParams().height / 2); 
        } else {  
            this.dismiss();  
        }  
    }

	public interface onSelectListener
	{
		void onSelect(int po);
	}
}
