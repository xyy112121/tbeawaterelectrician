package com.tbea.tb.tbeawaterelectrician.util.permissonutil;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends PermissionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init(){
        //单个权限获取
        checkPermission(new CheckPermListener() {
            @Override
            public void Granted() {
                Toast.makeText(MainActivity.this, "通过", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void Denied() {
                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                //检查是否选择了不再提醒
            }
        }, "请求获取照相机权限", Manifest.permission.CAMERA);

        //多个权限获取
        checkPermission(new CheckPermListener() {
            @Override
            public void Granted() {
                Toast.makeText(MainActivity.this, "通过", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void Denied() {
                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                //如果选择不在提示
            }
        }, "请求获取照相机和读取文件权限", Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

}
