package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model;

import com.google.gson.annotations.SerializedName;
import com.tbea.tb.tbeawaterelectrician.http.BaseResponseModel;

import java.util.List;

/**
 * Created by programmer on 2017/11/10.
 */

public class MeetingViewResponseModel extends BaseResponseModel {

    public Data data;

    public  class Data {
        public Meetingbaseinfo meetingbaseinfo;//会议基本信息
        public Checkininfo checkininfo;//签到信息
        public List<OrganizeCompanyModel> organizecompanylist;//举办单位列表

        public  class Meetingbaseinfo {
            public String id;
            public String meetingcode;//会议编号
            public String meetingtime;//格式化后的会议时间
            public String meetingstarttime;//会议开始时间
            public String meetingendtime;//会议结束时间
            public String meetingplace;//会议地点
        }

        public  class Checkininfo {
            public String checkintime;//签到时间
            public String checkinplace;//签到地点
        }

        public  class OrganizeCompanyModel {
            public String id;
            public String name;//公司名称
            public String mastername;//负责人姓名
            public String masterthumbpicture;//负责人头像
            public String companytypeicon;//公司类型 Icon
        }
    }
}
