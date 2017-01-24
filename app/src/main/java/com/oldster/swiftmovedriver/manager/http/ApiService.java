package com.oldster.swiftmovedriver.manager.http;

import com.oldster.swiftmovedriver.constants.EndPoints;
import com.oldster.swiftmovedriver.dao.CommentDataCollectionDao;
import com.oldster.swiftmovedriver.dao.DriverDataCollectionDao;
import com.oldster.swiftmovedriver.dao.JobDataWithImageCollectionDao;
import com.oldster.swiftmovedriver.dao.RatingDataCollectionDao;
import com.oldster.swiftmovedriver.dao.TemplatesMessageDado;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Old'ster on 11/8/2559.
 */
public interface ApiService {


    /**
     * FOR DRIVER
     */


    ///////////////////////////POST///////////////////////////////////
    @FormUrlEncoded
    @POST(EndPoints.DRIVER_NOTIFICATION_USER)
    Call<TemplatesMessageDado> sendNotificationToUser(@Path("uid") int uid,
                                                      @Field("body") String body);

    /**
     * NEW
     **/
    @FormUrlEncoded
    @POST(EndPoints.DRIVER_CREATE)
    Call<DriverDataCollectionDao> registerDriver(@Field("fname") String fname,
                                                 @Field("lname") String lname,
                                                 @Field("email") String email,
                                                 @Field("password") String password,
                                                 @Field("tel") String tel,
                                                 @Field("id_card") String idCard,
                                                 @Field("address") String address,
                                                 @Field("sex") String sex,
                                                 @Field("province") String province,
                                                 @Field("typeCar") String typeCar,
                                                 @Field("brandCar") String brandCar,
                                                 @Field("modelCar") String modelCar,
                                                 @Field("colorCar") String colorCar,
                                                 @Field("plateCar") String plateCar,
                                                 @Field("provincePlateCar") String provincePlateCar,
                                                 @Field("liftStatus") boolean liftStatus,
                                                 @Field("liftPrice") int liftPrice,
                                                 @Field("liftPlusStatus") boolean liftPlusStatus,
                                                 @Field("liftPlusPrice") int liftPlusPrice,
                                                 @Field("cartStatus") boolean cartStatus,
                                                 @Field("cartPrice") int cartPrice,
                                                 @Field("startPrice") int startPrice,
                                                 @Field("startKm") int startKm,
                                                 @Field("chargeRate") int chargeRate
    );

    @FormUrlEncoded
    @POST(EndPoints.DRIVER_LOGIN)
    Call<DriverDataCollectionDao> loginDriver(@Field("email") String email, @Field("password") String password);


    ////////////////////////////PUT///////////////////////////////////
    @FormUrlEncoded
    @PUT(EndPoints.DRIVER_UPDATE_FCM)
    Call<TemplatesMessageDado> driverUpdateFcm(@Path("did") int did,
                                               @Field("driver_fcm_id") String driverFcmId);

    @FormUrlEncoded
    @PUT(EndPoints.DRIVER_UPDATE_JOB_STATUS)
    Call<TemplatesMessageDado> driverUpdateJobStatus(@Path("jid") int jid,
                                                     @Field("status_name") String statusName,
                                                     @Field("status_message") String statusMessage,
                                                     @Field("user_id") int userId);

    @FormUrlEncoded
    @PUT(EndPoints.DRIVER_UPDATE_JOB_STATUS_FINISH)
    Call<TemplatesMessageDado> driverUpdateJobStatus2(@Path("jid") int jid,
                                                      @Field("status_name") String statusName,
                                                      @Field("status_message") String statusMessage,
                                                      @Field("user_id") int userId,
                                                      @Field("driver_id") int driverId);

    /**
     * NEW
     **/
    @FormUrlEncoded
    @PUT(EndPoints.DRIVER_UPDATE_CAR)
    Call<DriverDataCollectionDao> updateDetailCar(@Path("did") int did,
                                                  @Field("type_car") String typeCar,
                                                  @Field("brand_car") String brandCar,
                                                  @Field("model_car") String modelCar,
                                                  @Field("color_car") String colorCar,
                                                  @Field("plate_car") String plateCar,
                                                  @Field("province_plate_car") String provincePlateCar,
                                                  @Field("password_old") String passwordOld);

    @FormUrlEncoded
    @PUT(EndPoints.DRIVER_UPDATE_SERVICE)
    Call<DriverDataCollectionDao> updateDetailService(@Path("did") int did,
                                                  @Field("lift_status") boolean liftStatus,
                                                  @Field("lift_price") int liftPrice,
                                                  @Field("lift_plus_status") boolean liftPlusStatus,
                                                  @Field("lift_plus_price") int liftPlusPrice,
                                                  @Field("cart_status") boolean cartStatus,
                                                  @Field("cart_price") int cartPrice,
                                                  @Field("start_price") int startPrice,
                                                  @Field("start_km") int startKm,
                                                  @Field("charge_rate") int chargeRate,
                                                  @Field("password_old") String passwordOld);
    @FormUrlEncoded
    @PUT(EndPoints.DRIVER_UPDATE_INFO)
    Call<DriverDataCollectionDao> updateDetailInfo(@Path("did") int did,
                                                  @Field("fname") String fname,
                                                  @Field("lname") String lname,
                                                  @Field("email") String email,
                                                  @Field("tel") String tel,
                                                  @Field("id_card") String idCard,
                                                  @Field("address") String address,
                                                  @Field("sex") String sex,
                                                  @Field("province") String province,
                                                  @Field("img_name") String imgName,
                                                  @Field("img_encode") String imgEncode,
                                                  @Field("password_new") String passwordNew,
                                                  @Field("password_old") String passwordOld);
    @FormUrlEncoded
    @PUT(EndPoints.DRIVER_UPDATE_POSITION)
    Call<TemplatesMessageDado> updateDriverPosition(@Path("did") int did,
                                                  @Field("lat") double lat,
                                                  @Field("lng") double lng);

    ////////////////////////////GET/////////////////////////////////////
    @GET(EndPoints.DRIVER_GET_DATA_JOB_PROCESS)
    Call<JobDataWithImageCollectionDao> loadDataJobProcess(@Path("did") int did);

    @GET(EndPoints.DRIVER_GET_DATA_JOB_HISTORY)
    Call<JobDataWithImageCollectionDao> loadDataJobHistory(@Path("did") int did);

    @GET(EndPoints.DRIVER_GET_DATA_JOB_Discard)
    Call<JobDataWithImageCollectionDao> loadDataJobDiscard(@Path("did") int did);

    @GET(EndPoints.DRIVER_GET_ID_JOB)
    Call<JobDataWithImageCollectionDao> loadDataJobById(@Path("jid") int jid);

    @GET(EndPoints.DRIVER_GET_DATA_COMMENT)
    Call<CommentDataCollectionDao> loadDataComment(@Path("did") int did);

    @GET(EndPoints.DRIVER_GET_SUM_RATING)
    Call<RatingDataCollectionDao> loadDataRating(@Path("did") int did);
    /**
     * NEW
     */



}
