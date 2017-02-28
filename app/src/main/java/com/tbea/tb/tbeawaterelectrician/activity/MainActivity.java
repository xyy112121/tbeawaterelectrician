package com.tbea.tb.tbeawaterelectrician.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.account.RegisterActivity;
import com.tbea.tb.tbeawaterelectrician.activity.scanCode.LogisticsTailAfterActivity;
import com.tbea.tb.tbeawaterelectrician.activity.scanCode.ScanCodeActivity;
import com.tbea.tb.tbeawaterelectrician.component.MainNavigateTabBar;
import com.tbea.tb.tbeawaterelectrician.fragment.HomeFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.my.MyFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.nearby.NearbyFragment;
import com.tbea.tb.tbeawaterelectrician.fragment.thenLive.TakeFragment;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;


public class MainActivity extends TopActivity {
    private MainNavigateTabBar mNavigateTabBar;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private String[] mPermissions = new String[]{};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= 23){
            mPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CALL_PHONE};
            PermissionGen.needPermission(MainActivity.this,100,mPermissions);
        }
        mNavigateTabBar = (MainNavigateTabBar) findViewById(R.id.mainTabBar);

        mNavigateTabBar.onRestoreInstanceState(savedInstanceState);

        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_home, R.drawable.icon_home_select, "首页"));
        mNavigateTabBar.addTab(NearbyFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_nearby, R.drawable.icon_nearby_select, "附近"));
        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(0, 0, ""));
        mNavigateTabBar.addTab(TakeFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_then_live, R.drawable.icon_then_live_select, "接活"));
        mNavigateTabBar.addTab(MyFragment.class, new MainNavigateTabBar.TabParam(R.drawable.icon_my, R.drawable.icon_my_select, "我"));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        listener();
        MyApplication.instance.addActivity(MainActivity.this);
    }

    private void listener(){
        findViewById(R.id.tab_post_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScanCodeActivity.class));
            }
        });
    }


    @PermissionFail(requestCode = 100)
    private void doFailSomething() {
        for (int i = 0; i < mPermissions.length; i++) {
            boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[i]);
            if(isTip){
                Toast.makeText(MainActivity.this,"你需要允许访问权限，才可正常使用该功能！",Toast.LENGTH_SHORT).show();
                finish();
                break;
            }else {
                showMissingPermissionDialog();
                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigateTabBar.onSaveInstanceState(outState);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
    }


}
