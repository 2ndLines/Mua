package com.baoshi.mua.decorator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ThinkPad on 2014/11/28.
 */
public class QRHeaderDecorator extends RecyclerView.ItemDecoration {
    private int columnCount;
    private int headerHeight;

    public QRHeaderDecorator(int columnCount, int headerHeight){
        this.columnCount = columnCount;
        this.headerHeight = headerHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildPosition(view) < columnCount){
            outRect.set(0,headerHeight,0,0);
        }else{
            outRect.set(0,0,0,0);
        }
    }
}
