package com.baoshi.mua;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by ThinkPad on 2014/12/6.
 */
public class SpannableStringHelper {
    private final static String SPLIT = "#";

    public interface OnSpanClickListener {
        public void onSpanClick(View view, String piece);
    }


    private static class SingletonHolder {
        final static SpannableStringHelper helper = new SpannableStringHelper();
    }

    public static SpannableStringHelper getInstance() {
        return SingletonHolder.helper;
    }

    public SpannableString buildSpannableString(String origString, OnSpanClickListener clickListener) {

        if (origString != null && !origString.trim().isEmpty()) {
            SpannableString ss = new SpannableString(origString);
            if(origString.contains(SPLIT)){

                int firstSplit = origString.indexOf(SPLIT);
                if (firstSplit != origString.length() - 1) {//The position of # is not the end of origString.

                    String subString = origString.substring(firstSplit);
//                    Lg.d("___SubString = " + subString);
                    String[] pieces = subString.split(SPLIT);
                    int index = 0;
                    if (pieces != null) {
                        for (String piece : pieces) {
                            if(piece == null || piece.isEmpty()) continue;
                            int start = origString.indexOf(SPLIT, index);
                            int end = start + piece.length() + 1;//start with #, so plus 1.
                            ss.setSpan(new Clickable(clickListener, piece), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = end;
                        }
                    }
                }
            }else{

                String richName = new StringBuffer("@").append(origString).append(":").toString();
                ss = new SpannableString(richName);
                ss.setSpan(new Clickable(clickListener,origString),0,richName.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return ss;
        }
        return null;
    }

    private class Clickable extends ClickableSpan {
        final private OnSpanClickListener clickListener;
        final String pieceString;

        public Clickable(OnSpanClickListener listener, String piece) {
            clickListener = listener;
            pieceString = piece;
        }

        @Override
        public void onClick(View widget) {
            if (clickListener != null) {
                clickListener.onSpanClick(widget, pieceString);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#91a7ff")); //R.color.material_blue_300
            ds.setUnderlineText(false);//remove underline
        }
    }

}
