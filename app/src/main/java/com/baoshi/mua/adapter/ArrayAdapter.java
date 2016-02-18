package com.baoshi.mua.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ThinkPad on 2014/11/19.
 */
public abstract class ArrayAdapter<T> extends BaseAdapter<T> {
    private final Object mLock = new Object();
    private boolean mNotifyOnChange = true;
    private List<T> mData ;

    public ArrayAdapter(){
        mData = new ArrayList<T>();
    }

    public ArrayAdapter(T[] objects){
        mData = new ArrayList<T>();
        Collections.addAll(mData, objects);
    }

    public ArrayAdapter(List<T> data) {
        if(data == null) {
            mData = new ArrayList<T>();
        }else{
            mData = data;
        }
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    public boolean ismNotifyOnChange() {
        return mNotifyOnChange;
    }

    public void add(int position, T object){
        synchronized (mLock) {
            mData.add(position,object);
        }

        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void add(T object) {
        synchronized (mLock) {
            mData.add(object);
        }

        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public List<T> getData(){
        return mData;
    }

    public void changeData(List<T> data){
        synchronized (mLock){
            mData.clear();
            mData.addAll(data);
        }
        if(mNotifyOnChange) notifyDataSetChanged();

    }

    public void addAll(Collection<? extends T> objects) {
        synchronized (mLock) {
            mData.addAll(objects);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void remove(T object) {
        synchronized (mLock) {
            mData.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mData.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public int getPosition(T object) {
        synchronized (mLock) {
            return mData.indexOf(object);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
