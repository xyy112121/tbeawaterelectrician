package com.tbea.tb.tbeawaterelectrician.http;

import android.util.Log;

import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;


public class ReqUploadFile {
	private static final String TAG="ReqUploadFile";
	protected ReqHead reqHead;
	private Map<String,String> params;
	private Map<String,String> files;
	protected HttpResponse rsp;
	
	public ReqUploadFile(ReqHead reqHead, Map<String,String> params, Map<String,String> files){
		this.reqHead=reqHead;
		this.params=params;
		this.files=files;
	}
	
	public void req() throws  IOException, JSONException {
		String url= MyApplication.instance.getServicePath();
		HttpPost request=new HttpPost(url);
		Header[] headers=reqHead.getHeaders();
		request.setHeaders(headers);
		MultipartEntity mpEntity = new MultipartEntity();
		if(params!=null && params.size()>0){
			Set<String> keys=params.keySet();
			for(String key:keys)
			{
				ContentType contentType = ContentType.create("text/plain", "UTF-8");
				StringBody body = new StringBody(params.get(key),contentType);
				mpEntity.addPart(key,body);
			}
		}

		if(files!=null && files.size()>0){
			Set<String> keys=files.keySet();
			for(String key:keys){
				File file=new File(files.get(key));
				mpEntity.addPart(key, new FileBody(file));
			}
		}
		request.setEntity(mpEntity);
		DefaultHttpClient client=new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 20*1000);
        HttpConnectionParams.setSoTimeout(params, 20*1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, true);
		rsp = client.execute(request);
	}
	
	public String getRspContext() throws ParseException, IOException {
		String context= EntityUtils.toString(rsp.getEntity());
		Log.d(TAG, "CONTEXT:"+context);
		return context;
	}
	
	public String getHeaderValue(String key){
		Header[] headers=rsp.getHeaders(key);
		if(headers!=null && headers.length>0)return rsp.getHeaders(key)[0].getValue();
		return null;
	}
	
}
