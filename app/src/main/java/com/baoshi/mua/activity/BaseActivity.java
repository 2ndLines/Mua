package com.baoshi.mua.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.baoshi.mua.provider.MuaDatabaseHelper;
import com.baoshi.mua.service.LeanCloudSpiceService;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.octo.android.robospice.SpiceManager;

import java.io.File;

/**
 * Base class to use for activities in Android.
 *
 * You can simply call {@link #getHelper()} to get your helper class, or {@link #getConnectionSource()} to get a
 * {@link ConnectionSource}.
 *
 * The method {@link #getHelper()} assumes you are using the default helper factory -- see {@link OpenHelperManager}. If
 * not, you'll need to provide your own helper instances which will need to implement a reference counting scheme. This
 * method will only be called if you use the database, and only called once for this activity's life-cycle. 'close' will
 * also be called once for each call to createInstance.
 *
 */
public abstract class BaseActivity extends ActionBarActivity {

    private SpiceManager spiceManager = new SpiceManager(LeanCloudSpiceService.class);
    private volatile MuaDatabaseHelper helper;
    private volatile boolean created = false;
    private volatile boolean destroyed = false;
    private static Logger logger = LoggerFactory.getLogger(BaseActivity.class);
    protected HostCallback callback;

    public interface HostCallback{
        public void callback();
    }

    public SpiceManager getSpiceManager(){
        return spiceManager;
    }

    /**
     * Get a helper for this action.
     */
    public MuaDatabaseHelper getHelper() {
        if (helper == null) {
            if (!created) {
                throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
            } else if (destroyed) {
                throw new IllegalStateException(
                        "A call to onDestroy has already been made and the helper cannot be used after that point");
            } else {
                throw new IllegalStateException("Helper is null for some unknown reason");
            }
        } else {
            return helper;
        }
    }

    public void hideSoftIM(View focusView){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(focusView != null){
            imm.hideSoftInputFromInputMethod(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setHostCallback(HostCallback cb){
        callback = cb;
    }

    /**
     * Get a connection source for this action.
     */
    public ConnectionSource getConnectionSource() {
        return getHelper().getConnectionSource();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        if (helper == null) {
            helper = getHelperInternal(this);
            created = true;
        }
    }

    protected void initActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!spiceManager.isStarted()){
            spiceManager.start(getApplicationContext());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseHelper(helper);
        destroyed = true;

        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
    }

    /**
     * This is called internally by the class to populate the helper object instance. This should not be called directly
     * by client code unless you know what you are doing. Use {@link #getHelper()} to get a helper instance. If you are
     * managing your own helper creation, override this method to supply this activity with a helper instance.
     *
     * <p>
     * <b> NOTE: </b> If you override this method, you most likely will need to override the
     * {@link #releaseHelper(MuaDatabaseHelper)} method as well.
     * </p>
     */
    protected MuaDatabaseHelper getHelperInternal(Context context) {
        @SuppressWarnings({ "unchecked", "deprecation" })
        MuaDatabaseHelper newHelper = (MuaDatabaseHelper) OpenHelperManager.getHelper(context,MuaDatabaseHelper.class);
        logger.trace("{}: got new helper {} from OpenHelperManager", this, newHelper);
        return newHelper;
    }

    /**
     * Release the helper instance created in {@link #getHelperInternal(Context)}. You most likely will not need to call
     * this directly since {@link #onDestroy()} does it for you.
     *
     * <p>
     * <b> NOTE: </b> If you override this method, you most likely will need to override the
     * {@link #getHelperInternal(Context)} method as well.
     * </p>
     */
    protected void releaseHelper(MuaDatabaseHelper helper) {
        OpenHelperManager.releaseHelper();
        logger.trace("{}: helper {} was released, set to null", this, helper);
        this.helper = null;
    }

    public static File getDataCacheDir(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"Mua");
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    public static final File getImageCacheDir(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }
}
