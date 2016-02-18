package com.baoshi.mua;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.baoshi.mua.activity.BaseActivity;
import com.baoshi.mua.helper.PubTypeHelper;
import com.baoshi.mua.model.avos.AVComment;
import com.baoshi.mua.model.avos.AVImage;
import com.baoshi.mua.model.avos.AVPublication;
import com.baoshi.mua.model.avos.AVUserProfile;
import com.baoshi.mua.model.avos.User;
import com.baoshi.mua.model.orm.OrmPublication;
import com.baoshi.mua.model.orm.OrmUser;
import com.baoshi.mua.utils.Lg;
import com.baoshi.mua.view.GlobalView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ThinkPad on 2014/11/4.
 */
public class App extends Application {
    public static final String HINT_IMAGE_NAME = "demo.jpg";
    private static App instance;
    private OrmPublication ormPublication;
    /*private static class SingletonHolder{
       public static final App instance = new App();
    }*/

    public static App getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLeanCloud();
        instance = this;
        localInit();

//        showGlobalWindow();
    }

    private void localInit(){
        //create default hint image in ComposeActivity
        new Thread(new Runnable() {
            @Override
            public void run() {
                cachePicHintToSD(HINT_IMAGE_NAME);
            }
        }).start();

        //Initialize publication type
        PubTypeHelper.getInstance().init(getApplicationContext());

        //Initialize image loader
        initImageLoader(getApplicationContext());
    }

    private void initLeanCloud() {
        AVObject.registerSubclass(AVPublication.class);
        AVObject.registerSubclass(AVImage.class);
        AVObject.registerSubclass(AVUserProfile.class);
        AVObject.registerSubclass(AVComment.class);
        AVOSCloud.initialize(getApplicationContext(), "8mk2k417yomq3pjrwhp56o7kbe4gjpe9vdldokhrbxdwv3g4",
                "npx9yb1w8jvo84ln1wwj3gtcrv75en9dmotqvitjb05z3bh9");
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.

        int[] thumbnailSize = generateThumbnailSize((int) getResources().getDimension(R.dimen.thumbnail_size));
        int[] photoSize = getBestSimpleSize();
        Lg.d(String.format("_thumbnail size[%d,%d], photoSize[%d, %d]", thumbnailSize[0], thumbnailSize[1], photoSize[0], photoSize[1]));
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(thumbnailSize[0], thumbnailSize[1]) //cache thumbnail or like thumbnail
                .diskCacheExtraOptions(photoSize[0], photoSize[1], null) // cache large photo,
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY)
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(5 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
//	        .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }

    public int[] getBestSimpleSize() {

        int[] size = new int[2];
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;

        size[1] = getSizeOf324(size[0]);

        return size;
    }

    public int[] generateThumbnailSize(int widthInDp) {
        Lg.d("__seed width = " + widthInDp);
        int dw = widthInDp;
        int dh = getSizeOf324(dw);

        return new int[]{dw, dh};

    }

    /**
     * @param size
     * @return the result : size = 3:4
     */
    private int getSizeOf324(int size) {
        return size * 4 / 3;
    }

    private int dp2Px(int dpValue) {

        final float density = getResources().getDisplayMetrics().density;

        return (int) (dpValue * density + 0.5f);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageLoader.getInstance().clearMemoryCache();
    }

    private void cachePicHintToSD(String fileName) {
        File file = new File(BaseActivity.getDataCacheDir(), fileName);
        if (!file.exists()) {
            InputStream is = getResources().openRawResource(R.raw.insert_pic_hint);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                is.close();
                fos.close();
                Lg.d("___Success to create hint image ? " + file.exists());
            } catch (FileNotFoundException e) {
                Lg.d("__FileNotFoundException__");
                e.printStackTrace();
            } catch (IOException e) {
                Lg.d("__IOException__");
                e.printStackTrace();
            }
        }
    }

    public File getHintImageFile(){
        return new File(BaseActivity.getDataCacheDir(),HINT_IMAGE_NAME);
    }

    public void handleLeanCloudException(AVException ex){
        if(ex == null) return;

        final int errorCode = ex.getCode();
        int resId = 0;

        switch (errorCode){
            case 127:
                resId = R.string.phone_number_is_invalid;
                break;
            case 210:
                resId = R.string.phone_pwd_dismatch;
                break;
            case 213:
                resId = R.string. phone_is_not_regesitered;
                break;
            case 214:
                resId = R.string.phone_number_has_token;
                break;
            case 215:
                resId = R.string. phone_number_is_not_verified;
                break;
            default:
                break;
        }

        if(resId != 0){
            Lg.toast(getApplicationContext(),resId);
        }else{
            Lg.toast(getApplicationContext(),new StringBuilder().append("code = ").append(ex.getCode())
                    .append(", message:").append(ex.getMessage()).toString());
        }

    }

    public void showGlobalWindow(){
        new GlobalView(getApplicationContext()).showWindow();
    }

    public OrmPublication getOrmPublication() {
        return ormPublication;
    }

    public void setOrmPublication(OrmPublication ormPublication) {
        this.ormPublication = ormPublication;
    }

    public OrmUser getCurrentOrmUser(){
        User user = AVUser.getCurrentUser(User.class);
        OrmUser ormUser = new OrmUser(user);

        return ormUser;
    }
}
