package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 *积分提现成功界面
 */

public class WalletWithdrawCashSuccessActivity extends TopActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_withdraw_cash_success);
        if("list".equals(getIntent().getStringExtra("flag"))){
            initTopbar("提现成功");
            findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_finish).setVisibility(View.GONE);
        }else {
            initTopbar("提现成功","历史记录",this);
        }
        String name = getIntent().getStringExtra("distributorname");
        String money = getIntent().getStringExtra("money");
        String takemoneytime = getIntent().getStringExtra("takemoneytime");
        ((TextView)findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_time)).setText(takemoneytime);
        ((TextView)findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_name)).setText(name);
        ((TextView)findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_mobilenumber)).setText(money);

        findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(WalletWithdrawCashSuccessActivity.this, WalletIncomeAndExpensesListActivity.class);
            startActivity(intent);
        }
}

