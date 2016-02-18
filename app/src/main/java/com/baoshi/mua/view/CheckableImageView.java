package com.baoshi.mua.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * Created by ThinkPad on 2014/11/18.
 */
public class CheckableImageView extends ImageView implements Checkable {
    private static final int[] checkedStateSet = new int[]{android.R.attr.state_checked};
    private boolean checked = false;
//    private Drawable background;
    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean b) {
        if(checked != b){
            checked = b;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, checkedStateSet);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
}
