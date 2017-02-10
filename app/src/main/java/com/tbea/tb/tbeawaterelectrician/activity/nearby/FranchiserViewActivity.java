package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.fragment.nearby.CompanyDynamicsFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.nearby.NearbyCommodithFragment;

/**
 * Created by cy on 2016/12/26.总经销商
 */

public class FranchiserViewActivity extends TopActivity {
    private Fragment mCurrentFragment;
    private NearbyCommodithFragment mNearbyPurchaseFragment;
    private CompanyDynamicsFragment mCompanyDynamicsFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_franchiser_view);
        initTopbar("经销商信息");
        listener();
        if(mNearbyPurchaseFragment == null){
            mNearbyPurchaseFragment = new NearbyCommodithFragment();
        }
        switchFragment(mCurrentFragment,mNearbyPurchaseFragment,"");
        mCurrentFragment = mNearbyPurchaseFragment;
    }

    private void listener(){
        findViewById(R.id.fragment_view_all_goods_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.franchiser_goods_search_condition_view).setVisibility(View.VISIBLE);
                findViewById(R.id.franchiser_goods_search_condition_layout).setVisibility(View.VISIBLE);
                setCompaySearchTextColor(R.id.fragment_view_all_goods_size,R.id.fragment_view_all_goods_tv);
                if(mNearbyPurchaseFragment == null){
                    mNearbyPurchaseFragment = new NearbyCommodithFragment();
                }
                switchFragment(mCurrentFragment,mNearbyPurchaseFragment,"");
                mCurrentFragment = mNearbyPurchaseFragment;
            }
        });

        findViewById(R.id.fragment_view_company_dynamics_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCompaySearchTextColor(R.id.fragment_view_company_dynamics_size,R.id.fragment_view_company_dynamics_tv);
                findViewById(R.id.franchiser_goods_search_condition_layout).setVisibility(View.GONE);
                findViewById(R.id.franchiser_goods_search_condition_view).setVisibility(View.GONE);
                if(mCompanyDynamicsFragment == null){
                    mCompanyDynamicsFragment = new CompanyDynamicsFragment();
                }
                switchFragment(mCurrentFragment,mCompanyDynamicsFragment,"");
                mCurrentFragment = mCompanyDynamicsFragment;
            }
        });
    }

    private void setCompaySearchTextColor(int id1,int id2){
        setTextColor(R.id.fragment_view_all_goods_size,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.fragment_view_all_goods_tv,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.fragment_view_company_dynamics_size,getResources().getColor(R.color.text_gray));
        setTextColor(R.id.fragment_view_company_dynamics_tv,getResources().getColor(R.color.text_gray));
        setTextColor(id1,getResources().getColor(R.color.blue4));
       setTextColor(id2,getResources().getColor(R.color.blue4));
    }

    private void setTextColor(int ids,int color){
        ((TextView)findViewById(ids)).setTextColor(color);
    }

    /**
     * 切换Fragment
     *
     * @param from
     * @param to
     * @param toTag
     */
    public void switchFragment(Fragment from, Fragment to, String toTag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (from == null) {
            transaction.add(R.id.franchiser_view_condition, to, toTag);
        } else if (from != to) {
            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.franchiser_view_condition, to, toTag);
            } else {
                transaction.hide(from).show(to);
            }
        }
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

}
