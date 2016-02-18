package com.baoshi.mua.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ThinkPad on 2014/11/27.
 */
public class SelfAdapteLayoutManager extends StaggeredGridLayoutManager {
    /**
     * Creates a StaggeredGridLayoutManager with given parameters.
     *
     * @param spanCount   If orientation is vertical, spanCount is number of columns. If
     *                    orientation is horizontal, spanCount is number of rows.
     * @param orientation {@link #VERTICAL} or {@link #HORIZONTAL}
     */
    public SelfAdapteLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        if (getItemCount() != 0) {
            View view = recycler.getViewForPosition(0);

            if (view != null) {
                measureChild(view, widthSpec, heightSpec);
                int measureWidth = View.MeasureSpec.getSize(widthSpec);
                int measureHeight = view.getMeasuredHeight();

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                if (lp != null) {
                    measureHeight += (lp.topMargin + lp.bottomMargin);
                }
                final int itemCount = getItemCount();
                final int spanCount = getSpanCount();

                int row = itemCount / spanCount;
                if (itemCount % spanCount != 0) {
                    row += 1;
                }

                //2 rows at most
                if(row > 2){
                    row = 2;
                }

                measureHeight *= row;
                int padding = getPaddingTop() + getPaddingBottom();
                measureHeight += padding;
//                Lg.d(String.format("___itemCount = %s, ___spanCount = %s,_row = %s, measureWidth = &s, destHeight=%s",getItemCount(),getSpanCount(),row,measureWidth, measureHeight));

                setMeasuredDimension(measureWidth, measureHeight);
            } else {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
            }
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }
}
