package com.tbea.tb.tbeawaterelectrician.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.tbea.tb.tbeawaterelectrician.component.CircleImageView;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.component.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.EventCity;
import com.tbea.tb.tbeawaterelectrician.util.EventFlag;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.ToastUtil;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by abc on 16/12/28.个人信息
 */

public class MyInformationActivity extends TopActivity implements View.OnClickListener {
    private final int RESULT_EMAIL = 1000;
    private final int RESULT_NICKNAME = 1001;
    private final int ADDR_SELECT = 100;
    List<LocalMedia> mSelectList = new ArrayList<>();
    ImageView mHeaderView;

    private String mProvince;
    private String mCity;
    private String mLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_information);
        initTopbar("个人信息");

        mProvince = MyApplication.instance.getProvince();
        mCity = MyApplication.instance.getCity();
        mLocation = MyApplication.instance.getDistrict();
        listener();
        getDate();
    }

    public void listener() {

        mHeaderView = (ImageView) findViewById(R.id.info_head);


        /**
         * 性别
         */
        findViewById(R.id.info_sex_layout).setOnClickListener(this);

        //出生日期
        findViewById(R.id.info_birthday_layout).setOnClickListener(this);

        findViewById(R.id.info_email_layout).setOnClickListener(this);

        findViewById(R.id.info_nickName_layout).setOnClickListener(this);
        findViewById(R.id.info_address_layout).setOnClickListener(this);
        findViewById(R.id.info_servicescope_layout).setOnClickListener(this);
        findViewById(R.id.info_introduce_layout).setOnClickListener(this);

        findViewById(R.id.info_addr_layout).setOnClickListener(this);

        findViewById(R.id.info_head).setOnClickListener(this);

    }

    //根据时间获取年龄
    public String getAgeAchen(String birthdaYear, String birthdayMonth, String birthdayDay) throws Exception {
        // 创建 Calendar 对象
        Calendar birthday = Calendar.getInstance();
        // 设置传入的时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 指定一个日期
        Date date = dateFormat.parse(birthdaYear + "-" + birthdayMonth + "-" + birthdayDay);
        // 对 calendar 设置为 date 所定的日期
        birthday.setTime(date);

        // 创建 Calendar 对象
        Calendar now = Calendar.getInstance();
        // 设置传入的时间格式
        Date nowDate = dateFormat.parse(dateFormat.format(new Date()));
        now.setTime(nowDate);

        String mString = "";

        int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) + 1 - birthday.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

        // 日期为当前年/月判断
        if ((year == 0 && month == 0 && day < 0)) {
            ToastUtil.showMessage("请选择正确的出生年月", mContext);
            return mString;
        }
        // 退位计算年/月/日
        if (day < 0) {
            month -= 1;
            now.add(Calendar.MONTH, -1);//得到上一个月，用来得到上个月的天数
            day = day + now.getActualMaximum(Calendar.DAY_OF_MONTH);//得到该月天数
        }
        if (month < 0) {
            year -= 1;
            month = month + 12;
        }
        // 退位计算后超过当前日期的年龄
        if (year < 0) {
            ToastUtil.showMessage("请选择正确的出生年月", mContext);
            return mString;
        }
        // 大于一岁的
        if (year > 0) {
            if (year > 12) {
                mString = year + "岁";
            } else {
                if (month != 0 && month != 12) {
                    mString = year + "岁" + month + "个月";
                } else {
                    if (month == 12) {
                        year += 1;
                        mString = year + "岁";
                    } else {
                        mString = year + "岁";
                    }
                }
            }
        } else {// 不满一岁的
            // 不满一个月的
            if (month == 0) {
                mString = day + "天";
            } else { // 大于一个月的
                if (month == 12) {
                    year += 1;
                    mString = year + "岁";
                } else {
                    mString = month + "月" + day + "天";
                }
            }
        }
        return mString;
    }

    /**
     * 获取信息
     */
    public void getDate() {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo re = (RspInfo) msg.obj;
                        if (re.isSuccess()) {
                            UserInfo obj = (UserInfo) re.getDateObj("personinfo");
                            CircleImageView imageView = (CircleImageView) findViewById(R.id.info_head);
                            ImageLoader.getInstance().displayImage(MyApplication.instance.getImgPath() + obj.getPicture(), imageView);
                            ((TextView) findViewById(R.id.info_birthday)).setText(obj.getBirthday());
                            ((TextView) findViewById(R.id.info_email)).setText(obj.getMailaddr());
                            String campanyName = obj.getCompanyname();
                            ((TextView) findViewById(R.id.info_companyname)).setText(campanyName);
                            ((TextView) findViewById(R.id.info_sex)).setText(obj.getSex());
                            ((TextView) findViewById(R.id.info_workyear)).setText(obj.getOldyears());
                            ((TextView) findViewById(R.id.info_nickName)).setText(obj.getNickname());
                            String state = ShareConfig.getConfigString(mContext, Constants.WHETHERIDENTIFIEDID, "notidentify");
                            // 当用户没有认证通过时，显示昵称，用户可以修改昵称；当用户认证通过后，不显示昵称，显示真实姓名，且不可修改
                            if ("identified".equals(state)) {
                                ((TextView) findViewById(R.id.info_nickName)).setText(obj.getRealname());
                            }
                            ((TextView) findViewById(R.id.info_address_tv)).setText(obj.getAddress());
                            ((TextView) findViewById(R.id.info_servicescope_tv)).setText(obj.getServicescope());
                            ((TextView) findViewById(R.id.info_introduce_tv)).setText(obj.getIntroduce());

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
                    RspInfo re = userAction.getUser();
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }

    /**
     * 更改信息
     *
     * @param sex        性别 先生传male  女士传Female
     * @param birthday   生日 日
     * @param birthmonth 生日 月
     */
    public void updateInfo(final String sex, final String birthyear, final String birthday, final String birthmonth) {
        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        if (re.isSuccess()) {
                            UtilAssistants.showToast("操作成功！", mContext);
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
                    RspInfo1 re = userAction.updateInfo("", sex, "", birthyear, birthday, birthmonth);
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info_sex_layout:
                showSex();
                break;
            case R.id.info_birthday_layout:
                showDate();
                break;
            case R.id.info_email_layout:
                String email = ((TextView) findViewById(R.id.info_email)).getText() + "";
                Intent intent = new Intent(mContext, EmailEditActivity.class);
                intent.putExtra("code", email);
                startActivityForResult(intent, RESULT_EMAIL);
                break;
            case R.id.info_head:
                showDialog(view);
                break;
            case R.id.info_addr_layout:
                intent = new Intent(mContext, AddressEditListActivity.class);
                intent.putExtra("flag", "");
                startActivity(intent);
                break;
            case R.id.info_nickName_layout:
                String state = ShareConfig.getConfigString(mContext, Constants.WHETHERIDENTIFIEDID, "notidentify");
                if (!"identified".equals(state)) {
                    String nickName = ((TextView) findViewById(R.id.info_nickName)).getText() + "";
                    intent = new Intent(mContext, NickNameEditActivity.class);
                    intent.putExtra("code", nickName);
                    intent.putExtra("title", "昵称");
                    intent.putExtra("viewId", R.id.info_nickName);
                    startActivityForResult(intent, RESULT_NICKNAME);
                }
                break;
            case R.id.info_servicescope_layout:
                String nickName = ((TextView) findViewById(R.id.info_servicescope_tv)).getText() + "";
                intent = new Intent(mContext, NickNameEditActivity.class);
                intent.putExtra("code", nickName);
                intent.putExtra("title", "服务范围");
                intent.putExtra("viewId", R.id.info_servicescope_tv);
                startActivityForResult(intent, RESULT_NICKNAME);

                break;
            case R.id.info_introduce_layout:
                nickName = ((TextView) findViewById(R.id.info_introduce_tv)).getText() + "";
                intent = new Intent(mContext, NickNameEditActivity.class);
                intent.putExtra("code", nickName);
                intent.putExtra("title", "个人介绍");
                intent.putExtra("viewId",  R.id.info_introduce_tv);
                startActivityForResult(intent, RESULT_NICKNAME);

                break;
            case R.id.info_address_layout:
                intent = new Intent(mContext, AddressCitySelectActivity.class);
                intent.putExtra("withall", "0");//不显示全部
                intent.putExtra("province", mProvince);
                intent.putExtra("city", mCity);
                intent.putExtra("zone", mLocation);
                startActivityForResult(intent, ADDR_SELECT);
                break;
        }
    }

    private void showSex() {
        OptionPicker picker = new OptionPicker(mContext, new String[]{
                "男", "女"
        });
        picker.setOffset(1);
        picker.setSelectedIndex(1);
        picker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                String sex = "female";
                if ("男".equals(option)) {
                    sex = "male";
                }
                ((TextView) findViewById(R.id.info_sex)).setText(option);
                updateInfo(sex, "", "", "");
            }
        });
        picker.setAnimationStyle(R.style.PopWindowAnimationFade);
        picker.show();
    }

    private void showDate() {
        DatePicker picker = new DatePicker(MyInformationActivity.this, DatePicker.YEAR_MONTH_DAY);
        picker.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        picker.setLabel("", "", "");
        Calendar c = Calendar.getInstance();//首先要获取日历对象
        picker.setRange(1949, c.get(Calendar.YEAR));
//                picker.setLineColor(ContextCompat.getColor(mContext, R.color.white));
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                ((TextView) findViewById(R.id.info_birthday)).setText(year + "年" + month + "月" + day + "日");
                try {
                    String age = getAgeAchen(year, month, day);
                    ((TextView) findViewById(R.id.info_workyear)).setText(age);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                updateInfo("", year, day, month);
            }
        });
        picker.setAnimationStyle(R.style.PopWindowAnimationFade);
        picker.show();
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
        // 进入相册
        PictureSelector.create(mContext)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .theme(R.style.picture_default_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .compress(true)
                .isCamera(false)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .selectionMedia(mSelectList)// 是否传入已选图片 List<LocalMedia> list
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    public void updateHead(final String filePath) {
        try {
            final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
            dialog.setText("请等待");
            dialog.show();
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    dialog.dismiss();
                    switch (msg.what) {
                        case ThreadState.SUCCESS:
                            RspInfo1 re = (RspInfo1) msg.obj;
                            if (re.isSuccess()) {
                                UtilAssistants.showToast("操作成功！", mContext);
                                EventBus.getDefault().post(new EventCity(EventFlag.EVENT_MY_HEAD));
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
                        RspInfo1 re = userAction.updateHead(filePath);
                        handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                    } catch (Exception e) {
                        handler.sendEmptyMessage(ThreadState.ERROR);
                    }
                }
            }).start();
        } catch (Exception e) {
            UtilAssistants.showToast("操作失败!", mContext);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_EMAIL:
                    String email = data.getStringExtra("code");
                    ((TextView) findViewById(R.id.info_email)).setText(email);
                    break;
                case RESULT_NICKNAME:
                    String nickName = data.getStringExtra("code");
                    int viewId = data.getIntExtra("viewId", 0);
                    ((TextView) findViewById(viewId)).setText(nickName);
                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    mSelectList = PictureSelector.obtainMultipleResult(data);
                    ImageLoader.getInstance().displayImage("file://" + mSelectList.get(0).getCompressPath(), mHeaderView);
                    updateHead(mSelectList.get(0).getCompressPath());
                    break;
                case ADDR_SELECT:
                    updateAddress(data);
                    break;
            }
        }
    }

    /**
     * 修改所在地
     */
    private void updateAddress(Intent data) {
        String text = data.getStringExtra("text");
        ((TextView) findViewById(R.id.info_address_tv)).setText(text);
//        mProvinceId = data.getStringExtra("provinceId");
//        mCityId = data.getStringExtra("cityId");
//        mLocationId = data.getStringExtra("locationId");
        final String province = data.getStringExtra("province");
        final String city = data.getStringExtra("city");
        final String location = data.getStringExtra("location");

        final CustomDialog dialog = new CustomDialog(mContext, R.style.MyDialog, R.layout.tip_wait_dialog);
        dialog.setText("请等待");
        dialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case ThreadState.SUCCESS:
                        RspInfo1 re = (RspInfo1) msg.obj;
                        UtilAssistants.showToast(re.getMsg(), mContext);
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
                    RspInfo1 re = userAction.updateInfoAddr(province,city,location, "");
                    handler.obtainMessage(ThreadState.SUCCESS, re).sendToTarget();
                } catch (Exception e) {
                    handler.sendEmptyMessage(ThreadState.ERROR);
                }
            }
        }).start();
    }
}
