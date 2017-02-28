package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * 我的举报
 */

public class Appeal {
    private String id;
    private String title;
    private String appealtime;
    private String replycontent;
    private String  appealcategoryid;//举报类型
    private String  scanaddress;
    private String provinceid;
    private String  cityid;
    private String  distributorid;
    private String  commodityid;
    private String appealcontent;

    public String getAppealtime() {
        return appealtime;
    }

    public void setAppealtime(String appealtime) {
        this.appealtime = appealtime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReplycontent() {
        return replycontent;
    }

    public void setReplycontent(String replycontent) {
        this.replycontent = replycontent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppealcategoryid() {
        return appealcategoryid;
    }

    public void setAppealcategoryid(String appealcategoryid) {
        this.appealcategoryid = appealcategoryid;
    }

    public String getAppealcontent() {
        return appealcontent;
    }

    public void setAppealcontent(String appealcontent) {
        this.appealcontent = appealcontent;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getCommodityid() {
        return commodityid;
    }

    public void setCommodityid(String commodityid) {
        this.commodityid = commodityid;
    }

    public String getDistributorid() {
        return distributorid;
    }

    public void setDistributorid(String distributorid) {
        this.distributorid = distributorid;
    }

    public String getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid;
    }

    public String getScanaddress() {
        return scanaddress;
    }

    public void setScanaddress(String scanaddress) {
        this.scanaddress = scanaddress;
    }
}
