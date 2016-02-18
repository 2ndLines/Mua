package com.baoshi.mua.model;

import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.utils.Lg;
import com.google.gson.Gson;

/**
 * Created by ThinkPad on 2014/11/8.
 */
public class Creator {
    private String objectId;
    private String nickname;
    private String headUrl;
    private String phoneNumber;

    public Creator(User user){
        if(user != null) {
            setObjectId(user.getObjectId());
            setNickname(user.getNickname());
            setPhoneNumber(user.getMobilePhoneNumber());
            setHeadUrl(user.getHeadUrl());
        }
        Lg.d("____Creator Info = " + this.toString());
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }

    public static Creator toJava(String json){
        return new Gson().fromJson(json, Creator.class);
    }
}
