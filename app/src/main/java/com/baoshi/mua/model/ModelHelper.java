package com.baoshi.mua.model;

import com.baoshi.mua.model.orm.OrmComment;
import com.baoshi.mua.model.orm.OrmImage;
import com.baoshi.mua.model.orm.OrmPubFollow;
import com.baoshi.mua.model.orm.OrmPubList;
import com.baoshi.mua.model.orm.OrmPublication;
import com.baoshi.mua.model.orm.OrmUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2014/11/6.
 */
public final class ModelHelper {

    public static List<Class<?>> getExposedClasses(){
        List<Class<?>> classCollection = new ArrayList<Class<?>>();

        classCollection.add(OrmPublication.class);
        classCollection.add(OrmImage.class);
        classCollection.add(OrmComment.class);
        classCollection.add(OrmPubList.class);
        classCollection.add(OrmUser.class);
        classCollection.add(OrmPubFollow.class);
//        classCollection.add(UserProfile.class);

        return classCollection;
    }
}
