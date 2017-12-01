package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.model.JsToJavaPictureTbeaModuel;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.model.TbeaPictrueResponseModel;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;


import java.io.Serializable;

/**
 * Created by DELL on 2017/11/24.
 */

public class JsToJava {
    Context mContext;

    public JsToJava(Context c) {
        mContext = c;
    }

    /**
     * 与js交互时用到的方法，在js里直接调用的(特变电工-图片参数)
     */
    @JavascriptInterface
    public void showlargepicture(String value) {
        if (value != null || !value.isEmpty()) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            final JsToJavaPictureTbeaModuel moduel = gson.fromJson(value, JsToJavaPictureTbeaModuel.class);
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case ThreadState.SUCCESS:
                            TbeaPictrueResponseModel model = (TbeaPictrueResponseModel) msg.obj;
                            if (model.isSuccess()) {
                                if (model.data != null) {
                                    Intent intent = new Intent(mContext, PictureShowActivity.class);
                                    intent.putExtra("images", (Serializable) model.data.picturelist);
                                    intent.putExtra("index", moduel.sequence);
                                    mContext.startActivity(intent);
                                }

                            } else {
                                ToastUtil.showMessage(model.getMsg(),mContext);
                            }
                            break;
                        case ThreadState.ERROR:
                            ToastUtil.showMessage("操作失败！",mContext);
                            break;
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserAction action = new UserAction();
                    try {
                        TbeaPictrueResponseModel model = action.getCommodityPicture(moduel.id);
                        handler.obtainMessage(ThreadState.SUCCESS, model).sendToTarget();
                    } catch (Exception e) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    }
                }
            }).start();
        }
    }
}
