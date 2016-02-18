package com.baoshi.mua.helper;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.location.core.AMapLocException;
import com.amap.api.services.core.LatLonPoint;
import com.baoshi.mua.listener.LocationCompleteListener;
import com.baoshi.mua.utils.Lg;

/**
 * Created by ThinkPad on 2014/11/21.
 */
public class LocationHelper implements Runnable, AMapLocationListener {
    private static final int LOCATION_DISTANCE = 10;
    private Context context;
    private static LocationHelper instance;
    private LocationManagerProxy locationManager;
    private LocationCompleteListener completeListener;
    private LatLonPoint curPoint;
    private final Object mLock = new Object();
    private Handler myHandler;
    private AMapLocation currentLocation;

    public static LocationHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (LocationHelper.class){
                LocationHelper temp = instance;
                if(temp == null){
                    temp = new LocationHelper(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    private LocationHelper(Context context) {
        this.context = context;
    }

    public void setLocationCompleteListener(LocationCompleteListener listener) {
        completeListener = listener;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void run() {
        stopLocation();
    }

    /**
     * Locate once
     */
    public void updateLocationOnce() {
        updateLocation(-1);
    }

    public void updateLocation(long interval) {
        stopLocation();
        locationManager = LocationManagerProxy.getInstance(context);
        locationManager.requestLocationData(LocationProviderProxy.AMapNetwork, interval, LOCATION_DISTANCE, this);

        if (myHandler == null) {
            myHandler = new Handler();
        }
        myHandler.postDelayed(this, 10000);//Destroy location thread after 10s.
    }

    public void onStop() {
        stopLocation();
    }

    public void stopLocation() {
        synchronized (mLock) {
            if (locationManager != null) {
                locationManager.removeUpdates(this);
                locationManager.destroy();
            }
            locationManager = null;
            if(myHandler != null){
                myHandler.removeCallbacks(this);
            }

        }
    }

    public LatLonPoint getCurPoint() {
        return curPoint;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Lg.d("___location complete. Address:" + aMapLocation.getAddress());
        final AMapLocation location = aMapLocation;
        deliverResult(location);
    }

    @Deprecated
    @Override
    public void onLocationChanged(Location location) { }
    @Deprecated
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Lg.d("__location status = " + status);
    }
    @Deprecated
    @Override
    public void onProviderEnabled(String provider) { }
    @Deprecated
    @Override
    public void onProviderDisabled(String provider) { }

    private static final int CHECK_INTERVAL = 1000 * 30;
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
        boolean isNewer = timeDelta > 0;
Lg.d(String.format("__isSignificantlyNewer = %s,_isSignificantlyOlder=%s, _isNewer=%s",isSignificantlyNewer,isSignificantlyOlder,isNewer));
        // If it's been more than two minutes since the current location,
        // use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must
            // be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());
        Lg.d(String.format("\n isLessAccurate = %s,isMoreAccurate=%s, isSignificantlyLessAccurate=%s",isLessAccurate,isMoreAccurate,isSignificantlyLessAccurate));
        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void deliverResult(AMapLocation location){
        if (location.getAddress() != null && location.getAMapException().getErrorCode() == 0) {
            curPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
            if (completeListener != null) {
                completeListener.onLocationSuccess(location);
            }
        } else {
            handleLocationError(location.getAMapException());
            if (completeListener != null) {
                completeListener.onLocationFail(location.getAMapException());
            }
        }
        stopLocation();
    }

    /**
     * 0	正常
     * 21	IO 操作异常
     * 22	连接异常
     * 23	连接超时
     * 24	无效的参数
     * 25	空指针异常
     * 26	url 异常
     * 27	未知主机
     * 28	服务器连接失败
     * 29	协议解析错误
     * 30	http 连接失败
     * 31	未知的错误
     * 32	key 鉴权失败
     *
     * @param ex
     */
    private void handleLocationError(AMapLocException ex) {
        Lg.toast(getContext(), ex.getErrorMessage());
    }

}
