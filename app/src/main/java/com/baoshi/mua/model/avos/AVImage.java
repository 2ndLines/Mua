package com.baoshi.mua.model.avos;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.baoshi.mua.App;
import com.baoshi.mua.model.PublicContract;
import com.baoshi.mua.model.orm.OrmImageContract;
import com.baoshi.mua.utils.Lg;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.ByteArrayOutputStream;
import java.util.List;

@AVClassName("AVPubImage")
public class AVImage extends AVObject {
    public static final String KEY_FILE = "photo_file";
    public AVImage(){
        //For avos cloud
    }

    public AVImage(String photoUri) {
        setPhotoUri(photoUri);
    }

    public AVPublication getPublication() {
        try {
            return super.getAVObject(OrmImageContract.PUBLICATION, AVPublication.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPublication(AVPublication publication) {
        super.add(OrmImageContract.PUBLICATION, publication);
    }

    public String getThumbnail() {
        return super.getString(OrmImageContract.THUMBURL);
    }

    public void setThumbnail(String thumbnail) {
        super.put(OrmImageContract.THUMBURL,thumbnail);
    }

    public String getPhotoUri() {
        return super.getString(OrmImageContract.PHOTOURI);
    }

    public void setPhotoUri(String photoUri) {
        super.put(OrmImageContract.PHOTOURI,photoUri);
    }


    public List<AVComment> getComments() {
        return super.getList(PublicContract.COMMENTS,AVComment.class);
    }

    public void setComments(List<AVComment> comments) {
        super.addAll(PublicContract.COMMENTS, comments);
    }

    public void addComment(AVComment comment){
        super.add(PublicContract.COMMENTS,comment);
    }

    public int getPraised() {
        return super.getInt(OrmImageContract.PRAISED);
    }

    public void addPraised() {
        super.increment(OrmImageContract.PRAISED);
    }

    public void setPhotoFile(AVFile file){
        super.put(KEY_FILE, file);
    }

    public AVFile getPhotoFile(){
        return super.getAVFile(KEY_FILE);
    }

    public boolean uploadPhoto(int thumbnailWidth, int thumbnailHeight) {
        String uri = getPhotoUri();
        if (uri != null) {
            byte[] bitmap = bitmapToBytes(uri);
            AVFile file = new AVFile(String.valueOf(uri.hashCode()), bitmap);
            try {
                file.save();
                String url = file.getUrl();
                setPhotoUri(url);

                String thumbnail = file.getThumbnailUrl(false,thumbnailWidth, thumbnailHeight);
                setThumbnail(thumbnail);

                Lg.d(String.format("___upload photo! thumbnail = %s,\n ______URL = %s \n",thumbnail,url));

                setPhotoFile(file);
                return true;

            } catch (AVException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private byte[] bitmapToBytes(String uri){
        int[] size = App.getInstance().getBestSimpleSize();

        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(uri, new ImageSize(size[0],size[1]));

        if(bitmap != null) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,bao);
            int quality = 80;
            while(bao.toByteArray().length/1024 > 100){
                bao.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG,quality,bao);
                quality -= 10;
            }
Lg.d("___bitmap size = " + bao.toByteArray().length/1024 +"kb");
            return bao.toByteArray();
        }

        return null;
    }
}
