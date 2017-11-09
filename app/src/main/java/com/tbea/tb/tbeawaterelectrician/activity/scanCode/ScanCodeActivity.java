package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.lib_zxing.activity.CaptureFragment;
import com.tbea.tb.tbeawaterelectrician.lib_zxing.activity.CodeUtils;
import com.tbea.tb.tbeawaterelectrician.lib_zxing.decoding.CaptureActivityHandler;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;


/**
 * Created by cy on 2017/1/16.扫码界面
 */

public class ScanCodeActivity extends TopActivity {
    private boolean mFlag = false;//控制是否打开闪关灯
    //    private String mScanCodeType = "fanli";
    private final int REQUEST_IMAGE = 100;
    CaptureFragment captureFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_layout);
        /**
         /* 执行扫面Fragment的初始化操作
         */
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.activity_scancode_my_camera);

        captureFragment.setAnalyzeCallback(analyzeCallback);
        /**
         * 替换我们的扫描控件
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
        listener();
    }


    private void listener() {
//        findViewById(R.id.input_code_iv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ScanCodeActivity.this,CodeInputActivity.class);
//                intent.putExtra("scanCodeType",mScanCodeType);
//                startActivity(intent);
//
//            }
//        });

//        ((CheckBox)findViewById(R.id.scan_code_type)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b){
//                    mScanCodeType = "fanli";
//                }else {
//                    mScanCodeType = "suyuan";
//                }
//            }
//        });

        findViewById(R.id.scan_code_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanCodeActivity.this, ScanCodeHistoryActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.scan_code_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localIntent2 = new Intent();
                localIntent2.addCategory(Intent.CATEGORY_OPENABLE);
                localIntent2.setType("image/*");
                localIntent2.putExtra("return-data", true);
                localIntent2
                        .setAction("android.intent.action.GET_CONTENT");
                startActivityForResult(localIntent2, REQUEST_IMAGE);
            }
        });

        findViewById(R.id.scan_code_flashlight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFlag == false) {
                    /**
                     * 打开闪光灯
                     */
                    CodeUtils.isLightEnable(true);
                    mFlag = true;
                } else {
                    CodeUtils.isLightEnable(false);
                    mFlag = false;
                }
            }
        });

        findViewById(R.id.scan_code_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
//                ContentResolver cr = getContentResolver();
                try {
//                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片

                    CodeUtils.analyzeBitmap(UtilAssistants.getPath(ScanCodeActivity.this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            provingScanCode(result);//二维码有效性检验
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(ScanCodeActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });

//                    if (mBitmap != null) {
//                        mBitmap.recycle();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, final String result) {
            provingScanCode(result);//二维码有效性检验
        }

        @Override
        public void onAnalyzeFailed() {
            Toast.makeText(ScanCodeActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
        }
    };

    public void provingScanCode(final String result) {
        final CustomDialog dialog = new CustomDialog(ScanCodeActivity.this, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待...");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Intent intent = new Intent();
//                            if (mScanCodeType.equals("suyuan")) {
//                                intent.setClass(ScanCodeActivity.this, SuYuanViewActivity.class);
//                            } else {
//                                intent.setClass(ScanCodeActivity.this, ScanCodeViewActivity.class);
//                                intent.putExtra("type", "net");
//                            }
//                            intent.putExtra("scanCode", result);
//                            startActivity(intent);

                        } else {
                            final CustomDialog dialog1 = new CustomDialog(ScanCodeActivity.this, R.style.MyDialog, R.layout.tip_delete_dialog);
                            dialog1.setText(re.getMsg());
                            dialog1.setConfirmBtnIsCloseWindow(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                    continuePreview();
                                }
                            });
                            dialog1.show();
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
                    RspInfo1 re = userAction.getFanLi(result, MyApplication.instance.getAddrsss());
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }


    public void continuePreview() {
        if (captureFragment.getHandler() != null) {
            ((CaptureActivityHandler) captureFragment.getHandler()).restartPreviewAndDecode();
        }
    }
}
