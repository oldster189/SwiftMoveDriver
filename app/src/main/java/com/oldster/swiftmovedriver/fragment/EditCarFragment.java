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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.dao.DriverDataCollectionDao;
import com.oldster.swiftmovedriver.dao.DriverDataDao;
import com.oldster.swiftmovedriver.databinding.FragmentEditCarBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditCarFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = EditCarFragment.class.getSimpleName();
    private FragmentEditCarBinding mBind;
    private ProgressDialog progressDialog;
    private String typeCar, brandCar, modelCar, colorCar, provincePlateCar, plateCar;
    private String[] dataTypeCar;
    private String[] dataProvince;
    private DriverDataManager driverDataManager;
    private DriverDataDao driver;
    private String typeCarThai;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public EditCarFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static EditCarFragment newInstance() {
        EditCarFragment fragment = new EditCarFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_car, container, false);
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




        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ข้อมูลรถที่ให้บริการ");
        }
        progressDialog = new ProgressDialog(getContext());
        switch (driver.getDriverDetailType()) {
            case "Pickup":
                typeCarThai = "รถกระบะ";
                break;
            case "Truck":
                typeCarThai = "รถกระบะทึบ";
                break;
            case "EcoCar":
                typeCarThai = "รถ 5 ประตู";
                break;
        }
        loadDataCarDriver();
        mBind.btnSave.setOnClickListener(this);

    }

    private void loadDataCarDriver() {
        dataProvince = getResources().getStringArray(R.array.province);
        ArrayAdapter<CharSequence> adapterProvince = ArrayAdapter.createFromResource(getContext(),
                R.array.province, android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerProvince.setAdapter(adapterProvince);
        mBind.spinnerProvince.setOnItemSelectedListener(provincePlateListener);
        dataTypeCar = getResources().getStringArray(R.array.typeCar);
        ArrayAdapter<CharSequence> adapterTypeCar = ArrayAdapter.createFromResource(getContext(),
                R.array.typeCar, android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerTypeCar.setAdapter(adapterTypeCar);
        mBind.spinnerTypeCar.setOnItemSelectedListener(typeCarListener);
        for (int i = 0; i < dataTypeCar.length; i++) {
            if (dataTypeCar[i].equals(typeCarThai)) {
                mBind.spinnerTypeCar.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < dataProvince.length; i++) {
            if (dataProvince[i].equals(driver.getDriverDetailProvinceLicensePlate())) {
                mBind.spinnerProvince.setSelection(i);
                break;
            }
        }
        mBind.editTextModel.setText(driver.getDriverDetailModel());
        mBind.editTextBrand.setText(driver.getDriverDetailBrand());
        mBind.editTextColor.setText(driver.getDriverDetailColor());
        mBind.editTextPlateCar.setText(driver.getDriverDetailLicensePlate());

    }

    private void updateDataCarDriver() {
        if (!validateBrandCar()) {
            return;
        }
        if (!validateModelCar()) {
            return;
        }
        if (!validateColorCar()) {
            return;
        }
        if (!validatePlateCar()) {
            return;
        }
        progressDialog.setMessage("กำลังบันทึกข้อมูล...");
        progressDialog.show();
        Call<DriverDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .updateDetailCar(driver.getDriverId(), typeCar, brandCar, modelCar,
                        colorCar, plateCar, provincePlateCar, mBind.editTextPasswordOld.getText().toString());
        call.enqueue(new Callback<DriverDataCollectionDao>() {
            @Override
            public void onResponse(Call<DriverDataCollectionDao> call, Response<DriverDataCollectionDao> response) {
                if (response.isSuccessful()) {
                    DriverDataCollectionDao dao = response.body();
                    if (dao.isSuccess()) {
                        driverDataManager.setDao(dao);
                        showToast("ปรับปรุงข้อมูลสำเร็จ");
                        mBind.editTextPasswordOld.setText("");
                    } else {
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

    private void showToast(String text) {
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private boolean validateBrandCar() {
        brandCar = mBind.editTextBrand.getText().toString().trim();

        if (brandCar.isEmpty()) {
            mBind.inputLayoutBrand.setError("ระบุยี่ห้อรถ");
            requestFocus(mBind.editTextBrand);
            return false;
        } else {
            mBind.inputLayoutBrand.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateModelCar() {
        modelCar = mBind.editTextModel.getText().toString().trim();

        if (modelCar.isEmpty()) {
            mBind.inputLayoutModel.setError("ระบุรุ่นรถ");
            requestFocus(mBind.editTextModel);
            return false;
        } else {
            mBind.inputLayoutModel.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateColorCar() {
        colorCar = mBind.editTextColor.getText().toString().trim();

        if (colorCar.isEmpty()) {
            mBind.inputLayoutColor.setError("ระบุสีรถ");
            requestFocus(mBind.editTextColor);
            return false;
        } else {
            mBind.inputLayoutColor.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePlateCar() {
        plateCar = mBind.editTextPlateCar.getText().toString().trim();

        if (plateCar.isEmpty()) {
            mBind.inputLayoutPlateCar.setError("ระบุป้ายทะเบียรถ");
            requestFocus(mBind.editTextPlateCar);
            return false;
        } else {
            mBind.inputLayoutPlateCar.setErrorEnabled(false);
        }
        return true;
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

    /********************
     * Listener Zone
     ********************/
    AdapterView.OnItemSelectedListener typeCarListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String type = dataTypeCar[i];
            switch (type) {
                case "รถกระบะ":
                    typeCar = "Pickup";
                    break;
                case "รถกระบะทึบ":
                    typeCar = "Truck";
                    break;
                default:
                    typeCar = "EcoCar";
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    final AdapterView.OnItemSelectedListener provincePlateListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            provincePlateCar = dataProvince[i];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public void onClick(View view) {
        if (view == mBind.btnSave) {
            if (mBind.editTextPasswordOld.getText().toString().length() >= 8) {
                updateDataCarDriver();
                mBind.inputLayoutPasswordOld.setErrorEnabled(false);
            } else {
                mBind.inputLayoutPasswordOld.setError("ระบุรหัสผ่านปัจจุบัน");
                requestFocus(mBind.editTextPasswordOld);
            }
        }
    }

    /********************
     * Inner Class
     ********************/
}
