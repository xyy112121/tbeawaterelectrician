package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/1/22.
 */

public class Register {
    private String mobile;//电话
    private String password;//密码
    private String verifycode;//验证码
    private String realname;//验证码
    private String personid;//身份证号
    private String personidcard1;//身份证正面
    private String personidcard2;//身份证反面
    private String personidcardwithperson;//手持身份证

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifycode() {
        return verifycode;
    }

    public void setVerifycode(String verifycode) {
        this.verifycode = verifycode;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getPersonidcard1() {
        return personidcard1;
    }

    public void setPersonidcard1(String personidcard1) {
        this.personidcard1 = personidcard1;
    }

    public String getPersonidcard2() {
        return personidcard2;
    }

    public void setPersonidcard2(String personidcard2) {
        this.personidcard2 = personidcard2;
    }

    public String getPersonidcardwithperson() {
        return personidcardwithperson;
    }

    public void setPersonidcardwithperson(String personidcardwithperson) {
        this.personidcardwithperson = personidcardwithperson;
    }
}
