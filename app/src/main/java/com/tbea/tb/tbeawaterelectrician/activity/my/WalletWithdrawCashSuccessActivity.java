package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 *积分提现成功界面
 */

public class WalletWithdrawCashSuccessActivity extends TopActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_withdraw_cash_success);
        initTopbar("");
        String name = getIntent().getStringExtra("name");
        String money = getIntent().getStringExtra("money");
        String addr = getIntent().getStringExtra("addr");
        String mobile = getIntent().getStringExtra("mobile");
        ((TextView)findViewById(R.id.wallet_withdraw_cash_view_money)).setText(money);
        ((TextView)findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_name)).setText(name);
        ((TextView)findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_addr)).setText(addr);
        ((TextView)findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_mobilenumber)).setText(mobile);
    }

}
