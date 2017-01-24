package com.oldster.swiftmovedriver.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.ForgotPasswordActivity;
import com.oldster.swiftmovedriver.activity.MainActivity;
import com.oldster.swiftmovedriver.activity.RegisterActivity;
import com.oldster.swiftmovedriver.dao.DriverDataCollectionDao;
import com.oldster.swiftmovedriver.databinding.FragmentLoginBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment implements View.OnClickListener {

    /********************
     * Variable
     ********************/
    private String TAG = LoginFragment.class.getSimpleName();
    private FragmentLoginBinding mBind;
    private String email;
    private String pass;
    private DriverDataManager driverDataManager;
    private DriverDataCollectionDao dataDriver;
    private ProgressDialog progressDialog;

    /********************
     * Functions
     ********************/
    public LoginFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
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
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GrandHotel-Regular.otf");
        mBind.tvLogo.setTypeface(type);

        mBind.textViewSignup.setOnClickListener(this);
        mBind.tvForgotPassword.setOnClickListener(this);
        mBind.btnLogin.setOnClickListener(this);

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


    @Override
    public void onClick(View v) {
        if (v == mBind.textViewSignup) {
            startActivity(new Intent(getContext(), RegisterActivity.class));
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }
        if (v == mBind.tvForgotPassword) {
            startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
            getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
        }
        if (v == mBind.btnLogin) {

            loginDriver();
        }
    }


    private void loginDriver() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        progressDialog.setMessage("กำลังบันทึกข้อมูล...");
        progressDialog.show();
        Call<DriverDataCollectionDao> call = HttpManager.getInstance()
                .getService().loginDriver(email, pass);
        call.enqueue(new Callback<DriverDataCollectionDao>() {
            @Override
            public void onResponse(Call<DriverDataCollectionDao> call, Response<DriverDataCollectionDao> response) {
                if (response.isSuccessful()) {
                    dataDriver = response.body();
                    if (dataDriver.isSuccess()) {
                        driverDataManager.setDao(dataDriver);
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), dataDriver.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), dataDriver.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, response.errorBody().toString());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DriverDataCollectionDao> call, Throwable t) {
                Log.e(TAG + " onFailure ", t.toString());
                progressDialog.dismiss();
            }
        });

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private boolean validateEmail() {
        email = mBind.editTextEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
            requestFocus(mBind.editTextEmail);
            return false;
        } else {
            mBind.inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        pass = mBind.editTextPassword.getText().toString().trim();
        if (pass.isEmpty() || pass.length() < 8) {
            mBind.inputLayoutPassword.setError("ระบุรหัสผ่าน");
            requestFocus(mBind.editTextPassword);
            return false;
        } else {
            mBind.inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }
    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}
