package com.tbea.tb.tbeawaterelectrician.activity.nearby.model;



import com.tbea.tb.tbeawaterelectrician.http.BaseResponseModel;

import java.util.List;

/**
 * Created by DELL on 2017/11/24.
 */

public class TbeaPictrueResponseModel extends BaseResponseModel {

    public DataBean data;

    public class DataBean {
        public List<PicturelistBean> picturelist;

    }
}
