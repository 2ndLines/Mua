package com.baoshi.mua.provider;

import com.baoshi.mua.model.ModelHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.octo.android.robospice.persistence.ormlite.RoboSpiceContentProvider;
import com.octo.android.robospice.persistence.ormlite.RoboSpiceDatabaseHelper;

import java.util.List;

public class MuaContentProvider extends RoboSpiceContentProvider {

    public static final String DATABASE_NAME = "mua_database.db";
    public static final int DATABASE_VERSION = 1;
    public static final String AUTHORITY = "com.baoshi.mua.provider.MuaContentProvider";
    public static final String MINETYPE_NAME = "com.baoshi.mua.provider";

    @Override
    public RoboSpiceDatabaseHelper getHelper() {
        return OpenHelperManager.getHelper(getContext().getApplicationContext(), MuaDatabaseHelper.class);
    }

    @Override
    public List<Class<?>> getExposedClasses(){
        return ModelHelper.getExposedClasses();
    }

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
