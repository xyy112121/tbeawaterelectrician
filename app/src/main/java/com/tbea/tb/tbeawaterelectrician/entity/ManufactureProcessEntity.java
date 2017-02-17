package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/2/15.
 */

public class ManufactureProcessEntity {
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
