package com.baoshi.mua.persister;

import com.baoshi.mua.model.Carrier;
import com.baoshi.mua.model.LocationInfo;
import com.baoshi.mua.utils.Lg;
import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;

/**
 * Created by ThinkPad on 2014/11/7.
 */
public class GsonDataPersister extends BaseDataType {
    public static int DEFAULT_WIDTH = 255;
    private static final GsonDataPersister singleTon = new GsonDataPersister();

    public static GsonDataPersister getSingleton() {
        return singleTon;
    }

    public GsonDataPersister() {
        super(SqlType.STRING, new Class[]{LocationInfo.class, Carrier.class});
    }

    /**
     * @param sqlType Type of the class as it is persisted in the databases.
     * @param classes Associated classes for this type. These should be specified if you want this type to be always used
     */
    public GsonDataPersister(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return defaultStr;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        //        return super.javaToSqlArg(fieldType, javaObject);
        String str = new Gson().toJson(javaObject);

        Lg.d("____java to sqlArg = " + str);
        return str;
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
//        return null;
        String result = results.getString(columnPos);

        return new Gson().fromJson(result, fieldType.getType());
    }

    @Override
    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }
}
