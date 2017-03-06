package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.thenLive.TakeViewActivity;
import com.tbea.tb.tbeawaterelectrician.component.BadgeView;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.MessageCategory;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的消息列表
 */

public class MessageListActivity  extends TopActivity{
    private ListView mListView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        initTopbar("我的消息");
        mListView = (ListView)findViewById(R.id.listview);
        mAdapter = new MyAdapter(MessageListActivity.this);
        mListView.setAdapter(mAdapter);
        getListDate();
    }

    /**
     * 获取数据
     */
    public void getListDate(){
        final CustomDialog dialog = new CustomDialog(MessageListActivity.this,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<MessageCategory> list = (List<MessageCategory>)re.getDateObj("messagecategorylist");
                            if(list != null){
                                mAdapter.addAll(list);
                            }

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
                    RspInfo re = userAction.getMessageList();
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private class MyAdapter extends BaseAdapter{
        private Context mContext;
        private List<MessageCategory> mList = new ArrayList<>();

        public MyAdapter(Context context){
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
        public View getView(int i, View v, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View view = (View)inflater.inflate(R.layout.activity_message_list_item,null);
            final MessageCategory obj = mList.get(i);
            final ImageView imageView = (ImageView) view.findViewById(R.id.message_item_picture);
            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getPicture(),imageView);
            ((TextView)view.findViewById(R.id.message_item_title)).setText(obj.getName());
            ((TextView)view.findViewById(R.id.message_item_time)).setText(obj.getLasttime());
            ((TextView)view.findViewById(R.id.message_item_content)).setText(obj.getLastmessagetitle());

            if(!"0".equals(obj.getNewcount()) && !"".equals(obj.getNewcount())){
                BadgeView badgeView = new BadgeView(mContext,imageView);
                badgeView.setText(obj.getNewcount());
                badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                badgeView.setBadgeMargin(0, 0); // 水平和竖直方向的间距
                badgeView.show();
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MessageViewActivity.class);
                    intent.putExtra("id",obj.getId());
                    startActivity(intent);
                }
            });
            return view;
        }

        public  void addAll(List<MessageCategory> list){
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }
}
