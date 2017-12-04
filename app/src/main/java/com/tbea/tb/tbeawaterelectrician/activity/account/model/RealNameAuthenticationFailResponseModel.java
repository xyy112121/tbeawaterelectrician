package com.tbea.tb.tbeawaterelectrician.activity.account.model;

import java.util.List;

/**
 * Created by programmer on 2017/12/4.
 */

public class RealNameAuthenticationFailResponseModel {
    public List<FailedreasonBean> failedreasonlist;

    public class FailedreasonBean {
        public String reason;
    }
}
