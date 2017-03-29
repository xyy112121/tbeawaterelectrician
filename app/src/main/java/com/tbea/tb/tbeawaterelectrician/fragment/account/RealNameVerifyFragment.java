package com.tbea.tb.tbeawaterelectrician.fragment.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.account.Register2Activity;
import com.tbea.tb.tbeawaterelectrician.component.CustomPopWindow;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.UserAction;
import com.tbea.tb.tbeawaterelectrician.util.ThreadState;
import com.tbea.tb.tbeawaterelectrician.util.UtilAssistants;

import java.io.File;

/**
 * Created by abc on 16/12/15. 实名认证注册
 */

public class RealNameVerifyFragment extends Fragment {
    private Uri mUri;
    private static final int RESULT_CAMERA = 0x000001;//相机
    private static final int RESULT_PHOTO = 0x000002;//图片
    private String personidcard1Path;//身份证正面
    private String personidcard2Path;//身份证反面
    private String personidcardwithpersonPath;//手持身份证图片
    private int mFlag;//判断当前选择的图片是什么
    private  View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (View)inflater.inflate(R.layout.fragment_register2,null);
        TextView lookImageView = (TextView)mView.findViewById(R.id.look_give_typical_examples_image);
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(),
                "iconfont/iconfont.ttf");
        lookImageView.setTypeface(iconfont);
        listener(mView);
        return mView;
    }

    public void listener(final View view){
        view.findViewById(R.id.register_update_image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = FlagImage.personidcard1;
                showDialog(view);
            }
        });

        view.findViewById(R.id.register_update_image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = FlagImage.personidcard2;
                showDialog(view);
            }
        });

        view.findViewById(R.id.register_update_image3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = FlagImage.personidcardwithperson;
                showDialog(view);
            }
        });

        view.findViewById(R.id.look_give_typical_examples_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Register2Activity)getActivity()).showGiveTypicalExamplesImage();
            }
        });

        view.findViewById(R.id.register_commit_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String realname = ((EditText)view.findViewById(R.id.regist_realname)).getText()+"";
                    final String personid = ((EditText)view.findViewById(R.id.regist_personid)).getText()+"";
//                    if(realname.equals("")){
//                        showToast("请输入真实姓名");
//                        return;
//                    }
////                    if(isIDCard(personid) == false){
////                        showToast("请输入正确的身份证号");
////                        return;
////                    }
//
//                    if(personidcard1Path.equals("")){
//                        showToast("请选择需要上传的身份证正面");
//                        return;
//                    }
//                    if(personidcard2Path.equals("")){
//                        showToast("请选择需要上传的身份证反面");
//                        return;
//                    }
//                    if(personidcard1Path.equals("")){
//                        showToast("请选择需要上传的手持身份证照片");
//                        return;
//                    }

                    final Handler handler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what){
                                case ThreadState.SUCCESS:
                                    RspInfo1 re = (RspInfo1) msg.obj;
                                    showToast(re.getMsg());
                                    break;
                                case  ThreadState.ERROR:
                                    showToast("操作失败，请重试！");
                                    break;
                            }
                        }
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Register2Activity activity = (Register2Activity)getActivity();
                                activity.mObj.setRealname(realname);
                                activity.mObj.setPersonid(personid);
                                activity.mObj.setPersonidcard1(personidcard1Path);
                                activity.mObj.setPersonidcard2(personidcard2Path);
                                activity.mObj.setPersonidcardwithperson(personidcardwithpersonPath);
                                UserAction action = new UserAction();
                                RspInfo1 result = action.register(activity.mObj);
                                handler.obtainMessage(ThreadState.SUCCESS,result).sendToTarget();
                            } catch (Exception e) {
                                handler.sendEmptyMessage(ThreadState.ERROR);
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    /**
     * 验证身份证号
     */
    public  boolean isIDCard(String personid) {
        String telRegex = "/^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$/;";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (personid.equals("")) return false;
        else return personid.matches(telRegex);
    }

    /**
     * 选择相册Dialog
     */
    protected void showDialog(View view) {
        final CustomPopWindow popWindow = new CustomPopWindow(getActivity(),
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
                RealNameVerifyFragment.this.startActivityForResult(cameraIntent, RESULT_CAMERA);
            } else if("album".equals(mType)){//相册选择图片
                Intent localIntent2 = new Intent();
                localIntent2.setType("image/*");
                localIntent2.putExtra("return-data", true);
                localIntent2
                        .setAction("android.intent.action.GET_CONTENT");
                RealNameVerifyFragment.this.startActivityForResult(localIntent2,RESULT_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            switch (requestCode){
                case RESULT_CAMERA:
                    String filePath = mUri.getPath();
                    showImage(filePath);//显示图片
                    break;
                case RESULT_PHOTO:
                    if(data != null){
//                       filePath = data.getData().getPath();
                        filePath = UtilAssistants.getPath(getActivity(),data.getData());
                        showImage(filePath);//显示图片
                    }
                    break;

            }
        }
    }

    public  void showImage(String filePath){
        try {
//            Display display = getActivity().getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//            int width = size.x;
//            int height = size.y;
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width/2-30,200);
            Bitmap bitmap = UtilAssistants.getBitmapFromPath(filePath,new Point(1024,1024));
            ImageView  imageView = null;
            if(mFlag == FlagImage.personidcard1){
                personidcard1Path = filePath;
                imageView = (ImageView)mView.findViewById(R.id.register_update_image1);
            }
            if(mFlag == FlagImage.personidcard2){
                personidcard2Path = filePath;
                imageView = (ImageView)mView.findViewById(R.id.register_update_image2);
            }
            if(mFlag == FlagImage.personidcardwithperson){
                personidcardwithpersonPath = filePath;
                imageView = (ImageView)mView.findViewById(R.id.register_update_image3);
            }

//            int width = imageView.getWidth();
//            if(mFlag == FlagImage.personidcard1){
//                width = width-10;
//            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageView.getWidth(),imageView.getHeight());
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(bitmap);
            mFlag = 0;
        } catch (Exception e) {
            showToast("操作失败!");
        }
    }

    private  class  FlagImage{
        public static final int personidcard1 = 1000;//身份证正面
        public static final int personidcard2 = 1001;//身份证反面
        public static final int personidcardwithperson = 1002;//手持身份证图片
    }


    public void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

}
