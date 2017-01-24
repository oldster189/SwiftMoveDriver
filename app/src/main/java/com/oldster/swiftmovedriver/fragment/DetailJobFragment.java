package com.oldster.swiftmovedriver.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.activity.MainActivity;
import com.oldster.swiftmovedriver.activity.MapActivity;
import com.oldster.swiftmovedriver.activity.ProcessJobActivity;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.constants.EndPoints;
import com.oldster.swiftmovedriver.dao.JobDataWithImageCollectionDao;
import com.oldster.swiftmovedriver.dao.JobDataWithImageDao;
import com.oldster.swiftmovedriver.dao.TemplatesMessageDado;
import com.oldster.swiftmovedriver.databinding.FragmentDetailJobBinding;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.util.DateTimeValue;
import com.oldster.swiftmovedriver.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class DetailJobFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = DetailJobFragment.class.getSimpleName();

    private FragmentDetailJobBinding mBind;
    private JobDataWithImageDao jobData;
    private String jid;
    private double total;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public DetailJobFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static DetailJobFragment newInstance(String jid) {
        DetailJobFragment fragment = new DetailJobFragment();
        Bundle args = new Bundle();
        args.putString("jid", jid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jid = getArguments().getString("jid");

        init(savedInstanceState);
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_job, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
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


        mBind.tvMap.setOnClickListener(this);
        mBind.btnConfirm.setOnClickListener(this);
        mBind.btnCancel.setOnClickListener(this);
        mBind.btnGo.setOnClickListener(this);

        loadDataJobById();

    }

    private void loadDataJobById() {
        int id = Integer.parseInt(jid);
        Call<JobDataWithImageCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataJobById(id);
        call.enqueue(new Callback<JobDataWithImageCollectionDao>() {
            @Override
            public void onResponse(Call<JobDataWithImageCollectionDao> call, Response<JobDataWithImageCollectionDao> response) {
                if (response.isSuccessful()) {
                    JobDataWithImageCollectionDao dataCollectionDao = response.body();
                    jobData = dataCollectionDao.getData().get(0);
                    DecimalFormat df2 = new DecimalFormat("0.00");
                    String dateCurrent = getDateCurrent();
                    String dateJob = jobData.getJobDate() + " " + jobData.getJobTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date d1 = null;
                    Date d2 = null;
                    try {
                        d1 = format.parse(dateCurrent);
                        d2 = format.parse(dateJob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long diff = (d2 != null ? d2.getTime() : 0) - (d1 != null ? d1.getTime() : 0);
                    long diffHours = diff / (60 * 60 * 1000);

                    if (diffHours > 48) {
                        mBind.tvStatusJob.setText("งานในอนาคต");
                        mBind.linStatusJob.setBackgroundResource(R.drawable.bg_oval_red_pink);
                    } else if (diffHours > 24) {
                        mBind.tvStatusJob.setText("งานพรุ่งนี้");
                        mBind.linStatusJob.setBackgroundResource(R.drawable.bg_oval_purple);
                    } else if (diffHours > 0) {
                        mBind.tvStatusJob.setText("งานวันนี้");
                        mBind.linStatusJob.setBackgroundResource(R.drawable.bg_oval);
                    }
                    DateTimeValue date = new DateTimeValue(jobData.getJobDate(), jobData.getJobTime());
                    mBind.tvDateTime.setText(date.getDate() + " " + date.getMount() + " " + date.getYear() + " | " + date.getTime() + " น.");

                    switch (jobData.getDriverDetailType()) {
                        case "Pickup":
                            mBind.tvTypeCar.setText("รถกระบะ");
                            break;
                        case "Truck":
                            mBind.tvTypeCar.setText("รถกระบะตู้ทึบ");
                            break;
                        default:
                            mBind.tvTypeCar.setText("รถ 5 ประตู");
                            break;
                    }

                    if (jobData.getJobStatusName().equals("รอการยืนยัน")) {
                        mBind.btnCancel.setVisibility(View.VISIBLE);
                        mBind.btnConfirm.setVisibility(View.VISIBLE);
                        mBind.btnGo.setVisibility(View.GONE);
                    } else {
                        mBind.btnCancel.setVisibility(View.GONE);
                        mBind.btnConfirm.setVisibility(View.GONE);
                        mBind.btnGo.setVisibility(View.VISIBLE);
                    }
                    mBind.tvUser.setText(jobData.getUserFirstName() + " " + jobData.getUserLastName());

                    double distance = jobData.getJobDistance();
                    mBind.tvDistance.setText("" + df2.format(distance) + " กม.");

                    mBind.tvPositionFrom.setText(jobData.getJobFromNameAddress());
                    mBind.tvPositionTo.setText(jobData.getJobToNameAddress());
                    mBind.tvPositionTo.setText(jobData.getJobToNameAddress());
                    if (!jobData.getJobImg1().equals("")) {
                        Glide.with(getContext())
                                .load(EndPoints.BASE_URL_IMG_JOB + jobData.getJobImg1())
                                .crossFade()
                                .into(mBind.imgView1);
                    } else {
                        mBind.imgView1.setVisibility(View.GONE);
                    }
                    if (!jobData.getJobImg2().equals("")) {
                        Glide.with(getContext())
                                .load(EndPoints.BASE_URL_IMG_JOB + jobData.getJobImg2())
                                .crossFade()
                                .into(mBind.imgView2);
                    } else {
                        mBind.imgView2.setVisibility(View.GONE);
                    }
                    if (!jobData.getJobImg3().equals("")) {
                        Glide.with(getContext())
                                .load(EndPoints.BASE_URL_IMG_JOB + jobData.getJobImg3())
                                .crossFade()
                                .into(mBind.imgView3);
                    } else {
                        mBind.imgView3.setVisibility(View.GONE);
                    }


                    int chargeStartPrice = jobData.getJobChargeStartPrice();
                    mBind.tvChargeStartPrice.setText("฿ " + df2.format(chargeStartPrice) + "");

                    int chargeStartKm = jobData.getJobChargeStartKm();
                    int chargeRate = jobData.getJobCharge();
                    double distance2 = distance - chargeStartKm;
                    if (distance2 <= 0) {
                        distance2 = 0;
                    }
                    double ratePrice = distance2 * chargeRate;
                    mBind.tvChargePrice.setText("฿ " + df2.format(ratePrice) + "");
                    mBind.tvChargeKm.setText("ระยะทาง ( " + df2.format(distance2) + " กม.)");

                    String liftStatus = jobData.getJobServiceLiftStatus();
                    int liftPrice;
                    if (liftStatus.equals("t")) {
                        liftPrice = jobData.getJobServiceLiftPrice();
                        if (liftPrice == 0) {
                            mBind.tvServiceLift.setText("ฟรี");
                        } else {
                            mBind.tvServiceLift.setText("฿ " + df2.format(liftPrice) + "");
                        }
                    } else {
                        liftPrice = 0;
                        mBind.frameLift.setVisibility(View.GONE);
                    }


                    String liftPlusStatus = jobData.getJobServiceLiftPlusStatus();

                    int liftPlusPrice;
                    if (liftPlusStatus.equals("t")) {
                        liftPlusPrice = jobData.getJobServiceLiftPlusPrice();
                        if (liftPlusPrice == 0) {
                            mBind.tvServiceLiftPlusPrice.setText("ฟรี");
                        } else {
                            mBind.tvServiceLiftPlusPrice.setText("฿ " + df2.format(liftPlusPrice) + "");
                        }
                    } else {
                        liftPlusPrice = 0;
                        mBind.frameLiftPlus.setVisibility(View.GONE);
                    }

                    String cartStatus = jobData.getJobServiceCartStatus();
                    int cartPrice;
                    if (cartStatus.equals("t")) {
                        cartPrice = jobData.getJobServiceCartPrice();
                        if (cartPrice == 0) {
                            mBind.tvServiceCartPrice.setText("ฟรี");
                        } else {
                            mBind.tvServiceCartPrice.setText("฿ " + df2.format(cartPrice) + "");
                        }
                    } else {
                        cartPrice = 0;
                        mBind.frameCart.setVisibility(View.GONE);
                    }

                    total = (distance2 * chargeRate) + liftPrice + liftPlusPrice + cartPrice + chargeStartPrice;
                    mBind.tvListTotal.setText("฿ " + df2.format(total) + "");

                } else {
                    Log.e("job response", "มีข้อผิดพลาด" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<JobDataWithImageCollectionDao> call, Throwable t) {
                Log.e("job onFailure", t.toString());
            }
        });
    }

    private String getDateCurrent() {
        final Calendar c = Calendar.getInstance();
        int pYear = c.get(Calendar.YEAR);
        int pMonth = c.get(Calendar.MONTH);
        int pDay = c.get(Calendar.DAY_OF_MONTH);
        int pHour = c.get(Calendar.HOUR_OF_DAY);
        int pMin = c.get(Calendar.MINUTE);
        int pSec = c.get(Calendar.SECOND);
        return pYear + "-" + (pMonth + 1) + "-" + pDay + " " + pHour + ":" + pMin + ":" + pSec;
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

    private void displayAlertConfirm() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String message = "ยืนยันการรับงาน";
        builder.setMessage(message)
                .setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("ยืนยัน",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DecimalFormat df = new DecimalFormat("000000");
                                updateJobStatus("รอการดำเนินการ", "ผู้ให้บริการได้ยืนยันการรับงานหมายเลข #" + df.format(jobData.getJobId()) + " เรียบร้อยแล้ว\nรอการดำเนินการตามเวลาที่กำหนด");
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
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
                    startActivity(new Intent(getContext(), MainActivity.class));

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

    private void displayAlertCancel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String message = "ยืนยันการยกเลิก";
        builder.setMessage(message)
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("ยืนยัน",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DecimalFormat df = new DecimalFormat("000000");
                                updateJobStatus("ยกเลิก", "ผู้ให้บริการได้ยกเลิกรายการหมายเลข #" + df.format(jobData.getJobId()) + " ของคุณ");
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {

        if (v == mBind.tvMap) {
            Intent intent = new Intent(getContext(), MapActivity.class);
            intent.putExtra("frLat", jobData.getJobFromLatitude());
            intent.putExtra("frLng", jobData.getJobFromLongitude());
            intent.putExtra("toLat", jobData.getJobToLatitude());
            intent.putExtra("toLng", jobData.getJobToLongitude());
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }

        if (v == mBind.btnConfirm) {
            displayAlertConfirm();
        }
        if (v == mBind.btnCancel) {
            displayAlertCancel();
        }
        if (v == mBind.btnGo) {
            Intent intent = new Intent(getContext(), ProcessJobActivity.class);
            intent.putExtra("jobData", jobData);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }
    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}
