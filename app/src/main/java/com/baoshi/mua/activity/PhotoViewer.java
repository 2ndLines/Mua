package com.baoshi.mua.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;

import com.baoshi.mua.App;
import com.baoshi.mua.R;
import com.baoshi.mua.utils.Lg;
import com.baoshi.mua.view.CheckableImageView;
import com.baoshi.mua.view.PopupButtonList;
import com.baoshi.mua.view.ProgressWheel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

//import android.view.View;

/**
 * Created by ThinkPad on 2014/11/12.
 */
public class PhotoViewer extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener{
    public static final int REQUEST_CODE_PREVIEW_PHOTO = 100;

    private int initPos;
//    private HashMap<String, Bitmap> uriBmpMap;
    private PhotoPagerAdapter adapter;
    private boolean isFromNetwork = false;
    private ViewPager viewPager;
//    private List<String> selectedData;

    private PopupButtonList buttonList;
    private Button buttonDone;
    private HashMap<String, Boolean> selectedMap = new HashMap<String, Boolean>();
    private CheckableImageView toggle;
    private MediaScannerConnection mediaScanner;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        Lg.d("__onNewIntent()__");
    }

    private void handleIntent(Intent intent){
        List<String> list = null;
        if (intent != null) {
            initPos = intent.getIntExtra(Constant.KEY_INIT_POSITION, 0);
            list = intent.getStringArrayListExtra(Constant.KEY_URIS);

            isFromNetwork = intent.getBooleanExtra(Constant.KEY_URI_TYPE_IS_REMOTE,false);

        }

        if(list != null){
            setSelectedMap(list);
            ViewPager pager = (ViewPager) findViewById(R.id.photo_pager);
            pager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.viewpager_margin));
            adapter = new PhotoPagerAdapter(R.layout.layout_photo, list);
            pager.setAdapter(adapter);
            pager.setCurrentItem(initPos);
            if(list.size() > 2){
                pager.setOffscreenPageLimit(3);
            }
            viewPager = pager;
            if(isFromNetwork){
                initMediaScanner();
            }else{
                initBottomBar();
            }
            pager.setOnPageChangeListener(this);
        }
    }

    private void setSelectedMap(Collection<String> collection){
        selectedMap.clear();
        for(String uri : collection){
            selectedMap.put(uri,true);
        }

    }

    private void initMediaScanner(){
        if(mediaScanner == null) {
            mediaScanner = new MediaScannerConnection(this,null);
        }
        mediaScanner.connect();
    }

    private PopupButtonList createButtonList() {
//        View view = getLayoutInflater().inflate(R.layout.layout_popup_button_list, null, false);
        PopupButtonList buttons = new PopupButtonList.Builder(this)
                .addButton(R.string.button_save, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveBitmapToFile(adapter.getCurrentBitmap());
                    }
                }).addButton(R.string.button_trnsmit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).addButton(R.string.button_favorite, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).addButton(R.string.button_complain, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).build();

        return buttons;
    }

    private void showButtonList(){
        if(buttonList == null){
            buttonList = createButtonList();
        }

        buttonList.show(getWindow().getDecorView());
    }

    private void saveBitmapToFile(Bitmap bitmap){
        if(bitmap == null){
            Lg.e("___bitmap is null");
            return;
        }

        List<String> allImages = adapter.getData();
        String url = allImages.get(viewPager.getCurrentItem());

        //Name by url hash code to avoid to save it repeatedly.
        String name = new StringBuilder("IMG").append(url.hashCode()).append(".jpg").toString();

        File imageFile = new File(BaseActivity.getDataCacheDir() ,name);
        if(imageFile.exists()){
           Lg.toast(this,R.string.image_had_been_saved);
        }else{
            try {
                imageFile.createNewFile();
                FileOutputStream os = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
                os.flush();
                os.close();
                mediaScanner.scanFile(this,new String[]{imageFile.getPath()}, new String[]{"image/jpg"},
                        new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(final String path, final Uri uri) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (uri != null) {
                                    Lg.toast(PhotoViewer.this,getString(R.string.success_to_save_image, path));
                                }else{
                                    Lg.toast(PhotoViewer.this,R.string.fail_to_save_image);
                                }
                            }
                        });
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Lg.toast(this,R.string.fail_to_save_image);
            } catch (IOException e) {
                e.printStackTrace();
                Lg.toast(this,R.string.fail_to_save_image);
            }
        }
    }

    private void initBottomBar(){
        View bar = ((ViewStub) findViewById(R.id.viewstub_bottom_bar)).inflate();
        toggle = (CheckableImageView) bar.findViewById(R.id.toggle);
        toggle.setVisibility(View.VISIBLE);
        toggle.setChecked(true);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = adapter.getItem(viewPager.getCurrentItem());
                selectedMap.put(uri, toggle.isChecked());
            }
        });
    }

    public void onDoneClick(View view){
        Intent intent = new Intent();
        intent.putStringArrayListExtra(PhotoChooser.SELECTED_KEY, new ArrayList<String>(getSelectedData()));
        setResult(Activity.RESULT_OK, intent);
        PhotoViewer.this.finish();
    }

    private List<String> getSelectedData(){
        if(selectedMap != null ){
            List<String> data = new ArrayList<String>();
            for(String uri : selectedMap.keySet()){
                if(selectedMap.get(uri)){
                    data.add(uri);
                }
            }
            return data;
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        handleIntent(getIntent());
        Lg.d("__onCreate__");

    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter != null){
            adapter.cleanupAttacher();
        }

        if(mediaScanner != null) {
            mediaScanner.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id){
            case R.id.button_done:
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int position) {
        String uri = adapter.getItem(position);
        boolean selected = selectedMap.get(uri);
        if(toggle != null) {
            toggle.setChecked(selected);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class PhotoPagerAdapter extends PagerAdapter {
        private List<String> urlSet;
        private int layoutId;
        private DisplayImageOptions options;
        private PhotoViewAttacher attacher;

        public PhotoPagerAdapter(int _resId, List<String> list) {
            this.layoutId = _resId;

            urlSet = new ArrayList<String>(list);
            options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .build();
        }

        public List<String> getData(){
            return urlSet;
        }

        @Override
        public int getCount() {
            if (urlSet != null) {
                return urlSet.size();
            }
            return 0;
        }

        public Bitmap getCurrentBitmap(){
            return attacher.getImageView().getDrawingCache();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View rootView = LayoutInflater.from(getApplicationContext()).inflate(layoutId, container, false);
            assert rootView != null;
            final PhotoView pv = (PhotoView) rootView.findViewById(R.id.photo_view);
            attacher = new PhotoViewAttacher(pv);

            attacher.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);
                    showButtonList();
                    return true;
                }
            });
            final ProgressWheel wheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
            String url = urlSet.get(position);

            App app = (App) getApplication();
            int[] simpleSize = app.getBestSimpleSize();

            ImageSize imageSize = new ImageSize(simpleSize[0],simpleSize[1]);
            ImageLoader.getInstance().loadImage(url, imageSize, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    wheel.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    wheel.setVisibility(View.GONE);
                    Lg.toast(getApplicationContext(), "Fail to download photo!!");
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Lg.d(String.format("loadedImage size=[%s, %s]", loadedImage.getWidth(), loadedImage.getHeight()));
                    pv.setImageBitmap(loadedImage);
                    attacher.update();
                    wheel.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    wheel.setVisibility(View.GONE);
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    wheel.setProgress(360 * current / total);
                }
            });
            container.addView(rootView, 0);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view.equals(o);
        }

        public void cleanupAttacher(){
            if(attacher != null){
                attacher.cleanup();
            }
        }

        public String getItem(int position){
            return urlSet.get(position);
        }
    }

    public class Constant{
        /**
         * Initial position of viewpager
         */
        public static final String KEY_INIT_POSITION = "position";

        public static final String KEY_URIS = "uris";

        /**
         * download from network
         */
        public static final String KEY_URI_TYPE_IS_REMOTE = "uri_is_url";

        /**
         * upload from local.
         */
        public static final String ACTION_UPLOAD_OR_NOT = "action.add.or.remove";
    }
}
