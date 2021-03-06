package com.tbea.tb.tbeawaterelectrician.fragment.nearby;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.tbea.tb.tbeawaterelectrician.R;

/**
 * Created by cy on 2016/12/19.公司动态
 */

public class CompanyDynamicsFragment extends Fragment {
    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private boolean isVisible;
    private ListView mListView;
    private MyAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearby_franchiser_layout, container, false);
        initUI();//实例化控件
        isPrepared = true;
        lazyLoad();//加载数据

        return view;
    }

    /**
     * 实例化组件
     */
    private void initUI() {
        mListView = (ListView)view.findViewById(R.id.franchiser_select_list);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        view.findViewById(R.id.franchiser_search_condition_layout).setVisibility(View.GONE);
        view.findViewById(R.id.franchiser_search_condition_view).setVisibility(View.GONE);
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
                    R.layout.fragment_company_dynamics_item, null);

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
    private void lazyLoad() {
        if (isPrepared && isVisible) {
//            mRefreshLayout.beginRefreshing();
        }
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
        } else {
            isVisible = false;
        }
    }
}

