package com.tbea.tb.tbeawaterelectrician.entity;

import java.util.List;

/**
 * Created by cy on 2017/2/18.
 */

public class Order  {
    private String orderid;
    private String ordercompany;
    private String orderstatusid;
    private String orderstatus;
    private String ordercommoditynumber;
    private String ordertotlefee;
    private String deliveryfee;
    private String promotioninfo;
    private List<ProductInfo> commoditylist;



    public List<ProductInfo> getCommoditylist() {
        return commoditylist;
    }

    public void setCommoditylist(List<ProductInfo> commoditylist) {
        this.commoditylist = commoditylist;
    }

    public String getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(String deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public String getOrdercommoditynumber() {
        return ordercommoditynumber;
    }

    public void setOrdercommoditynumber(String ordercommoditynumber) {
        this.ordercommoditynumber = ordercommoditynumber;
    }

    public String getOrdercompany() {
        return ordercompany;
    }

    public void setOrdercompany(String ordercompany) {
        this.ordercompany = ordercompany;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getOrderstatusid() {
        return orderstatusid;
    }

    public void setOrderstatusid(String orderstatusid) {
        this.orderstatusid = orderstatusid;
    }

    public String getOrdertotlefee() {
        return ordertotlefee;
    }

    public void setOrdertotlefee(String ordertotlefee) {
        this.ordertotlefee = ordertotlefee;
    }

    public String getPromotioninfo() {
        return promotioninfo;
    }

    public void setPromotioninfo(String promotioninfo) {
        this.promotioninfo = promotioninfo;
    }
}
