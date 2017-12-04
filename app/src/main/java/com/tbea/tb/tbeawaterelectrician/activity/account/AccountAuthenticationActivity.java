package com.tbea.tb.tbeawaterelectrician.activity.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.component.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.entity.Register;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 认证
 */

public class AccountAuthenticationActivity extends TopActivity {
    private String personidcard1Path = "";//身份证正面
    private String personidcard2Path = "";//身份证反面
    private String personidcardwithpersonPath = "";//手持身份证图片
    private int mFlag;//判断当前选择的图片是什么
    private String mIdentify;
    private Button mFinishView;
    List<LocalMedia> mSelectList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_authentication);
        initTopbar("实名认证");
        TextView lookImageView = (TextView) findViewById(R.id.look_give_typical_examples_image);
        Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(),
                "iconfont/iconfont.ttf");
        lookImageView.setTypeface(iconfont);
        mIdentify = ShareConfig.getConfigString(mContext, Constants.WHETHERIDENTIFIEDID, "notidentify");
        listener();
    }

    private void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, String> useridentifyinfo = (Map<String, String>) re.getDateObj("useridentifyinfo");
                            ((EditText) findViewById(R.id.regist_realname)).setText(useridentifyinfo.get("realname"));
                            ((EditText) findViewById(R.id.regist_personid)).setText(useridentifyinfo.get("personcardid"));
                            ImageView imageView1 = (ImageView) findViewById(R.id.register_update_image1);
                            ImageView imageView2 = (ImageView) findViewById(R.id.register_update_image2);
                            ImageView imageView3 = (ImageView) findViewById(R.id.register_update_image3);
                            if (TextUtils.isEmpty(useridentifyinfo.get("personidcard1")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + useridentifyinfo.get("personidcard1"), imageView1);
                            if (TextUtils.isEmpty(useridentifyinfo.get("personidcard2")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + useridentifyinfo.get("personidcard2"), imageView2);
                            if (TextUtils.isEmpty(useridentifyinfo.get("personidcardwithperson")))
                                ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + useridentifyinfo.get("personidcardwithperson"), imageView3);

                        } else {
                            UtilAssistants.showToast(re.getMsg(), mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        UtilAssistants.showToast("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getAccountAuthentication();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    private void setView() {
        (findViewById(R.id.regist_realname)).setFocusable(false);
        (findViewById(R.id.regist_personid)).setFocusable(false);
        findViewById(R.id.register_update_image1).setClickable(false);
        findViewById(R.id.register_update_image2).setClickable(false);
        findViewById(R.id.register_update_image3).setClickable(false);
        mFinishView.setEnabled(false);
        mFinishView.setText("认证中");
    }

    private void setViewTrue() {
        setViewEdit(R.id.regist_realname);
        setViewEdit(R.id.regist_personid);
        findViewById(R.id.register_update_image1).setClickable(true);
        findViewById(R.id.register_update_image2).setClickable(true);
        findViewById(R.id.register_update_image3).setClickable(true);
        mFinishView.setEnabled(true);
        mFinishView.setBackgroundResource(R.drawable.btn_bg_blue);
        mFinishView.setText("提交审核");
    }

    private void setViewEdit(int id) {
        EditText editText = (EditText)findViewById(id);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        if(editText.getId() == R.id.regist_realname){
            editText.requestFocus();
        }
    }


    public void listener() {
        mFinishView = (Button) findViewById(R.id.register_commit_review);
        if ("identifying".equals(mIdentify) || "identified".equals(mIdentify)) {//正在认证和认证通过
            setView();
        }

        //认证失败和认证通过
        if ("identifyfailed".equals(mIdentify) || "identified".equals(mIdentify)) {
            mFinishView.setBackgroundResource(R.drawable.btn_bg_red);
            mFinishView.setEnabled(true);
            mFinishView.setText("重新认证");
        }

        getDate();
        findViewById(R.id.register_update_image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = FlagImage.personidcard1;
                showDialog(view);
            }
        });

        findViewById(R.id.register_update_image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = FlagImage.personidcard2;
                showDialog(view);
            }
        });

        findViewById(R.id.register_update_image3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = FlagImage.personidcardwithperson;
                showDialog(view);
            }
        });

        findViewById(R.id.look_give_typical_examples_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ExampleImageActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.register_commit_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("重新认证".equals(mFinishView.getText() + "") && "identified".equals(mIdentify)) {
                    setViewTrue();
                } else {
                    try {
                        final String realname = ((EditText) findViewById(R.id.regist_realname)).getText() + "";
                        final String personid = ((EditText) findViewById(R.id.regist_personid)).getText() + "";
                        if (realname.equals("")) {
                            UtilAssistants.showToast("请输入真实姓名", mContext);
                            return;
                        }
                        if (isIDCard(personid) == false) {
                            UtilAssistants.showToast("请输入正确的身份证号", mContext);
                            return;
                        }

                        if ("".equals(personidcard1Path)) {
                            UtilAssistants.showToast("请选择需要上传的身份证正面", mContext);
                            return;
                        }
                        if ("".equals(personidcard2Path)) {
                            UtilAssistants.showToast("请选择需要上传的身份证反面", mContext);
                            return;
                        }
                        if ("".equals(personidcardwithpersonPath)) {
                            UtilAssistants.showToast("请选择需要上传的手持身份证照片", mContext);
                            return;
                        }

                        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
                        dialog.setText("请等待...");
                        dialog.show();

                        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                dialog.dismiss();
                                switch (msg.what) {
                                    case ThreadState.SUCCESS:
                                        RspInfo1 re = (RspInfo1) msg.obj;
                                        UtilAssistants.showToast(re.getMsg(), mContext);
                                        if (re.isSuccess()) {
                                            ShareConfig.setConfig(mContext, Constants.WHETHERIDENTIFIEDID, "identifying");
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                        break;
                                    case ThreadState.ERROR:
                                        UtilAssistants.showToast("操作失败，请重试！", mContext);
                                        break;
                                }
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Register obj = new Register();
                                    obj.setRealname(realname);
                                    obj.setPersonid(personid);
                                    obj.setPersonidcard1(personidcard1Path);
                                    obj.setPersonidcard2(personidcard2Path);
                                    obj.setPersonidcardwithperson(personidcardwithpersonPath);
                                    UserAction action = new UserAction();
                                    RspInfo1 result = action.accountAuthentication(obj);
                                    handler.obtainMessage(ThreadState.SUCCESS, result).sendToTarget();
                                } catch (Exception e) {
                                    handler.sendEmptyMessage(ThreadState.ERROR);
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 验证身份证号
     */
    public boolean isIDCard(String personid) {
        String telRegex = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";
        if (personid.equals("")) return false;
        else return personid.matches(telRegex);
    }

    /**
     * 选择相册Dialog
     */
    protected void showDialog(View view) {
        final CustomPopWindow popWindow = new CustomPopWindow((Activity) mContext,
                R.id.body_bg_view, true, R.style.PopWindowAnimationFade,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        popWindow.addButtonForGroup1("拍照", 0xFFFF1E14, new ConfirmBtnClickListener(
                "camera", popWindow));
        popWindow.addButtonForGroup1("从相册选择", 0, new ConfirmBtnClickListener(
                "album", popWindow));
        popWindow.addButtonForGroup2("取 消", 0, null);
        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    class ConfirmBtnClickListener implements View.OnClickListener {
        private String mType = "camera";
        private CustomPopWindow mPopWindow;

        public ConfirmBtnClickListener(String type, CustomPopWindow popWindow) {
            this.mType = type;
            this.mPopWindow = popWindow;
        }

        @Override
        public void onClick(View v) {
            mPopWindow.dismiss();
            if ("camera".equals(mType)) {//图片
                PictureSelector.create(mContext)
                        .openCamera(PictureMimeType.ofImage())
                        .compress(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            } else if ("album".equals(mType)) {//相册选择图片
                openImage();
            }
        }
    }

    private void openImage() {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(mContext)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .theme(R.style.picture_default_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .compress(true)
                .isCamera(false)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .selectionMedia(mSelectList)// 是否传入已选图片 List<LocalMedia> list
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    mSelectList = PictureSelector.obtainMultipleResult(data);
//                    ImageLoader.getInstance().displayImage("file://" + mSelectList.get(0).getCompressPath(), mHeaderView);
                    showImage(mSelectList.get(0).getCompressPath());
                    break;

            }
        }
    }

    public void showImage(String filePath) {
        try {
            Bitmap bitmap = UtilAssistants.getBitmapFromPath(filePath, new Point(1024, 1024));
            ImageView imageView = null;
            if (mFlag == FlagImage.personidcard1) {
                personidcard1Path = filePath;
                imageView = (ImageView) findViewById(R.id.register_update_image1);
            }
            if (mFlag == FlagImage.personidcard2) {
                personidcard2Path = filePath;
                imageView = (ImageView) findViewById(R.id.register_update_image2);
            }
            if (mFlag == FlagImage.personidcardwithperson) {
                personidcardwithpersonPath = filePath;
                imageView = (ImageView) findViewById(R.id.register_update_image3);
            }

//            int width = imageView.getWidth();
//            if(mFlag == FlagImage.personidcard1){
//                width = width-10;
//            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageView.getWidth(), imageView.getHeight());
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(bitmap);
            mFlag = 0;
        } catch (Exception e) {
            UtilAssistants.showToast("操作失败!", mContext);
        }
    }

    private class FlagImage {
        public static final int personidcard1 = 1000;//身份证正面
        public static final int personidcard2 = 1001;//身份证反面
        public static final int personidcardwithperson = 1002;//手持身份证图片
    }
}
