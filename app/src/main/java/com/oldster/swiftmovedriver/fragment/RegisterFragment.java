package com.oldster.swiftmovedriver.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.databinding.FragmentRegisterBinding;


public class RegisterFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    /********************
     * Variable
     ********************/
    FragmentRegisterBinding mBind;
    private String TAG = RegisterFragment.class.getSimpleName();
    private int num = 0;
    private int lower = 0;
    private int upper = 0;
    private String pw;
    private String ch;

    private String fname, lname, email, pass, passre, tel, idCard, gender, province, address;
    private int count;
    private View lastView;


    /********************
     * Functions
     ********************/
    public RegisterFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
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
        mBind.btnNext.setOnClickListener(this);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GrandHotel-Regular.otf");
        mBind.tvLogo.setTypeface(type);
        final String[] data = getResources().getStringArray(R.array.province);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.province, android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerProvince.setAdapter(adapter);
        mBind.spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBind.editTextPassword.addTextChangedListener(new MyTextWatcher(mBind.editTextPassword));
        mBind.editTextFname.setOnFocusChangeListener(this);
        mBind.editTextLname.setOnFocusChangeListener(this);
        mBind.editTextEmail.setOnFocusChangeListener(this);
        mBind.editTextPassword.setOnFocusChangeListener(this);
        mBind.editTextPasswordRe.setOnFocusChangeListener(this);
        mBind.editTextTel.setOnFocusChangeListener(this);
        mBind.editTextIdCard.setOnFocusChangeListener(this);
        mBind.editTextAddress.setOnFocusChangeListener(this);
        mBind.btnNext.setOnClickListener(this);


    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
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

    private boolean validateFname() {
        fname = mBind.editTextFname.getText().toString().trim();

        if (fname.isEmpty()) {
            mBind.inputLayoutFname.setError("ระบุชื่อ");
            requestFocus(mBind.editTextFname);
            return false;
        } else {
            mBind.inputLayoutFname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLname() {
        lname = mBind.editTextLname.getText().toString().trim();

        if (lname.isEmpty()) {
            mBind.inputLayoutLname.setError("ระบุนามสกุล");
            requestFocus(mBind.editTextLname);
            return false;
        } else {
            mBind.inputLayoutLname.setErrorEnabled(false);
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

    private boolean validatePasswordRe() {
        passre = mBind.editTextPasswordRe.getText().toString().trim();
        if (passre.isEmpty() || !passre.equals(pass)) {
            mBind.inputLayoutPasswordRe.setError("รหัสผ่านไม่ตรงกัน ลองใหม่อีกครั้ง");
            requestFocus(mBind.editTextPasswordRe);
            return false;
        } else {
            mBind.inputLayoutPasswordRe.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTel() {
        tel = mBind.editTextTel.getText().toString().trim();
        if (tel.isEmpty() || tel.length() < 10) {
            mBind.inputLayoutTel.setError("ระบุหมายเลขโทรศัพท์");
            requestFocus(mBind.editTextTel);
            return false;
        } else {
            mBind.inputLayoutTel.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateIdCard() {
        idCard = mBind.editTextIdCard.getText().toString().trim();
        if (!checkIDCard(mBind.editTextIdCard.getText().toString())) {
            mBind.inputLayoutIdCard.setError("ระบุหมายเลขบัตรประชาชนให้ถูกต้อง");
            requestFocus(mBind.editTextIdCard);
            return false;
        } else {
            mBind.inputLayoutIdCard.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress() {
        address = mBind.editTextAddress.getText().toString().trim();
        if (address.isEmpty()) {
            mBind.inputLayoutAddress.setError("ระบุที่อยู่");
            requestFocus(mBind.editTextAddress);
            return false;
        } else {
            mBind.inputLayoutAddress.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mBind.btnNext) {
            sentDataToRegisterService();
        }
    }

    private void sentDataToRegisterService() {
        if (!validateFname()) {
            return;
        }
        if (!validateLname()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validatePasswordRe()) {
            return;
        }
        if (!validateTel()) {
            return;
        }
        if (!validateIdCard()) {
            return;
        }
        if (!validateAddress()) {
            return;
        }

        if (mBind.rgGender.getCheckedRadioButtonId() == R.id.rbFeMale) {
            gender = "F";
        } else {
            gender = "M";
        }

        FragmentTransaction f = getFragmentManager().beginTransaction();
        f.setCustomAnimations(
                R.anim.from_right, R.anim.to_left,
                R.anim.from_left, R.anim.to_right
        );
        f.replace(R.id.contentContainer, RegisterServiceFragment.newInstance(fname, lname, email, pass, tel, idCard, gender, province, address));
        f.addToBackStack(null);
        f.commit();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view == mBind.editTextFname) {
            if (!b) {
                if (mBind.editTextFname.getText().toString().isEmpty()) {
                    mBind.inputLayoutFname.setError("ระบุชื่อ");
                } else {
                    mBind.inputLayoutFname.setErrorEnabled(false);
                }
            } else {
                // mBind.inputLayoutFname.setErrorEnabled(false);
            }
        }
        if (view == mBind.editTextLname) {
            if (!b) {
                if (mBind.editTextLname.getText().toString().isEmpty()) {
                    mBind.inputLayoutLname.setError("ระบุนามสกุล");
                } else {
                    mBind.inputLayoutLname.setErrorEnabled(false);
                }
            } else {
                //  mBind.inputLayoutLname.setErrorEnabled(false);
            }
        }
        if (view == mBind.editTextEmail) {
            if (!b) {
                if (mBind.editTextEmail.getText().toString().isEmpty() || !isValidEmail(mBind.editTextEmail.getText().toString())) {
                    mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
                } else {
                    mBind.inputLayoutEmail.setErrorEnabled(false);
                }
            } else {
                //  mBind.inputLayoutEmail.setErrorEnabled(false);
            }
        }
        if (view == mBind.editTextPassword) {
            if (!b) {
                if (mBind.editTextPassword.getText().toString().isEmpty() || mBind.editTextPassword.getText().toString().length() < 8) {
                    mBind.inputLayoutPassword.setError("ระบุอย่างน้อย 8 ตัวอักขระ");
                } else {
                    mBind.inputLayoutPassword.setErrorEnabled(false);
                }
            } else {
                // mBind.inputLayoutPassword.setErrorEnabled(false);
                if (!isValidEmail(mBind.editTextEmail.getText().toString())) {
                    mBind.inputLayoutEmail.setError("ระบุอีเมลให้ถูกต้อง");
                }
            }
        }
        if (view == mBind.editTextPasswordRe) {
            if (!b) {
                if (mBind.editTextPasswordRe.getText().toString().isEmpty() ||
                        !mBind.editTextPasswordRe.getText().toString().equals(mBind.editTextPassword.getText().toString())) {
                    mBind.inputLayoutPasswordRe.setError("ระบุรหัสผ่านอีกครั้ง");
                } else {
                    mBind.inputLayoutPasswordRe.setErrorEnabled(false);
                }
            } else {
                // mBind.inputLayoutPasswordRe.setErrorEnabled(false);
                if (mBind.editTextPassword.getText().toString().length() < 8) {
                    mBind.inputLayoutPassword.setError("ระบุรหัสผ่านให้ถูกต้อง");
                }
            }
        }
        if (view == mBind.editTextTel) {
            if (!b) {
                if (mBind.editTextTel.getText().toString().isEmpty() || mBind.editTextTel.getText().toString().length() < 10) {
                    mBind.inputLayoutTel.setError("ระบุหมายเลขโทรศัพท์");
                } else {
                    mBind.inputLayoutTel.setErrorEnabled(false);
                }
            } else {
                //  mBind.inputLayoutTel.setErrorEnabled(false);
                if (!mBind.editTextPassword.getText().toString().equals(mBind.editTextPasswordRe.getText().toString())) {
                    mBind.inputLayoutPasswordRe.setError("รหัสผ่านไม่ตรงกัน ลองใหม่อีกครั้ง");
                }
            }
        }
        if (view == mBind.editTextIdCard) {
            if (!b) {
                if (!checkIDCard(mBind.editTextIdCard.getText().toString())) {
                    mBind.inputLayoutIdCard.setError("ระบุหมายเลขบัตรประชาชน");
                } else {
                    mBind.inputLayoutIdCard.setErrorEnabled(false);
                }
            } else {
                //mBind.inputLayoutIdCard.setErrorEnabled(false);
                if (mBind.editTextTel.getText().toString().length() != 10) {
                    mBind.inputLayoutTel.setError("ระบุหมายเลขโทรศัพท์ให้ถูกต้อง");
                }
            }
        }
        if (view == mBind.editTextAddress) {
            if (b) {
                if (!checkIDCard(mBind.editTextIdCard.getText().toString())) {
                    mBind.inputLayoutIdCard.setError("ระบุหมายเลขบัตรประชาชนให้ถูกต้อง");
                }
            }
        }
    }

    private boolean checkIDCard(String idCard) {
        if (idCard.length() != 13) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Integer.parseInt(idCard.charAt(i) + "") * (13 - i);
        }
        if ((11 - (sum % 11)) % 10 == Integer.parseInt(idCard.charAt(12) + ""))
            return true;
        return false;
    }

    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editTextPassword:
                    pw = mBind.editTextPassword.getText().toString();
                    if (pw.length() >= 8) {
                        for (int i = 0; i < pw.length(); i++) {
                            ch = String.valueOf(pw.charAt(i));
                            if (isNumeric(ch)) {
                                num++;
                            } else if (Character.isLowerCase(pw.charAt(i))) {
                                lower++;
                            } else if (Character.isUpperCase(pw.charAt(i))) {
                                upper++;
                            }
                        }
                        if (pw.length() >= 8 && lower >= 1 && upper >= 1 && num >= 1) {
                            mBind.tvTextLevelPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.newColorGreenNormal));
                            mBind.tvTextLevelPassword.setText("ความปลอดภัย : มาก ");
                        } else if (pw.length() >= 8 && lower >= 1 || upper >= 1) {
                            mBind.tvTextLevelPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
                            mBind.tvTextLevelPassword.setText("ความปลอดภัย : ปานกลาง ");
                        } else if (num >= 8 && lower == 0 && upper == 0) {
                            mBind.tvTextLevelPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                            mBind.tvTextLevelPassword.setText("ความปลอดภัย : น้อย ");
                        }
                    } else {
                        mBind.tvTextLevelPassword.setText("");
                    }
                    num = 0;
                    lower = 0;
                    upper = 0;
                    break;
            }

        }

    }

}
