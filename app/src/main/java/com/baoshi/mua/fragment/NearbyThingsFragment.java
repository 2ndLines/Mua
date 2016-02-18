package com.baoshi.mua.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoshi.mua.adapter.CursorAdapter;
import com.baoshi.mua.provider.MuaDatabaseHelper;

/**
 * Created by ThinkPad on 2014/11/10.
 */
public class NearbyThingsFragment extends RefreshListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter cursorAdapter;
    private LoaderManager loaderManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = getHostActivity().getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setAdapter(cursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String selection = null;
        String[] selectionArgs = null;
        if(bundle != null){
            selection = bundle.getString(MuaDatabaseHelper.KEY_SELECTION);
            selectionArgs = bundle.getStringArray(MuaDatabaseHelper.KEY_SELECTION_ARGS);
        }
        return getHostActivity().getHelper().getPublicationCursorLoader(selection,selectionArgs);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void reload(String selection, String[] selectionArgs){
        Bundle bundle = new Bundle();
        bundle.putString(MuaDatabaseHelper.KEY_SELECTION, selection);
        bundle.putStringArray(MuaDatabaseHelper.KEY_SELECTION_ARGS, selectionArgs);
        LoaderManager lm = loaderManager;
        if(lm != null ){
            if(lm.hasRunningLoaders()){
                lm.destroyLoader(0);
            }
            lm.restartLoader(0,bundle,this);
        }

    }

}
