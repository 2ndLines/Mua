package com.baoshi.mua.model.orm;

import android.provider.BaseColumns;

import com.baoshi.mua.annotation.MuaContract;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by ThinkPad on 2014/12/4.
 */
@MuaContract
@AdditionalAnnotation.Contract
@DatabaseTable
public class OrmPubList {
    public static final String CACHE_KEY_NEARBY = "pub_nearby";
    public static final String CACHE_KEY_ALL = "pub_all";

    @DatabaseField(generatedId = true, columnName = BaseColumns._ID)
    private int id;

    @ForeignCollectionField(eager = true, maxEagerLevel = 2,orderColumnName = "createdAt", orderAscending = false)
    private Collection<OrmPublication> allPubs;

    @ForeignCollectionField(eager = true, maxEagerLevel = 2,orderColumnName = "createdAt", orderAscending = false)
    private Collection<OrmPublication> nearByPubs;

    public OrmPubList(){
        //For Ormlite
    }

    public Collection<OrmPublication> getAllPubs() {
        return Collections.unmodifiableCollection(allPubs);
    }

    public void setAllPubs(Collection<OrmPublication> allPubs) {
        this.allPubs = allPubs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection<OrmPublication> getNearByPubs() {
        return nearByPubs;
    }

    public void setNearByPubs(Collection<OrmPublication> nearByPubs) {
        this.nearByPubs = nearByPubs;
    }

    public void addNearByPub(OrmPublication publication){
        nearByPubs.add(publication);
    }
}
