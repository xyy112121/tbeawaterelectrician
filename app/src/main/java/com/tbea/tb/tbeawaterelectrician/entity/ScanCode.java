package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * 返利
 */

public class ScanCode {
    public static final String ID = "_id";
    public static final String Scan_ID = "Scan_ID";
    public static final String Name = "name";
    public static final String Price = "price";
    public static final String Picture = "picture";
    public static final String Specification = "specification";
    public static final String Rebatemoney = "rebatemoney";
    public static final String Scantime = "scantime";
    public static final String Scanaddress = "scanaddress";
    public static final String Distributor = "distributor";
    public static final String Commodityname = "commodityname";
    public static final String Commodityspec = "commodityspec";
    public static final String Manufacturedate = "manufacturedate";

    private String id;
    private String name;
    private String price;
    private String picture;
    private String specification;
    private String rebatemoney;
    private String scantime;
    private String scanaddress;
    private String distributor;
    private String commodityname;
    private String commodityspec;
    private String manufacturedate;

    public String getCommodityname() {
        return commodityname;
    }

    public void setCommodityname(String commodityname) {
        this.commodityname = commodityname;
    }

    public String getCommodityspec() {
        return commodityspec;
    }

    public void setCommodityspec(String commodityspec) {
        this.commodityspec = commodityspec;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManufacturedate() {
        return manufacturedate;
    }

    public void setManufacturedate(String manufacturedate) {
        this.manufacturedate = manufacturedate;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRebatemoney() {
        return rebatemoney;
    }

    public void setRebatemoney(String rebatemoney) {
        this.rebatemoney = rebatemoney;
    }

    public String getScanaddress() {
        return scanaddress;
    }

    public void setScanaddress(String scanaddress) {
        this.scanaddress = scanaddress;
    }

    public String getScantime() {
        return scantime;
    }

    public void setScantime(String scantime) {
        this.scantime = scantime;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}
