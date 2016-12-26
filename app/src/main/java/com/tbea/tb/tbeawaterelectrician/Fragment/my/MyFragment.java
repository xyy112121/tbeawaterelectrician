package com.tbea.tb.tbeawaterelectrician.Fragment.my;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;
import com.tbea.tb.tbeawaterelectrician.activity.order.OrderListActivity;

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

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        ((MainActivity)getActivity()).setTopGone();
        super.onHiddenChanged(hidden);
    }
}
