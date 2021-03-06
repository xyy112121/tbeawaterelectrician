package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
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
import com.tbea.tb.tbeawaterelectrician.entity.Appeal;
import com.tbea.tb.tbeawaterelectrician.entity.Condition;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;

/**
 * 我的举报填写页
 */

public class MyAccusationEditActivity extends TopActivity {
    private List<Condition> mTypeList;
    //    private Uri mUri;
//    private static final int RESULT_CAMERA = 0x000001;//相机
//    private static final int RESULT_PHOTO = 0x000002;//图片
    private List<String> appealImages = new ArrayList<>();
    private Appeal mObj = new Appeal();
    private OptionPicker mPicker;
    List<LocalMedia> mSelectList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accusation_edit);
        initTopbar("我要举报");
        initUI();
        listener();
        getType();
    }

    private void initUI() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中...");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            Map<String, Object> data = (Map<String, Object>) re.getData();
                            Map<String, String> appealinfo = (Map<String, String>) data.get("appealinfo");
                            if (appealinfo != null) {

                                ((TextView) findViewById(R.id.accusation_edit_city_name)).setText(MyApplication.instance.getAddrsss());
                                ((TextView) findViewById(R.id.accusation_edit_city_name2)).setText(appealinfo.get("provincename") + appealinfo.get("cityname"));
                                ((TextView) findViewById(R.id.accusation_edit_city_distributor)).setText(appealinfo.get("distributor"));
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date curDate = new Date(System.currentTimeMillis());
                                //获取当前时间
                                String str = formatter.format(curDate);
                                ((TextView) findViewById(R.id.accusation_edit_time)).setText(str);
                                ((TextView) findViewById(R.id.accusation_edit_name)).setText(getIntent().getStringExtra("name"));
                                mObj.setProvinceid(appealinfo.get("provinceid"));
                                mObj.setCityid(appealinfo.get("cityid"));
                                mObj.setDistributorid(appealinfo.get("distributorid"));
                                mObj.setCommodityid(getIntent().getStringExtra("commodityid"));
                            }
                        } else {
                            ToastUtil.showMessage(re.getMsg(), mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo1 re = userAction.getMyAccusationInfo();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取举报类型
     */
    public void getType() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("加载中");
        dialog.show();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            mTypeList = (List<Condition>) re.getDateObj("appealcategorylist");
                            if (mTypeList != null) {
                                ((TextView) findViewById(R.id.accusation_edit_type)).setText(mTypeList.get(0).getName());
                                String[] dates = new String[mTypeList.size()];
                                for (int i = 0; i < mTypeList.size(); i++) {
                                    dates[i] = mTypeList.get(i).getName();
                                }

                                mPicker = new OptionPicker((Activity) mContext, dates);
                                mPicker.setOffset(1);
                                mPicker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                                mPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                                    @Override
                                    public void onOptionPicked(String option) {
                                        ((TextView) findViewById(R.id.accusation_edit_type)).setText(option);
                                    }
                                });
                                mPicker.setAnimationStyle(R.style.PopWindowAnimationFade);
//                                picker.show();
                            }

                        } else {
                            ToastUtil.showMessage("操作失败！", mContext);
                        }

                        break;
                    case ThreadState.ERROR:
                        ToastUtil.showMessage("操作失败！", mContext);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserAction userAction = new UserAction();
                    RspInfo re = userAction.getAccusationType();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 事件
     */
    private void listener() {
        findViewById(R.id.accusation_success_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        findViewById(R.id.accusation_edit_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPicker.show();
//                getType();
            }
        });

        findViewById(R.id.accusation_edit_image_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view);
            }
        });
    }

    /**
     * 提交
     */
    private void submit() {
        try {
            final String appealcontent = ((EditText) findViewById(R.id.accusation_edit_appealcontent)).getText() + "";
            final String type = ((TextView) findViewById(R.id.accusation_edit_type)).getText() + "";
            if (appealcontent.equals("")) {
                ToastUtil.showMessage("请填写举报内容", mContext);
                return;
            }
            if (type.equals("")) {
                ToastUtil.showMessage("请填写举报类型", mContext);
                return;
            }

            final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
            dialog.setText("加载中");
            dialog.show();
            @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    dialog.dismiss();
                    switch (msg.what) {
                        case ThreadState.SUCCESS:
                            RspInfo1 re = (RspInfo1) msg.obj;
                            if (re.isSuccess()) {
                                startActivity(new Intent(mContext, MyAccusationSuccessActivity.class));
                                finish();
                            } else {
                                ToastUtil.showMessage(re.getMsg(), mContext);
                            }
                            break;
                        case ThreadState.ERROR:
                            ToastUtil.showMessage("操作失败，请重试！", mContext);
                            break;
                    }
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (Condition item : mTypeList) {
                            if (item.getName().equals(type)) {
                                mObj.setAppealcategoryid(item.getId());
                            }
                        }
                        mObj.setAppealcontent(appealcontent);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());
                        //获取当前时间
                        mObj.setAppealtime(formatter.format(curDate));
                        mObj.setScanaddress(MyApplication.instance.getAddrsss());
                        UserAction action = new UserAction();
                        String scanCode = getIntent().getStringExtra("scancode");
                        RspInfo1 re = action.submitAppeal(mObj, appealImages, scanCode);
                        handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                    } catch (Exception e) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if ("camera".equals(mType)) {//相机
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
                    showImage(mSelectList.get(0).getCompressPath());//显示图片
                    break;

            }
        }
    }

    public void showImage(String filePath) {
        try {
            appealImages.add(filePath);
            Bitmap bitmap = UtilAssistants.getBitmapFromPath(filePath, new Point(1024, 1024));
            final RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_accusation_edit_image, null);
            final ImageView imageView = (ImageView) layout.findViewById(R.id.image_view);
            imageView.setTag(filePath);
            imageView.setImageBitmap(bitmap);
            ImageView delectView = (ImageView) layout.findViewById(R.id.delect);
            final LinearLayout parentLayout = (LinearLayout) findViewById(R.id.accusation_edit_image_layout);
            delectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parentLayout.removeView(layout);
                    appealImages.remove(imageView.getTag());
                }
            });

            parentLayout.addView(layout);
        } catch (Exception e) {
            ToastUtil.showMessage("操作失败，请重试！", mContext);
        }
    }


}
