package com.baoshi.mua.listener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.core.AMapLocException;

/**
 * Created by ThinkPad on 2014/11/21.
 */
public interface LocationCompleteListener {
    public void onLocationSuccess(AMapLocation location);
    public void onLocationFail(AMapLocException exception);
}
