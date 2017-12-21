package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.model.ServicesCopeInfoModel;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务范围选择
 */

public class ServicesCopeSelectActivity extends TopActivity implements View.OnClickListener {
    private RecyclerView mRv;
    private MyAdapter mAdapter;
    private List<String> mIds = new ArrayList<>();
    private List<ServicesCopeInfoModel.Servicescope> mSeleteObjs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicescope);
        initTopbar("服务范围", "保存", this);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(mContext));
        String registZone = getIntent().getStringExtra("registZone");
        ((TextView) findViewById(R.id.registzone_tv)).setText("注册区域：" + registZone);
        mSeleteObjs = (List<ServicesCopeInfoModel.Servicescope>) getIntent().getSerializableExtra("selectObj");
        mAdapter = new MyAdapter(mContext);
        mRv.setAdapter(mAdapter);
        getDate();
    }

    /**
     * 获取信息
     */
    public void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            String json = gson.toJson(re.getData());
                            ServicesCopeInfoModel model = gson.fromJson(json, ServicesCopeInfoModel.class);
                            if (model.servicescopelist != null) {
                                mAdapter.addAll(model.servicescopelist);
                            }
                        } else {
                            ToastUtil.showMessage(re.getMsg(), mContext);
                        }
                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.getServicesCopeList();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (mIds.size() < 1) {
            ToastUtil.showMessage("请先选择服务范围", mContext);
            return;
        }
        final StringBuffer buffer = new StringBuffer();
        for (String id : mIds) {
            buffer.append(id);
            if (buffer.length() > 0) {
                buffer.append(",");
            }
        }
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            finish();
                        } else {
                            ToastUtil.showMessage(re.getMsg(), mContext);
                        }
                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.saveServicesCope(buffer.toString());
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        protected Context mContext;
        protected List<ServicesCopeInfoModel.Servicescope> mDatas = new ArrayList<>();
        protected LayoutInflater mInflater;

        public MyAdapter(Context mContext) {
            this.mContext = mContext;
            mInflater = LayoutInflater.from(mContext);
        }

        public void removeAll() {
            mDatas.clear();
            notifyDataSetChanged();
        }


        public List<ServicesCopeInfoModel.Servicescope> getDatas() {
            return mDatas;
        }

        public MyAdapter addAll(List<ServicesCopeInfoModel.Servicescope> datas) {
            mDatas = datas;
            notifyDataSetChanged();
            return this;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyAdapter.ViewHolder(mInflater.inflate(R.layout.activity_servicescope_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
            final ServicesCopeInfoModel.Servicescope model = mDatas.get(position);
            holder.tvCity.setText(model.servicescope);
            for (ServicesCopeInfoModel.Servicescope item : mSeleteObjs) {
                if (item.servicescopeid.equals(model.servicescopeid)) {
                    holder.ck.setChecked(true);
                    mIds.add(model.servicescopeid);
                }
            }
            holder.ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mIds.add(model.servicescopeid);
                    } else {
                        mIds.remove(model.servicescopeid);
                    }
                }
            });


            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean isCheck = holder.ck.isChecked();
                    if (isCheck) {
                        holder.ck.setChecked(false);
                    } else {
                        holder.ck.setChecked(true);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDatas != null ? mDatas.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCity;
            View content;
            CheckBox ck;

            public ViewHolder(View itemView) {
                super(itemView);
                tvCity = (TextView) itemView.findViewById(R.id.servicescope_tv);
                ck = (CheckBox) itemView.findViewById(R.id.servicescope_ck);
                content = itemView.findViewById(R.id.content);
            }
        }
    }
}
