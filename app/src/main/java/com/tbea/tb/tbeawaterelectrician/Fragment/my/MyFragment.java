package com.tbea.tb.tbeawaterelectrician.fragment.my;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.MyInformationActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.OrderListActivity;
import com.tbea.tb.tbeawaterelectrician.activity.my.SetionActivity;

/**
 * Created by abc on 16/12/18.
 */

public class MyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTopGone();
        View view = (View)inflater.inflate(R.layout.fragment_my,null);
        listener(view);
        return  view;
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        ((MainActivity)getActivity()).setTopGone();
        super.onHiddenChanged(hidden);
    }
}
