package com.oldster.swiftmovedriver.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 28/11/2559.
 */

public class JobDataWithImageDao implements Parcelable {
    @SerializedName("job_id")    private int jobId;
    @SerializedName("job_from_latitude")    private double jobFromLatitude;
    @SerializedName("job_from_longitude")    private double jobFromLongitude;
    @SerializedName("job_from_name_address")    private String jobFromNameAddress;

    @SerializedName("job_to_latitude")    private double jobToLatitude;
    @SerializedName("job_to_longitude")    private double jobToLongitude;
    @SerializedName("job_to_name_address")    private String jobToNameAddress;

    @SerializedName("job_time")    private String jobTime;
    @SerializedName("job_date")    private String jobDate;

    @SerializedName("job_service_lift_status")    private String jobServiceLiftStatus;
    @SerializedName("job_service_lift_price")    private int jobServiceLiftPrice;

    @SerializedName("job_service_lift_plus_status")    private String jobServiceLiftPlusStatus;
    @SerializedName("job_service_lift_plus_price")    private int jobServiceLiftPlusPrice;

    @SerializedName("job_service_cart_status")    private String jobServiceCartStatus;
    @SerializedName("job_service_cart_price")    private int jobServiceCartPrice;

    @SerializedName("job_charge_start_price")    private int jobChargeStartPrice;
    @SerializedName("job_charge_start_km")    private int jobChargeStartKm;
    @SerializedName("job_charge")    private int jobCharge;

    @SerializedName("job_status_name")    private String jobStatusName;
    @SerializedName("job_distance")    private double jobDistance;
    @SerializedName("job_created_at")    private String jobCreatedAt;

    @SerializedName("user_id")    private int userId;
    @SerializedName("user_first_name")    private String userFirstName;
    @SerializedName("user_last_name")    private String userLastName;
    @SerializedName("user_email")    private String userEmail;
    @SerializedName("user_img_name")    private String userImgName;
    @SerializedName("user_fcm_id")    private String userFcmId;
    @SerializedName("user_tel")    private String userTel;
    @SerializedName("driver_detail_type")    private String driverDetailType;
    @SerializedName("job_img_1")    private String jobImg1;
    @SerializedName("job_img_2")    private String jobImg2;
    @SerializedName("job_img_3")    private String jobImg3;


    public JobDataWithImageDao(){

    }

    protected JobDataWithImageDao(Parcel in) {
        jobId = in.readInt();
        jobFromLatitude = in.readDouble();
        jobFromLongitude = in.readDouble();
        jobFromNameAddress = in.readString();
        jobToLatitude = in.readDouble();
        jobToLongitude = in.readDouble();
        jobToNameAddress = in.readString();
        jobTime = in.readString();
        jobDate = in.readString();
        jobServiceLiftStatus = in.readString();
        jobServiceLiftPrice = in.readInt();
        jobServiceLiftPlusStatus = in.readString();
        jobServiceLiftPlusPrice = in.readInt();
        jobServiceCartStatus = in.readString();
        jobServiceCartPrice = in.readInt();
        jobChargeStartPrice = in.readInt();
        jobChargeStartKm = in.readInt();
        jobCharge = in.readInt();
        jobStatusName = in.readString();
        jobDistance = in.readDouble();
        jobCreatedAt = in.readString();
        userId = in.readInt();
        userFirstName = in.readString();
        userLastName = in.readString();
        userEmail = in.readString();
        userImgName = in.readString();
        userFcmId = in.readString();
        userTel = in.readString();
        driverDetailType = in.readString();
        jobImg1 = in.readString();
        jobImg2 = in.readString();
        jobImg3 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(jobId);
        dest.writeDouble(jobFromLatitude);
        dest.writeDouble(jobFromLongitude);
        dest.writeString(jobFromNameAddress);
        dest.writeDouble(jobToLatitude);
        dest.writeDouble(jobToLongitude);
        dest.writeString(jobToNameAddress);
        dest.writeString(jobTime);
        dest.writeString(jobDate);
        dest.writeString(jobServiceLiftStatus);
        dest.writeInt(jobServiceLiftPrice);
        dest.writeString(jobServiceLiftPlusStatus);
        dest.writeInt(jobServiceLiftPlusPrice);
        dest.writeString(jobServiceCartStatus);
        dest.writeInt(jobServiceCartPrice);
        dest.writeInt(jobChargeStartPrice);
        dest.writeInt(jobChargeStartKm);
        dest.writeInt(jobCharge);
        dest.writeString(jobStatusName);
        dest.writeDouble(jobDistance);
        dest.writeString(jobCreatedAt);
        dest.writeInt(userId);
        dest.writeString(userFirstName);
        dest.writeString(userLastName);
        dest.writeString(userEmail);
        dest.writeString(userImgName);
        dest.writeString(userFcmId);
        dest.writeString(userTel);
        dest.writeString(driverDetailType);
        dest.writeString(jobImg1);
        dest.writeString(jobImg2);
        dest.writeString(jobImg3);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobDataWithImageDao> CREATOR = new Creator<JobDataWithImageDao>() {
        @Override
        public JobDataWithImageDao createFromParcel(Parcel in) {
            return new JobDataWithImageDao(in);
        }

        @Override
        public JobDataWithImageDao[] newArray(int size) {
            return new JobDataWithImageDao[size];
        }
    };

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public double getJobFromLatitude() {
        return jobFromLatitude;
    }

    public void setJobFromLatitude(double jobFromLatitude) {
        this.jobFromLatitude = jobFromLatitude;
    }

    public double getJobFromLongitude() {
        return jobFromLongitude;
    }

    public void setJobFromLongitude(double jobFromLongitude) {
        this.jobFromLongitude = jobFromLongitude;
    }

    public String getJobFromNameAddress() {
        return jobFromNameAddress;
    }

    public void setJobFromNameAddress(String jobFromNameAddress) {
        this.jobFromNameAddress = jobFromNameAddress;
    }

    public double getJobToLatitude() {
        return jobToLatitude;
    }

    public void setJobToLatitude(double jobToLatitude) {
        this.jobToLatitude = jobToLatitude;
    }

    public double getJobToLongitude() {
        return jobToLongitude;
    }

    public void setJobToLongitude(double jobToLongitude) {
        this.jobToLongitude = jobToLongitude;
    }

    public String getJobToNameAddress() {
        return jobToNameAddress;
    }

    public void setJobToNameAddress(String jobToNameAddress) {
        this.jobToNameAddress = jobToNameAddress;
    }

    public String getJobTime() {
        return jobTime;
    }

    public void setJobTime(String jobTime) {
        this.jobTime = jobTime;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public String getJobServiceLiftStatus() {
        return jobServiceLiftStatus;
    }

    public void setJobServiceLiftStatus(String jobServiceLiftStatus) {
        this.jobServiceLiftStatus = jobServiceLiftStatus;
    }

    public int getJobServiceLiftPrice() {
        return jobServiceLiftPrice;
    }

    public void setJobServiceLiftPrice(int jobServiceLiftPrice) {
        this.jobServiceLiftPrice = jobServiceLiftPrice;
    }

    public String getJobServiceLiftPlusStatus() {
        return jobServiceLiftPlusStatus;
    }

    public void setJobServiceLiftPlusStatus(String jobServiceLiftPlusStatus) {
        this.jobServiceLiftPlusStatus = jobServiceLiftPlusStatus;
    }

    public int getJobServiceLiftPlusPrice() {
        return jobServiceLiftPlusPrice;
    }

    public void setJobServiceLiftPlusPrice(int jobServiceLiftPlusPrice) {
        this.jobServiceLiftPlusPrice = jobServiceLiftPlusPrice;
    }

    public String getJobServiceCartStatus() {
        return jobServiceCartStatus;
    }

    public void setJobServiceCartStatus(String jobServiceCartStatus) {
        this.jobServiceCartStatus = jobServiceCartStatus;
    }

    public int getJobServiceCartPrice() {
        return jobServiceCartPrice;
    }

    public void setJobServiceCartPrice(int jobServiceCartPrice) {
        this.jobServiceCartPrice = jobServiceCartPrice;
    }

    public int getJobChargeStartPrice() {
        return jobChargeStartPrice;
    }

    public void setJobChargeStartPrice(int jobChargeStartPrice) {
        this.jobChargeStartPrice = jobChargeStartPrice;
    }

    public int getJobChargeStartKm() {
        return jobChargeStartKm;
    }

    public void setJobChargeStartKm(int jobChargeStartKm) {
        this.jobChargeStartKm = jobChargeStartKm;
    }

    public int getJobCharge() {
        return jobCharge;
    }

    public void setJobCharge(int jobCharge) {
        this.jobCharge = jobCharge;
    }

    public String getJobStatusName() {
        return jobStatusName;
    }

    public void setJobStatusName(String jobStatusName) {
        this.jobStatusName = jobStatusName;
    }

    public double getJobDistance() {
        return jobDistance;
    }

    public void setJobDistance(double jobDistance) {
        this.jobDistance = jobDistance;
    }

    public String getJobCreatedAt() {
        return jobCreatedAt;
    }

    public void setJobCreatedAt(String jobCreatedAt) {
        this.jobCreatedAt = jobCreatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImgName() {
        return userImgName;
    }

    public void setUserImgName(String userImgName) {
        this.userImgName = userImgName;
    }

    public String getUserFcmId() {
        return userFcmId;
    }

    public void setUserFcmId(String userFcmId) {
        this.userFcmId = userFcmId;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getDriverDetailType() {
        return driverDetailType;
    }

    public void setDriverDetailType(String driverDetailType) {
        this.driverDetailType = driverDetailType;
    }

    public String getJobImg1() {
        return jobImg1;
    }

    public void setJobImg1(String jobImg1) {
        this.jobImg1 = jobImg1;
    }

    public String getJobImg2() {
        return jobImg2;
    }

    public void setJobImg2(String jobImg2) {
        this.jobImg2 = jobImg2;
    }

    public String getJobImg3() {
        return jobImg3;
    }

    public void setJobImg3(String jobImg3) {
        this.jobImg3 = jobImg3;
    }
}
