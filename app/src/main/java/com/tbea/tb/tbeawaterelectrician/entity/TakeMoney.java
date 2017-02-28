package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * 提现成功
 */

public class TakeMoney {
    private String  id;
    private String distributorname;
    private String money;
    private String takemoneytime;

    public String getDistributorname() {
        return distributorname;
    }

    public void setDistributorname(String distributorname) {
        this.distributorname = distributorname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTakemoneytime() {
        return takemoneytime;
    }

    public void setTakemoneytime(String takemoneytime) {
        this.takemoneytime = takemoneytime;
    }
}
