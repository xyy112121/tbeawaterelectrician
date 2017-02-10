package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * 消息
 */

public class MessageCategory {
    private String id;
    private String picture;
    private String newcount;
    private String name;
    private String lasttime;
    private String lastmessagetitle;
    private String question;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastmessagetitle() {
        return lastmessagetitle;
    }

    public void setLastmessagetitle(String lastmessagetitle) {
        this.lastmessagetitle = lastmessagetitle;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewcount() {
        return newcount;
    }

    public void setNewcount(String newcount) {
        this.newcount = newcount;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
