package com.oldster.swiftmovedriver.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 5/12/2559.
 */

public class DriverDataDao {
    @SerializedName("driver_id")
    @Expose
    private int driverId;
    @SerializedName("driver_fname")
    @Expose
    private String driverFname;
    @SerializedName("driver_lname")
    @Expose
    private String driverLname;
    @SerializedName("driver_email")
    @Expose
    private String driverEmail;
    @SerializedName("driver_tel")
    @Expose
    private String driverTel;
    @SerializedName("driver_fcm_id")
    @Expose
    private String driverFcmId;
    @SerializedName("driver_address")
    @Expose
    private String driverAddress;
    @SerializedName("driver_id_card")
    @Expose
    private String driverIdCard;
    @SerializedName("driver_sex")
    @Expose
    private String driverSex;
    @SerializedName("driver_position_latitude")
    @Expose
    private double driverPositionLatitude;
    @SerializedName("driver_position_longitude")
    @Expose
    private double driverPositionLongitude;
    @SerializedName("driver_province")
    @Expose
    private String driverProvince;
    @SerializedName("driver_img_name")
    @Expose
    private String driverImgName;
    @SerializedName("driver_created_at")
    @Expose
    private String driverCreatedAt;
    @SerializedName("driver_change_at")
    @Expose
    private String driverChangeAt;
    @SerializedName("driver_detail_id")
    @Expose
    private int driverDetailId;
    @SerializedName("driver_detail_type")
    @Expose
    private String driverDetailType;
    @SerializedName("driver_detail_brand")
    @Expose
    private String driverDetailBrand;
    @SerializedName("driver_detail_model")
    @Expose
    private String driverDetailModel;
    @SerializedName("driver_detail_color")
    @Expose
    private String driverDetailColor;
    @SerializedName("driver_detail_license_plate")
    @Expose
    private String driverDetailLicensePlate;
    @SerializedName("driver_detail_province_license_plate")
    @Expose
    private String driverDetailProvinceLicensePlate;
    @SerializedName("driver_detail_service_lift_status")
    @Expose
    private String driverDetailServiceLiftStatus;
    @SerializedName("driver_detail_service_lift_price")
    @Expose
    private int driverDetailServiceLiftPrice;
    @SerializedName("driver_detail_service_lift_plus_status")
    @Expose
    private String driverDetailServiceLiftPlusStatus;
    @SerializedName("driver_detail_service_lift_plus_price")
    @Expose
    private int driverDetailServiceLiftPlusPrice;
    @SerializedName("driver_detail_service_cart_status")
    @Expose
    private String driverDetailServiceCartStatus;
    @SerializedName("driver_detail_service_cart_price")
    @Expose
    private int driverDetailServiceCartPrice;
    @SerializedName("driver_detail_charge_start_price")
    @Expose
    private int driverDetailChargeStartPrice;
    @SerializedName("driver_detail_charge_start_km")
    @Expose
    private int driverDetailChargeStartKm;
    @SerializedName("driver_detail_charge")
    @Expose
    private int driverDetailCharge;

    public String getDriverIdCard() {
        return driverIdCard;
    }

    public void setDriverIdCard(String driverIdCard) {
        this.driverIdCard = driverIdCard;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverFname() {
        return driverFname;
    }

    public void setDriverFname(String driverFname) {
        this.driverFname = driverFname;
    }

    public String getDriverLname() {
        return driverLname;
    }

    public void setDriverLname(String driverLname) {
        this.driverLname = driverLname;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverTel() {
        return driverTel;
    }

    public void setDriverTel(String driverTel) {
        this.driverTel = driverTel;
    }

    public String getDriverFcmId() {
        return driverFcmId;
    }

    public void setDriverFcmId(String driverFcmId) {
        this.driverFcmId = driverFcmId;
    }

    public String getDriverAddress() {
        return driverAddress;
    }

    public void setDriverAddress(String driverAddress) {
        this.driverAddress = driverAddress;
    }

    public String getDriverSex() {
        return driverSex;
    }

    public void setDriverSex(String driverSex) {
        this.driverSex = driverSex;
    }

    public double getDriverPositionLatitude() {
        return driverPositionLatitude;
    }

    public void setDriverPositionLatitude(double driverPositionLatitude) {
        this.driverPositionLatitude = driverPositionLatitude;
    }

    public double getDriverPositionLongitude() {
        return driverPositionLongitude;
    }

    public void setDriverPositionLongitude(double driverPositionLongitude) {
        this.driverPositionLongitude = driverPositionLongitude;
    }

    public String getDriverProvince() {
        return driverProvince;
    }

    public void setDriverProvince(String driverProvince) {
        this.driverProvince = driverProvince;
    }

    public String getDriverImgName() {
        return driverImgName;
    }

    public void setDriverImgName(String driverImgName) {
        this.driverImgName = driverImgName;
    }

    public String getDriverCreatedAt() {
        return driverCreatedAt;
    }

    public void setDriverCreatedAt(String driverCreatedAt) {
        this.driverCreatedAt = driverCreatedAt;
    }

    public String getDriverChangeAt() {
        return driverChangeAt;
    }

    public void setDriverChangeAt(String driverChangeAt) {
        this.driverChangeAt = driverChangeAt;
    }

    public int getDriverDetailId() {
        return driverDetailId;
    }

    public void setDriverDetailId(int driverDetailId) {
        this.driverDetailId = driverDetailId;
    }

    public String getDriverDetailType() {
        return driverDetailType;
    }

    public void setDriverDetailType(String driverDetailType) {
        this.driverDetailType = driverDetailType;
    }

    public String getDriverDetailBrand() {
        return driverDetailBrand;
    }

    public void setDriverDetailBrand(String driverDetailBrand) {
        this.driverDetailBrand = driverDetailBrand;
    }

    public String getDriverDetailModel() {
        return driverDetailModel;
    }

    public void setDriverDetailModel(String driverDetailModel) {
        this.driverDetailModel = driverDetailModel;
    }

    public String getDriverDetailColor() {
        return driverDetailColor;
    }

    public void setDriverDetailColor(String driverDetailColor) {
        this.driverDetailColor = driverDetailColor;
    }

    public String getDriverDetailLicensePlate() {
        return driverDetailLicensePlate;
    }

    public void setDriverDetailLicensePlate(String driverDetailLicensePlate) {
        this.driverDetailLicensePlate = driverDetailLicensePlate;
    }

    public String getDriverDetailProvinceLicensePlate() {
        return driverDetailProvinceLicensePlate;
    }

    public void setDriverDetailProvinceLicensePlate(String driverDetailProvinceLicensePlate) {
        this.driverDetailProvinceLicensePlate = driverDetailProvinceLicensePlate;
    }

    public String getDriverDetailServiceLiftStatus() {
        return driverDetailServiceLiftStatus;
    }

    public void setDriverDetailServiceLiftStatus(String driverDetailServiceLiftStatus) {
        this.driverDetailServiceLiftStatus = driverDetailServiceLiftStatus;
    }

    public int getDriverDetailServiceLiftPrice() {
        return driverDetailServiceLiftPrice;
    }

    public void setDriverDetailServiceLiftPrice(int driverDetailServiceLiftPrice) {
        this.driverDetailServiceLiftPrice = driverDetailServiceLiftPrice;
    }

    public String getDriverDetailServiceLiftPlusStatus() {
        return driverDetailServiceLiftPlusStatus;
    }

    public void setDriverDetailServiceLiftPlusStatus(String driverDetailServiceLiftPlusStatus) {
        this.driverDetailServiceLiftPlusStatus = driverDetailServiceLiftPlusStatus;
    }

    public int getDriverDetailServiceLiftPlusPrice() {
        return driverDetailServiceLiftPlusPrice;
    }

    public void setDriverDetailServiceLiftPlusPrice(int driverDetailServiceLiftPlusPrice) {
        this.driverDetailServiceLiftPlusPrice = driverDetailServiceLiftPlusPrice;
    }

    public String getDriverDetailServiceCartStatus() {
        return driverDetailServiceCartStatus;
    }

    public void setDriverDetailServiceCartStatus(String driverDetailServiceCartStatus) {
        this.driverDetailServiceCartStatus = driverDetailServiceCartStatus;
    }

    public int getDriverDetailServiceCartPrice() {
        return driverDetailServiceCartPrice;
    }

    public void setDriverDetailServiceCartPrice(int driverDetailServiceCartPrice) {
        this.driverDetailServiceCartPrice = driverDetailServiceCartPrice;
    }

    public int getDriverDetailChargeStartPrice() {
        return driverDetailChargeStartPrice;
    }

    public void setDriverDetailChargeStartPrice(int driverDetailChargeStartPrice) {
        this.driverDetailChargeStartPrice = driverDetailChargeStartPrice;
    }

    public int getDriverDetailChargeStartKm() {
        return driverDetailChargeStartKm;
    }

    public void setDriverDetailChargeStartKm(int driverDetailChargeStartKm) {
        this.driverDetailChargeStartKm = driverDetailChargeStartKm;
    }

    public int getDriverDetailCharge() {
        return driverDetailCharge;
    }

    public void setDriverDetailCharge(int driverDetailCharge) {
        this.driverDetailCharge = driverDetailCharge;
    }
}
