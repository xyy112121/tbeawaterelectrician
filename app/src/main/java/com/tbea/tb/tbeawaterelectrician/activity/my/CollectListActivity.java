package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.CommodithViewActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Collect;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 我的收藏列表
 */

public class CollectListActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {
    private BGARefreshLayout mRefreshLayout;
    private ListView mListView;
    private MyAdapter mAdapter;
    private int mPage = 1;
    private int mPagesiz = 10;
    private List<String> selectList = new ArrayList<>();
    private boolean mFlag = false;//判断当前取消收藏的按钮是否出现

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_search_list);
        initTopbar("我的收藏", "编辑", this);
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter(CollectListActivity.this);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(CollectListActivity.this, true));
        mRefreshLayout.beginRefreshing();
        findViewById(R.id.wallet_list_layout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消收藏
                cancelCollect();
            }
        });
    }

    /**
     * 获取数据
     */
    public void getListDate() {
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Collect> list = (List<Collect>) re.getDateObj("mysavelist");
                            if (list != null) {
                                mAdapter.addAll(list);
                            } else {
                                mListView.setSelection(mAdapter.getCount());
                                if (mPage > 1) {//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }

                        } else {
                            ToastUtil.showMessage(re.getMsg(), mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getCollectList(mPage++, mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
        mAdapter.removeAll();
        mPage = 1;
        getListDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        getListDate();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (mFlag == false) {
            for (CheckBox ck : mAdapter.ckList) {
                ck.setVisibility(View.VISIBLE);
            }
            findViewById(R.id.wallet_list_layout).setVisibility(View.VISIBLE);
            mFlag = true;
            ((TextView) findViewById(R.id.top_center)).setText("我的收藏");
            ((TextView) findViewById(R.id.top_right_text)).setText("完成");
        } else {
            for (CheckBox ck : mAdapter.ckList) {
                ck.setVisibility(View.GONE);
            }
            findViewById(R.id.wallet_list_layout).setVisibility(View.GONE);
            mFlag = false;
            ((TextView) findViewById(R.id.top_center)).setText("编辑收藏");
            ((TextView) findViewById(R.id.top_right_text)).setText("编辑");
        }
    }

    /**
     * 取消收藏
     */
    public void cancelCollect() {
        if (selectList.size() < 0) {
            ToastUtil.showMessage("请选择需要取消收藏的商品！", mContext);
            return;
        }
        final CustomDialog dialog = new CustomDialog(CollectListActivity.this, R.style.MyDialog, R.layout.tip_delete_dialog);
        dialog.show();
        dialog.setConfirmBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        }, "取消");

        dialog.setCancelBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final CustomDialog dialog2 = new CustomDialog(CollectListActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
                dialog2.setText("请等待...");
                dialog2.show();
                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        dialog2.dismiss();
                        switch (msg.what) {
                            case ThreadState.SUCCESS:
                                RspInfo1 re = (RspInfo1) msg.obj;
                                if (re.isSuccess()) {
                                    mPage = 1;
                                    mRefreshLayout.beginRefreshing();
                                } else {
                                    ToastUtil.showMessage(re.getMsg(), mContext);
                                }

                                break;
                            case ThreadState.ERROR:
                                ToastUtil.showMessage("操作失败！", mContext);
                                break;
                        }
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UserAction userAction = new UserAction();
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < selectList.size(); i++) {
                                if (i > 0) {
                                    sb.append(",");
                                }
                                sb.append(selectList.get(i));
                            }
                            RspInfo1 re = userAction.cancelCollect(sb.toString());
                            handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                        } catch (Exception e) {
                            handler.sendEmptyMessage(ThreadState.ERROR);
                        }
                    }
                }).start();
            }
        }, "确定");

    }

    private class MyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;

        public List<Collect> mList = new ArrayList<>();

        public List<CheckBox> ckList = new ArrayList<>();

        /**
         * 构造函数
         *
         * @param context android上下文环境
         */
        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            FrameLayout view = (FrameLayout) layoutInflater.inflate(
                    R.layout.activity_collect_list_item, null);
            final Collect obj = mList.get(position);
            ImageView imageView = (ImageView) view.findViewById(R.id.company_item_picture);
            if (!obj.getPicture().equals("")) {
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.getPicture(), imageView);
            }
            ((TextView) view.findViewById(R.id.company_item_name)).setText(obj.getName());
            ((TextView) view.findViewById(R.id.company_item_money)).setText("￥" + obj.getPrice());
            CheckBox ck = (CheckBox) view.findViewById(R.id.company_item_ck);
            if (mFlag) {
                ck.setVisibility(View.VISIBLE);
            }
            ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        selectList.add(obj.getId());
                    } else {
                        selectList.remove(obj.getId());
                    }
                }
            });
            ckList.add(ck);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommodithViewActivity.class);
                    intent.putExtra("id", obj.getCommodityid());
                    startActivity(intent);
                }
            });
            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                mList.remove(index);
                notifyDataSetChanged();
            }
        }

        public void addAll(List<Collect> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }
}
