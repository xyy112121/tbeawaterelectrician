package com.tbea.tb.tbeawaterelectrician.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.SparseArray;

import java.lang.ref.SoftReference;

public class PDUtil {
	private static SparseArray<SoftReference<ProgressDialog>> pds=new SparseArray<SoftReference<ProgressDialog>>();
	
	public static String DEFAULT_MSG="请求中，请稍候...";
	
	public static void showPD(Context context){
		showPD(context,DEFAULT_MSG);
	}
	
	public static void showPD(Context context, String msg){
		int hashCode=context.hashCode();
		ProgressDialog pd=getPD(hashCode);
		if(pd!=null){
			pd.setMessage(msg);
		}else{
			pd=new ProgressDialog(context);
			pd.setMessage(msg);
			pds.put(hashCode,new SoftReference<ProgressDialog>(pd));
		}
		pd.show();
	}
	
	public static void hidePD(Context context){
		if(context!=null){
			int hashCode=context.hashCode();
			ProgressDialog pd=getPD(hashCode);
			if(pd!=null)pd.dismiss();
			pds.remove(hashCode);
		}
	}
	
	private static ProgressDialog getPD(int key){
		SoftReference<ProgressDialog> rpd=pds.get(key);
		if(rpd!=null)return rpd.get();
		return null;
	}
}
