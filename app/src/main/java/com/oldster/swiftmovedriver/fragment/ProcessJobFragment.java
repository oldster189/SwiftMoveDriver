package com.oldster.swiftmovedriver.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.activity.HistoryJobActivity;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.dao.JobDataWithImageDao;
import com.oldster.swiftmovedriver.dao.TemplatesMessageDado;
import com.oldster.swiftmovedriver.databinding.FragmentProcessJobBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.manager.SharedPreferencesJobProcess;
import com.oldster.swiftmovedriver.util.DateTimeValue;
import com.oldster.swiftmovedriver.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class ProcessJobFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, View.OnClickListener, RoutingListener {

    /********************
     * Variable
     ********************/

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String TAG = ProcessJobFragment.class.getSimpleName();
    private FragmentProcessJobBinding mBind;
    private Vibrator mVibrate;
    private ProgressDialog progressDialog;
    private Handler handle;
    private int checkState = 0;
    private int counter = 0;
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private static final int START_PROGRESS = 4;
    private JobDataWithImageDao jobData;
    private LatLng start;
    private LatLng waypoint;
    private LatLng end;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorGreenBlue, R.color.colorAccent, R.color.colorStack};
    private boolean isFromPosition = true;
    private SharedPreferencesJobProcess mPref;
    private DecimalFormat df;
    private DriverDataManager driverDataManager;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "state_process_job";

    /********************
     * Functions
     ********************/
    public ProcessJobFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ProcessJobFragment newInstance(JobDataWithImageDao jobData) {
        ProcessJobFragment fragment = new ProcessJobFragment();
        Bundle args = new Bundle();
        args.putParcelable("jobData", jobData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobData = getArguments().getParcelable("jobData");
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_process_job, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        mPref = new SharedPreferencesJobProcess();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        polylines = new ArrayList<>();
        driverDataManager = new DriverDataManager();
        pref = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    @SuppressWarnings("UnusedParameters")


    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            public String toName;
            public String frName;
            public String distance;
            public String price;
            public String jid;
            public String status;
            public JSONObject data;
            public String payload;
            public String message;
            public String title;

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
        if (jobData != null) {
            int state = pref.getInt("checkState", 0);
            if (state != 0) {
                checkState = state;
                switch (checkState) {
                    case 1:
                        mBind.tvMessageNavigation.setText("เลื่อนเมื่อถึงต้นทางแล้ว");
                        break;
                    case 2:
                        mBind.tvMessageNavigation.setText("เลื่อนเมื่อถึงปลายทางแล้ว");
                        break;
                    case 3:
                        mBind.tvMessageNavigation.setText("เลื่อนเพื่อปิดงาน");
                        break;
                }
            }

            mBind.relativeProcessJob.setVisibility(View.VISIBLE);
            mBind.tvMessageProcessJob.setVisibility(View.GONE);
            DecimalFormat df2 = new DecimalFormat("0.00");
            df = new DecimalFormat("000000");
            Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bangna-new.ttf");
            mBind.tvMessageNavigation.setTypeface(type);
            mBind.tvIdJob.setText("#" + df.format(jobData.getJobId()) + "");

            DateTimeValue date = new DateTimeValue(jobData.getJobDate(), jobData.getJobTime());
            mBind.tvDateTime.setText(date.getDate() + " " + date.getMount() + " " + date.getYear() + " | " + date.getTime() + " น.");
            mBind.tvPositionFrom.setText(jobData.getJobFromNameAddress());
            mBind.tvPositionTo.setText(jobData.getJobToNameAddress());


            int chargeStartPrice = jobData.getJobChargeStartPrice();
            int chargeStartKm = jobData.getJobChargeStartKm();
            int chargeRate = jobData.getJobCharge();


            double distance = jobData.getJobDistance();

            String liftStatus = jobData.getJobServiceLiftStatus();
            int liftPrice;
            if (liftStatus.equals("t")) {
                liftPrice = jobData.getJobServiceLiftPrice();
            } else {
                liftPrice = 0;
            }


            String liftPlusStatus = jobData.getJobServiceLiftPlusStatus();

            int liftPlusPrice;
            if (liftPlusStatus.equals("t")) {
                liftPlusPrice = jobData.getJobServiceLiftPlusPrice();
            } else {
                liftPlusPrice = 0;
            }

            String cartStatus = jobData.getJobServiceCartStatus();
            int cartPrice;
            if (cartStatus.equals("t")) {
                cartPrice = jobData.getJobServiceCartPrice();
            } else {
                cartPrice = 0;
            }
            double dis2 = distance - chargeStartKm;
            if (dis2 <= 0) {
                dis2 = 0;
            }

            double total = (dis2 * chargeRate) + liftPrice + liftPlusPrice + cartPrice + chargeStartPrice;

            mBind.tvTotalPrice.setText("฿ " + df2.format(total) + "");
            mBind.tvTypeCar.setText("" + df2.format(jobData.getJobDistance()) + " กม.");
            progressDialog = new ProgressDialog(getContext());
            mBind.seekBarNext.setOnSeekBarChangeListener(this);
            mBind.cardViewContact.setOnClickListener(this);
            mBind.cardViewNavigation.setOnClickListener(this);
            mVibrate = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            mBind.relativeProcessJob.setVisibility(View.GONE);
            mBind.tvMessageProcessJob.setVisibility(View.VISIBLE);
        }


    }

    private void getRouting() {
        start = new LatLng(latitude, longitude);
        waypoint = new LatLng(jobData.getJobFromLatitude(), jobData.getJobFromLongitude());
        end = new LatLng(jobData.getJobToLatitude(), jobData.getJobToLongitude());

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(start, waypoint, end)
                .build();
        routing.execute();
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
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
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

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        //TODO: Save Instance State data
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        //TODO: Restore Instance State data
    }


    /********************
     * Listener Zone
     ********************/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekBar.setProgress(3);
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        int mProgress = seekBar.getProgress();
        if (mProgress >= 0 && mProgress <= START_PROGRESS) {
            seekBar.setProgress(START_PROGRESS);
        }
        if (mProgress > START_PROGRESS && mProgress <= 50) {
            // Vibrate for 300 milliseconds
            seekBar.setProgress(START_PROGRESS);
            mVibrate.vibrate(200);
        } else if (mProgress > 50) {
            // Vibrate for 300 milliseconds
            seekBar.setProgress(97);
            mVibrate.vibrate(200);
            progressDialog.setMessage("กำลังปรับปรุงสถานะ");
            progressDialog.show();

            checkState++;
            editor.putInt("checkState", checkState);
            editor.apply();

            handle = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    progressDialog.dismiss();
                    switch (checkState) {
                        case 1:
                            mPref.setJobDataDao(jobData);
                            isFromPosition = true;
                            mBind.tvMessageNavigation.setText("เลื่อนเมื่อถึงต้นทางแล้ว");
                            mBind.tvNavigation.setText("นำทางไปต้นทาง");
                            seekBar.setProgress(START_PROGRESS);
                            updateJobStatus("อยู่ระหว่างดำเนินการ", "รายการหมายเลข #" + df.format(jobData.getJobId()) + " ผู้ให้บริการได้เริ่มออกเดินทางแล้ว โปรดเตรียมสิ่งของให้พร้อมสำหรับการขนย้าย");
                            break;
                        case 2:
                            isFromPosition = false;
                            mBind.tvMessageNavigation.setText("เลื่อนเมื่อถึงปลายทางแล้ว");
                            mBind.tvNavigation.setText("นำทางไปปลายทาง");
                            seekBar.setProgress(START_PROGRESS);
                            sentNotificationToUser("รายการหมายเลข #" + df.format(jobData.getJobId()) + " ผู้ให้บริการถึงต้นทางเรียบร้อยแล้ว");
                            break;
                        case 3:
                            mBind.tvMessageNavigation.setText("เลื่อนเพื่อปิดงาน");
                            seekBar.setProgress(START_PROGRESS);
                            sentNotificationToUser("รายการหมายเลข #" + df.format(jobData.getJobId()) + " ผู้ให้บริการถึงปลายทางเรียบร้อยแล้ว");
                            break;
                        default:
                            updateJobStatus2("เสร็จสิ้น", "รายการหมายเลข #" + df.format(jobData.getJobId()) + " ได้ขนย้ายสิ่งของสำเร็วแล้ว ขอบคุณที่ใช้บริการ");
                            editor.clear();
                            editor.commit();

                    }
                }
            };
            handle.sendEmptyMessageDelayed(0, 1000);

        }
    }

    private void updateJobStatus(String status, String message) {
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .driverUpdateJobStatus(jobData.getJobId(),
                        status,
                        message,
                        jobData.getUserId());
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "ปรับปรุงสถานะสำเร็จ!!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("job response", "มีข้อผิดพลาด" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.e("job onFailure", t.toString());
            }
        });
    }

    private void updateJobStatus2(String status, String message) {
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .driverUpdateJobStatus2(jobData.getJobId(),
                        status,
                        message,
                        jobData.getUserId(),
                        driverDataManager.getDao().getData().get(0).getDriverId());
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    mPref.clear();
                    startActivity(new Intent(getContext(), HistoryJobActivity.class));
                    getActivity().finish();
                    Toast.makeText(getContext(), "ปรับปรุงสถานะสำเร็จ!!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("job response", "มีข้อผิดพลาด" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.e("job onFailure", t.toString());
            }
        });
    }

    private void sentNotificationToUser(String message) {
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .sendNotificationToUser(jobData.getUserId(), message);
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "ปรับปรุงสถานะสำเร็จ!!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("job response", "มีข้อผิดพลาด" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.e("job onFailure", t.toString());
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (jobData != null)
            getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        getRouting();
    }

    @Override
    public void onClick(View v) {
        if (v == mBind.cardViewNavigation) {
            if (isFromPosition) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                        jobData.getJobFromLatitude() + "," + jobData.getJobFromLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } else {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                        jobData.getJobToLatitude() + "," + jobData.getJobToLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        }
        if (v == mBind.cardViewContact) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jobData.getUserTel()));
            startActivity(intent);
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 16));
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(getContext(), COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        MarkerOptions markFrom = new MarkerOptions().position(start).title(getString(R.string.marker_title_from));
        markFrom.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car2));

        MarkerOptions markerWaypoint = new MarkerOptions().position(waypoint).title(getString(R.string.marker_title_from));
        markerWaypoint.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_form));

        MarkerOptions markerTo = new MarkerOptions().position(end).title(getString(R.string.marker_title_to));
        markerTo.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_to));

        mMap.addMarker(markFrom);
        mMap.addMarker(markerWaypoint);
        mMap.addMarker(markerTo);
    }

    @Override
    public void onRoutingCancelled() {

    }
    /********************
     * Inner Class
     ********************/
}
