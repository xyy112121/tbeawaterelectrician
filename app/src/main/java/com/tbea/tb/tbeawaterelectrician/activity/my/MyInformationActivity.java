package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.app.Activity;
import android.app.usage.UsageEvents;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.component.CircleImageView;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.component.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.EventCity;
import com.tbea.tb.tbeawaterelectrician.util.EventFlag;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Map;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by abc on 16/12/28.个人信息
 */

public class MyInformationActivity extends TopActivity {
    private Context mContext;
    private final int RESULT_EMAIL = 1000;
    private static final int RESULT_CAMERA = 0x000001;//相机
    private static final int RESULT_PHOTO = 0x000002;//图片
    private Uri mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_information);
        mContext = this;
        initTopbar("个人信息");
        listener();
        getDate();
    }

    public  void listener(){
        /**
         * 性别
         */
        findViewById(R.id.info_sex_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionPicker picker = new OptionPicker((Activity)mContext, new String[]{
                        "先生", "女士"
                });
                picker.setOffset(1);
                picker.setSelectedIndex(1);
                picker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(String option) {
                        String sex = "female";
                        if("先生".equals(option)){
                            sex = "male";
                        }
                        ((TextView)findViewById(R.id.info_sex)).setText(option);
                        updateInfo(sex,"","");
                    }
                });
                picker.setAnimationStyle(R.style.PopWindowAnimationFade);
                picker.show();
            }
        });

        //出生日期
        findViewById(R.id.info_birthday_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker picker = new DatePicker(MyInformationActivity.this, DatePicker.MONTH_DAY);
                picker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                picker.setLabel("","","");
//                picker.setLineColor(ContextCompat.getColor(mContext, R.color.white));
                picker.setOnDatePickListener(new DatePicker.OnMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String month, String day) {
                        ((TextView)findViewById(R.id.info_birthday)).setText(month+"月"+day+"日");
                        updateInfo("",day,month);
                    }
                });
                picker.setAnimationStyle(R.style.PopWindowAnimationFade);
                picker.show();
            }
        });

        findViewById(R.id.info_email_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((TextView)findViewById(R.id.info_email)).getText()+"";
                Intent intent = new Intent(mContext,EmailEditActivity.class);
                intent.putExtra("code",email);
                startActivityForResult(intent,RESULT_EMAIL);
            }
        });

        findViewById(R.id.info_addr_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,AddressEditListActivity.class);
                intent.putExtra("flag","");
                startActivity(intent);
            }
        });

        findViewById(R.id.info_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view);
            }
        });

    }

    /**
     * 获取信息
     */
    public  void getDate(){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo)msg.obj;
                        if(re.isSuccess()){
                            UserInfo obj = (UserInfo) re.getDateObj("personinfo");
                            CircleImageView imageView = (CircleImageView)findViewById(R.id.info_head);
                            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath()+obj.getPicture(),imageView);
                            ((TextView)findViewById(R.id.info_birthday)).setText(obj.getBirthday());
                            ((TextView)findViewById(R.id.info_email)).setText(obj.getMailaddr());
                            ((TextView)findViewById(R.id.info_sex)).setText(obj.getSex());
                        }else {
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
                    RspInfo re = userAction.getUser();
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 更改信息
     * @param sex 性别 先生传male  女士传Female
     * @param birthday 生日 日
     * @param birthmonth 生日 月
     */
    public  void updateInfo(final String sex, final String birthday, final String birthmonth){
        final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1)msg.obj;
                        if(re.isSuccess()){
                            UtilAssistants.showToast("操作成功！");
                        }else {
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
                    RspInfo1 re = userAction.updateInfo(sex,"",birthday,birthmonth);
                    handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
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
                Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/Images");
                if (!file.exists()) {
                    file.mkdirs();
                }
                mUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory() + "/Images/",
                        "cameraImg"
                                + String.valueOf(System.currentTimeMillis())
                                + ".jpg"));
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        mUri);
                cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent, RESULT_CAMERA);
            } else if("album".equals(mType)){//相册选择图片
                Intent localIntent2 = new Intent();
                localIntent2.setType("image/*");
                localIntent2.putExtra("return-data", true);
                localIntent2
                        .setAction("android.intent.action.GET_CONTENT");
                startActivityForResult(localIntent2,RESULT_PHOTO);
            }
        }
    }

    public  void updateHead(final String filePath){
        try {
            final  Bitmap bitmap = UtilAssistants.getBitmapFromPath(filePath,new Point(1024,1024));
            final CustomDialog dialog = new CustomDialog(mContext,R.style.MyDialog,R.layout.tip_wait_dialog);
            dialog.setText("请等待");
            dialog.show();
            final Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    dialog.dismiss();
                    switch (msg.what){
                        case ThreadState.SUCCESS:
                            RspInfo1 re = (RspInfo1)msg.obj;
                            if(re.isSuccess()){
                                UtilAssistants.showToast("操作成功！");
                                final ImageView imageView = (ImageView)findViewById(R.id.info_head);
                                imageView.setImageBitmap(bitmap);
                                EventBus.getDefault().post(new EventCity(EventFlag.EVENT_MY_HEAD));
                            }else {
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
                        RspInfo1 re = userAction.updateHead(filePath);
                        handler.obtainMessage(ThreadState.SUCCESS,re).sendToTarget();
                    } catch (Exception e) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    }
                }
            }).start();
        } catch (Exception e) {
            UtilAssistants.showToast("操作失败!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(resultCode == RESULT_OK){
           switch (requestCode){
               case RESULT_EMAIL:
                   String  email = data.getStringExtra("code");
                   ((TextView)findViewById(R.id.info_email)).setText(email);
                   break;
               case RESULT_CAMERA:
                   String filePath = mUri.getPath();
                   updateHead(filePath);//显示图片
                   break;
               case RESULT_PHOTO:
                   if(data != null){
//                       filePath = data.getData().getPath();
                       filePath = UtilAssistants.getPath(mContext,data.getData());
                       updateHead(filePath);//显示图片
                   }
                   break;
           }
       }
    }
}
