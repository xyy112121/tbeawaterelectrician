package com.tbea.tb.tbeawaterelectrician.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;
import com.tbea.tb.tbeawaterelectrician.util.Constants;
import com.tbea.tb.tbeawaterelectrician.util.ShareConfig;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application  {
	public static final String SP_NAME="LXC_UPUP";
	public static final int PAGE_SIZE=10;

	private static final String SERVICE_PATH="http://www.u-shang.net/enginterface/index.php";
	private static final String IMG_SERVICE_PATH="http://www.u-shang.net/";
	
	
	public static MyApplication instance;
	private List<SoftReference<Activity>> activitys=new ArrayList<SoftReference<Activity>>();
	private boolean mOnline = false;


//	private LocationClient mLocationClient;
	@Override
	public void onCreate() {
		super.onCreate();
		instance=this;
		ZXingLibrary.initDisplayOpinion(this);
		initUniversalImageLoader();
//		ImageLoaderUtil.initImageLoader(this);
//		//百度定位
//		mLocationClient = new LocationClient(getApplicationContext());
//		mLocationClient.registerLocationListener( this );
//		LocationClientOption option=new LocationClientOption();
//		option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//		option.setScanSpan(60 * 1000);
//		mLocationClient.setLocOption(option);
//		mLocationClient.start();
//		mLocationClient.requestLocation();
//		SDKInitializer.initialize(getApplicationContext());
		//加载保存的位置信息
//		loadLoaclInfo();
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
		return longitude;
	}

	public String getLatitude(){
		return latitude;
	}

	private String longitude="104.363965";
	private String latitude="31.132588";
//	@Override
//	public void onReceiveLocation(BDLocation location) {
//		Log.d("lxclxc", "更新位置信息");
//		if (location == null) return ;
//		latitude= String.format("%.3f",location.getLatitude());
//		longitude= String.format("%.3f",location.getLongitude());
//	}


	private UserInfo2 cuUserInfo;
	public void saveLoginInfo(String userName, String password){
		SharedPreferences spf=getSharedPreferences(MyApplication.SP_NAME, MODE_PRIVATE);
		SharedPreferences.Editor edit=spf.edit();
		edit.putString("loginUsername", userName);
		edit.putString("loginPassword", password);
		edit.commit();
	}

	public void setUserInfo(UserInfo2 info){
		cuUserInfo=info;
	}

	public UserInfo2 getUserInfo(){
		return cuUserInfo;
	}

	public boolean isLogin(){
		return cuUserInfo!=null;
	}

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


	private String provinceId;
	public String getProvinceId(){
		return provinceId!=null?provinceId:"";
	}
	private String provinceName;
	public String getProvinceName(){
		return provinceName!=null?provinceName:"";
	}
	private String cityId;
	public String getCityId(){
		return cityId!=null?cityId:"";
	}
	private String cityName;
	public String getCityName(){
		return cityName!=null?cityName:"重庆";
	}
	private String areaId;
	public String getAreaId(){
		return areaId!=null?areaId:"";
	}

	private String areaName;
	public String getAreaName(){
		return areaName!=null?areaName:"";
	}

	public void setLocalInfo(String provinceId, String provinceName, String cityId, String cityName, String areaId, String areaName){
		this.provinceId=provinceId;
		this.provinceName=provinceName;
		this.cityId=cityId;
		this.cityName=cityName;
		this.areaId=areaId;
		this.areaName=areaName;
		SharedPreferences spf=getSharedPreferences(MyApplication.SP_NAME, MODE_PRIVATE);
		SharedPreferences.Editor edit=spf.edit();
		edit.putString("provinceId", provinceId);
		edit.putString("provinceName", provinceName);

		edit.putString("cityId", cityId);
		edit.putString("cityName", cityName);

		edit.putString("areaId", areaId);
		edit.putString("areaName", areaName);
		edit.commit();
	}

//	private void loadLoaclInfo(){
//		SharedPreferences spf=getSharedPreferences(AppApp.SP_NAME, MODE_PRIVATE);
//		this.provinceId= spf.getString("provinceId",null);
//		this.provinceName= spf.getString("provinceName",null);
//		this.cityId= spf.getString("cityId",null);
//		this.cityName= spf.getString("cityName",null);
//		this.areaId= spf.getString("areaId",null);
//		this.areaName= spf.getString("areaName",null);
//	}

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
}
