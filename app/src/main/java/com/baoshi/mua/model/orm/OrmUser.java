package com.baoshi.mua.model.orm;

import android.provider.BaseColumns;

import com.avos.avoscloud.AVGeoPoint;
import com.baoshi.mua.annotation.MuaContract;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.persister.GsonDataPersister;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

import java.util.Collection;

/**
 * Created by ThinkPad on 2014/12/4.
 */

@MuaContract
@AdditionalAnnotation.Contract
@DatabaseTable
public class OrmUser extends BaseDaoEnabled<OrmUser,String> {
    @DatabaseField(id = true,columnName = BaseColumns._ID)
    private String id;
    @DatabaseField
    private String nickname;
    @DatabaseField
    private String username;
    @DatabaseField
    private String phoneNumber;
    @DatabaseField
    private String sex;
    @DatabaseField
    private String birth;
    @DatabaseField
    private String headUrl;
    @DatabaseField(persisterClass = GsonDataPersister.class)
    private LocationInfo locationInfo;

    /**
     * All of delivered publications.
     * One to many.
     */
    @ForeignCollectionField
    private Collection<OrmPublication> delivered;

    @DatabaseField
    private AVGeoPoint geoPoint;

    public OrmUser(){
        //For ormlite
    }

    public OrmUser(String nickName){
        setNickname(nickName);
    }

    public OrmUser(User user){
        setId(user.getObjectId());
        setNickname(user.getNickname());
        setBirth(user.getBirth());

        setHeadUrl(user.getHeadUrl());
        setSex(user.getSex());
        setPhoneNumber(user.getMobilePhoneNumber());

        setUsername(user.getUsername());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }
}
