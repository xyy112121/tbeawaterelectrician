package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.entity.Receive;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *积分提现
 */

public class WalletWithdrawCashActivity extends TopActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_withdraw_cash);
        initTopbar("积分提现");
        getDate();
        findViewById(R.id.wallet_withdraw_cash_finsh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = ((EditText)findViewById(R.id.wallet_withdraw_cash_money)).getText()+"";
                if("".equals(money)){
                    Toast.makeText(WalletWithdrawCashActivity.this,"请填写提现金额！",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(WalletWithdrawCashActivity.this,WalletWithdrawCashViewActivity.class);
                intent.putExtra("money",money);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取数据
     */
    public void getDate(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Map<String, Object> data1 = (Map<String, Object>) re.getData();
                            Map<String, Object> data = (Map<String, Object>) data1.get("mymoneyinfo");
                            String currentMoney = data.get("currentmoney")+"";
                            Double canexChangeMoney = (Double) data.get("canexchangemoney");
                            String text = "积分金额￥"+currentMoney+"当前可提现金额￥"+canexChangeMoney;
                            ((TextView)findViewById(R.id.wallet_withdraw_cash_info)).setText(text);
                        }else {
                            UtilAssistants.showToast(re.getMsg());
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
                    RspInfo1 re = userAction.getCanexChangeMoney();
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
