package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 积分提现
 */

public class WalletWithdrawCashViewActivity extends TopActivity {
    private String mTakeMoneyCode = "";
    private Timer timer = new Timer();
    private boolean isStart = false;
    private Context mContext;
    private String mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_withdraw_cash_view);
        mContext = this;
        ((TextView) findViewById(R.id.top_center)).setText("提现凭证");
        ((TextView) findViewById(R.id.top_right_text)).setText("删除");
        String money = getIntent().getStringExtra("money");
        String distributorid = getIntent().getStringExtra("distributorid");
        if ("".equals(money) || money == null) {
            String id = getIntent().getStringExtra("takemoneycodeid");
            getDate2(id);
        } else {
            getDate(money, distributorid);
        }

        (findViewById(R.id.top_right_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_delete_dialog);
                dialog.setText("删除后提现二维码将会失效");
                dialog.setText2("如需提现请重新生成二维码");
                dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                }, "取消");
                dialog.setCancelBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if ("".equals(mId)) {
                            UtilAssistants.showToast("删除失败！",mContext);
                        } else {
                            delect(mId);
                        }
                    }
                }, "删除");
                dialog.show();
            }
        });

        findViewById(R.id.top_left_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 根据id获取数据
     */
    public void getDate2(final String takemoneycodeid) {
        //TBEAENG005001006002
        final CustomDialog dialog = new CustomDialog(WalletWithdrawCashViewActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String, String> myMoneyInfo = (Map<String, String>) data.get("mymoneyinfo");
                            Map<String, String> distriButorInfo = (Map<String, String>) data.get("distributorinfo");
                            String validexpiredtime = "有效期:" + myMoneyInfo.get("validexpiredtime");
//                            String status = "状态:"+myMoneyInfo.get("status");
                            mId = myMoneyInfo.get("id");
                            mTakeMoneyCode = myMoneyInfo.get("takemoneycode");
                            String money = "￥" + myMoneyInfo.get("money");
                            String qrcodepicture = MyApplication.instance.getImgPath() + myMoneyInfo.get("qrcodepicture");
                            String note = myMoneyInfo.get("note");
                            String name = "提现单位:" + distriButorInfo.get("name");
                            String addr = "地址:" + distriButorInfo.get("addr");
                            String mobilenumber = "电话:" + distriButorInfo.get("mobilenumber");

                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_validexpiredtime)).setText(validexpiredtime);
//                            ((TextView)findViewById(R.id.wallet_withdraw_cash_view_status)).setText(status);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_takemoneycode)).setText(mTakeMoneyCode);
                            ImageView imageView = (ImageView) findViewById(R.id.wallet_withdraw_cash_view_qrcodepicture);
                            ImageLoader.getInstance().displayImage(qrcodepicture, imageView);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_money)).setText(money);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_note)).setText(note);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_name)).setText(name);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_addr)).setText(addr);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_mobilenumber)).setText(mobilenumber);
                            getCanexChangeMoneySuccess();
                        } else {
                            UtilAssistants.showToast(re.getMsg(),mContext);
                            finish();
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！",mContext);
                        finish();
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.createCodeById(takemoneycodeid);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取数据
     */
    public void getDate(final String money, final String distributorId) {
        final CustomDialog dialog = new CustomDialog(WalletWithdrawCashViewActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String, String> myMoneyInfo = (Map<String, String>) data.get("mymoneyinfo");
                            Map<String, String> distriButorInfo = (Map<String, String>) data.get("distributorinfo");
                            String validexpiredtime = "有效期:" + myMoneyInfo.get("validexpiredtime");
//                            String status = "状态:"+myMoneyInfo.get("status");
                            mId = myMoneyInfo.get("id");
                            mTakeMoneyCode = myMoneyInfo.get("takemoneycode");
                            String money = "￥" + myMoneyInfo.get("money");
                            String qrcodepicture = MyApplication.instance.getImgPath() + myMoneyInfo.get("qrcodepicture");
                            String note = myMoneyInfo.get("note");
                            String name = "提现单位:" + distriButorInfo.get("name");
                            String addr = "地址:" + distriButorInfo.get("addr");
                            String mobilenumber = "电话:" + distriButorInfo.get("mobilenumber");

                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_validexpiredtime)).setText(validexpiredtime);
//                            ((TextView)findViewById(R.id.wallet_withdraw_cash_view_status)).setText(status);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_takemoneycode)).setText(mTakeMoneyCode);
                            ImageView imageView = (ImageView) findViewById(R.id.wallet_withdraw_cash_view_qrcodepicture);
                            ImageLoader.getInstance().displayImage(qrcodepicture, imageView);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_money)).setText(money);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_note)).setText(note);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_name)).setText(name);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_addr)).setText(addr);
                            ((TextView) findViewById(R.id.wallet_withdraw_cash_view_distributorinfo_mobilenumber)).setText(mobilenumber);
                            getCanexChangeMoneySuccess();
                        } else {
                            UtilAssistants.showToast(re.getMsg(),mContext);
                            finish();
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！",mContext);
                        finish();
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.createCode(money, distributorId);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    //处理调用定时器成功以后的操作
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ThreadState.ERROR:
                    if (isStart == false) {
                        isStart = true;
                        timer.schedule(timerTask, 1000, 10000);//10秒后执行
                    }
                    break;
                case ThreadState.SUCCESS:
                    RspInfo1 re = (RspInfo1) msg.obj;
                    if (re.isSuccess()) {
                        timer.cancel();
                        Map<String, Object> data = (Map<String, Object>) re.getData();
                        Map<String, String> takemoneyinfo = (Map<String, String>) data.get("takemoneyinfo");
//                       Map<String, String> distriButorInfo = (Map<String, String>) data.get("distributorinfo");
                        String money = "￥" + takemoneyinfo.get("money");
                        String name = takemoneyinfo.get("distributorname");
                        String takemoneytime = takemoneyinfo.get("takemoneytime");
                        Intent intent = new Intent(WalletWithdrawCashViewActivity.this, WalletWithdrawCashSuccessActivity.class);
                        intent.putExtra("money", money);
                        intent.putExtra("distributorname", name);
                        intent.putExtra("takemoneytime", takemoneytime);
                        intent.putExtra("flag", "view");
                        startActivity(intent);
                        finish();
                    } else {
                        if (isStart == false) {
                            isStart = true;
                            timer.schedule(timerTask, 1000, 10000);//10秒后执行
                        }
                    }
                    break;
            }

        }
    };

    /**
     * 定时器
     */

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            try {
                UserAction userAction = new UserAction();
                RspInfo1 re = userAction.getCanexChangeMoneySuccess(mTakeMoneyCode);
                handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
            } catch (Exception e) {
                handler.sendEmptyMessage(ThreadState.ERROR);
            }
        }
    };

    public void getCanexChangeMoneySuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.getCanexChangeMoneySuccess(mTakeMoneyCode);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 删除数据
     */
    public void delect(final String id) {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            UtilAssistants.showToast(re.getMsg(),mContext);
                            finish();
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
                    RspInfo1 re = userAction.delectTakeMoneyCodeId(id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
