package com.baoshi.mua.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.baoshi.mua.model.ModelHelper;
import com.baoshi.mua.model.avos.AVPublication;
import com.baoshi.mua.utils.Lg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.octo.android.robospice.persistence.ormlite.ContractHelper;
import com.octo.android.robospice.persistence.ormlite.RoboSpiceDatabaseHelper;

import java.sql.SQLException;

/**
 * Created by ThinkPad on 2014/11/6.
 */
public class MuaDatabaseHelper extends RoboSpiceDatabaseHelper {
    public static final String KEY_SELECTION = "selection";
    public static final String KEY_SELECTION_ARGS = "selection_args";

    private volatile boolean isAllTableCreated = false;
    private final Context context;
    public MuaDatabaseHelper(Context context) {
        super(context, MuaContentProvider.DATABASE_NAME, MuaContentProvider.DATABASE_VERSION);
        Lg.d("++++Initialize MuaDatabaseHelper!!+++");
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        super.onCreate(database, connectionSource);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        super.onUpgrade(database, connectionSource, oldVersion, newVersion);
    }

    private void initializeTablesIfNeeded() {
        Lg.d("__do initializeTablesIfNeeded()__");
        if (!isAllTableCreated) {

            if (ModelHelper.getExposedClasses() != null) {
                for (Class<?> clazz : ModelHelper.getExposedClasses()) {
                    createTableIfNotExists(clazz);
                }
            }
            isAllTableCreated = true;
        }
    }

    private void createTableIfNotExists(Class<?> clazz) {
        try {
            TableUtils.createTableIfNotExists(this.getConnectionSource(), clazz);
        } catch (SQLException e) {
            Lg.e( "Could not create cache entry table");
        }
    }

    public CursorLoader getPublicationCursorLoader( String selection, String[] selectionArgs){

        try {
            Uri uri = ContractHelper.getContentUri(AVPublication.class);
            return getCursorLoader( uri,null,selection,selectionArgs,"createdAt desc");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CursorLoader getCursorLoader(Uri uri, String[] projection, String selection, String[] selectionArgs, String order){
        return new CursorLoader(getContext(), uri,projection,selection,selectionArgs,order);
    }


    private Context getContext(){
        return context;
    }
}
