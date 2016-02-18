package com.baoshi.mua.model;

import com.google.gson.Gson;

/**
 * Created by ThinkPad on 2014/12/4.
 */
public class Carrier {
    private String type;
    private CarForm form = CarForm.NORMAL;

    public String getType() {
        return type;
    }

    public Carrier setType(String type) {
        this.type = type;
        return this;
    }

    public CarForm getForm() {
        return form;
    }

    public Carrier setForm(CarForm form) {
        this.form = form;
        return this;
    }

    public static Carrier toJava(String json) {
        return new Gson().fromJson(json, Carrier.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public enum CarForm {
        NORMAL(0),
        PAPER(1),//信纸
        TOME(2),//卷轴
        HORN(3);//号角

        CarForm(int i) {
        }

        @Override
        public String toString() {
            return this.name();
        }
    }
}
