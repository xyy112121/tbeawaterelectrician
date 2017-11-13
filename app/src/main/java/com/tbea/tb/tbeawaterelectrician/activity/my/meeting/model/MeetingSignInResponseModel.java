package com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model;

import com.tbea.tb.tbeawaterelectrician.http.BaseResponseModel;

/**
 * Created by programmer on 2017/11/10.
 */

public class MeetingSignInResponseModel extends BaseResponseModel {

    public Meetingcheckininfo meetingcheckininfo;

    public class Meetingcheckininfo {
        public String meetingid;
        public String checkstatus;
        public String checkintime;
        public String checkinplace;
    }

}
