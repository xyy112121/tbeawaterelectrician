package com.tbea.tb.tbeawaterelectrician.entity;

import java.util.List;

/**
 * Created by cy on 2017/2/3.
 */

public class SuYuan {
    private String name;
    private String specifications;
    private String manudate;
    private String deliverdate;
    private String destination;
    private String manufacture;
    private List<Manufactureprocess> manufactureprocess;

    public List<Manufactureprocess> getManufactureprocess() {
        return manufactureprocess;
    }

    public void setManufactureprocess(List<Manufactureprocess> manufactureprocess) {
        this.manufactureprocess = manufactureprocess;
    }

    public String getDeliverdate() {
        return deliverdate;
    }

    public void setDeliverdate(String deliverdate) {
        this.deliverdate = deliverdate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getManudate() {
        return manudate;
    }

    public void setManudate(String manudate) {
        this.manudate = manudate;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }
}
