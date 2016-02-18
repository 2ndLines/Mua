package com.baoshi.mua.listener;

import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

/**
 * Created by ThinkPad on 2014/11/21.
 */
public interface PoiSearchCompleteListener {
    public void onPoiSearchComplete(PoiSearch.Query query, PoiResult result, int resultCode);
}
