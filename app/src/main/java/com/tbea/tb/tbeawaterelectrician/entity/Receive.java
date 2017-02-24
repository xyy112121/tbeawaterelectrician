package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/2/6.
 */

public class Receive {
    private String  id;
    private String event;
    private String thisvalue;
    private String currenttotlemoney;
    private String  money;
    private String time;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getThisvalue() {
        return thisvalue;
    }

    public void setThisvalue(String thisvalue) {
        this.thisvalue = thisvalue;
    }

    public String getCurrenttotlemoney() {
        return currenttotlemoney;
    }

    public void setCurrenttotlemoney(String currenttotlemoney) {
        this.currenttotlemoney = currenttotlemoney;
    }
}
