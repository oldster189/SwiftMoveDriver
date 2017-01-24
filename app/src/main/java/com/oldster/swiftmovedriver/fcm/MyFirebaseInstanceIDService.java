package com.oldster.swiftmovedriver.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.oldster.swiftmovedriver.manager.DriverUpdateFcmManager;


/**
 * Created by Old'ster on 24/8/2559.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        //Save token to disk cache
        savePreferencesFcm(refreshToken);
    }

    private void savePreferencesFcm(String refreshToken) {
        DriverUpdateFcmManager updateFcmManager = new DriverUpdateFcmManager();
        updateFcmManager.storeFcmToken(refreshToken);
    }
}
