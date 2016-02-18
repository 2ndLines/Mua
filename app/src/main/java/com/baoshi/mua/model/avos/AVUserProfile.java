package com.baoshi.mua.model.avos;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;


@AVClassName("AVUserProfile")
public class AVUserProfile extends AVObject{
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AVUserProfile(){

	}

    class PubType {
        String typeStr;
        boolean used;
    }
}
