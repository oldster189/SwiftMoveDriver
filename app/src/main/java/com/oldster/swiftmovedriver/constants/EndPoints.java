package com.oldster.swiftmovedriver.constants;

/**
 * Created by Lincoln on 06/01/16.
 */
public class EndPoints {


    //URL
  //private static final String URL = "http://192.168.137.1/swiftmove/";
   private static final String URL = "http://54.169.217.171/";
    public static final String BASE_URL_REST = URL + "v1/";

    //URL IMG
    public static final String BASE_URL_IMG = URL + "images/users/";
    public static final String BASE_URL_IMG_JOB = URL + "images/jobs/";

    /**
     * FOR DRIVER
     */

    public static final String BASE_URL_IMG_DRIVER = URL + "images/driver/";

    //POST
    public static final String DRIVER_GET_DATA_AFTER_SORT = "driver/position";
    public static final String DRIVER_NOTIFICATION_USER = "driver/notification/{uid}";

    /**
     * new
     */
    public static final String DRIVER_CREATE = "driver/create";
    public static final String DRIVER_LOGIN = "driver/login";




    //PUT

    public static final String DRIVER_UPDATE_FCM = "driver/update/fcm/{did}";
    public static final String DRIVER_UPDATE_JOB_STATUS = "driver/update/job/status/{jid}";
    public static final String DRIVER_UPDATE_JOB_STATUS_FINISH = "driver/update/job/status2/{jid}";

    /**
     * new
     */
    public static final String DRIVER_UPDATE_POSITION = "driver/update/position/{did}";
    public static final String DRIVER_UPDATE_SERVICE = "driver/update/service/{did}";
    public static final String DRIVER_UPDATE_CAR = "driver/update/car/{did}";
    public static final String DRIVER_UPDATE_INFO = "driver/update/info/{did}";

    //GET

    public static final String DRIVER_GET_DATA_JOB_PROCESS = "driver/job/process/data/{did}";
    public static final String DRIVER_GET_DATA_JOB_HISTORY = "driver/job/history/data/{did}";
    public static final String DRIVER_GET_DATA_JOB_Discard = "driver/job/discard/data/{did}";
    public static final String DRIVER_GET_ID_JOB = "driver/job/id/{jid}";
    public static final String DRIVER_GET_DATA_COMMENT = "driver/get/comment/{did}";
    /**
     * new
     */
    public static final String DRIVER_GET_SUM_RATING = "driver/get/sum/rating/{did}";
    public static final String DRIVER_GET_COMMENT = "/driver/get/comment/{did}";
    //PUSH Notification

    /**
     * FOR JOB
     */
    public static final String JOB_INSERT = "job/insert";

}
