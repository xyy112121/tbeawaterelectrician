package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.fragment.my.OrderListFragmnet;
import com.tbea.tb.tbeawaterelectrician.fragment.nearby.FragmentAdapter;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 16/12/24.订单列表
 */

public class OrderListActivity extends TopActivity {
    private Context mContext;
    private List<FrameLayout> mListLayout = new ArrayList<>();
    private int mIndex = 0;
    private int mIndex2 = -1;//前一次点击的下标
    private int screenWidth;
    public String stateId = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_order_list2);
            initTopbar("我的订单");
            mContext = this;
            //获取屏幕的宽度
            DisplayMetrics outMetrics = new DisplayMetrics();
            getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            screenWidth = outMetrics.widthPixels;
            getStateDate();

        }catch (Exception e){
            System.out.print(e);
        }

    }

    /**
     * 获取订单状态
     */
    public  void getStateDate(){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
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
                            final List<Condition> list = (List<Condition>)re.getDateObj("orderstatuslist");
                            if(list != null){
                                LinearLayout parentLayout = (LinearLayout)findViewById(R.id.order_list_state_layout);
                                for ( int i = 0;i<list.size();i++){
                                    final  int index = i;
                                    Condition item = list.get(i);
                                    final FrameLayout layout = (FrameLayout)getLayoutInflater().inflate(R.layout.activity_order_list_top_item,null);
                                    TextView t =  (TextView)layout.findViewById(R.id.order_list_top_tv);
                                    t.setText(item.getName());
                                    if(i == 0){
                                        ((TextView)layout.findViewById(R.id.order_list_top_tv)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                                        layout.findViewById(R.id.order_list_top_line).setVisibility(View.VISIBLE);
                                        swithFragment();
                                    }
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(screenWidth/list.size(), LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layout.setLayoutParams(lp);
                                    layout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            stateId = list.get(index).getId();
                                            mIndex2 = mIndex;
                                            mIndex = index;
                                            if(mIndex2 != -1 && mIndex != mIndex2 ){
                                                ((TextView)view.findViewById(R.id.order_list_top_tv)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gtay2));
                                                (view.findViewById(R.id.order_list_top_line)).setVisibility(View.VISIBLE);
                                                setViewColor();
                                            }
                                            swithFragment();
                                        }
                                    });
                                    mListLayout.add(layout);
                                    parentLayout.addView(layout);
                                }

                            }
                        }else {
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
                    RspInfo re  = userAction.getOrderState();
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    public void setViewColor(){
        FrameLayout layout = mListLayout.get(mIndex2);
        ((TextView)layout.findViewById(R.id.order_list_top_tv)).setTextColor(ContextCompat.getColor(mContext,R.color.text_gray));
        (layout.findViewById(R.id.order_list_top_line)).setVisibility(View.GONE);
    }

    private void swithFragment(){
        OrderListFragmnet f = new OrderListFragmnet();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame,f);
        t.commit();
    }

}
