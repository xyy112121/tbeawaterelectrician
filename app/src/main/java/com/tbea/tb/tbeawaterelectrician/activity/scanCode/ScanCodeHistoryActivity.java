package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.ScanCode;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by cy on 2017/1/18.
 */

public class ScanCodeHistoryActivity extends TopActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private ListView mListView;
    private MyAdapter mAdapter;
    private Context mContext;
    private  int mPage = 1;
    private int mPagesiz =10 ;
    private BGARefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_list);
        mContext = this;
        initTopbar("扫码记录");
//        List<ScanCode> list = ScanCodeSqlManager.queryAll();
        initUI();
        mRefreshLayout.beginRefreshing();
}

    /**
     * 实例化组件
     */
    private void initUI() {
        mListView = (ListView)findViewById(R.id.my_wallet_listview);
        mAdapter = new MyAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mContext, true));
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String id = mAdapter.mList.get(i).getId();
//                delect(id);
//                return false;
//            }
//        });
    }

    /**
     * 删除数据
     */
    public void delect(final String id){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
               dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            UtilAssistants.showToast(re.getMsg(),mContext);
                            mRefreshLayout.beginRefreshing();
                        }else {
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
                    RspInfo1 re = userAction.delectScanCode(id);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取数据
     */
    public void getDate(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            List<Map<String,String>> list =  (List<Map<String,String>>) data.get("scanlist");
                            List<ScanCode> receiveList = new ArrayList<>();
                            if(list != null){
                                for (int i = 0;i< list.size();i++){
                                    ScanCode scanCode = new ScanCode();
                                    scanCode.setCommodityid(list.get(i).get("commodityid"));
                                    scanCode.setName(list.get(i).get("name"));
                                    scanCode.setRebatemoney(list.get(i).get("rebatemoney"));
                                    scanCode.setScanrebatestatus(list.get(i).get("scanrebatestatus"));
                                    scanCode.setScantime(list.get(i).get("scantime"));
                                    scanCode.setId(list.get(i).get("id"));
                                    receiveList.add(scanCode);
                                }
                                mAdapter.addAll(receiveList);
                            }else {
                                if(mPage >1){//防止分页的时候没有加载数据，但是页数已经增加，导致下一次查询不正确
                                    mPage--;
                                }
                            }
                        }else {
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
                    RspInfo1 re = userAction.getScanCodeList(mPage++,mPagesiz);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPage = 1;
        mAdapter.removeAll();
        getDate();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getDate();
        return true;
    }

    private class  MyAdapter extends BaseAdapter{
        private Context mContext;
        private List<ScanCode> mList = new ArrayList<>();

        private MyAdapter(Context context){
            this.mContext = context;
        }

        private void addAll(List<ScanCode> list){
            mList.addAll(list);
            notifyDataSetChanged();
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
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            FrameLayout view = (FrameLayout) layoutInflater.inflate(
                    R.layout.activity_wallet_income_expenses_list_item, null);
            final  ScanCode obj = mList.get(i);
            view.findViewById(R.id.check_box).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.wallet_income_expences_event)).setText(obj.getName());
            ((TextView)view.findViewById(R.id.wallet_income_expences_thisvalue)).setText("￥"+obj.getRebatemoney());
            ((TextView)view.findViewById(R.id.wallet_income_expences_time)).setText(obj.getScantime());
            ((TextView)view.findViewById(R.id.wallet_income_expences_currenttotlemoney)).setText(obj.getScanrebatestatus());

//            ImageView imageView = (ImageView)view.findViewById(R.id.scancode_history_picture);
//            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getPicture(),imageView);
//            view.findViewById(R.id.scan_code_history_delect).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                   showDialog("是否清除该条扫码记录？",i,obj.getId());
//                }
//            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(ScanCodeHistoryActivity.this,ScanCodeViewActivity.class);
//                    intent.putExtra("type","local");
//                    Gson json = new Gson();
//                    intent.putExtra("obj",json.toJson(obj));
//                    startActivity(intent);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_delete_dialog);
                    dialog.setConfirmBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    },"取消");
                    dialog.setCancelBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            delect(obj.getId());
                        }
                    },"确定");
                    dialog.show();
                    return false;
                }
            });
            return view;
        }

        public  void remove(int postion){
            mList.remove(postion);
            notifyDataSetChanged();
        }

        public  void removeAll(){
            mList.clear();
            notifyDataSetChanged();
        }
    }

    public void showDialog(String text, final int postion, final String id){
        final CustomDialog dialog = new CustomDialog(ScanCodeHistoryActivity.this,R.style.MyDialog,R.layout.tip_delete_dialog);
        dialog.setText(text);
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
                if(postion != -1){
                    long result = ScanCodeSqlManager.delect(id);
                    if(result == 1){
                        mAdapter.remove(postion);
                    }
                }else {
                    long result = ScanCodeSqlManager.delectAll();
                    if(result == 1){
                        mAdapter.removeAll();
                    }
                }
            }
        },"是");
    }


}
