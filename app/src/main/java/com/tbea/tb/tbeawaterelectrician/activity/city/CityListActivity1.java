package com.tbea.tb.tbeawaterelectrician.activity.city;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.CityBean;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by cy on 2017/1/25.
 */

public class CityListActivity1 extends Activity {
    private String mCityName = "";
    private RecyclerView mRv;
    private CityAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;
    private LinearLayoutManager mManager;
    private List<CityBean> mDatas;

    private SuspensionDecoration mDecoration;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;


//    private StickyListHeadersListView mListView;
//    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list1);
//        mListView = (StickyListHeadersListView)findViewById(R.id.list);
//        mAdapter = new MyAdapter(CityListActivity1.this);
//        mListView.setAdapter(mAdapter);
//        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_city_list_head,null);
//        mListView.addHeaderView(layout);

        listener();

        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));

        mAdapter = new CityAdapter(this, mDatas);
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {
            @Override
            protected void onBindHeaderHolder(final ViewHolder holder, int headerPos, int layoutId, Object o) {
                holder.setText(R.id.city_list_head_addr, (String) o);
                holder.getView(R.id.city_list_head_gps).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.setText(R.id.city_list_head_addr, MyApplication.instance.getProvince() + " " + MyApplication.instance.getCity() + " " + MyApplication.instance.getDistrict());
                    }
                });

                holder.getView(R.id.city_list_head_addr).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("cityName", MyApplication.instance.getCity());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        };
        mHeaderAdapter.setHeaderView(R.layout.activity_city_list_head, "四川省 德阳市 旌阳区");

        mRv.setAdapter(mHeaderAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, mDatas).setHeaderViewCount(mHeaderAdapter.getHeaderViewCount()));

        //如果add两个，那么按照先后顺序，依次渲染。
        mRv.addItemDecoration(new DividerItemDecoration(CityListActivity1.this, DividerItemDecoration.VERTICAL_LIST));

        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar

        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager);//设置RecyclerView的LayoutManager

        getDate();
    }

    private void listener() {
        final EditText editText = (EditText) findViewById(R.id.city_list_search_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mCityName = editText.getText() + "";
//                    mAdapter.removeAll();
                    getDate();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.city_list_search_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Condition condition = (Condition) mAdapter.getItem(i-1);
//                Intent intent = new Intent();
//                intent.putExtra("cityId",condition.getId());
//                intent.putExtra("cityName",condition.getName());
//                setResult(RESULT_OK,intent);
//                finish();
//            }
//        });
//

//
//        findViewById(R.id.city_list_head_addr).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
////                intent.putExtra("cityId");
//                intent.putExtra("cityName",MyApplication.instance.getCity());
//                setResult(RESULT_OK,intent);
//                finish();
//            }
//        });

    }

    private void getDate() {
        final CustomDialog dialog = new CustomDialog(CityListActivity1.this, R.style.MyDialog, R.layout.tip_wait_dialog);
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
                            List<Condition> list = (List<Condition>) re.getDateObj("citylist");
                            if (list != null) {
                                mDatas = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    CityBean cityBean = new CityBean(list.get(i).getName(), list.get(i).getId());
//                                    cityBean.setCity();//设置城市名称
                                    mDatas.add(cityBean);
                                }

                                mIndexBar.setmSourceDatas(mDatas)//设置数据
                                        .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount())//设置HeaderView数量
                                        .invalidate();

                                mAdapter.setDatas(mDatas);
                                mHeaderAdapter.notifyDataSetChanged();
                                mDecoration.setmDatas(mDatas);
                            }
//                                mAdapter.addAll(list);
                        } else {
                            ToastUtil.showMessage(re.getMsg(), CityListActivity1.this);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", CityListActivity1.this);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getCityList(mCityName);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private List<Condition> mList = new ArrayList<>();
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void addAll(List<Condition> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.activity_city_list_item, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(mList.get(position).getName());

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.activity_city_list_item_head, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            holder.text.setText("选择城市");
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return 0;
        }

        class HeaderViewHolder {
            TextView text;
        }

        class ViewHolder {
            TextView text;
        }

    }
}
