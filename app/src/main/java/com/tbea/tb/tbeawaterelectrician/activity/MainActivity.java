package com.tbea.tb.tbeawaterelectrician.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.component.MainNavigateTabBar;
import com.tbea.tb.tbeawaterelectrician.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private MainNavigateTabBar mNavigateTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigateTabBar = (MainNavigateTabBar) findViewById(R.id.mainTabBar);

        mNavigateTabBar.onRestoreInstanceState(savedInstanceState);

        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_home, R.drawable.icon_home_select, "首页"));
        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_nearby, R.drawable.icon_nearby_select, "附近"));
        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(0, 0, ""));
        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_then_live, R.drawable.icon_then_live_select, "接活"));
        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_my, R.drawable.icon_my_select, "我"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigateTabBar.onSaveInstanceState(outState);
    }
}
