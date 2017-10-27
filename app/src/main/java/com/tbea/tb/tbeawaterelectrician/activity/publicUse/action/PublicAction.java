package com.tbea.tb.tbeawaterelectrician.activity.publicUse.action;



import com.tbea.tb.tbeawaterelectrician.activity.publicUse.model.NetUrlResponseModel;
import com.tbea.tb.tbeawaterelectrician.service.impl.BaseAction;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2017/8/23.
 */

public class PublicAction extends BaseAction {


    /**
     * 获取html的Url
     */
    public NetUrlResponseModel getUrl() throws Exception {
        NetUrlResponseModel rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        String result = sendRequest("TBEAENG015001002000", pairs);
        rspInfo = gson.fromJson(result, NetUrlResponseModel.class);
        return rspInfo;
    }
}
