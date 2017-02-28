package com.tbea.tb.tbeawaterelectrician.fragment.my;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.my.AboutActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.CollectListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MessageListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MyAccusationEditActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MyAccusationListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MyInformationActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.OrderListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.ServiceCenterActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.SetionActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.WalletIncomeAndExpensesListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.WalletListActivity;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.EventCity;
import com.tbea.tb.tbeawaterelectrician.util.EventFlag;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

/**
 * Created by abc on 16/12/18.
 */

public class MyFragment extends Fragment {
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (View)inflater.inflate(R.layout.fragment_my,null);
        getDate(mView);
        listener(mView);
        EventBus.getDefault().register(this);
        return  mView;
    }

    public void getDate(final View view){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String, String> personInfo = (Map<String, String>) data.get("personinfo");
                            Map<String, String> serviceInfo = (Map<String, String>) data.get("serviceinfo");
                            ((TextView)view.findViewById(R.id.user_name)).setText(personInfo.get("name"));
                            ((TextView)view.findViewById(R.id.user_mobile)).setText(personInfo.get("mobile"));
                            String url = MyApplication.instance.getImgPath()+personInfo.get("picture");
                            ImageView imageView = (ImageView)view.findViewById(R.id.user_picture);
                            ImageLoader.getInstance().displayImage(url,imageView);
                            ((TextView)view.findViewById(R.id.user_wallet_size)).setText(serviceInfo.get("userscore")+"");
                            ((TextView)view.findViewById(R.id.tv_message_size)).setText(serviceInfo.get("newmessagenumber")+"条未读消息");
                            ((TextView)view.findViewById(R.id.tv_collect_size)).setText(serviceInfo.get("savedatanumber"));
                            ((TextView)view.findViewById(R.id.tv_accusation_size)).setText(serviceInfo.get("appealnumber"));

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
                    RspInfo1 re = userAction.getUserInfo();
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private void listener(View view){
        view.findViewById(R.id.my_orader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrderListActivity.class));
            }
        });

        view.findViewById(R.id.image_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SetionActivity.class));
            }
        });

        view.findViewById(R.id.fragment_my_edit_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyInformationActivity.class));
            }
        });

        view.findViewById(R.id.my_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WalletListActivity.class);
//                Intent intent = new Intent(getActivity(), WalletWithdrawCashViewActivity.class);
                String size = ((TextView)view.findViewById(R.id.user_wallet_size)).getText()+"";
                intent.putExtra("size",size);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.my_accusation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyAccusationListActivity.class);
//                Intent intent = new Intent(getActivity(), MyAccusationEditActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.my_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CollectListActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.my_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MessageListActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.my_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ServiceCenterActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.my_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(EventCity event) {
        if (event == null) return;
        if(EventFlag.EVENT_MY_HEAD.equals(event.getEventFlag())){
            getDate(mView);
        }
    }
}
