package com.baoshi.mua.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.baoshi.mua.R;

/**
 * Created by ThinkPad on 2014/11/17.
 */
public class PopupButtonList extends PopupWindow {
    //    private Context context;
//    private LinearLayout contentView;
    private PopupWindow popupWindow;

    public PopupButtonList(Builder builder) {
        popupWindow = builder.popupWindow;
    }

    public void show(View parent) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public static class Builder {
        private LinearLayout layout;
        private PopupWindow popupWindow;
        private Context context;

        public Builder(Context context1) {
            context = context1;
            init(context1);
        }

        private void init(Context context) {
            this.context = context;
            layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_popup_button_list, null, false);

            popupWindow = new PopupWindow(layout);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_background));
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);
            popupWindow.setAnimationStyle(R.style.popup_translate_anim);
        }

        public Builder addButton(int resId, final View.OnClickListener onClickListener) {
            Button button = (Button) LayoutInflater.from(context).inflate(R.layout.layout_button_default, null, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (resId == android.R.string.cancel) {
                lp.setMargins(10, 10, 10, 5);
                button.setBackgroundResource(R.drawable.button_default_light_selector);
            } else {
                lp.setMargins(40, 10, 40, 10);
            }
            button.setLayoutParams(lp);
            button.setText(resId);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    if(onClickListener != null){
                        onClickListener.onClick(v);
                    }
                }
            });

            layout.addView(button);

            return this;
        }

        public PopupButtonList build() {
            addButton(android.R.string.cancel, null);
            return new PopupButtonList(this);
        }
    }

}
