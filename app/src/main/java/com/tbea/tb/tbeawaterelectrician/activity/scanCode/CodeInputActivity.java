package com.tbea.tb.tbeawaterelectrician.activity.scanCode;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

/**
 * Created by cy on 2017/1/16.条形码输入界面
 */

public class CodeInputActivity extends TopActivity {
    private String mScanCodeType = "fanli";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_input);
        initTopbar("手工输入");
        mScanCodeType = getIntent().getStringExtra("scanCodeType");

        findViewById(R.id.scan_code_input_comfire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = ((TextView)findViewById(R.id.scan_code_input_text)).getText()+"";
                if("".equals(result)){
                    UtilAssistants.showToast("请输入扫描码！");
                    return;
                }

                provingScanCode(result);
            }
        });
    }

    public  void provingScanCode(final String result){
        final CustomDialog dialog = new CustomDialog(CodeInputActivity.this,R.style.MyDialog,R.layout.tip_wait_dialog);
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
                            if(mScanCodeType.equals("suyuan")){
                                intent.setClass(CodeInputActivity.this,SuYuanViewActivity.class);
                            }else {
                                intent.setClass(CodeInputActivity.this,ScanCodeViewActivity.class);
                                intent.putExtra("type","net");
                            }
                            intent.putExtra("scanCode",result);
                            startActivity(intent);

                        } else {
                            UtilAssistants.showToast(re.getMsg());
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
                    RspInfo1 re = userAction.provingScanCode(result,mScanCodeType);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
