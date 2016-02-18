package com.baoshi.mua.model.orm;

import com.baoshi.mua.annotation.MuaContract;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

/**
 * Created by ThinkPad on 2014/12/13.
 * A join table class for many-to-many relationship between publication with follower.
 */
@MuaContract
@AdditionalAnnotation.Contract
@DatabaseTable
public class OrmPubFollow extends BaseDaoEnabled {
    public static final String PUBLICATION_ID_FIELD_NAME = "publication_id";
    public static final String USER_ID_FIELD_NAME ="user_id";


    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private OrmPublication publication;

    @DatabaseField(foreign = true)
    private OrmUser user;

    public OrmPubFollow(){
        //For Ormlite
    }

    public OrmPubFollow(OrmUser user, OrmPublication publication){
        this.publication = publication;
        this.user = user;
    }
}
