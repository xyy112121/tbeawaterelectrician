package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.fragment.my.WalletPayFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.my.WalletRevenueFragment;

/**
 * 我的钱包
 */

public class WalletListActivity extends TopActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_list);
        initTopbar("我的钱包");
        String size = getIntent().getStringExtra("size");
        ((TextView)findViewById(R.id.my_wallet_list_size)).setText(size);
        listener();
        WalletRevenueFragment fragment = new WalletRevenueFragment();
        switchFragment(fragment);
    }

    private  void  listener(){
        findViewById(R.id.my_wallet_revenue_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.my_wallet_pay_image).setVisibility(View.GONE);
                findViewById(R.id.my_wallet_revenue_image).setVisibility(View.VISIBLE);
                WalletRevenueFragment fragment = new WalletRevenueFragment();
                switchFragment(fragment);
            }
        });

        findViewById(R.id.my_wallet_pay_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.my_wallet_revenue_image).setVisibility(View.GONE);
                findViewById(R.id.my_wallet_pay_image).setVisibility(View.VISIBLE);
                WalletPayFragment fragment = new WalletPayFragment();
                switchFragment(fragment);
            }
        });

        findViewById(R.id.my_wallet_list_withdraw_cash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletListActivity.this,WalletWithdrawCashActivity.class));
            }
        });
    }

    private void switchFragment(Fragment to){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.my_wallet_content_layout, to);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
