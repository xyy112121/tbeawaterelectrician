package com.tbea.tb.tbeawaterelectrician.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.RegisterActivity;

/**
 * Created by abc on 16/12/15.手机号注册
 */

public class RegisterPhoneFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_register1,null);
        listener(view);
        return view;
    }

    public void listener(View view){
        view.findViewById(R.id.register_next_bth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity activity = ((RegisterActivity)getActivity());
                if(activity.mRealNameVerifyFragment == null){
                    activity.mRealNameVerifyFragment = new RealNameVerifyFragment();
                }
                ((RegisterActivity)getActivity()).switchFragment(RegisterPhoneFragment.this,activity.mRealNameVerifyFragment,"");
            }
        });

    }
}
