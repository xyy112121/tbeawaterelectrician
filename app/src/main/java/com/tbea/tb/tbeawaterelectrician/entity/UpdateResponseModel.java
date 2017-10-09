package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by programmer on 2017/10/9.
 */

public class UpdateResponseModel {
    public VersioninfoBean versioninfo;

    public  class VersioninfoBean {
        public String tipswitch;
        public String upgradedescription;
        public String mustupgrade;
        public int versioncode;
        public String versionname;
        public String jumpurl;
    }
}
