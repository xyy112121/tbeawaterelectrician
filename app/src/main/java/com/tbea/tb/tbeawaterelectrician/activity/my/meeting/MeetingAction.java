package com.tbea.tb.tbeawaterelectrician.activity.my.meeting;

import com.tbea.tb.tbeawaterelectrician.activity.my.meeting.model.MeeingListResponseMode;
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
}
