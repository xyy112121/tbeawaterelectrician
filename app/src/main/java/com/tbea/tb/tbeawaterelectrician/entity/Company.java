package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/1/24.商家
 */

public class Company {
    private String  id;
    private String  picture;
    private String companyname;
    private String distance;
    private String commoditydesc;
    private String price;
    private String companyid;
    private String companytypeid;

    public String getCompanytypeid() {
        return companytypeid;
    }

    public void setCompanytypeid(String companytypeid) {
        this.companytypeid = companytypeid;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCommoditydesc() {
        return commoditydesc;
    }

    public void setCommoditydesc(String commoditydesc) {
        this.commoditydesc = commoditydesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
