package com.tbea.tb.tbeawaterelectrician.fragment.thenLive;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;

/**
 * Created by abc on 16/12/18.接活
 */

public class TakeFragment extends Fragment {
    private ListView mListView;
    private MyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_take,null);

        mListView = (ListView)view.findViewById(R.id.take_select_list);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        listener(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        ((MainActivity)getActivity()).setTopShow();
        super.onHiddenChanged(hidden);
    }

    public  void listener(View view){
//          // 类型
//            findViewById(R.id.expert_selelct_list_region_layout)
//                    .setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            showView(v, R.id.expert_selelct_list_region_iamge);
//                        }
//                    });
//            // 性质
//            findViewById(R.id.expert_selelct_list_section_layout)
//                    .setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            showView(v, R.id.expert_selelct_list_section_image);
//                        }
//                    });
//
//            // 等级
//            findViewById(R.id.expert_selelct_list_sort_layout).setOnClickListener(
//                    new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            showView(v, R.id.expert_selelct_list_sort_iamge);
//                        }
//                    });
//
//            findViewById(R.id.expert_select_list_search_conditions_view)
//                    .setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            hideSearchConditions();
//                        }
//                    });
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
                    R.layout.fragment_take_item_layout, null);

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
}
