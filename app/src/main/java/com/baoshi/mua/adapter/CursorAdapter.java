package com.baoshi.mua.adapter;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.baoshi.mua.utils.Lg;

/**
 * Created by ThinkPad on 2014/11/18.
 */
public abstract class CursorAdapter<T> extends BaseAdapter<T> {
    private static final int NO_ID = -1;

    protected Cursor mCursor;
    private int mRowIdColumn = NO_ID;


    public void changeCursor(Cursor newCursor){
        Cursor old = swapCursor(newCursor);
        if(old != null){
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor){
        if(mCursor == newCursor){
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(mCursor != null){
            mRowIdColumn = mCursor.getColumnIndexOrThrow(BaseColumns._ID);
            Lg.d("__swapCursor, size = " + mCursor.getCount() + "_rowId column = " + mRowIdColumn);
            notifyDataSetChanged();
        }else{
            mRowIdColumn = -1;
        }
        return oldCursor;
    }

    @Override
    public long getItemId(int position) {
        if(mCursor != null){
            if(mCursor.moveToPosition(position)){
                return mCursor.getLong(mRowIdColumn);
            }else{
                return 0;
            }
        }
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if(mCursor != null){
            return mCursor.getCount();
        }

        return 0;
    }
}
