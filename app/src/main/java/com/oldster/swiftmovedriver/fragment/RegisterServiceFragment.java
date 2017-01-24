package com.oldster.swiftmovedriver.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.MainActivity;
import com.oldster.swiftmovedriver.dao.DriverDataCollectionDao;
import com.oldster.swiftmovedriver.databinding.FragmentRegisterServiceBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterServiceFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private FragmentRegisterServiceBinding mBind;
    private String TAG = RegisterServiceFragment.class.getSimpleName();
    private String pw;
    private boolean checkInputPassword = false;
    private String ch;
    private int num = 0;
    private int lower = 0;
    private int upper = 0;
    private ProgressDialog progressDialog;
    private String fname, lname, email, pass, tel, idCard, gender, province, address;
    private String typeCar, brandCar, modelCar, colorCar, provincePlateCar, plateCar;
    private int startKm, startPrice, ratePrice, serviceLiftPrice = 0, serviceLifPlusPrice = 0, serviceCartPrice = 0;
    private boolean serviceLiftStatus = false, serviceLiftPlusStatus = false, serviceCartStatus = false;
    private String[] dataTypeCar;
    private String[] dataProvince;

    private DriverDataManager driverDataManager;
    private DriverDataCollectionDao dao;

    /********************
     * Functions
     ********************/
    public RegisterServiceFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static RegisterServiceFragment newInstance(
            String fname, String lname, String email,
            String pass, String tel, String idCard,
            String gender, String province, String address) {
        RegisterServiceFragment fragment = new RegisterServiceFragment();
        Bundle args = new Bundle();
        args.putString("fname", fname);
        args.putString("lname", lname);
        args.putString("email", email);
        args.putString("pass", pass);
        args.putString("tel", tel);
        args.putString("idCard", idCard);
        args.putString("gender", gender);
        args.putString("province", province);
        args.putString("address", address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fname = getArguments().getString("fname");
        lname = getArguments().getString("lname");
        email = getArguments().getString("email");
        tel = getArguments().getString("tel");
        idCard = getArguments().getString("idCard");
        gender = getArguments().getString("gender");
        province = getArguments().getString("province");
        address = getArguments().getString("address");
        pass = getArguments().getString("pass");

        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_register_service, container, false);

        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        driverDataManager = new DriverDataManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        progressDialog = new ProgressDialog(getContext());
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
        mBind.cbLift.setOnClickListener(this);
        mBind.cbLiftPlus.setOnClickListener(this);
        mBind.cbCart.setOnClickListener(this);
        mBind.btnLogin.setOnClickListener(this);
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
        if (mBind.cbLift.isChecked()) {
            if (mBind.editTextLiftPrice.getText().toString().trim().isEmpty()) {
                mBind.editTextLiftPrice.setError("ระบุราคา");
                requestFocus(mBind.editTextLiftPrice);
                return false;
            } else {
                serviceLiftStatus = true;
                serviceLiftPrice = Integer.parseInt(mBind.editTextLiftPrice.getText().toString().trim());
            }
        }
        return true;
    }

    private boolean validateServiceLiftPlusPrice() {
        if (mBind.cbLiftPlus.isChecked()) {

            if (mBind.editTextLiftPlusPrice.getText().toString().trim().isEmpty()) {
                mBind.editTextLiftPlusPrice.setError("ระบุราคา");
                requestFocus(mBind.editTextLiftPlusPrice);
                return false;
            } else {
                serviceLifPlusPrice = Integer.parseInt(mBind.editTextLiftPlusPrice.getText().toString().trim());
                serviceLiftPlusStatus = true;
            }
        }
        return true;
    }

    private boolean validateServiceCartPrice() {
        if (mBind.cbCart.isChecked()) {
            if (mBind.editTextCartPrice.getText().toString().trim().isEmpty()) {
                mBind.editTextCartPrice.setError("ระบุราคา");
                requestFocus(mBind.editTextCartPrice);
                return false;
            } else {
                serviceCartPrice = Integer.parseInt(mBind.editTextCartPrice.getText().toString().trim());
                serviceCartStatus = true;
            }
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    @Override
    public void onClick(View v) {
        if (v == mBind.cbLift) {
            if (mBind.cbLift.isChecked()) {
                mBind.editTextLiftPrice.setEnabled(true);
                requestFocus(mBind.editTextLiftPrice);
            } else {
                mBind.editTextLiftPrice.setEnabled(false);
            }
        }
        if (v == mBind.cbLiftPlus) {
            if (mBind.cbLiftPlus.isChecked()) {
                mBind.editTextLiftPlusPrice.setEnabled(true);
                requestFocus(mBind.editTextLiftPlusPrice);
            } else {
                mBind.editTextLiftPlusPrice.setEnabled(false);
            }
        }
        if (v == mBind.cbCart) {
            if (mBind.cbCart.isChecked()) {
                mBind.editTextCartPrice.setEnabled(true);
                requestFocus(mBind.editTextCartPrice);
            } else {
                mBind.editTextCartPrice.setEnabled(false);
            }
        }
        if (v == mBind.btnLogin) {
            registerDriver();
        }
    }

    private void registerDriver() {
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
                .registerDriver(fname, lname, email, pass, tel, idCard, address, gender, province, typeCar, brandCar, modelCar,
                        colorCar, plateCar, provincePlateCar, serviceLiftStatus, serviceLiftPrice,
                        serviceLiftPlusStatus, serviceLifPlusPrice, serviceCartStatus, serviceCartPrice, startPrice, startKm, ratePrice);
        call.enqueue(new Callback<DriverDataCollectionDao>() {
            @Override
            public void onResponse(Call<DriverDataCollectionDao> call, Response<DriverDataCollectionDao> response) {
                if (response.isSuccessful()) {
                    dao = response.body();
                    if (dao.isSuccess()) {
                        driverDataManager.setDao(dao);
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), dao.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), dao.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG + " else ", response.errorBody().toString());

                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DriverDataCollectionDao> call, Throwable t) {
                Log.e(TAG + " onFailure ", t.toString());
                Toast.makeText(getContext(), "ไม่สามารถติดต่อกับระบบได้!!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

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
    /********************
     * Inner Class
     ********************/
}
