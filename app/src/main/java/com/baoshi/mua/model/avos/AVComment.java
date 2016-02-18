package com.baoshi.mua.model.avos;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.baoshi.mua.model.orm.OrmCommentContract;

@AVClassName("AVComment")
public class AVComment extends AVObject {
    public AVComment(){}

    public AVPublication getPublication() {
        try {
            return super.getAVObject(OrmCommentContract.PUBLICATION,AVPublication.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setPublication(AVPublication publication) {
        super.addUnique(OrmCommentContract.PUBLICATION, publication);
    }

    public User getCreator() {
        return super.getAVUser(OrmCommentContract.CREATOR, User.class);
    }

    public void setCreator(User creator) {
        super.put(OrmCommentContract.CREATOR,creator);
    }

    public void setImage(AVImage image){
        super.put(OrmCommentContract.IMAGE, image);
    }

    public AVImage getImage(){
        try {
            return super.getAVObject(OrmCommentContract.IMAGE,AVImage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getReplyTo() {
        return super.getString(OrmCommentContract.REPLYTO);
    }

    public void setReplyTo(String replyTo) {
        super.put(OrmCommentContract.REPLYTO,replyTo);
    }

    public String getContent() {
        return super.getString(OrmCommentContract.CONTENT);
    }

    public void setContent(String content) {
        super.put(OrmCommentContract.CONTENT,content);
    }

}
