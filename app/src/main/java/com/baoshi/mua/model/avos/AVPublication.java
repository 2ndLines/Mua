package com.baoshi.mua.model.avos;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.baoshi.mua.model.Carrier;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.model.PublicContract;
import com.baoshi.mua.model.orm.OrmPublicationContract;

import java.util.Date;
import java.util.List;


@AVClassName("AVPublication")
public class AVPublication extends AVObject {
    public static final String KEY_FOLLOW = "key_follow";
    private Date deliveredAt;
    private List<String> imageList;

    public AVPublication(){}

    public AVPublication(User creator){
        setCreator(creator);
    }

    public String getCaption() {
        return super.getString(OrmPublicationContract.CAPTION);
    }

    public void setCaption(String caption) {
        super.put(OrmPublicationContract.CAPTION, caption);
    }

    public List<AVComment> getComments() {
        return super.getList(PublicContract.COMMENTS);
    }

    public void setComments(List<AVComment> comments) {
        super.addAll(PublicContract.COMMENTS, comments);
    }

    public void addComment(AVComment AVComment){
        super.add(PublicContract.COMMENTS, AVComment);
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageUris(List<String> imageList) {
        this.imageList = imageList;
    }

    public List<AVImage> getImages() {
        return super.getList(PublicContract.IMAGES,AVImage.class);
    }

    public void setImages(List<AVImage> images) {
        super.addAll(PublicContract.IMAGES, images);
    }

    public User getCreator() {
       return super.getAVUser(OrmPublicationContract.CREATOR,User.class);
    }

    public void setCreator(User creator) {
        super.put(OrmPublicationContract.CREATOR,creator);
    }

    public Carrier getCarrier() {
        String json = super.getString(OrmPublicationContract.CARRIER);
        return Carrier.toJava(json);
    }

    public void setCarrier(Carrier carrier) {
        super.put(OrmPublicationContract.CARRIER, carrier.toString());
    }

    public void setGeoPoint(double lat, double lng){
        super.put(OrmPublicationContract.GEOPOINT, new AVGeoPoint(lat,lng));
    }

    public AVGeoPoint getGeoPoint(){
        return super.getAVGeoPoint(OrmPublicationContract.GEOPOINT);
    }

    public void setDeliveredAt(Date date){
        this.deliveredAt = date;
    }

    public Date getDeliveredAt(){
        if(super.getCreatedAt() == null){
            return deliveredAt;
        }else{
            return super.getCreatedAt();
        }
    }

    public int getPraised() {
        return super.getInt(OrmPublicationContract.PRAISED);
    }

    public void addPraised() {
        super.increment(OrmPublicationContract.PRAISED);
    }

    public int getBrowsed() {
        return super.getInt(OrmPublicationContract.BROWSED);
    }

    public void addBrowsed(){
        super.increment(OrmPublicationContract.BROWSED);
    }

    public LocationInfo getLocationInfo() {
        String json = super.getString(OrmPublicationContract.LOCATIONINFO);
        return LocationInfo.toJava(json);
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        super.put(OrmPublicationContract.LOCATIONINFO, locationInfo.toString());
    }

    public void follow(){
        User currentUser = AVUser.getCurrentUser(User.class);
        if(currentUser != null) {
            AVRelation<User> relation = getRelation(KEY_FOLLOW);
            relation.add(currentUser);
        }
    }

    public void unfollow(){
        User currentUser = AVUser.getCurrentUser(User.class);
        if(currentUser != null) {
            AVRelation<User> relation = getRelation(KEY_FOLLOW);
            relation.remove(currentUser);
        }
    }
}
