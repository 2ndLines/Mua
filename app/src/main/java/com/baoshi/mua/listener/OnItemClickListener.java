package com.baoshi.mua.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ThinkPad on 2014/11/20.
 */
public interface OnItemClickListener<T> {
    public void onItemClick(RecyclerView.Adapter<?> adapter, T data, int position );
}
