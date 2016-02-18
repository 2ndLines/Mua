package com.baoshi.mua.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.services.core.LatLonPoint;
import com.google.gson.Gson;

/**
 * Created by ThinkPad on 2014/11/6.
 */
public class LocationInfo implements Parcelable{
    public static final String KEY = "location_info";

    double lat;
    double lng;
//    LatLonPoint point;
    String province;
    String city;
    String district;
    String title;
    String address;

    public LocationInfo(){}

    public LatLonPoint getPoint() {
        return new LatLonPoint(lat,lng);
    }

    public void setPoint(LatLonPoint point) {
        lat = point.getLatitude();
        lng = point.getLongitude();
//        this.point = point;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static LocationInfo toJava(String json){
        return new Gson().fromJson(json,LocationInfo.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public LocationInfo(Parcel in ){
        title = in.readString();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public static final Creator<LocationInfo> CREATOR = new Creator<LocationInfo>() {
        @Override
        public LocationInfo createFromParcel(Parcel source) {
            return new LocationInfo(source);
        }

        @Override
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };
}
