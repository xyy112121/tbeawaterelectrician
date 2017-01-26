package com.tbea.tb.tbeawaterelectrician.entity;

/**
 * Created by cy on 2017/1/23.首页页面上显示的数据类型子类
 */

public class HomeDateSon {
    private String  id;

    public class advertiselist{
        private String  id;
        private String name;
    }

    public class newmessage1{
        private String  id;
        private String name;

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

    public Newmessage2 newNewMessage2(){
        return new Newmessage2();
    }

    /**
     * 返利
     */
    public class Newmessage2{
        private String  id;
        private String name;
        private String user_id;
        private  String message;
        private String picture;
        private String username;
        private  String money;

        public void setId(String id) {
            this.id = id;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public String getMoney() {
            return money;
        }

        public String getName() {
            return name;
        }

        public String getPicture() {
            return picture;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getUsername() {
            return username;
        }
    }
}
