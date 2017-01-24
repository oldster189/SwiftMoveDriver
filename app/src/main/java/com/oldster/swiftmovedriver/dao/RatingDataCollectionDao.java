package com.oldster.swiftmovedriver.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 18/12/2559.
 */

public class RatingDataCollectionDao {
    @SerializedName("success") private boolean success;
    @SerializedName("data")    private List<RatingDataDao> data;

    public RatingDataCollectionDao() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<RatingDataDao> getData() {
        return data;
    }

    public void setData(List<RatingDataDao> data) {
        this.data = data;
    }
}
