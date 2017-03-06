package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * 采购
 */

public class Commodith {
    private String  id;
    private String name;
    private String  picture;
    private String price;
    private String distance;
    private String specification;
    private String companyid;
    private String companytypeid;

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}
