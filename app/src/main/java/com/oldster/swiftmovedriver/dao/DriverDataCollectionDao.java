package com.oldster.swiftmovedriver.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 5/12/2559.
 */

public class DriverDataCollectionDao {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<DriverDataDao> data;
    @SerializedName("message")
    private String message;

    public DriverDataCollectionDao() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DriverDataDao> getData() {
        return data;
    }

    public void setData(List<DriverDataDao> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
