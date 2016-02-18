package com.baoshi.mua.service;

import android.app.Application;

import com.baoshi.mua.model.ModelHelper;
import com.baoshi.mua.provider.MuaDatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.ormlite.ContractHelper;
import com.octo.android.robospice.persistence.ormlite.InDatabaseObjectPersisterFactory;
import com.octo.android.robospice.persistence.ormlite.RoboSpiceDatabaseHelper;

/**
 * Created by ThinkPad on 2014/11/6.
 */
public class LeanCloudSpiceService extends SpiceService {

    @Override
    public CacheManager createCacheManager(Application app) throws CacheCreationException {
        RoboSpiceDatabaseHelper helper = OpenHelperManager.getHelper(app, MuaDatabaseHelper.class);
        InDatabaseObjectPersisterFactory factory =
                new InDatabaseObjectPersisterFactory(app, helper, ContractHelper.getContractClasses(ModelHelper.getExposedClasses()));
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(factory);

        return cacheManager;
    }


}
