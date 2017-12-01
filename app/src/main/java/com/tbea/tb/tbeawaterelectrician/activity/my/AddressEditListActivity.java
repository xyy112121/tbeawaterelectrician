package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.BadgeView;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Address;
import com.tbea.tb.tbeawaterelectrician.entity.MessageCategory;
import com.tbea.tb.tbeawaterelectrician.entity.Receive;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *收货地址列表
 */

public class AddressEditListActivity extends TopActivity {
    private ListView mListView;
    private MyAdapter mAdapter;
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        initTopbar("收货地址管理");
        findViewById(R.id.addr_list_add).setVisibility(View.VISIBLE);
        mContext = this;
        mListView = (ListView)findViewById(R.id.listview);
        mAdapter = new MyAdapter(mContext);
        mListView.setAdapter(mAdapter);
        getListDate();
        listener();
    }

    private void listener(){
        ((Button)findViewById(R.id.addr_list_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(mContext,AddressEditActivity.class),100);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_delete_dialog);
                dialog.show();
                dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                },"否");
                dialog.setCancelBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        String id = ((Address)mAdapter.getItem(i)).getId();
                        delect(id);

                    }
                },"是");
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Gson gson = new Gson();
                Address obj = (Address) mAdapter.getItem(i);
                String objGson = gson.toJson(obj);
                Intent intent = new Intent();
                intent.putExtra("obj",objGson);
                if("select".equals(getIntent().getStringExtra("flag"))){
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    intent.setClass(mContext,AddressEditActivity.class);
                    intent.putExtra("flag","edit");
                    startActivityForResult(intent,100);
                }
            }
        });
    }

    private void delect(final String id){
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
                           mAdapter.removeAll();
                            getListDate();
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
                    RspInfo1 re = userAction.delectAddr(id);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 100){
            mAdapter.removeAll();
            getListDate();
        }
    }

    /**
     * 获取数据
     */
    public void getListDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            List<Address> list = (List<Address>) re.getDateObj("addresslist");
                            if (list != null) {
                                mAdapter.addAll(list);
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
                    RspInfo re = userAction.getAddrList();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<Address> mList = new ArrayList<>();

        public MyAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View view = (View) inflater.inflate(R.layout.addr_edit_list_item, null);
            try {
                final Address obj = mList.get(i);
                ((TextView)view.findViewById(R.id.addr_item_contactperson)).setText(obj.getContactperson());
                ((TextView)view.findViewById(R.id.addr_item_contactmobile)).setText(obj.getContactmobile());
                if("1".equals(obj.getIsdefault())){
                    view.findViewById(R.id.addr_item_isdefault).setVisibility(View.VISIBLE);

                }else {
                    view.findViewById(R.id.addr_item_isdefault).setVisibility(View.GONE);
                }
                ((TextView)view.findViewById(R.id.addr_item_address)).setText(obj.getAddress());
            }catch (Exception e){
                Log.d("",e.getMessage());
            }

            return view;
        }

        public void addAll(List<Address> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mList.clear();
            notifyDataSetChanged();
        }
    }
}
