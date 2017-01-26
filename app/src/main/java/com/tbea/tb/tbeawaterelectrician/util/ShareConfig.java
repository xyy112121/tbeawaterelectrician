package com.tbea.tb.tbeawaterelectrician.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShareConfig {

	// 设置配置值
	@SuppressLint({ "CommitPrefEdits", "WorldReadableFiles" })
	public static void setConfig(Context context, String name,
								 Object valueObject) {
		SharedPreferences shared = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		if (valueObject instanceof String) {
			String value = (String) valueObject;
			editor.putString(name, value);
		} else if (valueObject instanceof Integer) {
			int value = (Integer) valueObject;
			editor.putInt(name, value);
		} else if (valueObject instanceof Float) {
			float value = (Float) valueObject;
			editor.putFloat(name, value);
		} else if (valueObject instanceof Long) {
			long value = (Long) valueObject;
			editor.putLong(name, value);
		} else if (valueObject instanceof Boolean) {
			boolean value = (Boolean) valueObject;
			editor.putBoolean(name, value);
		}
		editor.commit();
	}

	// 获取配置 string
	@SuppressLint("WorldReadableFiles")
	public static String getConfigString(Context context, String name,
										 String defaultValue) {
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return share.getString(name, defaultValue);
	}

	// 获取配置 int
	@SuppressLint("WorldReadableFiles")
	public static int getConfigInt(Context context, String name,
								   int defaultValue) {
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE
						+ Context.MODE_MULTI_PROCESS);
		return share.getInt(name, defaultValue);
	}

	// 获取配置 boolean
	@SuppressLint("WorldReadableFiles")
	public static boolean getConfigBoolean(Context context, String name,
										   boolean defaultValue) {
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE
						+ Context.MODE_MULTI_PROCESS);
		return share.getBoolean(name, defaultValue);
	}

	// 获取配置long
	@SuppressLint("WorldReadableFiles")
	public static long getConfigLong(Context context, String name,
									 long defaultValue) {
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE
						+ Context.MODE_MULTI_PROCESS);
		return share.getLong(name, defaultValue);
	}
}
