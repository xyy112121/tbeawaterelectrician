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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.BadgeView;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.MessageCategory;
import com.tbea.tb.tbeawaterelectrician.entity.Order;
import com.tbea.tb.tbeawaterelectrician.entity.ProductInfo;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价中心
 */

public class EvaluateListActivity extends TopActivity {
    private ListView mListView;
    private MyAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        initTopbar("我的消息");
        mContext = this;
        Gson gson = new Gson();
        String objJson = getIntent().getStringExtra("obj");
        List<ProductInfo> list = gson.fromJson(objJson,new TypeToken<List<ProductInfo>>(){}.getType());
        mListView = (ListView)findViewById(R.id.listview);
        mAdapter = new MyAdapter(mContext,list);
        mListView.setAdapter(mAdapter);
    }



    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<ProductInfo> mList = new ArrayList<>();

        public MyAdapter(Context context,List<ProductInfo> list){
            this.mContext = context;
            this.mList = list;
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
            View view = (View)inflater.inflate(R.layout.activity_order_evaluate_list_item,null);
            final ProductInfo obj = mList.get(i);
            final ImageView imageView = (ImageView) view.findViewById(R.id.order_evaluate_commdith_item_picture);
            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getCommoditypicture(),imageView);
            ((TextView)view.findViewById(R.id.order_evaluate_commdith_item_name)).setText(obj.getCommodityname());
            view.findViewById(R.id.order_evaluate_item_btn1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EvaluateEditActivity.class);
                    Gson gson = new Gson();
                    String objGson = gson.toJson(obj);
                    intent.putExtra("obj",objGson);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}
