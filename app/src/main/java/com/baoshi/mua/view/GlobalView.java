package com.baoshi.mua.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.baoshi.mua.R;

/**
 * Created by ThinkPad on 2014/12/2.
 */
public class GlobalView {
    private Context context;
    private WindowManager windowManager;
    private View content;

    public GlobalView(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showWindow(/*View view, int left, int top, int right, int bottom*/) {
//        content = view;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        /*lp.x = left;
        lp.y = top;
        lp.width = right - left;
        lp.height = bottom - top;*/
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        ImageView view1 = new ImageView(context);
        view1.setImageResource(R.drawable.photo_uranus);
        windowManager.addView(view1, lp);

    }

    public void dismissWindow(boolean anim) {

    }
}
