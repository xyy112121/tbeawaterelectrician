package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.zhouwei.library.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 搜索历史记录
 */
public class HistorySearchActivity extends TopActivity {
    private EditText searchTV;
    private ListView mListView;
    private MyAdapter mAdapter;
    private String mSearchtype = "1";
    CustomPopWindow mCustomPopWindow;
    TextView mTypeTextView;
    private String mType ="commodity";
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_history_search);
        searchTV = (EditText) findViewById(R.id.expert_search_text);
        mListView = (ListView) findViewById(R.id.search_history_list);
        mTypeTextView = (TextView) findViewById(R.id.search_tap_text);
        String searchValue = getIntent().getStringExtra("searchValue");
        searchTV.setText(searchValue);
        mAdapter = new MyAdapter();
        // 获取搜索记录文件内容
        SharedPreferences sp = getSharedPreferences("expert_search_history", 0);
        String history = sp.getString("expert", "");
        if (!"".equals(history)) {
            // 用逗号分割内容返回数组
            String[] history_arr = history.split(",");
            List<String> list = new ArrayList<>();
            if (history_arr.length >= 1) {
                // 保留前50条数据
                if (history_arr.length > 50) {
                    String[] newArrays = new String[50];
                    // 实现数组之间的复制
                    System.arraycopy(history_arr, 0, newArrays, 0, 50);
                    Collections.addAll(list, newArrays);
                } else {
                    Collections.addAll(list, history_arr);
                }
            } else {
                findViewById(R.id.search_history_del).setVisibility(View.GONE);
                findViewById(R.id.search_history_view).setVisibility(View.GONE);
            }
            // 设置适配器
            mListView.setAdapter(mAdapter);
            if (list != null && list.size() > 0) {
                mAdapter.addAll(list);
            }
        } else {
            findViewById(R.id.search_history_del).setVisibility(View.GONE);
            findViewById(R.id.search_history_view).setVisibility(View.GONE);
        }
        listener();
        getHeatSpeechDate();
    }

    /**
     * 获取热搜热词
     */
    public void getHeatSpeechDate() {
        final CustomDialog dialog = new CustomDialog(HistorySearchActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data1 = (Map<String, Object>) re.getData();
                            List<Map<String, Object>> data = (List<Map<String, Object>>) data1.get("hotwordlist");
                            if (data != null) {
                                for (int i = 0; i < data.size(); i++) {
                                    String name = data.get(i).get("name") + "";
                                    final FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.history_heat_speech_item, null);
                                    ((TextView) layout.findViewById(R.id.text)).setText(name);
                                    layout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String name = ((TextView) layout.findViewById(R.id.text)).getText() + "";
                                            search(name);
                                        }
                                    });
                                    ((LinearLayout) findViewById(R.id.scancode_history_heat_speech_layout)).addView(layout);
                                }
                            }
                        } else {
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
                    RspInfo1 re = userAction.getHeatSpeech(mSearchtype);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    public void listener() {
        findViewById(R.id.search_history_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        searchTV.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = searchTV.getText().toString().trim();
                    if (!"".equals(text) && text.length() >= 2) {
                        save(text);
                        search(text);
                    } else {
                        UtilAssistants.showToast("搜索内容至少需要两个字符！",mContext);
                    }
                }
                return false;
            }
        });

        findViewById(R.id.search_history_del).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                findViewById(R.id.search_history_view).setVisibility(View.GONE);
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
                search(mAdapter.list.get(position));
            }
        });

        findViewById(R.id.search_tap_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View contentView = LayoutInflater.from(mContext).inflate(R.layout.view_pop_menu, null);
                //处理popWindow 显示内容
                handleLogic(contentView);
                //创建并显示popWindow
                mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                        .setView(contentView)
                        .enableBackgroundDark(false) //弹出popWindow时，背景是否变暗
                        .setBgDarkAlpha(0.5f) // 控制亮度
                        .create()
                        .showAsDropDown(v);
            }
        });
    }

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                switch (v.getId()) {
                    //商品(commodity)、经销商(distributor)、商家(companyseller)
                    case R.id.menu1://商品
                        mType = "commodity";
                        mTitle = "商品";
                        break;
                    case R.id.menu2://商家
                        mType = "companyseller";
                        mTitle = "商家";
                        break;
                    case R.id.menu3://经销商
                        mType = "distributor";
                        mTitle = "经销商";
                        break;
                }
                mTypeTextView.setText(mTitle);

            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
    }

    public void search(String keyword) {
        Intent intent = new Intent(HistorySearchActivity.this, HistorySearchListActivity.class);
        intent.putExtra("keyword", keyword);
        intent.putExtra("type", mType);//当前选择查询的类型
        startActivity(intent);
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
            ((TextView) view.findViewById(R.id.expert_search_item_text)).setText(list.get(position));
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
                        if (list.get(position).equals(hisList.get(i))) {
                            hisList.remove(i);
                        }
                    }

                    StringBuilder builder = new StringBuilder();
                    for (String string : hisList) {
                        builder.append(string + ",");
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

        public void addAll(List<String> list) {
            this.list.addAll(list);
        }

        public void add(String text) {
            list.add(text);
            notifyDataSetChanged();
        }

        public void removeAll() {
            list.clear();
            notifyDataSetChanged();
        }
    }


    public void save(String text) {
        if (!"".equals(text)) {
            findViewById(R.id.search_history_del).setVisibility(View.VISIBLE);
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
