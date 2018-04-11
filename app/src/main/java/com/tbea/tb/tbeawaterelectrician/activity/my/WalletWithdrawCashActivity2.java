package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Distributor;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 积分提现 界面2
 */

public class WalletWithdrawCashActivity2 extends TopActivity {
    protected String mCanexChangeMoney;
    private Context mContext;
    private String mdistributorid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_withdraw_cash2);
        initTopbar("提现申请");
        mContext = this;
        getDate();
        findViewById(R.id.wallet_withdraw_cash_finsh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_delete_dialog);
                    dialog.setText("请您确认？");
                    dialog.setCancelBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            String money = ((EditText) findViewById(R.id.wallet_withdraw_cash_money)).getText() + "";
                            if ("".equals(money) || "0".equals(money)) {
                                Toast.makeText(WalletWithdrawCashActivity2.this, "请填写提现金额！", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Double mo = Double.parseDouble(money);
                            if (mo > Double.parseDouble(mCanexChangeMoney)) {
                                UtilAssistants.showToast("不能大于最大提现金额",mContext);
                                return;
                            }
                            Intent intent = new Intent(WalletWithdrawCashActivity2.this, WalletWithdrawCashViewActivity.class);
                            intent.putExtra("money", money);
                            intent.putExtra("distributorid", mdistributorid);
                            startActivity(intent);
                            finish();
                        }
                    }, "确认");
                    dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    }, "取消");
                    dialog.show();
                } catch (Exception e) {
                    Log.d("", "");
                }

            }
        });

        findViewById(R.id.wallet_withdraw_cash_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) findViewById(R.id.wallet_withdraw_cash_money)).setText(mCanexChangeMoney + "");
            }
        });

    }



    private void initDistributorView(Distributor obj) {
        ((TextView) findViewById(R.id.wallet_withdraw_cash_name_tv)).setText(obj.getName());
        ((TextView) findViewById(R.id.wallet_withdraw_cash_phone_tv)).setText(obj.getMobilenumber());
    }


    /**
     * 获取数据
     */
    public void getDate() {
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data1 = (Map<String, Object>) re.getData();
                            Map<String, String> data = (Map<String, String>) data1.get("mymoneyinfo");
                            if (data != null) {
                                String mMoney = data.get("currentmoney");
                                mCanexChangeMoney = data.get("canexchangemoney");
                                String text = "提现金额￥" + mMoney + ",当前可提现金额￥" + mCanexChangeMoney;
                                ((TextView) findViewById(R.id.wallet_withdraw_cash_info)).setText(text);
                            }

                            Map<String, Object> recommondDistriButorInfo = (Map<String, Object>) data1.get("recommonddistributorinfo");
                            if (recommondDistriButorInfo != null) {
                                Distributor obj = new Distributor();
                                obj.setId(recommondDistriButorInfo.get("id") + "");
                                obj.setName("经销商："+recommondDistriButorInfo.get("name") + "");
//                                obj.setAddress("地址:" + recommondDistriButorInfo.get("address") + "");
                                obj.setMobilenumber("咨询电话：" + recommondDistriButorInfo.get("mobilenumber") + "");
//                                obj.setDistance(recommondDistriButorInfo.get("distance") + "");
//                                obj.setLatitude(recommondDistriButorInfo.get("latitude") + "");
//                                obj.setLongitude(recommondDistriButorInfo.get("longitude") + "");
                                mdistributorid = obj.getId();
                                initDistributorView(obj);
                            }
                        } else {
                            UtilAssistants.showToast(re.getMsg(),mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！",mContext);
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
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

}