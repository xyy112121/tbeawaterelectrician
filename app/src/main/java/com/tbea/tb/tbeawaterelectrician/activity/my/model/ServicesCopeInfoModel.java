package com.tbea.tb.tbeawaterelectrician.activity.my.model;

import java.util.List;

/**
 * Created by programmer on 2017/12/20.
 */

public class ServicesCopeInfoModel {

    public Userinfo userinfo;
    public List<Servicescope> servicescopelist;

    public static class Userinfo {
        public String registzone;
    }

    public class Servicescope{
        public String name;
    }
}
