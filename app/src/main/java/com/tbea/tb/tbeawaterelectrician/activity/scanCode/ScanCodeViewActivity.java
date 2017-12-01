package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MyAccusationEditActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.WalletIncomeAndExpensesActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.ScanCode;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *返利详情
 */

public class ScanCodeViewActivity extends TopActivity {
    private ScanCode mObj;
    private String mNeedappeal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_view);
        initTopbar("返利详情");
        if ("local".equals(getIntent().getStringExtra("type"))) {
            Gson gson = new Gson();
            ScanCode obj = gson.fromJson(getIntent().getStringExtra("obj"), ScanCode.class);
            setViewDate(obj);
        } else {
            getDate();//网络取数据
        }


        findViewById(R.id.scan_code_comfire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mObj != null) {
                    if (mObj.getNeedappeal().equals("0")) {
                        final CustomDialog dialog = new CustomDialog(ScanCodeViewActivity.this, R.style.MyDialog, R.layout.tip_delete_dialog);
                        dialog.setText("请您确认？");
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
                                comfire();
                            }
                        }, "确认");
                        dialog.show();
                    }
                }
            }
        });

        findViewById(R.id.scan_code_tip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mObj != null) {
                    if ("2".equals(mNeedappeal)) {
                        UtilAssistants.showToast("您已经举报过了",mContext);
                        return;
                    }
                    Intent intent = new Intent(ScanCodeViewActivity.this, MyAccusationEditActivity.class);
                    String scanCode = getIntent().getStringExtra("scanCode");
                    intent.putExtra("name", mObj.getCommodityname());
                    intent.putExtra("commodityid", mObj.getId());
                    intent.putExtra("scancode", scanCode);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.scan_code_mobilenumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mObj.getMobilenumber()));
                startActivity(intent);
            }
        });
    }


    private void comfire() {

        final CustomDialog dialog = new CustomDialog(ScanCodeViewActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo1 re = (RspInfo1) msg.obj;
                            if (re.isSuccess()) {
                                UtilAssistants.showToast(re.getMsg(),mContext);
                                startActivity(new Intent(ScanCodeViewActivity.this, WalletIncomeAndExpensesActivity.class));
                                finish();
                            } else {
                                UtilAssistants.showToast(re.getMsg(),mContext);
                            }
                        } catch (Exception e) {
                            UtilAssistants.showToast("操作失败！",mContext);
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
                    String scanCode = getIntent().getStringExtra("scanCode");
                    RspInfo1 re = userAction.fanLiComfirm(scanCode);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private void setViewDate(ScanCode obj) {
        mObj = obj;
        ((TextView) findViewById(R.id.scan_code_view_name)).setText(obj.getName());
        ((TextView) findViewById(R.id.scan_code_view_price)).setText("￥" + obj.getPrice());
        ImageView imageView = (ImageView) findViewById(R.id.scan_code_view_picture);
        ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.getPicture(), imageView);
        ((TextView) findViewById(R.id.scan_code_view_rebatemoney)).setText("￥" + obj.getRebatemoney());
        ((TextView) findViewById(R.id.scan_code_view_scantime)).setText(obj.getScantime());
        ((TextView) findViewById(R.id.scan_code_view_scanaddress)).setText(obj.getScanaddress());
        ((TextView) findViewById(R.id.scan_code_view_distributor)).setText(obj.getDistributor());
        ((TextView) findViewById(R.id.scan_code_view_commodityname)).setText(obj.getCommodityname());
        ((TextView) findViewById(R.id.scan_code_view_commodityspec)).setText(obj.getCommodityspec());
        ((TextView) findViewById(R.id.scan_code_view_manufacturedate)).setText(obj.getManufacturedate());
        ((TextView) findViewById(R.id.scan_code_mobilenumber)).setText("窜货有奖举报电话:" + obj.getMobilenumber());

        ((TextView) findViewById(R.id.scan_code_appealreward)).setText(obj.getAppealreward());
        if (obj.getNeedappeal().equals("0")) {
            Button comfireBtn = (Button) findViewById(R.id.scan_code_comfire);
            Button tipBtn = (Button) findViewById(R.id.scan_code_tip);
            comfireBtn.setEnabled(true);
            tipBtn.setEnabled(false);
            findViewById(R.id.scan_code_userdistributor).setVisibility(View.GONE);
            findViewById(R.id.scan_code_userdistributor_tv).setVisibility(View.GONE);
            findViewById(R.id.scan_code_userdistributor_tv1).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.scan_code_view_distributor)).setTextColor(ContextCompat.getColor(ScanCodeViewActivity.this, R.color.black));
        } else {//不符合
            Button comfireBtn = (Button) findViewById(R.id.scan_code_comfire);
            Button tipBtn = (Button) findViewById(R.id.scan_code_tip);
            comfireBtn.setEnabled(false);
            tipBtn.setEnabled(true);
            ((TextView) findViewById(R.id.scan_code_userdistributor)).setText(obj.getUserdistributor());
            ((TextView) findViewById(R.id.scan_code_view_distributor)).setTextColor(ContextCompat.getColor(ScanCodeViewActivity.this, R.color.red));
        }

        if (ShareConfig.getConfigBoolean(ScanCodeViewActivity.this, Constants.ONLINE, false) == false) {
            findViewById(R.id.scan_code_comfire).setEnabled(false);
            findViewById(R.id.scan_code_tip).setEnabled(false);
        }
    }

    private void getDate() {
        final CustomDialog dialog = new CustomDialog(ScanCodeViewActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        try {
                            RspInfo1 re = (RspInfo1) msg.obj;
                            if (re.isSuccess()) {
                                Map<String, Object> data = (Map<String, Object>) re.getData();
                                ScanCode obj = new ScanCode();
                                Map<String, String> commodityinfo = (Map<String, String>) data.get("commodityinfo");
                                Map<String, String> scaninfo = (Map<String, String>) data.get("scaninfo");
                                Map<String, String> appealinfo = (Map<String, String>) data.get("appealinfo");
                                Map<String, String> userinfo = (Map<String, String>) data.get("userinfo");

                                if (userinfo != null) {
                                    obj.setUserdistributor(userinfo.get("userdistributor"));
                                    obj.setUserdistributorid(userinfo.get("userdistributorid"));
                                }

                                if (appealinfo != null) {
                                    obj.setAppealreward(appealinfo.get("appealreward"));
                                    obj.setMobilenumber(appealinfo.get("mobilenumber"));
                                    mNeedappeal = appealinfo.get("needappeal");
                                    obj.setNeedappeal(appealinfo.get("needappeal"));
                                }

                                if (commodityinfo != null) {
                                    obj.setId(commodityinfo.get("id"));
                                    obj.setName(commodityinfo.get("name"));
                                    obj.setPrice(commodityinfo.get("price"));
                                    obj.setPicture(commodityinfo.get("picture"));
                                    obj.setSpecification(commodityinfo.get("specification"));
                                }
                                if (scaninfo != null) {
                                    obj.setRebatemoney(scaninfo.get("rebatemoney"));
                                    obj.setScantime(scaninfo.get("scantime"));
                                    obj.setScanaddress(scaninfo.get("scanaddress"));
                                    obj.setDistributor(scaninfo.get("distributor"));
                                    obj.setCommodityname(scaninfo.get("commodityname"));
                                    obj.setCommodityspec(scaninfo.get("commodityspec"));
                                    obj.setManufacturedate(scaninfo.get("manufacturedate"));
                                }
//                                 ScanCodeSqlManager.insert(obj);
                                setViewDate(obj);

                            } else {
                                UtilAssistants.showToast(re.getMsg(),mContext);
                            }
                        } catch (Exception e) {
                            Log.d(e.getMessage(), "");
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
                    String scanCode = getIntent().getStringExtra("scanCode");
                    RspInfo1 re = userAction.getFanLi(scanCode, MyApplication.instance.getAddrsss());
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

}
