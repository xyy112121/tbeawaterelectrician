package com.tbea.tb.tbeawaterelectrician.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by programmer on 2017/11/21.
 */

public class SearchResponseModuel {

    public Searchtype searchtype;
    public List<SearchModel> list;

    public class Searchtype {
        public String searchtype;
    }

    public class SearchModel {
        public String price;
        public String standardprice;
        public String specification;
        public String companyid;
        public String id;
        public String picture;
        public String name;
        public String longitude;
        public String latitude;
        public String distance;
        public String address;
        public String companytypeid;
        public String withcompanyidentified;
        public String withcompanylisence;
        public String withguaranteemoney;
        public String withidentified;
    }


}
