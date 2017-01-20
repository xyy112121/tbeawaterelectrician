package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;

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
        mListView = (ListView)findViewById(R.id.scan_code_history_list);
        mAdapter = new MyAdapter(ScanCodeHistoryActivity.this);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View view) {
        showDialog("是否清除全部扫码记录？");
    }

    private class  MyAdapter extends BaseAdapter{
        private Context mContext;

        private MyAdapter(Context context){
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 4;
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
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            FrameLayout view = (FrameLayout) layoutInflater.inflate(
                    R.layout.activity_scancode_history_item, null);

            view.findViewById(R.id.scan_code_history_delect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   showDialog("是否清除该条扫码记录？");
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ScanCodeHistoryActivity.this,ScanCodeViewActivity.class);
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    public void showDialog(String text){
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
            }
        },"是");
    }


}
