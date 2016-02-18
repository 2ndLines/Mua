package com.baoshi.mua.request;

import com.avos.avoscloud.AVException;
import com.baoshi.mua.model.avos.AVImage;
import com.baoshi.mua.model.avos.AVPublication;
import com.baoshi.mua.utils.Lg;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.retry.RetryPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ThinkPad on 2014/11/29.
 */
public class PubUploadRequest extends SpiceRequest<Void> {
    private AVPublication publication;
    private int[] thumbnailSize;

    public PubUploadRequest(AVPublication publication, int[] thumbnailSize) {
        this(null);

        this.publication = publication;
        this.thumbnailSize = thumbnailSize;
    }

    public PubUploadRequest(Class<Void> clazz) {
        super(clazz);
    }

    @Override
    public RetryPolicy getRetryPolicy() {
//        return super.getRetryPolicy();
        return null;// do not retry
    }

    private void deleteImageIfFailToSave(Collection<AVImage> avImages) {
        if (avImages != null) {
            for (AVImage avImage : avImages) {
                if (avImage.getPhotoFile() != null) {
                    avImage.getPhotoFile().deleteEventually();
                }
                avImage.deleteEventually();
            }
        }
    }

    private void savePublication(AVPublication publication) {
        List<String> uris = publication.getImageList();
        List<AVImage> avImages = null;
        boolean allToSave = true;
        if (uris != null) {
            //Save images
             avImages = new ArrayList<AVImage>();
            for (String uri : uris) {
                AVImage avImage = new AVImage(uri);
                if (avImage.uploadPhoto(thumbnailSize[0], thumbnailSize[1])) {
                    try {
                        avImage.save();
                        avImages.add(avImage);
                    } catch (AVException e) {
                        e.printStackTrace();
                        allToSave = false;
                        break;
                    }
                }
            }
            publication.setImages(avImages);
        }
        //Save publication
        if(allToSave) {
            try {
                Lg.d("___start save publication___");
                publication.save();
            } catch (AVException e) {
                Lg.d("___Fail to save publication___ e = "+ e.getMessage());
                e.printStackTrace();
                deleteImageIfFailToSave(avImages);
            }
            Lg.d("___End save publication___");
        }else{
            deleteImageIfFailToSave(avImages);
        }

    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        savePublication(publication);
        return null;
    }
}
