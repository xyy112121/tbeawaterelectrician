package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.ScanCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cy on 2017/1/18.
 */

public class ScanCodeHistoryActivity extends TopActivity implements View.OnClickListener{
    private ListView mListView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_history);
        initTopbar("扫码记录","全部清空",this);
        List<ScanCode> list = ScanCodeSqlManager.queryAll();
        mListView = (ListView)findViewById(R.id.scan_code_history_list);
        mAdapter = new MyAdapter(ScanCodeHistoryActivity.this,list);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View view) {
        showDialog("是否清除全部扫码记录？",-1,"");
    }

    private class  MyAdapter extends BaseAdapter{
        private Context mContext;
        private List<ScanCode> mList = new ArrayList<>();

        private MyAdapter(Context context,List<ScanCode> list){
            this.mContext = context;
            if(list != null){
                mList.addAll(list);
            }
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
                    R.layout.activity_scancode_history_item, null);
            final  ScanCode obj = mList.get(i);
            ((TextView)view.findViewById(R.id.scancode_history_distributor)).setText(obj.getDistributor());
            ((TextView)view.findViewById(R.id.scancode_history_name)).setText(obj.getName());
            ((TextView)view.findViewById(R.id.scancode_history_rebatemoney)).setText(obj.getRebatemoney());

            ImageView imageView = (ImageView)view.findViewById(R.id.scancode_history_picture);
            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getPicture(),imageView);
            view.findViewById(R.id.scan_code_history_delect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   showDialog("是否清除该条扫码记录？",i,obj.getId());
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ScanCodeHistoryActivity.this,ScanCodeViewActivity.class);
                    intent.putExtra("type","local");
                    Gson json = new Gson();
                    intent.putExtra("obj",json.toJson(obj));
                    startActivity(intent);
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
