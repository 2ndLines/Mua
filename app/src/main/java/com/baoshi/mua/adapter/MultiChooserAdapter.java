package com.baoshi.mua.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baoshi.mua.R;
import com.baoshi.mua.utils.Lg;
import com.baoshi.mua.view.CheckableImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2014/11/20.
 */
public class MultiChooserAdapter extends CursorAdapter<String> {
    private Context mContext;
    private List<String> selected = new ArrayList<String>();
    private DisplayImageOptions options;
    private ImageSize imageSize;

    private long startTime, elapseTime;

    public MultiChooserAdapter(Context context, ImageSize imageSize) {
        mContext = context;
        selected = new ArrayList<String>();
//        this.imageSize = imageSize;
        setHasStableIds(true);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.gray)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();
    }

    public void setSelected(List<String> data) {
        if (data != selected) {
            selected.clear();
            selected.addAll(data);
            Lg.d("+++update selected data.+++");
            notifyDataSetChanged();
        }
    }

    public List<String> getSelected() {
        return selected;
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder holder, final String data, final int position) {
        final ImageViewHolder ivh = (ImageViewHolder) holder;
        updateToggle(ivh.getToggle(), data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleClick(ivh.getToggle(), data);
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(MultiChooserAdapter.this, data, position);
                }
            }
        });
        startTime = SystemClock.currentThreadTimeMillis();
        loadImage(ivh.getImageView(), data, imageSize, options);
    }

    private void onToggleClick(CheckableImageView toggle, String data) {
        toggle.toggle();
        if (toggle.isChecked()) {
            selected.add(data);
        } else {
            selected.remove(data);
        }

        updateToggle(toggle, data);
    }

    private void updateToggle(CheckableImageView toggle, String data) {
        toggle.setChecked(selected.contains(data));
    }

    @Override
    protected String getItem(int position) {
        long _id = getItemId(position);
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, _id).toString();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_grid_image_item, parent, false);
        return new ImageViewHolder(view);
    }

    private void loadImage(final ImageView imageView, String uri, ImageSize imageSize, DisplayImageOptions options) {
        if (imageSize != null) {
            ImageLoader.getInstance().loadImage(uri, imageSize, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    imageView.setImageBitmap(loadedImage);
                    Lg.d(String.format("LoadedImage size = [%s, %s], elapse = %sms", loadedImage.getWidth(), loadedImage.getHeight() , SystemClock.currentThreadTimeMillis() - startTime));
                }
            });
        } else {
            ImageLoader.getInstance().displayImage(uri, imageView, options);
        }
    }
}
