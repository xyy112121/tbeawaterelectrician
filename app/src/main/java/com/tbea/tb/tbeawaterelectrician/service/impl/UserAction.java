package com.tbea.tb.tbeawaterelectrician.service.impl;

import com.tbea.tb.tbeawaterelectrician.http.MD5Util;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 17/1/14.
 */

public class UserAction extends BaseAction {
    /**
     * 登录
     * @param phone 用户名
     * @param pwd 密码
     * @return
     * @throws Exception
     */
    public RspInfo login(String phone,String pwd) throws Exception{
        RspInfo rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("mobilenumber", phone));
        pairs.add(new BasicNameValuePair("userpas", MD5Util.getMD5String(pwd)));
        String result = sendRequest("TBEAENG001001004000",pairs);
        rspInfo = gson.fromJson(result,RspInfo.class);
        return  rspInfo;

    }
}
