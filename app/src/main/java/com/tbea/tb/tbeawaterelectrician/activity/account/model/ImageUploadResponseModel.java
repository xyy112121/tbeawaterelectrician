package com.tbea.tb.tbeawaterelectrician.activity.account.model;

import com.tbea.tb.tbeawaterelectrician.http.BaseResponseModel;

/**
 * 上传图片
 */

public class ImageUploadResponseModel extends BaseResponseModel {
    public DataBean data;

    public static class DataBean {
        public PictureinfoBean pictureinfo;

        public static class PictureinfoBean {
            public String picturesavenames;
        }
    }
}
