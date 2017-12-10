package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/2/9.
 */

public class UserInfo  {
    private String id;
    private String picture;

    private String nickname;
    private String name;
    private String mobile;
    private String mailaddr;
    private String sex;
    private String birthday;
    private String companyname;
    private String oldyears;
    private String realname;//真实姓名
    private String address;//所在地
    private String servicescope;//服务范围
    private String introduce;//个人介绍

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailaddr() {
        return mailaddr;
    }

    public void setMailaddr(String mailaddr) {
        this.mailaddr = mailaddr;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getOldyears() {
        return oldyears;
    }

    public void setOldyears(String oldyears) {
        this.oldyears = oldyears;
    }

    public String getRealname() {
        return realname;
    }

    public String getAddress() {
        return address;
    }

    public String getServicescope() {
        return servicescope;
    }

    public String getIntroduce() {
        return introduce;
    }
}
