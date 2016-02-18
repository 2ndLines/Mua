package com.baoshi.mua.model.orm;

import android.provider.BaseColumns;

import com.baoshi.mua.annotation.MuaContract;
import com.baoshi.mua.model.avos.AVImage;
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
public class OrmImage extends BaseDaoEnabled<OrmImage, String> {
    @DatabaseField(id = true, columnName = BaseColumns._ID)
    private String id;

    @DatabaseField(foreign = true)
    private OrmPublication publication;

    @ForeignCollectionField(eager = true)
    private Collection<OrmComment> comments;

    @DatabaseField
    private String photoUri;

    @DatabaseField
    private String thumbUrl;

    @DatabaseField
    private int praised;

    public OrmImage(){
        //For Ormlite
    }

    public OrmImage(String uri){
        setPhotoUri(uri);
    }

    public OrmImage(AVImage avImage){
        if(avImage != null) {
            setId(avImage.getObjectId());
            setPhotoUri(avImage.getPhotoUri());
            setThumbUrl(avImage.getThumbnail());
            setPraised(avImage.getPraised());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrmPublication getPublication() {
        return publication;
    }

    public void setPublication(OrmPublication publication) {
        this.publication = publication;
    }

    public Collection<OrmComment> getComments() {
        return comments;
    }

    public void setComments(Collection<OrmComment> comments) {
        this.comments = comments;
    }

    public void addComment(OrmComment comment){
        comments.add(comment);
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public int getPraised() {
        return praised;
    }

    public void setPraised(int praised) {
        this.praised = praised;
    }
}
