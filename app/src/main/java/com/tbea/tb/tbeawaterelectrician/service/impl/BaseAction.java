package com.tbea.tb.tbeawaterelectrician.service.impl;

import com.google.gson.Gson;
import com.tbea.tb.tbeawaterelectrician.http.MD5Util;
import com.tbea.tb.tbeawaterelectrician.http.ReqBase;
import com.tbea.tb.tbeawaterelectrician.http.ReqHead;
import com.tbea.tb.tbeawaterelectrician.http.ReqUploadFile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by abc on 17/1/14.
 */

public class BaseAction {
    public Gson gson;

    public  BaseAction(){
        gson = new Gson();
    }

    public String sendRequest(final String serviceCode,final List<NameValuePair> pairs) throws InterruptedException, IOException,
            ExecutionException {
        FutureTask task = new FutureTask(
                new Callable()
                {
                    public String call() throws Exception {
                        ReqHead rh = new ReqHead();
                        rh.serviceCode=serviceCode;
                        ReqBase req = new ReqBase(rh, pairs);
                        req.req();
                        String rspContext = req.getRspContext();
                        return rspContext;
                    }
                });
        new Thread(task).start();
        return (String)task.get();
    }

    /**
     * 注册
     * @return
     */
    public String regist(final String serviceCode,final Map<String ,String> paramsIn,final Map<String,String> fileIn ) throws  Exception{
        FutureTask task = new FutureTask(
                new Callable()
                {
                    public String call() throws Exception {
                        ReqHead rh = new ReqHead();
                        rh.serviceCode=serviceCode;
                        ReqUploadFile req = new ReqUploadFile(rh, paramsIn,fileIn);
                        req.req();
                        String rspContext = req.getRspContext();
                        return rspContext;
                    }
                });
        new Thread(task).start();
        return (String)task.get();
    }
}
