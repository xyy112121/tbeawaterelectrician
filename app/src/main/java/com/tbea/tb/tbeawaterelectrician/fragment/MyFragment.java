package com.tbea.tb.tbeawaterelectrician.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MainActivity;

/**
 * Created by abc on 16/12/18.
 */

public class MyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTopGone();
        View view = (View)inflater.inflate(R.layout.fragment_my,null);
        return  view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        ((MainActivity)getActivity()).setTopGone();
        super.onHiddenChanged(hidden);
    }
}
