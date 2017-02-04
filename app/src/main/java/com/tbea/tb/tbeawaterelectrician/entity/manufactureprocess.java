package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/2/3.
 */

public class ManuFactureProcess {
    public static final String ID = "_id";
    public static final String Processname = "processname";
    public static final String Department = "department";
    public static final String Processdate = "processdate";
    public static final String SuYuanId = "suyuanId";


    private String processname;
    private String department;
    private String processdate;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProcessname() {
        return processname;
    }

    public void setProcessname(String processname) {
        this.processname = processname;
    }

    public String getProcessdate() {
        return processdate;
    }

    public void setProcessdate(String processdate) {
        this.processdate = processdate;
    }
}
