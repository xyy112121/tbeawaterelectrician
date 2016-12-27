package com.tbea.tb.tbeawaterelectrician.fragment.nearby;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.FranchiserViewActivity;

/**
 * Created by cy on 2016/12/19.附近经销商
 */

public class NearbyFranchiserFagment extends LazyFragment {

    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private ListView mListView;
    private MyAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearby_franchiser_layout, container, false);
        initUI();//实例化控件
        isPrepared = true;
        lazyLoad();//加载数据
        listener();
        return view;
    }

    private void listener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(),FranchiserViewActivity.class));
            }
        });

    }

    /**
     * 实例化组件
     */
    private void initUI() {
        ((TextView)view.findViewById(R.id.franchiser_search_condition1)).setText("全部类型");
        ((TextView)view.findViewById(R.id.franchiser_search_condition2)).setText("全部品牌");
        ((TextView)view.findViewById(R.id.franchiser_search_condition3)).setText("全部区域");
        mListView = (ListView)view.findViewById(R.id.franchiser_select_list);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);
    }

    private class MyAdapter extends BaseAdapter {
        /**
         * android 上下文环境
         */
        private Context context;

        /**
         * 构造函数
         *
         * @param context
         *            android上下文环境
         */
        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 14;
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
                    R.layout.fragment_nearby_franchiser_item_layout, null);

            return view;
        }


        public void remove(int index) {
            if (index > 0) {
                notifyDataSetChanged();
            }
        }

        public void removeAll() {
            notifyDataSetChanged();
        }


    }

    /**
     * 实现懒加载,当屏幕显示这个界面的时候才会触发次方法
     */
    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {

        }
    }
}
