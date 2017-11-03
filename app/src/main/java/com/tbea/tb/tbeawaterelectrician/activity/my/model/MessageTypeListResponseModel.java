package com.tbea.tb.tbeawaterelectrician.activity.my.model;


import com.tbea.tb.tbeawaterelectrician.http.BaseResponseModel;

import java.util.List;

/**
 * Created by DELL on 2017/8/28.
 */

public class MessageTypeListResponseModel extends BaseResponseModel {

    public DataBean data;

    public static class DataBean {
        public List<MessagecategorylistBean> messagecategorylist;

        public static class MessagecategorylistBean {
            public String id;
            public String picture;
            public String newcount;
            public String name;
            public String lasttime;
            public String lastmessagetitle;
        }
    }
}
