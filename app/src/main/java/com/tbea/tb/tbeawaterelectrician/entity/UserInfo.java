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
}
