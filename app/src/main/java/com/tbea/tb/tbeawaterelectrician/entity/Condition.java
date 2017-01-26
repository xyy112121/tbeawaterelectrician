package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * 附近的条件实体
 */

public class Condition {
    private String  id;
    private String name;

    public Condition (String id,String name){
        this.id = id;
        this.name = name;
    }

    public  Condition (){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
