package com.baoshi.mua.adapter;

import android.support.v7.widget.RecyclerView;

import com.baoshi.mua.listener.OnItemClickListener;

/**
 * Created by ThinkPad on 2014/11/20.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener<T> itemClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> clickListener){
        itemClickListener = clickListener;
    }

    public OnItemClickListener<T> getOnItemClickListener(){
        return itemClickListener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        onBindView(holder,getItem(position), position);
    }

//    protected abstract View createView(ViewGroup parent, int viewTye);

    protected abstract void onBindView(RecyclerView.ViewHolder holder,T data, int position);

    protected abstract T getItem(int position);

}
