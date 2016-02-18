package com.baoshi.mua.model.orm;

import android.provider.BaseColumns;

import com.avos.avoscloud.AVGeoPoint;
import com.baoshi.mua.annotation.MuaContract;
import com.baoshi.mua.model.Carrier;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.model.avos.AVPublication;
import com.baoshi.mua.persister.GsonDataPersister;
import com.baoshi.mua.provider.MuaDatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by ThinkPad on 2014/12/4.
 */
@MuaContract
@AdditionalAnnotation.Contract
@DatabaseTable
public class OrmPublication extends BaseDaoEnabled<OrmPublication, String> {
    private static final String FOLLOWERS_ID_FIELD_NAME = "followers_id";

    private List<String> uriList;

    @DatabaseField(id = true, columnName = BaseColumns._ID)
    private String id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true )
    private OrmPubList ormPubList;

    @DatabaseField(foreign = true)
    private OrmUser creator;

    /*@DatabaseField(foreign = true)
    private OrmUser follower;
    *//**
     * All of followers
     * Many to many.
     *//*
    @ForeignCollectionField
    private Collection<OrmUser> followers;*/

    @ForeignCollectionField(eager = true)
    private Collection<OrmImage> images;

    @ForeignCollectionField
    private Collection<OrmComment> comments;

    @DatabaseField
    private String caption;

    @DatabaseField
    private int praised;

    @DatabaseField
    private int browsed;

    @DatabaseField(persisterClass = GsonDataPersister.class)
    private AVGeoPoint geoPoint;

    @DatabaseField(persisterClass = GsonDataPersister.class)
    private LocationInfo locationInfo;

    @DatabaseField(persisterClass = GsonDataPersister.class)
    private Carrier carrier;

    @DatabaseField
    private Date createdAt;

    public OrmPublication(){
        //For Ormlite
    }

    public OrmPublication(OrmUser creator){
        setCreator(creator);
    }

    public OrmPublication(AVPublication publication){
        setId(publication.getObjectId());
        OrmUser creator = new OrmUser(publication.getCreator());
        setCreator(creator);

        setCaption(publication.getCaption());
        setCreatedAt(publication.getDeliveredAt());
        setBrowsed(publication.getBrowsed());
        setPraised(publication.getPraised());
        setCarrier(publication.getCarrier());
        setGeoPoint(publication.getGeoPoint());
        setLocationInfo(publication.getLocationInfo());
        setImages(convertToOrmImage(publication.getImageList()));
        //TODO appending operation
//        setComments();
//        setFollowers();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrmUser getCreator() {
        return creator;
    }

    public void setCreator(OrmUser creator) {
        this.creator = creator;
    }

    private Collection<OrmImage> convertToOrmImage(Collection<String> avImages){
        if(avImages != null) {
            Collection<OrmImage> ormImages = new ArrayList<OrmImage>();
            for(String uri : avImages){
                ormImages.add(new OrmImage(uri));
            }
            return ormImages;
        }
        return null;
    }

    public Collection<OrmImage> getImages() {
        return images;
    }

    public void setImages(Collection<OrmImage> images) {
        this.images = images;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Collection<OrmComment> getComments() {
        return comments;
    }

    public void setComments(Collection<OrmComment> comments) {
        this.comments = comments;
    }

    public void addComments(OrmComment comment){
        comments.add(comment);
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getThumbUris(){
        Collection<OrmImage> ormImages = getImages();
        if(ormImages != null) {
            List<String> thumbs = new ArrayList<String>();

            for(OrmImage image : ormImages){
                String uri = image.getThumbUrl();
                if( uri == null) {
                    uri = image.getPhotoUri();
                }
                thumbs.add(uri);
            }
            return thumbs;
        }
        return null;
    }

    public List<String> getPhotoUrls(){
        Collection<OrmImage> ormImages = getImages();
        if(ormImages != null) {
            List<String> urls = new ArrayList<String>();
            for(OrmImage image : ormImages){
                urls.add(image.getPhotoUri());
            }

            return urls;
        }
        return null;
    }

    public int getPraised() {
        return praised;
    }

    public void setPraised(int praised) {
        this.praised = praised;
    }

    public int getBrowsed() {
        return browsed;
    }

    public void setBrowsed(int browsed) {
        this.browsed = browsed;
    }

    public AVGeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(AVGeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public void follow(MuaDatabaseHelper helper, OrmUser user){
        try {
            Dao pubUserDao = helper.getDao(OrmPubFollow.class);
            OrmPubFollow follow = new OrmPubFollow(user, this);
            pubUserDao.createOrUpdate(follow);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unfollow(MuaDatabaseHelper helper, OrmUser user){
        try {
            Dao pubUserDao = helper.getDao(OrmPubFollow.class);
            OrmPubFollow follow = new OrmPubFollow(user, this);
            pubUserDao.delete(follow);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OrmUser> getFollowers(MuaDatabaseHelper helper){
        try {
            PreparedQuery preparedQuery = makeUsersForPublicationQuery(helper);
            preparedQuery.setArgumentHolderValue(0,this);

            Dao userDao = helper.getDao(OrmUser.class);
            List<OrmUser> followers = userDao.query(preparedQuery);

            return followers;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private PreparedQuery<OrmUser> makeUsersForPublicationQuery(MuaDatabaseHelper helper) throws SQLException {

        Dao<OrmPubFollow, Integer> pubUserDao = helper.getDao(OrmPubFollow.class);
        QueryBuilder<OrmPubFollow,Integer> pubUserQB = pubUserDao.queryBuilder();
        pubUserQB.selectColumns(OrmPubFollow.USER_ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        pubUserQB.where().eq(OrmPubFollow.PUBLICATION_ID_FIELD_NAME, selectArg);

        Dao<OrmUser, String> userDao = helper.getDao(OrmUser.class);
        QueryBuilder<OrmUser, String> userQb = userDao.queryBuilder();
        userQb.where().in(OrmPublicationContract._ID,pubUserQB);

        return userQb.prepare();
    }

}

