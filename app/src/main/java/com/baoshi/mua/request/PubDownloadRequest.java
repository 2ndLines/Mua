package com.baoshi.mua.request;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.baoshi.mua.model.PublicContract;
import com.baoshi.mua.model.avos.AVComment;
import com.baoshi.mua.model.avos.AVImage;
import com.baoshi.mua.model.avos.AVPublication;
import com.baoshi.mua.model.orm.OrmComment;
import com.baoshi.mua.model.orm.OrmCommentContract;
import com.baoshi.mua.model.orm.OrmImage;
import com.baoshi.mua.model.orm.OrmPubList;
import com.baoshi.mua.model.orm.OrmPublication;
import com.baoshi.mua.model.orm.OrmPublicationContract;
import com.baoshi.mua.utils.Lg;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.retry.RetryPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ThinkPad on 2014/12/4.
 */
public class PubDownloadRequest extends SpiceRequest<OrmPubList> {
    private static final String DOT = ".";

    public PubDownloadRequest(Class<OrmPubList>  clazz) {
        super(clazz);
    }

    @Override
    public RetryPolicy getRetryPolicy() {
//        return super.getRetryPolicy();
        return null;
    }

    private List<AVPublication> queryPublications() throws AVException {
        AVQuery<AVPublication> query = AVQuery.getQuery(AVPublication.class);
        query.orderByDescending(OrmPublicationContract.CREATEDAT);
        query.include(PublicContract.IMAGES);
        query.include(new StringBuilder(PublicContract.IMAGES).append(DOT)
                .append(PublicContract.COMMENTS).append(DOT)
                .append(OrmCommentContract.CREATOR).toString());

        query.include(new StringBuilder(PublicContract.COMMENTS).append(DOT).append(OrmCommentContract.CREATOR).toString());
        query.include(OrmPublicationContract.CREATOR);

        List<AVPublication> list = query.find();

        return list;
    }

    private <T> void fetchCommentsAndAttach(T host, List<AVComment> avComments){
        if(avComments != null) {
            for(AVComment comment : avComments) {
                OrmComment  ormComment = new OrmComment(comment);
                if(host instanceof AVPublication){
                    OrmPublication publication = (OrmPublication) host;
                    ormComment.setPublication(publication);
                    publication.addComments(ormComment);
                }else if(host instanceof OrmImage){
                    OrmImage ormImage = (OrmImage) host;
                    ormImage.addComment(ormComment);
                    ormComment.setImage(ormImage);
                }
            }
        }
    }

    private void fetchImagesAndAttach(OrmPublication ormPublication, AVPublication avPublication){
        if(avPublication != null) {
            Collection<AVImage> avImages = avPublication.getImages();

            if(avImages != null) {

                Lg.d("___avImage size = " + avImages.size());
                List<OrmImage>  ormImages = new ArrayList<OrmImage>();
                for(AVImage avImage : avImages) {
                    OrmImage ormImage = new OrmImage(avImage);
                    ormImage.setPublication(ormPublication);
                    fetchCommentsAndAttach(ormImage, avImage.getComments());

                    ormImages.add(ormImage);
                }
                ormPublication.setImages(ormImages);
            }
        }
    }

    @Override
    public OrmPubList loadDataFromNetwork() throws Exception {
        List<AVPublication> list = queryPublications();
        Lg.d("___AVPublication Size = " + list.size());
        if(list != null) {
            OrmPubList ormColl = new OrmPubList();
            Collection<OrmPublication> collection = new ArrayList<OrmPublication>();
            for(AVPublication avPublication : list){
                OrmPublication ormPublication = new OrmPublication(avPublication);
                fetchImagesAndAttach(ormPublication, avPublication);
                fetchCommentsAndAttach(ormPublication, avPublication.getComments());
                collection.add(ormPublication);
            }
            ormColl.setAllPubs(collection);
            return ormColl;
        }
        return null;
    }
}
