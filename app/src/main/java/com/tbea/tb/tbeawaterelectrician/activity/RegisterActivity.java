package com.tbea.tb.tbeawaterelectrician.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.Fragment.RealNameVerifyFragment;
import com.tbea.tb.tbeawaterelectrician.Fragment.RegisterPhoneFragment;
import com.tbea.tb.tbeawaterelectrician.R;

/**
 * Created by abc on 16/12/15.注册页面
 */

public class RegisterActivity extends FragmentActivity implements View.OnClickListener{
   private Fragment mRegisterPhoneFragement;//手机号注册
    private Fragment mRealNameVerifyFragment;//实名注册
    private Fragment mCurrentFragement;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegisterPhoneFragement = new RegisterPhoneFragment();
        initView();
    }

    public  void initView(){
        TextView phoneView = (TextView)findViewById(R.id.register_phone_tv);
        TextView realNameView = (TextView)findViewById(R.id.register_real_name_verify_tv);
        Typeface iconfont = Typeface.createFromAsset(getAssets(),
                "iconfont/iconfont.ttf");
        phoneView.setTypeface(iconfont);
        realNameView.setTypeface(iconfont);
        phoneView.setOnClickListener(this);
        realNameView.setOnClickListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.register_fragment_layout, mRegisterPhoneFragement);
        transaction.addToBackStack(RegisterPhoneFragment.class.getName());
        transaction.commit();
        mCurrentFragement = (Fragment) mRegisterPhoneFragement;

    }

    @Override
    public void onClick(View view) {
        TextView phoneView = (TextView)findViewById(R.id.register_phone_tv);
        TextView realNameView = (TextView)findViewById(R.id.register_real_name_verify_tv);

        switch (view.getId()){
            case R.id.register_phone_tv:
                phoneView.setTextColor(getResources().getColor(R.color.head_color));
                realNameView.setTextColor(getResources().getColor(R.color.gray));
                if(mRegisterPhoneFragement == null){
                    mRegisterPhoneFragement = new RegisterPhoneFragment();
                }
                switchFragment(mCurrentFragement,mRegisterPhoneFragement,"");
                mCurrentFragement = mRegisterPhoneFragement;
                break;
            case R.id.register_real_name_verify_tv:
                phoneView.setTextColor(getResources().getColor(R.color.head_color));
                realNameView.setTextColor(getResources().getColor(R.color.head_color));
                if(mRealNameVerifyFragment == null){
                    mRealNameVerifyFragment = new RealNameVerifyFragment();
                }
                switchFragment(mCurrentFragement,mRealNameVerifyFragment,"");
                mCurrentFragement = mRealNameVerifyFragment;
                break;
        }
    }


    /**
     * 切换Fragment
     * @param from
     * @param to
     * @param toTag
     */
    public void switchFragment(Fragment from, Fragment to, String toTag) {
        if(from!=to){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(!to.isAdded()){
                transaction.hide(from).add(R.id.register_fragment_layout, to,toTag);
            }else{
                transaction.hide(from).show(to);
            }
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
