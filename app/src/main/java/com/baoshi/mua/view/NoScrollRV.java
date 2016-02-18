package com.baoshi.mua.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ThinkPad on 2014/11/26.
 */
public class NoScrollRV extends RecyclerView {
    public NoScrollRV(Context context) {
        super(context);
    }

    public NoScrollRV(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        int expandWidth = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.UNSPECIFIED);

//        super.onMeasure(widthSpec, heightSpec);
        super.onMeasure(expandSpec, heightSpec);
    }
}
