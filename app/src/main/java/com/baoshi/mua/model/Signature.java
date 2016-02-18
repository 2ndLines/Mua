package com.baoshi.mua.model;

import com.google.gson.Gson;

/**
 * Created by ThinkPad on 2014/11/6.
 */

public class Signature {
    private String text;
    private String recUrl;
    private int RecDuration;

    public String getRecUrl() {
        return recUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRecUrl(String recUrl) {
        this.recUrl = recUrl;
    }

    public int getRecDuration() {
        return RecDuration;
    }

    public void setRecDuration(int recDuration) {
        this.RecDuration = recDuration;
    }

    public static Signature toJava(String json) {
        return new Gson().fromJson(json, Signature.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
