package com.tbea.tb.tbeawaterelectrician.activity.my.model;


import com.tbea.tb.tbeawaterelectrician.http.BaseResponseModel;

import java.util.List;

/**
 * Created by DELL on 2017/8/28.
 */

public class MessageListResponseModel extends BaseResponseModel {

    public DataBean data;


    public class DataBean {
        public PageinfoBean pageinfo;
        public List<MessagelistBean> messagelist;

        public class PageinfoBean {
            public String title;
        }

        public class MessagelistBean {
            public String id;
            public String messagetime;
            public String title;
            public String isnew;
        }
    }
}
