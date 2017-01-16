package com.tbea.tb.tbeawaterelectrician.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


public class DownFileTask {
	public static final String SERVICE_CODE = "DownFileTask";
	private final String TAG = getClass().getName();
	private AsyncListener asynListener;
	private boolean hasError;
	private String errorMsg;
	
	public String fileUrl;
	public String fileType;
	public String localPath;
	
	
	public boolean isHasError() {
		return hasError;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public DownFileTask(AsyncListener asynListener) {
		this.asynListener = asynListener;
	}

	public void execute() {
		MeAsyncTask task = new MeAsyncTask();
		task.execute();
	}


	class MeAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			hasError = false;
			errorMsg = null;
			localPath=null;
			if(asynListener!=null)asynListener.start(SERVICE_CODE);
		}

		
		@Override
		protected Void doInBackground(Void... params) {
			FileOutputStream outStream=null;
			InputStream input=null;
			try {
				URL curl = new URL(fileUrl);
				HttpURLConnection conn = (HttpURLConnection)curl.openConnection();
				conn.setRequestMethod("POST");
				conn.setReadTimeout(1000);
				input = conn.getInputStream();
				localPath= Environment.getExternalStorageDirectory()+ File.separator+"NewHopeDown"+ File.separator+"_t."+fileType.toLowerCase(Locale.getDefault());
				File file=new File(localPath);
				file.mkdirs();
				if(file.exists()){
					file.delete();
					file.createNewFile();
				}else{
					file.createNewFile();
				}
				outStream =new FileOutputStream(file);
				int temp = 0;
				byte[] data = new byte[1024];
				while((temp = input.read(data))!=-1){
					outStream.write(data, 0, temp);
				}				
			} catch (Exception e) {
				hasError = true;
				errorMsg = e.getMessage();
				Log.e(TAG, e.getMessage(), e);
			}finally{
				try {
					if(outStream!=null){
						outStream.flush();
						outStream.close();
					}
					if(input!=null){
						input.close();
					}						
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(asynListener!=null)asynListener.finish(SERVICE_CODE);
		}
	}

}
