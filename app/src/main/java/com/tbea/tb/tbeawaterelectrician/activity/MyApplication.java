package com.tbea.tb.tbeawaterelectrician.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.baidu.location.Address;
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
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application implements BDLocationListener {
	public static final String SP_NAME="LXC_UPUP";
	public static final int PAGE_SIZE=10;

	private static final String SERVICE_PATH="http://www.u-shang.net/enginterface/index.php";
	private static final String IMG_SERVICE_PATH="http://www.u-shang.net/";
	
	
	public static MyApplication instance;
	private List<SoftReference<Activity>> activitys=new ArrayList<SoftReference<Activity>>();
	private boolean mOnline = false;


	private LocationClient mLocationClient;

	@Override
	public void onCreate() {
		super.onCreate();
		instance=this;
		ZXingLibrary.initDisplayOpinion(this);
		initUniversalImageLoader();
		//百度定位
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(this);
		LocationClientOption option=new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
		option.setIsNeedAddress(true);
		option.setOpenGps(true);// 打开gps
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(60 * 1000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		mLocationClient.requestLocation();
		SDKInitializer.initialize(getApplicationContext());
		//加载保存的位置信息
		loadLoaclInfo();
	}
	
	public int getActivityCount(){
		return activitys.size();
	}
	
	public String getImgPath(String imgName){
		return IMG_SERVICE_PATH+imgName;
	}


	public String getImgPath(){
		return IMG_SERVICE_PATH;
	}


	public String getServicePath(){
		return SERVICE_PATH;
	}
	
	public void exit(){
		for(SoftReference<Activity> sa:activitys)if(sa.get()!=null)sa.get().finish();
		System.exit(0);
	}
	
	public void addActivity(Activity activity){
		activitys.add(new SoftReference<Activity>(activity));
	}



	public String getUserId(){
		return ShareConfig.getConfigString(instance, Constants.USERID,"");
	}


	public String getLongitude(){
		return longitude!=null?longitude:"31.132588";
	}

	public String getLatitude(){
		return latitude!=null?latitude:"104.363965";
	}

	private String longitude;
	private String latitude;

//	public void logout(Activity activity){
//		SharedPreferences spf=getSharedPreferences(AppApp.SP_NAME, MODE_PRIVATE);
//		SharedPreferences.Editor edit=spf.edit();
//		edit.remove("loginUsername");
//		edit.remove("loginPassword");
//		//edit.commit();
//		edit.apply();
//		AppApp.instance.setUserInfo(null);
//		//exit();
//		for(SoftReference<Activity> sa:activitys){
//			Activity ta=sa.get();
//			if(ta!=null && ta!=activity){
//				sa.get().finish();
//			}
//		}
//		activitys.clear();
//		activity.startActivity(new Intent(activity,ActivityMain.class));
//		activity.finish();
//	}


	private String address;
	public String getAddrsss(){
		return address!=null?address.substring(2,address.length()):"四川省德阳市旌阳区东海路东段2号";
	}


	public void setLocalInfo(String addrsss,String latitude,String longitude ){
		this.address=addrsss;
		this.latitude = latitude;
		this.longitude = longitude;
		ShareConfig.setConfig(MyApplication.instance,"addess",addrsss);
		ShareConfig.setConfig(MyApplication.instance,"latitude",latitude);
		ShareConfig.setConfig(MyApplication.instance,"longitude",longitude);
	}

	private void loadLoaclInfo(){
		this.address= ShareConfig.getConfigString(MyApplication.instance,"addess",null);
		this.latitude = ShareConfig.getConfigString(MyApplication.instance,"latitude",null);
		this.longitude = ShareConfig.getConfigString(MyApplication.instance,"longitude",null);
	}

	//判断是否开启了Gps
	public void isGps(){
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//判断GPS是否正常启动
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			//返回开启GPS导航设置界面
			showMissingPermissionDialog();
			return;
		}
	}

	// 显示缺失权限提示
	public void showMissingPermissionDialog() {
		final CustomDialog dialog = new CustomDialog(MyApplication.instance,R.style.MyDialog, R.layout.tip_delete_dialog);
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
		},getResources().getString(R.string.settings));
		dialog.show();
	}

	private void initUniversalImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(new ColorDrawable(Color.parseColor("#f0f0f0")))
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
		if (location == null) return ;
		setLocalInfo(location.getAddress().address,String.format("%.3f",location.getLatitude()),String.format("%.3f",location.getLongitude()));
	}

	/**
	 * 获取应用版本号
	 * @return 版本号
	 */
	public static int getVersionCode() {
		int code = 1;
		if(instance == null) {
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
