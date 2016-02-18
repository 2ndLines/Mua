package com.baoshi.mua.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.baoshi.mua.R;
import com.baoshi.mua.view.CheckableImageView;

/**
 * Created by ThinkPad on 2014/11/18.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {

    public ImageViewHolder(View itemView) {
        super(itemView);
    }

    public ImageView getImageView(){
        return (ImageView) itemView.findViewById(R.id.thumbnail);
    }

    public CheckableImageView getToggle(){
        CheckableImageView toggle = (CheckableImageView) itemView.findViewById(R.id.toggle);
        if(toggle.getVisibility() != View.VISIBLE){
            toggle.setVisibility(View.VISIBLE);
        }
        return toggle;
    }
}
