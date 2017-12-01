package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.ManufactureProcessEntity;
import com.tbea.tb.tbeawaterelectrician.entity.SuYuan;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 溯源详情
 */

public class SuYuanViewActivity extends TopActivity {
    private ListView mListView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_suyuan_view);
        initTopbar("溯源详情");
        mListView = (ListView) findViewById(R.id.suyuan_view_manufactureprocess_listview);
        mAdapter = new MyAdapter(SuYuanViewActivity.this);
        mListView.setAdapter(mAdapter);
        getDate();
    }

    public void getDate() {
        final CustomDialog dialog = new CustomDialog(SuYuanViewActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
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
                                SuYuan suYuan = new SuYuan();
                                Map<String, String> productinfo = (Map<String, String>) data.get("productinfo");
                                List<Map<String, String>> manufactureProcessEntityList = (List<Map<String, String>>) data.get("manufactureprocess");
                                List<ManufactureProcessEntity> list = new ArrayList<>();

                                if (productinfo != null) {
                                    suYuan.setName(productinfo.get("name"));
                                    suYuan.setSpecifications(productinfo.get("specifications"));
                                    suYuan.setManudate(productinfo.get("manudate"));
                                    suYuan.setDeliverdate(productinfo.get("deliverdate"));
                                    suYuan.setDestination(productinfo.get("destination"));
                                    suYuan.setManufacture(productinfo.get("manufacture"));
                                }
                                if (manufactureProcessEntityList != null) {
                                    for (int i = 0; i < manufactureProcessEntityList.size(); i++) {
                                        ManufactureProcessEntity ManufactureProcessEntity = new ManufactureProcessEntity();
                                        ManufactureProcessEntity.setProcessname(manufactureProcessEntityList.get(i).get("processname"));
                                        ManufactureProcessEntity.setDepartment(manufactureProcessEntityList.get(i).get("department"));
                                        ManufactureProcessEntity.setProcessdate(manufactureProcessEntityList.get(i).get("processdate"));
                                        list.add(ManufactureProcessEntity);
                                    }
                                }
                                suYuan.setManufactureprocess(list);
                                View headView = getLayoutInflater().inflate(R.layout.activity_scancode_suyuan_view_item_head, null);
                                mListView.addHeaderView(headView);
                                mAdapter.addAll(list);
                                ((TextView) findViewById(R.id.suyuan_view_name)).setText(suYuan.getName());
                                ((TextView) findViewById(R.id.suyuan_view_specifications)).setText(suYuan.getSpecifications());
                                ((TextView) findViewById(R.id.suyuan_view_manudate)).setText(suYuan.getManudate());
                                ((TextView) findViewById(R.id.suyuan_view_deliverdate)).setText(suYuan.getDeliverdate());
                                ((TextView) findViewById(R.id.suyuan_view_manufacture)).setText(suYuan.getManufacture());
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
                    RspInfo1
                            re = userAction.getSuYuan(scanCode, MyApplication.instance.getAddrsss());
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private class MyAdapter extends BaseAdapter {
        private List<ManufactureProcessEntity> mList = new ArrayList<>();
        private Context mContext;

        public MyAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View view1 = layoutInflater.inflate(R.layout.activity_scancode_suyuan_view_item, null);
            ((TextView) view1.findViewById(R.id.suyuan_view_item_processname)).setText(mList.get(i).getProcessname());
            ((TextView) view1.findViewById(R.id.suyuan_view_item_processdate)).setText(mList.get(i).getProcessdate());
            ((TextView) view1.findViewById(R.id.suyuan_view_item_department)).setText(mList.get(i).getDepartment());
            return view1;
        }

        public void addAll(List<ManufactureProcessEntity> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

}
