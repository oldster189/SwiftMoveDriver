package com.oldster.swiftmovedriver.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.dao.DriverDataCollectionDao;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class DriverDataManager {


    private DriverDataCollectionDao dao;
    private Context mContext;

    public DriverDataCollectionDao getDao() {
        return dao;
    }

    public void setDao(DriverDataCollectionDao dao) {
        this.dao = dao;
        saveCache();
    }

    public DriverDataManager() {
        mContext = Contextor.getInstance().getContext();
        loadCache();
    }

    public int getCount() {
        if (dao == null) {
            return 0;
        }
        if (dao.getData() == null) {
            return 0;
        }
        return dao.getData().size();
    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("DriverData",
                Context.MODE_PRIVATE);
        String json = prefs.getString("driver_data", null);
        if (json == null)
            return;
        dao = new Gson().fromJson(json, DriverDataCollectionDao.class);
    }

    private void saveCache() {
        DriverDataCollectionDao cacheDao = new DriverDataCollectionDao();
        if (dao != null && dao.getData() != null)
            cacheDao.setData(dao.getData());
        String json = new Gson().toJson(cacheDao);
        SharedPreferences prefs = mContext.getSharedPreferences("DriverData",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("driver_data", json);
        editor.apply();
    }

    public void clear() {
        SharedPreferences prefs = mContext.getSharedPreferences("DriverData",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

    }

}
