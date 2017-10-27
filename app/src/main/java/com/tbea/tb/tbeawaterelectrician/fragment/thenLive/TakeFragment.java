package com.tbea.tb.tbeawaterelectrician.fragment.thenLive;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.CityListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.city.CityListActivity1;
import com.tbea.tb.tbeawaterelectrician.activity.my.MessageListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.publicUse.activity.NetWebViewActivity;
import com.tbea.tb.tbeawaterelectrician.activity.thenLive.TakeViewActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.entity.Take;
import com.tbea.tb.tbeawaterelectrician.fragment.HomeFragment;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by abc on 16/12/18.接活
 */

public class TakeFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private ListView mListView;
    private MyAdapter mAdapter;
    private View mView;
    private String mTasktypeId = "-10000";//任务类型
    private String mTimescopeId = "-10000";//时间类型
    private String mLocationId = "-10000";//区域类型
    private String mCityname = "德阳市";
    private String mCityid = null;
    private int mPage = 1;
    private int mPagesiz = 10;
    private BGARefreshLayout mRefreshLayout;
    private final int CITY_RESULT = 1002;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (View) inflater.inflate(R.layout.fragment_take, null);
        initUI();
        listener();
        mRefreshLayout.beginRefreshing();
        return mView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getMessageNumber();
        TextView cityView = (TextView) mView.findViewById(R.id.mian_city_text);
        if (!"".endsWith(MyApplication.instance.getCity()) && MyApplication.instance.getCity() != null) {
            cityView.setText(MyApplication.instance.getCity());
        }
    }

    //获取购物车数量
    private void getMessageNumber() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo re = (RspInfo) msg.obj;
                            if (re.isSuccess()) {
                                Map<String, String> shortcutinfo = (Map<String, String>) re.getDateObj("shortcutinfo");
                                if (shortcutinfo != null) {
                                    String newmessagenumber = shortcutinfo.get("newmessagenumber");
                                    ImageView imageView = (ImageView) mView.findViewById(R.id.open_my_message);
                                    if (newmessagenumber != null && !"".equals(newmessagenumber) && !"0".equals(newmessagenumber)) {
                                        imageView.setImageResource(R.drawable.icon_message_redpoint);
                                    } else {
                                        imageView.setImageResource(R.drawable.icon_message);
                                    }
                                }

                            } else {
                                UtilAssistants.showToast(re.getMsg());
                            }

                        } catch (Exception e) {
                            Log.e("", "");
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！");
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getMessageNumber();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();

    }

    public void initUI() {
        mListView = (ListView) mView.findViewById(R.id.take_select_list);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout) mView.findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));
        getMessageNumber();

        TextView cityView = (TextView) mView.findViewById(R.id.mian_city_text);
        if (!"".endsWith(MyApplication.instance.getCity()) && MyApplication.instance.getCity() != null) {
            cityView.setText(MyApplication.instance.getCity());
        }
    }


    public void listener() {
        mView.findViewById(R.id.take_search_condition1_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //任务类型
                getConditionDate(view, "TBEAENG004001001000");
            }
        });

        mView.findViewById(R.id.take_search_condition3_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //全部时间
                getConditionDate(view, "TBEAENG004001002000");
            }
        });

        mView.findViewById(R.id.take_search_condition2_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationList(view);
            }
        });

        mView.findViewById(R.id.mian_city_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CityListActivity1.class);
                TakeFragment.this.startActivityForResult(intent, CITY_RESULT);
            }
        });

        mView.findViewById(R.id.open_my_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MessageListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CITY_RESULT && resultCode == getActivity().RESULT_OK) {
            mCityid = data.getStringExtra("cityId");
            mCityname = data.getStringExtra("cityName");
            ((TextView) mView.findViewById(R.id.mian_city_text)).setText(mCityname);
            mPage = 1;
            mAdapter.removeAll();
            mRefreshLayout.beginRefreshing();
        }
    }

    /**
     * 获取数据
     */
    public void getListDate() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Take> list = (List<Take>) re.getDateObj("tasklist");
                            if (list != null) {
                                mAdapter.addAll(list);
                            } else {
                                if (mPage > 1) {//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }
                        } else {
                            UtilAssistants.showToast(re.getMsg());
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！");
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getTakeList(mTasktypeId, mLocationId, mTimescopeId, mCityname, mCityid, mPage++, mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取区域列表
     */
    public void getLocationList(final View view) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Condition> list = (List<Condition>) re.getDateObj("locationlist");
                            if (list != null) {
                                showDialog(view, list, "");
                            }
                        } else {
                            UtilAssistants.showToast("操作失败！");
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！");
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getLocationList(mCityname);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取查询条件列表
     *
     * @param view
     * @param methodName
     */
    public void getConditionDate(final View view, final String methodName) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Condition> list;
                            if ("TBEAENG004001001000".equals(methodName)) {
                                list = (List<Condition>) re.getDateObj("tasktypelist");
                            } else {
                                list = (List<Condition>) re.getDateObj("timescopelist");
                            }
                            if (list != null) {
                                showDialog(view, list, methodName);
                            }
                        } else {
                            UtilAssistants.showToast("操作失败！");
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！");
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getFranchiserType(methodName);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 查询条件Dialog
     */
    protected void showDialog(View view, List<Condition> data, final String methodName) {
        final CustomPopWindow popWindow = new CustomPopWindow(getActivity(),
                R.id.body_bg_view, true, R.style.PopWindowAnimationFade,
                RelativeLayout.LayoutParams.MATCH_PARENT, R.layout.pop_window_scrollview_layout);
        popWindow.addScrollViewForGroup1(data, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                if (methodName.equals("TBEAENG004001001000")) {//任务类型
                    mTasktypeId = popWindow.mSelectedId;
                    if (popWindow.mSelectedId.equals("")) {
                        mTasktypeId = "-10000";
                    }
                    ((TextView) mView.findViewById(R.id.take_search_condition1)).setText(popWindow.mSelectedName);
                } else if (methodName.equals("TBEAENG004001002000")) {//时间类型
                    mTimescopeId = popWindow.mSelectedId;
                    ((TextView) mView.findViewById(R.id.take_search_condition3)).setText(popWindow.mSelectedName);
                } else {
                    mLocationId = popWindow.mSelectedId;
                    ((TextView) mView.findViewById(R.id.take_search_condition2)).setText(popWindow.mSelectedName);
                }
                mPage = 1;
                mAdapter.removeAll();
                mRefreshLayout.beginRefreshing();
                getListDate();
            }
        });
        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉刷新
        mPage = 1;
        mAdapter.removeAll();
        getListDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多
        getListDate();
        return true;
    }

    private class MyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;
        private List<Take> mList = new ArrayList<>();

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
                    R.layout.fragment_take_item_layout, null);
            final Take obj = mList.get(position);
            ImageView imageView = (ImageView) view.findViewById(R.id.take_item_picture);
            if (!obj.getPicture().equals(""))
                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.getPicture(), imageView);
            ((TextView) view.findViewById(R.id.take_item_publishername)).setText(obj.getPublishername());
            ((TextView) view.findViewById(R.id.take_item_tasktitle)).setText(obj.getTasktitle());
            ((TextView) view.findViewById(R.id.take_item_publishtime)).setText(obj.getPublishtime());
            ((TextView) view.findViewById(R.id.take_item_taskaddress)).setText(obj.getTaskaddress());
            ((TextView) view.findViewById(R.id.take_item_taskdistance)).setText(obj.getTaskdistance());
            if (obj.getPublisheriscompany().equals("1")) {
                view.findViewById(R.id.take_item_publisheriscompany).setVisibility(View.VISIBLE);
            }
            if (obj.getPublisherwhetheridentify().equals("1")) {
                view.findViewById(R.id.take_item_publisherwhetheridentify).setVisibility(View.VISIBLE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(context, TakeViewActivity.class);
//                    intent.putExtra("id",obj.getId());
//                    startActivity(intent);
                    Intent intent = new Intent(getActivity(), NetWebViewActivity.class);
                    intent.putExtra("title", "接活详情");
                    String par = "taskdetail?taskid=" + obj.getId();
                    intent.putExtra("parameter", par);//URL后缀
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

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Take> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

    }
}
