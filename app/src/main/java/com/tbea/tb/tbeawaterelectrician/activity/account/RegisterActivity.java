package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.entity.Register;
import com.tbea.tb.tbeawaterelectrician.fragment.account.RealNameVerifyFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.account.RegisterPhoneFragment;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;

/**
 * Created by abc on 16/12/15.注册页面
 */

public class RegisterActivity extends  TopActivity implements View.OnClickListener{
    private Fragment mRegisterPhoneFragement;//手机号注册
    public Fragment mRealNameVerifyFragment;//实名注册
    private Fragment mCurrentFragement;
    public Register mObj;//注册实体

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mObj = new Register();
        mRegisterPhoneFragement = new RegisterPhoneFragment();
        initTopbar("注册");
        initView();
    }

    public  void initView(){
        TextView phoneView = (TextView)findViewById(R.id.register_phone_tv);
        TextView realNameView = (TextView)findViewById(R.id.register_real_name_verify_tv);

        Typeface iconfont = Typeface.createFromAsset(getAssets(),
                "iconfont/iconfont.ttf");
        phoneView.setTypeface(iconfont);
        realNameView.setTypeface(iconfont);
        ((TextView)findViewById(R.id.register_shili_image_cancel)).setTypeface(iconfont);
        phoneView.setOnClickListener(this);
        realNameView.setOnClickListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.register_fragment_layout, mRegisterPhoneFragement);
        transaction.addToBackStack(RegisterPhoneFragment.class.getName());
        transaction.commit();
        mCurrentFragement = (Fragment) mRegisterPhoneFragement;

        findViewById(R.id.register_shili_image_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.body_bg_view2).setVisibility(View.GONE);
                findViewById(R.id.regist_shili_image_2).setVisibility(View.GONE);
                findViewById(R.id.register_shili_image_cancel).setVisibility(View.GONE);
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() > 1){
           getSupportFragmentManager().popBackStack();
        }else {
            finish();
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

    //显示示例图片
    public void showGiveTypicalExamplesImage(){
        findViewById(R.id.body_bg_view2).setVisibility(View.VISIBLE);
        findViewById(R.id.regist_shili_image_2).setVisibility(View.VISIBLE);
        findViewById(R.id.register_shili_image_cancel).setVisibility(View.VISIBLE);
    }
}
