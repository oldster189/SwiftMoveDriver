package com.oldster.swiftmovedriver.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 18/12/2559.
 */

public class RatingDataDao {
    @SerializedName("rating_avg") private double ratingAvg;
    @SerializedName("rating_count") private int ratingCount;

    public RatingDataDao() {
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
