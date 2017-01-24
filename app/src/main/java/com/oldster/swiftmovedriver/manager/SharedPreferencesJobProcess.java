package com.oldster.swiftmovedriver.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.dao.JobDataWithImageDao;

/**
 * Created by Old'ster on 4/12/2559.
 */

public class SharedPreferencesJobProcess {
    private Context mContext;
    private JobDataWithImageDao jobDataDao;

    public SharedPreferencesJobProcess() {
        mContext = Contextor.getInstance().getContext();
        loadCache();
    }

    public JobDataWithImageDao getJobDataDao() {
        return jobDataDao;
    }

    public void setJobDataDao(JobDataWithImageDao jobDataDao) {
        this.jobDataDao = jobDataDao;
        saveCache();
    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("JobProcess",
                Context.MODE_PRIVATE);
        String json = prefs.getString("job_data", null);
        if (json == null)
            return;
        jobDataDao = new Gson().fromJson(json, JobDataWithImageDao.class);
    }

    private void saveCache() {
        String json = null;
        if (jobDataDao != null) {
            json = new Gson().toJson(jobDataDao);
        }
        SharedPreferences prefs = mContext.getSharedPreferences("JobProcess",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("job_data", json);
        editor.apply();
    }

    public void clear() {
        SharedPreferences prefs = mContext.getSharedPreferences("JobProcess",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

    }
}
