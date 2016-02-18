package com.baoshi.mua.model.avos;

import com.avos.avoscloud.AVUser;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.model.PublicContract;
import com.baoshi.mua.model.orm.OrmUserContract;

import java.util.List;


public class User extends AVUser {

	public String getNickname() {
        return super.getString(OrmUserContract.NICKNAME);
    }

	public void setNickname(String nickname) {
        super.put(OrmUserContract.NICKNAME,nickname);
	}


	public List<AVImage> getAlbum() {
		return super.getList(PublicContract.IMAGES, AVImage.class);
	}

	public void setAlbum(List<AVImage> album) {
        super.addAll(PublicContract.IMAGES,album);
	}

	public String getSex() {
        return super.getString(OrmUserContract.SEX);
	}

	public void setSex(String sex) {
        super.put(OrmUserContract.SEX,sex);
	}

	public String getBirth() {
        return super.getString(OrmUserContract.BIRTH);
	}

	public void setBirth(String birth) {
        super.put(OrmUserContract.BIRTH,birth);
	}

	public String getHeadUrl() {
        return super.getString(OrmUserContract.HEADURL);
	}

	public void setHeadUrl(String headUrl) {
        super.put(OrmUserContract.HEADURL,headUrl);
	}

    public LocationInfo getLocationInfo(){
        String json = super.getString(OrmUserContract.LOCATIONINFO);

        return LocationInfo.toJava(json);
    }

    public void setLocationInfo(LocationInfo locationInfo){
        super.put(OrmUserContract.LOCATIONINFO,locationInfo.toString());
    }

}
