package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *商品历史记录
 */
public class ShopHistorySearchActivity extends TopActivity{
	EditText searchTV;
	ListView mListView;
	MyAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_history_search);
		searchTV = (EditText)findViewById(R.id.expert_search_text);
		mListView = (ListView)findViewById(R.id.expert_select_search_history_list);
		String searchValue = getIntent().getStringExtra("searchValue");
		searchTV.setText(searchValue);
		mAdapter = new MyAdapter();
        // 获取搜索记录文件内容
        SharedPreferences sp = getSharedPreferences("expert_search_history", 0);
        String history = sp.getString("expert", "");
        if(!"".equals(history)){
        	// 用逗号分割内容返回数组
            String[] history_arr = history.split(",");
            
         
            List<String> list = new ArrayList<>();
            if(history_arr.length >= 1){
            	// 保留前50条数据
                if (history_arr.length > 50) {
                    String[] newArrays = new String[50];
                    // 实现数组之间的复制
                    System.arraycopy(history_arr, 0, newArrays, 0, 50);
                    Collections.addAll(list, newArrays);
                }else {
                	 Collections.addAll(list, history_arr);
        		}
            }else{
            	findViewById(R.id.expert_select_search_history_del).setVisibility(View.GONE);
            }
            // 设置适配器
            mListView.setAdapter(mAdapter);
            if(list != null && list.size() >0){
            	 mAdapter.addAll(list);
            }
        }else{
        	findViewById(R.id.expert_select_search_history_del).setVisibility(View.GONE);
        }
        listener();
	}
	
	public void listener(){
		findViewById(R.id.expert_search_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String text = searchTV.getText().toString();
						save(text);
						Intent intent = new Intent();
						intent.putExtra("searchValue", text);
						setResult(RESULT_OK, intent);
						finish();
					}
				});

		searchTV.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
										  KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					String text = searchTV.getText().toString();
					save(text);
					Intent intent = new Intent();
					intent.putExtra("searchValue", text);
					setResult(RESULT_OK, intent);
					finish();
				}
				return false;
			}
		});
		
		findViewById(R.id.expert_select_search_history_del).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 SharedPreferences mysp = getSharedPreferences("expert_search_history", 0);
			     SharedPreferences.Editor myeditor = mysp.edit();
			     myeditor.putString("expert", "").commit();
			     mAdapter.removeAll();
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				save(mAdapter.list.get(position));
				Intent intent = new Intent();
				intent.putExtra("searchValue", mAdapter.list.get(position));
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		findViewById(R.id.top_left).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	public class MyAdapter extends BaseAdapter {
		List<String> list = new ArrayList<>();

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = getLayoutInflater().inflate(R.layout.activity_shop_history_item, null);
			((TextView)view.findViewById(R.id.expert_search_item_text)).setText(list.get(position));
			view.findViewById(R.id.expert_search_item_del).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SharedPreferences mysp = getSharedPreferences("expert_search_history", 0);
			        String old_text = mysp.getString("expert", "");
			     // 用逗号分割内容返回数组
			        String[] history_arr = old_text.split(",");
			        List<String> hisList = new ArrayList<>();
			        Collections.addAll(hisList, history_arr);
			        for (int i = 0; i < hisList.size(); i++) {
						if(list.get(position).equals(hisList.get(i))){
							hisList.remove(i);
						}
					}
			        
			        StringBuilder builder = new StringBuilder();
			        for (String string : hisList) {
					    builder.append(string+",");
				    }

			        // 判断搜索内容是否已经存在于历史文件，已存在则不重复添加
			        SharedPreferences.Editor myeditor = mysp.edit();
				    myeditor.putString("expert", builder.toString()).commit();
			        list.remove(position);
			        notifyDataSetChanged();
//			        } 
				}
			});
			return view;
		}
		
		public void addAll(List<String> list){
			this.list.addAll(list);
		}
		
		public void add(String text){
			list.add(text);
			notifyDataSetChanged();
		}
		
		public void removeAll(){
			list.clear();
			notifyDataSetChanged();
		}
	}
	

    public void save(String text) {
    	if(!"".equals(text)){
    		findViewById(R.id.expert_select_search_history_del).setVisibility(View.VISIBLE);
            // 获取搜索框信息
            SharedPreferences mysp = getSharedPreferences("expert_search_history", 0);
            String old_text = mysp.getString("expert", "");
            
            // 利用StringBuilder.append新增内容，逗号便于读取内容时用逗号拆分开
            StringBuilder builder = new StringBuilder(old_text);
            builder.append(text + ",");

            // 判断搜索内容是否已经存在于历史文件，已存在则不重复添加
            if (!old_text.contains(text + ",")) {
                SharedPreferences.Editor myeditor = mysp.edit();
                myeditor.putString("expert", builder.toString());
                myeditor.commit();
                mAdapter.add(text);
            } 
    	}
    }
}
