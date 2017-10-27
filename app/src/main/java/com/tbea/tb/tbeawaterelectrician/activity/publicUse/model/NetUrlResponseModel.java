package com.tbea.tb.tbeawaterelectrician.activity.publicUse.model;


/**
 * Created by programmer on 2017/10/27.
 */

public class NetUrlResponseModel {
    private String msg;
    private boolean success;
    public DataBean data;

    public class DataBean {
        public String url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
