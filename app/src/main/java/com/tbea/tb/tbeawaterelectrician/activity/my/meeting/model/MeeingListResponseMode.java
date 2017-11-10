package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model;

import com.google.gson.annotations.SerializedName;
import com.tbea.tb.tbeawaterelectrician.http.BaseResponseModel;

import java.util.List;

/**
 *会议列表
 */

public class MeeingListResponseMode extends BaseResponseModel {
    public Data data;

    public  class Data {
        public List<MeetingModel> meetinglist;

        public  class MeetingModel {
            public String id;
            public String meetingcode;
            public String zone;
            public String checkintime;
        }
    }
}
