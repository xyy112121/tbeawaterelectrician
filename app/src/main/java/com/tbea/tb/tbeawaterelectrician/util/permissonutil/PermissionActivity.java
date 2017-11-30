package com.tbea.tb.tbeawaterelectrician.util.permissonutil;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.tbea.tb.tbeawaterelectrician.R;

import java.util.List;

/**
 * Created by jinkai on 2016/10/13.
 */

public class PermissionActivity extends AppCompatActivity implements PermissionUtil.PermissionCallbacks {
    public static final int RC_PERM = 123;
    /**
     * 权限回调接口
     */
    private CheckPermListener mListener;

    public interface CheckPermListener {
        //权限通过后的回调方法
        void Granted();//通过

        void Denied();//拒绝
    }

    public void checkPermission(CheckPermListener listener, String resString, String... mPerms) {
        mListener = listener;
        if (PermissionUtil.hasPermissions(this, mPerms)) {
            if (mListener != null)
                mListener.Granted();
        } else {
            PermissionUtil.requestPermissions(this,
                    resString,
                    RC_PERM, mPerms);
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //同意了部分请求
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    //全部拒绝
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (mListener != null) {
            mListener.Denied();
        }

        PermissionUtil.checkDeniedPermissionsNeverAskAgain(this,
                "请在设置-应用-权限打开相关权限",
                R.string.settings, R.string.camera_cancel, null, perms);
    }

    //全部同意
    @Override
    public void onPermissionsAllGranted() {
        if (mListener != null) {
            mListener.Granted();
        }
    }
}
