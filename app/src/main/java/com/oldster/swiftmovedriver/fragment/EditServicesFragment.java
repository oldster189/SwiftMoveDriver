package com.oldster.swiftmovedriver.fragment;

import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.dao.DriverDataCollectionDao;
import com.oldster.swiftmovedriver.dao.DriverDataDao;
import com.oldster.swiftmovedriver.databinding.FragmentEditServicesBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditServicesFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = EditServicesFragment.class.getSimpleName();
    private FragmentEditServicesBinding mBind;
    private ProgressDialog progressDialog;
    private DriverDataManager driverDataManager;
    private DriverDataDao driver;
    private String liftStatus;
    private String cartStatus;
    private String liftPlusStatus;
    private int startKm, startPrice, ratePrice, serviceLiftPrice = 0, serviceLifPlusPrice = 0, serviceCartPrice = 0;
    private boolean serviceLiftStatus = false, serviceLiftPlusStatus = false, serviceCartStatus = false;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public EditServicesFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static EditServicesFragment newInstance() {
        EditServicesFragment fragment = new EditServicesFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_services, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        driverDataManager = new DriverDataManager();
        driver = driverDataManager.getDao().getData().get(0);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ข้อมูลค่าบริการ");
        }
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
        progressDialog = new ProgressDialog(getContext());
        mBind.cbLiftStatus.setOnClickListener(this);
        mBind.cbLiftPlusStatus.setOnClickListener(this);
        mBind.cbCartStatus.setOnClickListener(this);
        mBind.btnSave.setOnClickListener(this);
        loadDataServices();

    }

    private void loadDataServices() {
        mBind.editTextStartKm.setText(driver.getDriverDetailChargeStartKm() + "");
        mBind.editTextStartPrice.setText(driver.getDriverDetailChargeStartPrice() + "");
        mBind.editTextRatePrice.setText(driver.getDriverDetailCharge() + "");
        liftStatus = driver.getDriverDetailServiceLiftStatus();
        liftPlusStatus = driver.getDriverDetailServiceLiftPlusStatus();
        cartStatus = driver.getDriverDetailServiceCartStatus();
        if (liftStatus.equals("t")) {
            mBind.cbLiftStatus.setChecked(true);
            mBind.editTextLiftPrice.setEnabled(true);
            mBind.editTextLiftPrice.setText(driver.getDriverDetailServiceLiftPrice() + "");
        }
        if (liftPlusStatus.equals("t")) {
            mBind.cbLiftPlusStatus.setChecked(true);
            mBind.editTextLiftPlusPrice.setEnabled(true);
            mBind.editTextLiftPlusPrice.setText(driver.getDriverDetailServiceLiftPlusPrice() + "");
        }
        if (cartStatus.equals("t")) {
            mBind.cbCartStatus.setChecked(true);
            mBind.editTextCartPrice.setEnabled(true);
            mBind.editTextCartPrice.setText(driver.getDriverDetailServiceCartPrice() + "");
        }

    }

    private void updateDataService() {
        if (!validateStartKm()) {
            return;
        }
        if (!validateStartPrice()) {
            return;
        }
        if (!validateRatePrice()) {
            return;
        }
        if (!validateServiceLiftPrice()) {
            return;
        }
        if (!validateServiceLiftPlusPrice()) {
            return;
        }
        if (!validateServiceCartPrice()) {
            return;
        }

        progressDialog.setMessage("กำลังบันทึกข้อมูล...");
        progressDialog.show();
        Call<DriverDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .updateDetailService(driver.getDriverId(),serviceLiftStatus,serviceLiftPrice,
                        serviceLiftPlusStatus,serviceLifPlusPrice,serviceCartStatus,serviceCartPrice,
                        startPrice,startKm,ratePrice,mBind.editTextPasswordOld.getText().toString());
        call.enqueue(new Callback<DriverDataCollectionDao>() {
            @Override
            public void onResponse(Call<DriverDataCollectionDao> call, Response<DriverDataCollectionDao> response) {
                if (response.isSuccessful()) {
                    DriverDataCollectionDao dao = response.body();
                    if (dao.isSuccess()){
                        driverDataManager.setDao(dao);
                        showToast("ปรับปรุงข้อมูลสำเร็จ");
                        mBind.editTextPasswordOld.setText("");
                        hideKeybroad(mBind.editTextPasswordOld);
                    }else {
                        Log.e(TAG, dao.getMessage());
                        showToast(dao.getMessage());
                    }
                } else {
                    showToast("ปรับปรุงข้อมูลไม่สำเร็จ!");
                    Log.e(TAG, response.errorBody().toString());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DriverDataCollectionDao> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                showToast("การเชื่อมต่อเครือข่ายล้มเหลว กรุณาลองใหม่อีกครั้ง!");
            }
        });

    }

    private boolean validateStartKm() {
        if (mBind.editTextStartKm.getText().toString().trim().isEmpty()) {
            mBind.inputLayoutStartKm.setError("ระบุระยะทางเริ่มต้น");
            requestFocus(mBind.editTextStartKm);
            return false;
        } else {
            startKm = Integer.parseInt(mBind.editTextStartKm.getText().toString().trim());
            mBind.inputLayoutStartKm.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateStartPrice() {
        if (mBind.editTextStartPrice.getText().toString().trim().isEmpty()) {
            mBind.inputLayoutStartPrice.setError("ระบุค่าบริการเริ่มต้น");
            requestFocus(mBind.editTextStartPrice);
            return false;
        } else {
            startPrice = Integer.parseInt(mBind.editTextStartPrice.getText().toString().trim());
            mBind.inputLayoutStartPrice.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateRatePrice() {
        if (mBind.editTextRatePrice.getText().toString().trim().isEmpty()) {
            mBind.inputLayoutRatePrice.setError("ระบุค่าบริการตามระยะทาง");
            requestFocus(mBind.editTextRatePrice);
            return false;
        } else {
            ratePrice = Integer.parseInt(mBind.editTextRatePrice.getText().toString().trim());
            mBind.inputLayoutRatePrice.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateServiceLiftPrice() {
        if (mBind.cbLiftStatus.isChecked()) {
            if (mBind.editTextLiftPrice.getText().toString().trim().isEmpty()) {
                mBind.editTextLiftPrice.setError("ระบุราคา");
                requestFocus(mBind.editTextLiftPrice);
                return false;
            } else {
                serviceLiftStatus = true;
                serviceLiftPrice = Integer.parseInt(mBind.editTextLiftPrice.getText().toString().trim());
            }
        }else {
            serviceLiftStatus = false;
            serviceLiftPrice = 0;
        }
        return true;
    }

    private boolean validateServiceLiftPlusPrice() {
        if (mBind.cbLiftPlusStatus.isChecked()) {

            if (mBind.editTextLiftPlusPrice.getText().toString().trim().isEmpty()) {
                mBind.editTextLiftPlusPrice.setError("ระบุราคา");
                requestFocus(mBind.editTextLiftPlusPrice);
                return false;
            } else {
                serviceLifPlusPrice = Integer.parseInt(mBind.editTextLiftPlusPrice.getText().toString().trim());
                serviceLiftPlusStatus = true;
            }
        }else {
            serviceLifPlusPrice = 0;
            serviceLiftPlusStatus = false;
        }
        return true;
    }

    private boolean validateServiceCartPrice() {
        if (mBind.cbCartStatus.isChecked()) {
            if (mBind.editTextCartPrice.getText().toString().trim().isEmpty()) {
                mBind.editTextCartPrice.setError("ระบุราคา");
                requestFocus(mBind.editTextCartPrice);
                return false;
            } else {
                serviceCartPrice = Integer.parseInt(mBind.editTextCartPrice.getText().toString().trim());
                serviceCartStatus = true;
            }
        }else {
            serviceCartPrice =0;
            serviceCartStatus = false;
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    private void hideKeybroad(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void showToast(String text) {
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT).show();


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

    @Override
    public void onClick(View view) {
        if (view==mBind.btnSave){
            if (mBind.editTextPasswordOld.getText().toString().length() >= 8) {
                updateDataService();
                mBind.inputLayoutPasswordOld.setErrorEnabled(false);
            } else {
                mBind.inputLayoutPasswordOld.setError("ระบุรหัสผ่านปัจจุบัน");
                requestFocus(mBind.editTextPasswordOld);
            }
        }
        if (view == mBind.cbLiftStatus) {
            if (mBind.cbLiftStatus.isChecked()) {
                mBind.editTextLiftPrice.setEnabled(true);
                requestFocus(mBind.editTextLiftPrice);
            } else {
                mBind.editTextLiftPrice.setEnabled(false);
            }
        }
        if (view == mBind.cbLiftPlusStatus) {
            if (mBind.cbLiftPlusStatus.isChecked()) {
                mBind.editTextLiftPlusPrice.setEnabled(true);
                requestFocus(mBind.editTextLiftPlusPrice);
            } else {
                mBind.editTextLiftPlusPrice.setEnabled(false);
            }
        }
        if (view == mBind.cbCartStatus) {
            if (mBind.cbCartStatus.isChecked()) {
                mBind.editTextCartPrice.setEnabled(true);
                requestFocus(mBind.editTextCartPrice);
            } else {
                mBind.editTextCartPrice.setEnabled(false);
            }
        }
    }


    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}
