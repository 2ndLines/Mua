package com.baoshi.mua.model.orm;

import android.provider.BaseColumns;

import com.baoshi.mua.annotation.MuaContract;
import com.baoshi.mua.model.Creator;
import com.baoshi.mua.model.avos.AVComment;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.persister.GsonDataPersister;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

import java.util.Date;

/**
 * Created by ThinkPad on 2014/12/4.
 */
@MuaContract
@AdditionalAnnotation.Contract
@DatabaseTable
public class OrmComment extends BaseDaoEnabled<OrmComment,String> {

    @DatabaseField(id = true,columnName = BaseColumns._ID)
    private String id;

    @DatabaseField(foreign = true)
    private OrmPublication publication;

    @DatabaseField(foreign = true)
    private OrmImage image;

    @DatabaseField(persisterClass = GsonDataPersister.class)
    private Creator creator;

    @DatabaseField
    private String replyTo;

    @DatabaseField
    private String content;

    @DatabaseField(dataType = DataType.DATE)
    private Date createdAt;

    public OrmComment(){
        //For Ormlite
    }

    public OrmComment(AVComment avComment){
        if(avComment != null) {
            setId(avComment.getObjectId());

            User user = avComment.getCreator();
            setCreator(new Creator(user));

            setCreatedAt(avComment.getCreatedAt());
            setReplyTo(avComment.getReplyTo());
            setContent(avComment.getContent());
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

    public OrmImage getImage() {
        return image;
    }

    public void setImage(OrmImage image) {
        this.image = image;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

}
