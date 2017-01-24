package com.oldster.swiftmovedriver.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 29/11/2559.
 */

public class JobDataWithImageCollectionDao implements Parcelable {
    @SerializedName("success") private boolean success;
    @SerializedName("data")    private List<JobDataWithImageDao> data;

    public JobDataWithImageCollectionDao(){

    }

    protected JobDataWithImageCollectionDao(Parcel in) {
        success = in.readByte() != 0;
        data = in.createTypedArrayList(JobDataWithImageDao.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobDataWithImageCollectionDao> CREATOR = new Creator<JobDataWithImageCollectionDao>() {
        @Override
        public JobDataWithImageCollectionDao createFromParcel(Parcel in) {
            return new JobDataWithImageCollectionDao(in);
        }

        @Override
        public JobDataWithImageCollectionDao[] newArray(int size) {
            return new JobDataWithImageCollectionDao[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<JobDataWithImageDao> getData() {
        return data;
    }

    public void setData(List<JobDataWithImageDao> data) {
        this.data = data;
    }
}
