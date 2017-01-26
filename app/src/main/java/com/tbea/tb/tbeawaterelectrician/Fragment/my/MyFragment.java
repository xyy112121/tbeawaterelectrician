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

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MyInformationActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.OrderListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.SetionActivity;
import com.tbea.tb.tbeawaterelectrician.entity.NearbyCompany;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by abc on 16/12/18.
 */

public class MyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_my,null);
//        getDate();
        listener(view);
        return  view;
    }

    public void getDate(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            List<Map<String,String>> list =  (List<Map<String,String>>) re.getDateObj("companylist");
                            List<NearbyCompany> companyList = new ArrayList<>();
                            if(list != null){
                                for (int i = 0;i< list.size();i++){
                                    NearbyCompany obj = new NearbyCompany();
                                    obj.setId(list.get(i).get("id"));
                                    obj.setPicture(list.get(i).get("picture"));
                                    obj.setName(list.get(i).get("name"));
                                    obj.setDistance(list.get(i).get("distance"));
                                    obj.setLatitude(list.get(i).get("latitude"));
                                    obj.setAddress(list.get(i).get("address"));
                                    obj.setCompanytypeid(list.get(i).get("companytypeid"));
                                    obj.setWithcompanyidentified(list.get(i).get("withcompanyidentified"));
                                    obj.setWithcompanylisence(list.get(i).get("withcompanylisence"));
                                    obj.setWithguaranteemoney(list.get(i).get("withguaranteemoney"));
                                    obj.setWithguaranteemoney(list.get(i).get("withidentified"));
                                    companyList.add(obj);
                                }
                            }else {
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
                    String  re = userAction.getUserInfo();
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
    }
}
