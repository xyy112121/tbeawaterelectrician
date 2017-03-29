package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/1/25.
 */

public class NearbyCompany {
    private String  id;
    private String picture;
    private String name;
    private String distance;
    private String latitude;
    private String longitude;
    private String address;
    private String companytypeid;
    private String withcompanyidentified;// 加V认证
    private String withcompanylisence;//工商认证
    private String withguaranteemoney;//消保认证
    private String withidentified;//个人认证

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanytypeid() {
        return companytypeid;
    }

    public void setCompanytypeid(String companytypeid) {
        this.companytypeid = companytypeid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWithcompanyidentified() {
        return withcompanyidentified;
    }

    public void setWithcompanyidentified(String withcompanyidentified) {
        this.withcompanyidentified = withcompanyidentified;
    }

    public String getWithcompanylisence() {
        return withcompanylisence;
    }

    public void setWithcompanylisence(String withcompanylisence) {
        this.withcompanylisence = withcompanylisence;
    }

    public String getWithguaranteemoney() {
        return withguaranteemoney;
    }

    public void setWithguaranteemoney(String withguaranteemoney) {
        this.withguaranteemoney = withguaranteemoney;
    }

    public String getWithidentified() {
        return withidentified;
    }

    public void setWithidentified(String withidentified) {
        this.withidentified = withidentified;
    }
}
