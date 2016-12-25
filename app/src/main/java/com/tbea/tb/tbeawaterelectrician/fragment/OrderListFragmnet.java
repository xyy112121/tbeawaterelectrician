package com.tbea.tb.tbeawaterelectrician.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tbea.tb.tbeawaterelectrician.R;

/**
 * Created by abc on 16/12/24.
 */

public class OrderListFragmnet extends LazyFragment {
    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private ListView mListView;
    private MyAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list, container, false);
        initUI();//实例化控件
        isPrepared = true;
        lazyLoad();//加载数据

        return view;
    }

    /**
     * 实例化组件
     */
    private void initUI() {
        mListView = (ListView)view.findViewById(R.id.order_listview);
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
                    R.layout.fragment_order_list_item, null);
            FrameLayout layout = (FrameLayout)layoutInflater.inflate(R.layout.fragment_order_list_item1,null);
            LinearLayout parentsLayout = (LinearLayout)view.findViewById(R.id.item_goods_layout);
            parentsLayout.removeAllViews();
//            if(position == 2){
//                parentsLayout.addView(layout);
//                parentsLayout.addView(layout);
//            }
//
//            if(position == 3){
//                parentsLayout.addView(layout);
//                parentsLayout.addView(layout);
//                parentsLayout.addView(layout);
//            }

            parentsLayout.addView(layout);
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
