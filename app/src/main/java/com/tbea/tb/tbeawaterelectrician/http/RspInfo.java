package com.tbea.tb.tbeawaterelectrician.http;

import com.tbea.tb.tbeawaterelectrician.entity.UserInfo2;

import java.util.Map;

public class RspInfo<T> {
	public Map<String,T> data;
	public String msg;
	public boolean success;

public T getDateObj(String key){
	return data.get(key);
}

}
