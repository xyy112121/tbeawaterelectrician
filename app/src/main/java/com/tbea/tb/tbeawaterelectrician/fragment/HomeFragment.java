package com.tbea.tb.tbeawaterelectrician.fragment;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
//import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cy on 2016/12/16.首页
 */

public class HomeFragment extends Fragment {
    private ListView mListView;
    private MyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_home,null);
        initView(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        ((MainActivity)getActivity()).setTopShow();
        super.onHiddenChanged(hidden);
    }

    private void initView(View view){
        Integer[] images={R.drawable.icon_testpic1,R.drawable.icon_testpic3};
        Banner banner = (Banner) view.findViewById(R.id.banner);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置轮播时间
//        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);

        mListView = (ListView)view.findViewById(R.id.home_list);
        mAdapter = new MyAdapter(getActivity());
        mListView.setAdapter(mAdapter);

    }

    private class MyAdapter extends BaseAdapter{
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
            return 3;
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
                    R.layout.home_nearby_shop_layout, null);

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
