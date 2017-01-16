package com.tbea.tb.tbeawaterelectrician.http;

import com.tbea.tb.tbeawaterelectrician.activity.MyApplication;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;


public class ReqHead {
	public String origDomain;
	public String protocolVar;
	public String appVersion;
	public String userId;
	public String serviceCode;
	public String actionCode;
	public String cityId;
	public String longitude;
	public String latitude;
	
	public ReqHead(){
		origDomain="Android";
		protocolVar="tbeaeng_v1";
		appVersion="V_1.0";
		userId = MyApplication.instance.getUserId();
		serviceCode="";
		actionCode="0";	
		cityId="德阳市";
		longitude=MyApplication.instance.getLongitude();
		latitude=MyApplication.instance.getLatitude();
	}
	

	public Header[] getHeaders(){
		ArrayList<Header> headers=new ArrayList<Header>();
	    headers.add(new BasicHeader("origdomain", origDomain));
		headers.add(new BasicHeader("protocolver", protocolVar));
		headers.add(new BasicHeader("appversion", appVersion));
		headers.add(new BasicHeader("userid", userId));
		headers.add(new BasicHeader("actioncode",actionCode));
		headers.add(new BasicHeader("servicecode", serviceCode));
		headers.add(new BasicHeader("dilicity", cityId));
		headers.add(new BasicHeader("longitude", longitude));
		headers.add(new BasicHeader("latitude", latitude));
		return headers.toArray(new Header[0]);
	}
}
