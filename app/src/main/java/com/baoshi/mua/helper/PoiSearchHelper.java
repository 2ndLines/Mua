package com.baoshi.mua.helper;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.core.AMapLocException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.baoshi.mua.listener.LocationCompleteListener;
import com.baoshi.mua.listener.PoiSearchCompleteListener;
import com.baoshi.mua.utils.Lg;

/**
 * Created by ThinkPad on 2014/11/21.
 */
public class PoiSearchHelper implements LocationCompleteListener, PoiSearch.OnPoiSearchListener {
    private static final int SEARCH_DISTANCE = 1000;
    private static final int SEARCH_COUNTRY_WIDE = -1;
    private static final int SIZE_PER_PAGE = 20;
    private static final int NO_FRESH = -2;
    private static final int NO_MORE = -1;
    private final static Object mLock = new Object();
    private static PoiSearchHelper instance;
    private LocationHelper locationHelper;
    private PoiSearchCompleteListener completeListener;

    private PoiSearch poiSearch;
    private PoiSearch.Query query;
    private int pageIndex = 0;
    private int curPageIndex = pageIndex;
    private boolean hasNext, hasPrevious;

    public static PoiSearchHelper getInstance(Context context) {
        if(instance == null){
            synchronized (PoiSearchHelper.class) {
                PoiSearchHelper temp = instance;

                if (temp == null) {
                    temp = new PoiSearchHelper(context);
                    instance = temp;
                }

            }
        }
        return instance;
    }

    private PoiSearchHelper(Context context) {
        locationHelper = LocationHelper.getInstance(context);
        locationHelper.setLocationCompleteListener(this);
    }

    private Context getContext() {
        return locationHelper.getContext();
    }

    public int getCurPageIndex() {
        return curPageIndex;
    }

    public void setCurPageIndex(int cuPageIndex) {
        this.curPageIndex = cuPageIndex;
    }

    public void setOnSearchCompleteListener(PoiSearchCompleteListener listener) {
        completeListener = listener;
    }

    public void searchByKeyword(String keyword, String city) {
        initQueryAndSearch(keyword, "", city, null, SEARCH_COUNTRY_WIDE);
    }

    /**
     * Locate once
     */
    public void searchAround() {
        searchAround(1000);//Do location every 1s until location success.
    }

    /**
     * @param interval Auto locate every <b>interval</b> ms.
     */
    public void searchAround(long interval) {
        locationHelper.updateLocation(interval);
    }

    private void searchNearby(String keyword, String city, LatLonPoint point) {
        initQueryAndSearch(keyword, "", city, point, SEARCH_DISTANCE);
    }

    public void reset() {
        locationHelper.stopLocation();
        setCurPageIndex(0);
        if (poiSearch != null) {
            poiSearch.setOnPoiSearchListener(null);
            poiSearch = null;
        }
        query = null;
        hasNext = hasPrevious = false;
    }

    /**
     * @param keyword    Search key
     * @param searchType which type to search
     * @param city       In which city to search, null mean to search country wild.
     * @param point      search center, work with param distance.
     * @param distance   search radius, work with param point.
     */
    public void initQueryAndSearch(String keyword, String searchType, String city, LatLonPoint point, int distance) {
        reset();
        PoiSearch.Query query = new PoiSearch.Query(keyword, searchType, city);
        query.setPageSize(SIZE_PER_PAGE);
        query.setPageNum(getCurPageIndex());
        this.query = query;
        PoiSearch search = new PoiSearch(locationHelper.getContext(), query);
        if (point != null) {
            search.setBound(new PoiSearch.SearchBound(point, distance));
        }
        search.setOnPoiSearchListener(this);
        poiSearch = search;
        search.searchPOIAsyn();

    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void previousPage() {
        if (poiSearch != null && query != null && hasPrevious) {

            query.setPageNum(--pageIndex);
            setCurPageIndex(pageIndex);
            poiSearch.searchPOIAsyn();
        } else {
            if (completeListener != null) {
                completeListener.onPoiSearchComplete(query, null, NO_FRESH);
            }
        }
    }

    public void nextPage() {
        if (poiSearch != null && query != null && hasNext) {
            query.setPageNum(++pageIndex);
            setCurPageIndex(pageIndex);
            poiSearch.searchPOIAsyn();
        } else {
            if (completeListener != null) {
                completeListener.onPoiSearchComplete(query, null, NO_MORE);
            }
        }
    }

    @Override
    public void onLocationSuccess(final AMapLocation location) {

        String city = location.getCityCode();
        String keyword = location.getDistrict();
        Lg.d(String.format("__city = %s, __keyword = %s,", location.getCity(), location.getDistrict()));
        if (keyword != null && !keyword.isEmpty()) {
            searchNearby(keyword, city, locationHelper.getCurPoint());
        }else {
            onLocationFail(location.getAMapException());
        }
    }

    @Override
    public void onLocationFail(AMapLocException exception) {
        Lg.d("Fail to location. Cause = " + exception.getErrorMessage());
        if(completeListener != null) {
            completeListener.onPoiSearchComplete(null,null,exception.getErrorCode());
        }
    }

    private void checkMovable(PoiResult result) {
        if (result != null) {
            hasNext = (result.getPageCount() - 1) > pageIndex;
            hasPrevious = result.getPageCount() != 0 && getCurPageIndex() > 0;
        } else {
            hasNext = hasPrevious = false;
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        final PoiResult result = poiResult;
        checkMovable(result);
        if (completeListener != null) {
            completeListener.onPoiSearchComplete(query, result, rCode);
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int rCode) {

    }
}
