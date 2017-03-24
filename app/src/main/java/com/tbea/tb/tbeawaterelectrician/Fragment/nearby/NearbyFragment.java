package com.tbea.tb.tbeawaterelectrician.fragment.nearby;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.CityListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MessageListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.HistorySearchActivity;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cy on 2016/12/19.
 */

public class NearbyFragment extends android.app.Fragment {

    //顶部三个LinearLayout
    private LinearLayout mTab01;
    private LinearLayout mTab02;
    private LinearLayout mTab03;

    //顶部的三个TextView
    private TextView id_tab01_info;
    private TextView id_tab02_info;
    private TextView id_tab03_info;

    //Tab的那个引导线
    private ImageView mTabLine;
    //屏幕的宽度
    private int screenWidth;

    private ViewPager mViewPager;
    private FragmentAdapter mAdapter;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    public static String mCityname = "德阳市";
    public static  String mCityid = null;
    private final int CITY_RESULT = 1001;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (View)inflater.inflate(R.layout.fragment_nearby,null);
        initView(mView);
        return mView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getMessageNumber();
    }

    //获取购物车数量
    private void  getMessageNumber(){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo re = (RspInfo) msg.obj;
                            if (re.isSuccess()) {
                                Map<String,String > shortcutinfo = (Map<String, String>) re.getDateObj("shortcutinfo");
                                if(shortcutinfo != null){
                                    String newmessagenumber = shortcutinfo.get("newmessagenumber");
                                    ImageView imageView = (ImageView) mView.findViewById(R.id.open_my_message);
                                    if(newmessagenumber != null && !"".equals(newmessagenumber) && !"0".equals(newmessagenumber)){
                                        imageView.setImageResource(R.drawable.icon_message_redpoint);
                                    }else {
                                        imageView.setImageResource(R.drawable.icon_message);
                                    }
                                }

                            } else {
                                UtilAssistants.showToast(re.getMsg());
                            }

                        }catch (Exception e){
                            Log.e("","");
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


    /**
     * 根据屏幕的宽度，初始化引导线的宽度
     */
    private void initTabLine(View view) {
        mTabLine = (ImageView) view.findViewById(R.id.id_tab_line);

        //获取屏幕的宽度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;

        //获取控件的LayoutParams参数(注意：一定要用父控件的LayoutParams写LinearLayout.LayoutParams)
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
        lp.width = screenWidth / 3;//设置该控件的layoutParams参数
        mTabLine.setLayoutParams(lp);//将修改好的layoutParams设置为该控件的layoutParams
    }

    /**
     * 初始化控件，初始化Fragment
     */
    private void initView(View view) {
        id_tab01_info = (TextView) view.findViewById(R.id.id_tab01_info);
        id_tab02_info = (TextView) view.findViewById(R.id.id_tab02_info);
        id_tab03_info = (TextView) view.findViewById(R.id.id_tab03_info);

        mTab01 = (LinearLayout) view.findViewById(R.id.id_tab01);
        mTab02 = (LinearLayout) view.findViewById(R.id.id_tab02);
        mTab03 = (LinearLayout) view.findViewById(R.id.id_tab03);

        mTab01.setOnClickListener(new TabOnClickListener(0));
        mTab02.setOnClickListener(new TabOnClickListener(1));
        mTab03.setOnClickListener(new TabOnClickListener(2));

        fragments.add(new NearbyFranchiserFagment());
        fragments.add(new NearbyShopFragment());
        fragments.add(new NearbyCommodithFragment());

        mViewPager = (ViewPager) view.findViewById(R.id.id_viewpager);
        //初始化Adapter
        mAdapter = new FragmentAdapter(((MainActivity)getActivity()).getSupportFragmentManager(), fragments);
//        mAdapter = new FragmentAdapter(((MainActivity)getActivity()).getSupportFragmentManager(),mViewPager, fragments);

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabOnPageChangeListener());
        initTabLine(view);

        view.findViewById(R.id.mian_city_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CityListActivity.class);
                NearbyFragment.this.startActivityForResult(intent,CITY_RESULT);
            }
        });

        view.findViewById(R.id.open_my_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MessageListActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.expert_search_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistorySearchActivity.class);
                startActivity(intent);
            }
        });

        getMessageNumber();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CITY_RESULT && resultCode == getActivity().RESULT_OK){
            mCityid  = data.getStringExtra("cityId");
            mCityname = data.getStringExtra("cityName");
            ((TextView)mView.findViewById(R.id.mian_city_text)).setText(mCityname);
            if(mViewPager.getCurrentItem() == 0){
                //附近经销商
                NearbyFranchiserFagment franchiserFagment = (NearbyFranchiserFagment) fragments.get(0);
                franchiserFagment.refreshDate();
            }
            if(mViewPager.getCurrentItem() == 1){
                //附近商家
                NearbyShopFragment franchiserFagment = (NearbyShopFragment) fragments.get(1);
                franchiserFagment.refreshDate();
            }
            if(mViewPager.getCurrentItem() == 2){
                //附近采购
                NearbyCommodithFragment franchiserFagment = (NearbyCommodithFragment) fragments.get(2);
                franchiserFagment.refreshDate();
            }
        }
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        id_tab01_info.setTextColor(ContextCompat.getColor(getActivity(),R.color.pea_green));
        id_tab02_info.setTextColor(ContextCompat.getColor(getActivity(),R.color.pea_green));
        id_tab03_info.setTextColor(ContextCompat.getColor(getActivity(),R.color.pea_green));
    }

    /**
     * 功能：点击主页TAB事件
     */
    public class TabOnClickListener implements View.OnClickListener {
        private int index;

        public TabOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            mViewPager.setCurrentItem(index);//选择某一页
        }

    }

    /**
     * 功能：Fragment页面改变事件
     */
    public class TabOnPageChangeListener implements ViewPager.OnPageChangeListener {

        //当滑动状态改变时调用
        public void onPageScrollStateChanged(int state) {

        }

        //当前页面被滑动时调用
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            fragments.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
            if(fragments.get(position).isAdded()){
                fragments.get(position).onResume(); // 调用切换后Fargment的onResume()
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
            //返回组件距离左侧组件的距离
            lp.leftMargin = (int) ((positionOffset + position) * screenWidth / 3);
            mTabLine.setLayoutParams(lp);
        }

        //当新的页面被选中时调用
        public void onPageSelected(int position) {
            //重置所有TextView的字体颜色
            resetTextView();
            switch (position) {
                case 0:
                    id_tab01_info.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                    break;
                case 1:
                    id_tab02_info.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                    break;
                case 2:
                    id_tab03_info.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                    break;
            }
        }
    }
}
