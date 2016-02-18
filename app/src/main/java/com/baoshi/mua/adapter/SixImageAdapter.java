package com.baoshi.mua.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baoshi.mua.R;
import com.baoshi.mua.utils.Lg;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by ThinkPad on 2014/11/20.
 */
public class SixImageAdapter extends ArrayAdapter<String> {
    private static final int MAX_SPAN_SIZE = 4;
    private static final int MINI_WIDTH = 480;
    private Context mContext;
    private int layoutId;
    private DisplayImageOptions options;
    private StaggeredGridLayoutManager layoutManager;
    private int cellSize = 0;
    private static final String TAG = "SixImageAdapter";
    private int itemMargin = 1;

    private RecyclerView.AdapterDataObserver observer ;

    public SixImageAdapter(Context context, List<String> data, int resId) {
        this(context,data);
        layoutId = resId;

    }

    public SixImageAdapter(Context context,List<String> data) {
        super(data);
        setHasStableIds(true);
        mContext = context;
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.gray)
                .showImageOnLoading(R.drawable.gray)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .build();
        itemMargin = (int)context.getResources().getDimension(R.dimen.six_image_item_margin);
        layoutId = R.layout.six_image_item;
    }

    public void setOptions(DisplayImageOptions opts) {
        options = opts;
    }

    protected View createView(ViewGroup parent, int viewTye) {
        final View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return view;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = createView(parent, viewType);
        cellSize = computeCellSize(parent);

        return new ImageViewHolder(itemView);
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder holder, final String data, final int position) {
        final ImageViewHolder ivh = (ImageViewHolder) holder;

        ((ImageViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(SixImageAdapter.this, data, position);
                }
            }
        });
        ImageView imageView = ivh.getImageView();
        updateLayoutParams(imageView,position);

        ImageLoader.getInstance().displayImage(data, imageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
            }
        });
    }

    public void setLayoutManager(StaggeredGridLayoutManager layoutManager){
        this.layoutManager = layoutManager;
    }

    private int computeCellSize(ViewGroup parent){
        int nestWidth = parent.getMeasuredWidth() - parent.getPaddingRight() - parent.getPaddingLeft();
        nestWidth = Math.max(nestWidth,MINI_WIDTH);
        return nestWidth/MAX_SPAN_SIZE;
    }

    private void updateSpanCount(){
        final int itemCount = getItemCount();
        Lg.d("___itemCount = " + itemCount + ", cellSize = " + cellSize);
        if(itemCount > 6){
            layoutManager.setSpanCount(4);
        } else if (itemCount > 2 && itemCount < 7){
            layoutManager.setSpanCount(3);
        }else if(itemCount == 2 ){
            layoutManager.setSpanCount(2);
        }else{
            layoutManager.setSpanCount(1);
        }
        Lg.d("___spanCount = " + layoutManager.getSpanCount());
    }

    private void updateLayoutParams(ImageView imageView, int position){
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        if(lp == null){
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        int[] size = resetCellSize(position);

        lp.width = size[0];
        lp.height = size[1];
        Lg.d(TAG, String.format("__width = %s, __height = %s", lp.width, lp.height));
        imageView.setLayoutParams(lp);
    }

    private int[] resetCellSize(int position){
        final int itemCount = getItemCount();
        int width = -1;//MATCH_PARENT
        int height = -2;//WRAP_PARENT

        switch (itemCount){
            case 2:
                width = -1;
                height = cellSize * 2;
                break;
            case 3:
                width = -1;
                height = cellSize + cellSize/2;
                break;
            case 4:
                width = -1;
                if(position == 2 || position == 1){
                    height = cellSize * 2 + itemMargin * 2;
                }else {
                    height = cellSize;
                }
                break;
            default:
                width = -1;
                if(itemCount%2 != 0){
                    if(position == itemCount%layoutManager.getSpanCount()){
                        height = cellSize * 2 + itemMargin * 2;
                    }else{
                        height = cellSize;
                    }
                }else{
                    height = cellSize;
                }

        }
        return new int[]{width, height};
    }

    public void registerAdapterDataObserver() {
        if(observer == null){

            observer = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    updateSpanCount();
                }
            };
        }else{
            super.unregisterAdapterDataObserver(observer);
        }
Lg.d(TAG,"__registerAdapterDataObserver()__");
        super.registerAdapterDataObserver(observer);
    }

    public void unregisterAdapterDataObserver() {
        Lg.d(TAG,"__unregisterAdapterDataObserver()__");
        super.unregisterAdapterDataObserver(observer);
    }
}
