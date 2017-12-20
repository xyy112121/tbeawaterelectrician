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
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务范围选择
 */

public class ServicesCopeSelectActivity extends TopActivity implements View.OnClickListener {
    private RecyclerView mRv;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicescope);
        initTopbar("服务范围", "保存", this);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(mContext));

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
                            if (model.userinfo != null) {
                                ((TextView) findViewById(R.id.registzone_tv)).setText("注册区域：" + model.userinfo.registzone);
                            }

                        } else {
                            UtilAssistants.showToast(re.getMsg(), mContext);
                        }
                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.getServicesCopeInfo();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {

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

        public MyAdapter setDatas(List<ServicesCopeInfoModel.Servicescope> datas) {
            mDatas = datas;
            return this;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyAdapter.ViewHolder(mInflater.inflate(R.layout.activity_servicescope, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
            final ServicesCopeInfoModel.Servicescope model = mDatas.get(position);
            holder.tvCity.setText(model.name);


//            holder.content.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final boolean isCheck = holder.ck.isChecked();
//                    if (isCheck) {
//                        mDesUsers.remove(model);
//                        mDesIds.remove(userBean.getUser().getId() + "");
//                        holder.ck.setChecked(false);
//                    } else {
//                        holder.ck.setChecked(true);
//                        mDesUsers.add(model);
//                        mDesIds.add(userBean.getUser().getId() + "");
//                    }
//                }
//            });
//            holder.ck.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final boolean isCheck = holder.ck.isChecked();
//                    if (isCheck) {
//                        mDesUsers.remove(model);
//                        mDesIds.remove(userBean.getUser().getId() + "");
//                        holder.ck.setChecked(false);
//                    } else {
//                        holder.ck.setChecked(true);
//                        mDesUsers.add(model);
//                        mDesIds.add(userBean.getUser().getId() + "");
//                    }
//                }
//            });

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
                tvCity = (TextView) itemView.findViewById(R.id.tvCity);
                ck = (CheckBox) itemView.findViewById(R.id.ck);
                content = itemView.findViewById(R.id.content);
            }
        }
    }
}
