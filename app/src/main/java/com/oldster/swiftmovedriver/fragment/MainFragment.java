package com.oldster.swiftmovedriver.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.adapter.CardProcessJobAdapter;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.dao.JobDataWithImageCollectionDao;
import com.oldster.swiftmovedriver.dao.TemplatesMessageDado;
import com.oldster.swiftmovedriver.databinding.FragmentMainBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.manager.JobProcessManager;
import com.oldster.swiftmovedriver.util.NotificationUtils;
import com.oldster.swiftmovedriver.util.VerticalSpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class MainFragment extends Fragment implements View.OnClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1234;
    /********************
     * Variable
     ********************/
    private FragmentMainBinding mBind;
    private String TAG = MainFragment.class.getSimpleName();
    private CardProcessJobAdapter cardProcessJobAdapter;
    private JobProcessManager jobProcessManager;
    private JobDataWithImageCollectionDao jobDataCollectionDao;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private GoogleApiClient googleApiClient;
    private double latitude;
    private double longitude;
    private DriverDataManager driverDataManager;
    private TemplatesMessageDado dao;


    private String title;
    private String message;
    private String payload;
    private JSONObject data;
    private String jid;
    private String status;
    private String price;
    private String distance;
    private String frName;
    private String toName;

    /********************
     * Functions
     ********************/
    public MainFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        driverDataManager = new DriverDataManager();
        cardProcessJobAdapter = new CardProcessJobAdapter(getContext());
        jobProcessManager = new JobProcessManager();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Contextor.getInstance().getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));
        //  cardProcessJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
        mBind.recyclerView.setAdapter(cardProcessJobAdapter);
        mBind.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
        mBind.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataJob();

            }
        });



        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    try {
                        title = intent.getStringExtra("title"); //check
                        message = intent.getStringExtra("message");//check
                        payload = intent.getStringExtra("payload");//check
                        data = new JSONObject(payload);//check
                        status = data.getString("status");//check
                        if (status.equals("new_job")) {
                            jid = data.getString("jid");
                            price = data.getString("price");
                            distance = data.getString("distance");
                            frName = data.getString("fr_name");
                            toName = data.getString("to_name");
                        }
                        if (status.equals("cancel_job")) {

                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Exception: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }

                    //Handle Code Here!!
                    loadDataJob();
                    if (status.equals("new_job")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(title)
                                .setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_advertising))
                                .setMessage("รายการหมายเลข : #000" + jid + "\n" +
                                        "ต้นทาง : " + frName + "\n" +
                                        "ปลายทาง : " + toName + "\n" +
                                        "ค่าบริการ : " + price + "\n" +
                                        "ระยะทาง : " + distance)
                                .setPositiveButton("ดูรายละเอียด", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getActivity(), DetailJobActivity.class);
                                        intent.putExtra("jid", jid);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                                    }
                                })

                                .setNegativeButton("ดูภายหลัง", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    } else if (status.equals("cancel_job")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(title)
                                .setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_advertising))
                                .setMessage(message)
                                .setPositiveButton("รับทราบ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                }


            }
        };
        loadDataJob();
        Log.e("asdasd","asdasdasd");
    }

    private void loadDataJob() {
        Log.e("did",driverDataManager.getDao().getData().get(0).getDriverId()+"");
        Call<JobDataWithImageCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataJobProcess(driverDataManager.getDao().getData().get(0).getDriverId());
        call.enqueue(new Callback<JobDataWithImageCollectionDao>() {
            @Override
            public void onResponse(Call<JobDataWithImageCollectionDao> call, Response<JobDataWithImageCollectionDao> response) {
                if (response.isSuccessful()) {
                    mBind.recyclerView.removeAllViews();
                    jobDataCollectionDao = response.body();
                    mBind.progressBar.setVisibility(View.GONE);
                    if (jobDataCollectionDao.getData().size() == 0) {
                        mBind.textViewMessage.setVisibility(View.VISIBLE);
                        mBind.imgNo.setVisibility(View.VISIBLE);
                        jobProcessManager.setJobDataCollectionDao(jobDataCollectionDao);
                        cardProcessJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
                    } else {
                        jobProcessManager.setJobDataCollectionDao(jobDataCollectionDao);
                        cardProcessJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
                        mBind.textViewMessage.setVisibility(View.GONE);
                        mBind.recyclerView.setVisibility(View.VISIBLE);
                        mBind.imgNo.setVisibility(View.GONE);
                    }
                    cardProcessJobAdapter.notifyDataSetChanged();
                    Log.e(TAG + " isSuccessful true", "true size"+jobDataCollectionDao.getData().size());
                } else {
                    Log.e(TAG + " isSuccessful else", response.errorBody().toString());
                }
                mBind.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JobDataWithImageCollectionDao> call, Throwable t) {
                mBind.progressBar.setVisibility(View.GONE);
                mBind.textViewMessage.setVisibility(View.VISIBLE);
                Log.e(TAG + " onFailure", t.toString());
                mBind.swipeRefreshLayout.setRefreshing(false);
                mBind.imgNo.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getCurrentLocation() {
        //CHECK PERMISSION ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission ACCESS_FINE_LOCATION
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        //Get current location
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    getCurrentLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(Contextor.getInstance().getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    /*
             * Save Instance State Here
             */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.e(TAG, "latitude " + latitude);
        Log.e(TAG, "longitude " + longitude);
        sentPositionDriver();
    }

    private void sentPositionDriver() {
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .updateDriverPosition(driverDataManager.getDao().getData().get(0).getDriverId(), latitude, longitude);
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    dao = response.body();
                    if (dao.isSuccess()) {
                        Log.e(TAG, dao.getMessage());
                    } else {
                        Log.e(TAG, dao.getMessage());
                    }
                } else {
                    Log.e(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}
