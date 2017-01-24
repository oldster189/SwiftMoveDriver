package com.oldster.swiftmovedriver.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.dao.JobDataWithImageCollectionDao;

public class JobHistoryManager {


    private Context mContext;

    private JobDataWithImageCollectionDao jobDataCollectionDao;

    public JobHistoryManager() {
        mContext = Contextor.getInstance().getContext();
        loadCache();
    }

    public JobDataWithImageCollectionDao getJobDataCollectionDao() {
        return jobDataCollectionDao;
    }

    public void setJobDataCollectionDao(JobDataWithImageCollectionDao jobDataCollectionDao) {
        this.jobDataCollectionDao = jobDataCollectionDao;
        saveCache();
    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("DriverJobHistory",
                Context.MODE_PRIVATE);
        String json = prefs.getString("driver_job_history_data",null);
        if (json == null)
            return;
        jobDataCollectionDao = new Gson().fromJson(json,JobDataWithImageCollectionDao.class);
    }

    private void saveCache() {
        JobDataWithImageCollectionDao cacheDao = new JobDataWithImageCollectionDao();
        if (jobDataCollectionDao != null && jobDataCollectionDao.getData() != null)
            cacheDao.setData(jobDataCollectionDao.getData());
        String json = new Gson().toJson(cacheDao);
        SharedPreferences prefs = mContext.getSharedPreferences("DriverJobHistory",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("driver_job_history_data", json);
        editor.apply();
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("jobDataCollectionDao", jobDataCollectionDao);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        jobDataCollectionDao = savedInstanceState.getParcelable("jobDataCollectionDao");
    }


}
