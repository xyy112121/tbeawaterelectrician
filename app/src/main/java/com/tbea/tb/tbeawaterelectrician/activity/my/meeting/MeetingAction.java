package com.tbea.tb.tbeawaterelectrician.activity.my.meeting;

import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model.MeeingListResponseMode;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model.MeetingSignInResponseModel;
import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model.MeetingViewResponseModel;
import com.tbea.tb.tbeawaterelectrician.http.RspInfo1;
import com.tbea.tb.tbeawaterelectrician.service.impl.BaseAction;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by programmer on 2017/11/9.
 */

public class MeetingAction extends BaseAction {

    public MeeingListResponseMode getMeetinList
            (String orderitem, String order, int page, int pagesize) throws Exception {
        MeeingListResponseMode model;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("orderitem", orderitem));
        pairs.add(new BasicNameValuePair("order", order));
        pairs.add(new BasicNameValuePair("page", String.valueOf(page)));
        pairs.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));
        String result = sendRequest("TBEAENG00500201001", pairs);
        model = gson.fromJson(result, MeeingListResponseMode.class);
        return model;
    }


    /**
     * 会议签到结果
     *
     * @param scanCode 扫码获取到的码
     * @param address  扫码地点详情
     */
    public RspInfo1 getSignInResult(String scanCode, String address) throws Exception {
        RspInfo1 rspInfo;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scancode", scanCode));
        pairs.add(new BasicNameValuePair("address", address));
        String result = sendRequest("TBEAENG00500201003", pairs);
        rspInfo = gson.fromJson(result, RspInfo1.class);
        return rspInfo;
    }

    /**
     * 获取会议详细
     */
    public MeetingViewResponseModel getPlumberMeetingView(String meetingid) throws Exception {
        MeetingViewResponseModel model;
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("meetingid", meetingid));
        String result = sendRequest("TBEAENG00500201002", pairs);
        model = gson.fromJson(result, MeetingViewResponseModel.class);
        return model;
    }
}
