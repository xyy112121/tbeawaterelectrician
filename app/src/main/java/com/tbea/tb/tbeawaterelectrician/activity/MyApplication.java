package com.tbea.tb.tbeawaterelectrician.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.component.CustomDialog;
import com.tbea.tb.tbeawaterelectrician.lib_zxing.DisplayUtil;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application implements BDLocationListener {

    private static final String IMG_SERVICE_PATH = "";
    private static final String SERVICE_PATH = "http://www.u-shang.net/enginterface/index.php";
//    private static final String SERVICE_PATH = "http://121.42.193.154:6698/enginterface/index.php";//测试


    public static MyApplication instance;
    private List<SoftReference<Activity>> activitys = new ArrayList<SoftReference<Activity>>();
    private boolean mOnline = false;


    private LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.DEBUG = true;
        UMShareAPI.get(this);
        initUniversalImageLoader();
        SDKInitializer.initialize(getApplicationContext());
        //百度定位
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);// 打开gps
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(60 * 1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.requestLocation();

       // 加载保存的位置信息
        loadLoaclInfo();

        initDisplayOpinion();

    }

    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
//		PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setWeixin("wxf0098beca31d85cc", "4767495ecded06e0fdc6e8b6a289d56f");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setSinaWeibo("3525593155", "ad55f2a5b4997936296ce052649c406e", "http://www.u-shang.net/enginterface/index.php/callback");
//		PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
    }

    public int getActivityCount() {
        return activitys.size();
    }

    public String getImgPath(String imgName) {
        return IMG_SERVICE_PATH + imgName;
    }


    public String getImgPath() {
        return IMG_SERVICE_PATH;
    }


    public String getServicePath() {
        return SERVICE_PATH;
    }

    public void exit() {
        for (SoftReference<Activity> sa : activitys) if (sa.get() != null) sa.get().finish();
//		System.exit(0);
    }

    public void addActivity(Activity activity) {
        activitys.add(new SoftReference<Activity>(activity));
    }

    /**
     * 结束指定的Activity
     */
    public void finishSingleActivity(Activity activity) {
        if (activity != null) {
            if (activitys.contains(activity)) {
                activitys.remove(activity);
            }
            activity.finish();
        }
    }


    public String getUserId() {
        return ShareConfig.getConfigString(instance, Constants.USERID, "");
    }


    public String getLongitude() {
        return longitude != null ? longitude : "31.132588";
    }

    public String getLatitude() {
        return latitude != null ? latitude : "104.363965";
    }

    private String longitude;
    private String latitude;


    private String address;
    private String city;
    private String province;
    private String district;

    public String getCity() {
        if (city == null) {
            city = "德阳市";
        }
        return city;
    }

    public String getProvince() {
        if (province == null) {
            province = "四川省";
        }
        return province;
    }

    public String getDistrict() {
        if (district == null) {
            district = "旌阳区";
        }
        return district;
    }

    public String getAddrsss() {
        return address != null ? address.substring(2, address.length()) : "四川省德阳市旌阳区东海路东段2号";
    }


    public void setLocalInfo(String addrsss, String latitude, String longitude) {
        this.address = addrsss;
        this.latitude = latitude;
        this.longitude = longitude;
        ShareConfig.setConfig(MyApplication.instance, "addess", addrsss);
        ShareConfig.setConfig(MyApplication.instance, "latitude", latitude);
        ShareConfig.setConfig(MyApplication.instance, "longitude", longitude);
    }

    private void loadLoaclInfo() {
        this.address = ShareConfig.getConfigString(MyApplication.instance, "addess", "四川省德阳市旌阳区东海路东段2号");
        this.latitude = ShareConfig.getConfigString(MyApplication.instance, "latitude", null);
        this.longitude = ShareConfig.getConfigString(MyApplication.instance, "longitude", null);
    }

    //判断是否开启了Gps
    public void isGps() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //返回开启GPS导航设置界面
            showMissingPermissionDialog(instance);
            return;
        }
    }

    // 显示缺失权限提示
    public void showMissingPermissionDialog(Context context) {
        final CustomDialog dialog = new CustomDialog(context, R.style.MyDialog, R.layout.tip_delete_dialog);
        dialog.setTitle(getResources().getString(R.string.help));
        dialog.setText(getResources().getString(R.string.string_gps_help_text));
        dialog.setConfirmBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }, getResources().getString(R.string.quit));
        dialog.setCancelBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MyApplication.instance.startActivity(intent);
            }
        }, getResources().getString(R.string.settings));
        dialog.show();
    }

    private void initUniversalImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_defult)
                .showImageForEmptyUri(R.drawable.icon_defult)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_defult)  //设置图片加载/解码过程中错误时候显示的图片
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        int memCacheSize = 1024 * 1024 * memClass / 8;

        File cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/jiecao/cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new UsingFreqLimitedMemoryCache(memCacheSize))
                .memoryCacheSize(memCacheSize)
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null) return;
        city = location.getAddress().city;
        province = location.getAddress().province;
        district = location.getAddress().district;
        setLocalInfo(location.getAddress().address, String.format("%.3f", location.getLatitude()), String.format("%.3f", location.getLongitude()));
    }

    /**
     * 获取应用版本号
     *
     * @return 版本号
     */
    public static int getVersionCode() {
        int code = 1;
        if (instance == null) {
            return code;
        }
        try {
            PackageInfo packageInfo = instance.getPackageManager().getPackageInfo(
                    "com.tbea.tb.tbeawaterelectrician", 0);
            code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return code;
    }

}
