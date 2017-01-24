package com.oldster.swiftmovedriver.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, DirectionCallback {

    /********************
     * Variable
     ********************/
    private String TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private LatLng origin;
    private LatLng destination;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int CODE_GET_CURRENT = 1111;
    private static final int CODE_ENABLE_MY_LOCATION = 2222;
    private double frLat;
    private double frLng;
    private double toLat;
    private double toLng;

    /********************
     * Functions
     ********************/
    public MapFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MapFragment newInstance(double frLat, double frLng, double toLat, double toLng) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putDouble("frLat", frLat);
        args.putDouble("frLng", frLng);
        args.putDouble("toLat", toLat);
        args.putDouble("toLng", toLng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frLat = getArguments().getDouble("frLat");
        frLng = getArguments().getDouble("frLng");
        toLat = getArguments().getDouble("toLat");
        toLng = getArguments().getDouble("toLng");

        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        origin = new LatLng(frLat, frLng);
        destination = new LatLng(toLat, toLng);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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


    private void requestDirection() {
        final String serverKey = "AIzaSyCPIc8hnlJy3VYV-IO8aIVatPUJVaNtw3Q";

        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .language(Language.THAI)
                .unit(Unit.METRIC)
                .execute(this);
    }

    private void animateCameraDirection() {
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination)
                .build();


        // mBinding.textViewDistance.setText(getString(R.string.route_distances) + " " + df.format(distanceKm) + " " + getString(R.string.kilomaters));


//        float bear = Float.parseFloat(bearing(latitudeFrom, longitudeFrom, latitudeTo, longitudeTo) + "");
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .bearing(bear)
//                .target(bounds.getCenter())
//                .build();
//
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));

    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODE_ENABLE_MY_LOCATION);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_ENABLE_MY_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), PackageManager.PERMISSION_GRANTED + "", Toast.LENGTH_SHORT).show();
                    // Permission Granted
                    enableMyLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /********************
     * Listener Zone
     ********************/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        requestDirection();
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            MarkerOptions markFrom = new MarkerOptions().position(origin).title(getString(R.string.marker_title_from));
            markFrom.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_form));
            MarkerOptions markerTo = new MarkerOptions().position(destination).title(getString(R.string.marker_title_to));
            markerTo.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_to));

            mMap.addMarker(markFrom);
            mMap.addMarker(markerTo);

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 2, Color.RED));

            animateCameraDirection();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }
    /********************
     * Inner Class
     ********************/
}
