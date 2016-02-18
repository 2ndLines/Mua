package com.octo.android.robospice.persistence.ormlite;

import android.app.Application;

import com.baoshi.mua.annotation.MuaContract;
import com.baoshi.mua.utils.Lg;
import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd.SubType;

import java.util.List;

public abstract class RoboSpiceContentProvider extends OrmLiteSimpleContentProvider<RoboSpiceDatabaseHelper> {
    @Override
    protected Class<RoboSpiceDatabaseHelper> getHelperClass() {
        return RoboSpiceDatabaseHelper.class;
    }

    @Override
    public RoboSpiceDatabaseHelper getHelper() {
        return new RoboSpiceDatabaseHelper((Application) getContext().getApplicationContext(), getDatabaseName(), getDatabaseVersion());
    }

    @Override
    public boolean onCreate() {
        MatcherController controller = new MatcherController();
        for (Class<?> clazz : getExposedClasses()) {
            try {
                if (!clazz.isAnnotationPresent(MuaContract.class)) {
                    throw new Exception("Class " + clazz + " is not annotated with the @MuaContract annotation.");
                }
                Class<?> contractClazz = ContractHelper.getContractClassForClass(clazz);
                int contentUriPatternMany = ContractHelper.getContentUriPatternMany(contractClazz);
                int contentUriPatternOne = ContractHelper.getContentUriPatternOne(contractClazz);
//                Lg.d(String.format("__contentUriPatternOne=%s,contentUriPatternMany=%s", contentUriPatternOne, contentUriPatternMany));
                controller.add(clazz, SubType.DIRECTORY, "", contentUriPatternMany);
                controller.add(clazz, SubType.ITEM, "#", contentUriPatternOne);
            } catch (Exception e) {
                Lg.e(e.getMessage());
            }
        }
        setMatcherController(controller);
        return true;
    }

    public abstract List<Class<?>> getExposedClasses();

    public abstract String getDatabaseName();

    public abstract int getDatabaseVersion();

}
