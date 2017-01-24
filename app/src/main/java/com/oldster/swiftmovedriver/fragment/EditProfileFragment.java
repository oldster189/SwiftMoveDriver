package com.oldster.swiftmovedriver.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.constants.EndPoints;
import com.oldster.swiftmovedriver.dao.DriverDataCollectionDao;
import com.oldster.swiftmovedriver.dao.DriverDataDao;
import com.oldster.swiftmovedriver.databinding.FragmentEditProfileBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_PICKER = 134;
    /********************
     * Variable
     ********************/
    private String TAG = EditProfileFragment.class.getSimpleName();
    private FragmentEditProfileBinding mBind;
    private ProgressDialog progressDialog;
    private DriverDataManager driverDataManager;
    private DriverDataDao driver;
    private String province;
    private String imgEncode;
    private String imgName;
    private ArrayList<Image> images = new ArrayList<>();
    private String encodedImage;
    private Bitmap yourbitmap;
    private int n;
    private Subscription subScription;
    private boolean isEditPassword = false;
    private String pw;
    private boolean checkInputPassword = false;
    private String ch;
    private int num = 0;
    private int lower = 0;
    private int upper = 0;
    private String email;
    private String fname;
    private String lname;
    private String tel;
    private String pass;
    private String address;
    private String idCard;
    private String gender;
    private boolean isUpdate = true;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public EditProfileFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
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
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ข้อมูลส่วนตัว");
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
        loadDriverData();
        mBind.btnEditPassword.setOnClickListener(this);
        mBind.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mBind.inputLayoutPassword.setErrorEnabled(false);
                pw = mBind.editTextPassword.getText().toString();
                if (pw.length() >= 8) {
                    checkInputPassword = true;
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
                    checkInputPassword = false;
                }
                num = 0;
                lower = 0;
                upper = 0;
            }
        });
        mBind.btnSave.setOnClickListener(this);
        mBind.imageProfile.setOnClickListener(this);
    }

    private void loadDriverData() {
        if (driver.getDriverImgName() != null && !driver.getDriverImgName().equals("")) {
            Glide.with(getContext())
                    .load(EndPoints.BASE_URL_IMG_DRIVER + driver.getDriverImgName())
                    .into(mBind.imageProfile);
        }
        mBind.editTextEmail.setText(driver.getDriverEmail());
        mBind.editTextFname.setText(driver.getDriverFname());
        mBind.editTextLname.setText(driver.getDriverLname());
        mBind.editTextTel.setText(driver.getDriverTel());
        Log.e(TAG, "idCard:" + driver.getDriverIdCard() + " address:" + driver.getDriverAddress());
        mBind.editTextIdCard.setText(driver.getDriverIdCard());
        mBind.editTextAddress.setText(driver.getDriverAddress());
        if (driver.getDriverSex().equals("M")) {
            mBind.rbMale.setChecked(true);
        } else {
            mBind.rbFeMale.setChecked(true);
        }
        final String[] data = getResources().getStringArray(R.array.province);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.province, android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerProvince.setAdapter(adapter);

        for (int i = 0; i < data.length; i++) {
            if (data[i].equals(driver.getDriverProvince())) {
                mBind.spinnerProvince.setSelection(i);
                break;
            }
        }
        mBind.spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void updateDataDriver() {
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
        progressDialog.setMessage("กำลังปรับปรุงข้อมูล...");
        progressDialog.show();
        if (driver.getDriverImgName() != null) {
            n = checkSubString(driver.getDriverImgName());
        } else {
            n = 1;
        }
        if (images.size() > 0) {
            imgEncode = encodedImage;
            imgName = "img_profile_" + driver.getDriverId() + "_" + n + ".jpg";
        }
        if (isUpdate) {
            Call<DriverDataCollectionDao> call = HttpManager.getInstance()
                    .getService()
                    .updateDetailInfo(driver.getDriverId(), fname, lname, email, tel,
                            idCard, address, gender, province, imgName, imgEncode,
                            pass, mBind.editTextPasswordOld.getText().toString());
            call.enqueue(new Callback<DriverDataCollectionDao>() {
                @Override
                public void onResponse(Call<DriverDataCollectionDao> call, Response<DriverDataCollectionDao> response) {
                    if (response.isSuccessful()) {
                        DriverDataCollectionDao dao = response.body();
                        if (dao.isSuccess()){
                            driverDataManager.setDao(dao);
                            showToast("ปรับปรุงข้อมูลสำเร็จ");
                            mBind.editTextPassword.setEnabled(false);
                            mBind.editTextPassword.setText("********");
                            mBind.editTextPasswordOld.setText("");
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
    }
    private int checkSubString(String text) {
        ArrayList<String> result = new ArrayList<>();
        for (int k = text.length(); k > 4; k--) {
            result.add(text.substring(k - 5, k - 4));
            Log.e("result.add", text.substring(k - 5, k - 4));
            if (text.substring(k - 6, k - 5).equals("_")) {
                break;
            }
        }
        int i = 0;
        int sum = 0;
        Log.e("result.size", result.size() + "");
        while (i < result.size()) {
            if (i == 0) {
                sum = sum + Integer.parseInt(result.get(0));
            }
            if (i == 1) {
                sum = sum + (10 * Integer.parseInt(result.get(1)));
            }
            i++;
        }
        Log.e("sum", sum + "");
        return (sum + 1);
    }

    private void showFileChooser() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_SINGLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
        startActivityForResult(intent, REQUEST_CODE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            mBind.progressBarImage.setVisibility(View.VISIBLE);
            setImageByReactiveX();
            //setImage();
        }
    }

    private Bitmap getImagesBitmap() {
        try {
            yourbitmap = ImageLoader.init().from(images.get(0).getPath()).requestSize(512, 512).getBitmap();
            encodedImage = ImageBase64.encode(yourbitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return yourbitmap;
    }

    private void setImageByReactiveX() {
        Observable<Bitmap> listObservable = Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return getImagesBitmap();
            }
        });
        subScription = listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        mBind.progressBarImage.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG + " Throwable", e.toString());
                        mBind.progressBarImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        setImage();
                    }
                });
    }

    private void unsubscribe() {
        if (subScription != null && !subScription.isUnsubscribed()) {
            subScription.unsubscribe();
        }
    }

    private void setImage() {
        Glide.with(getContext())
                .load(images.get(0).getPath())
                .into(mBind.imageProfile);
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
        if (mBind.editTextPassword.isEnabled()) {
            pass = mBind.editTextPassword.getText().toString().trim();
            if (pass.isEmpty() || pass.length() < 8) {
                mBind.inputLayoutPassword.setError("ระบุอย่างน้อย 8 ตัวอักขระ");
                requestFocus(mBind.editTextPassword);
                isUpdate = false;
                return false;
            } else {
                isUpdate = true;
                mBind.inputLayoutPassword.setErrorEnabled(false);
            }
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

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
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
        if (view == mBind.btnSave) {
            if (mBind.editTextPasswordOld.getText().toString().length() >= 8) {
                updateDataDriver();
                mBind.inputLayoutPasswordOld.setErrorEnabled(false);
            } else {
                mBind.inputLayoutPasswordOld.setError("ระบุรหัสผ่านปัจจุบัน");
                requestFocus(mBind.editTextPasswordOld);
            }
        }

        if (view == mBind.btnEditPassword) {
            if (!isEditPassword) {
                mBind.editTextPassword.setEnabled(true);
                mBind.editTextPassword.setText("");
                mBind.btnEditPassword.setText("ยกเลิก");
                isEditPassword = true;
                requestFocus(mBind.editTextPassword);
            } else {
                mBind.editTextPassword.setEnabled(false);
                mBind.editTextPassword.setText("********");
                mBind.btnEditPassword.setText("แก้ไข");
                isEditPassword = false;
                mBind.tvTextLevelPassword.setText("");
            }
        }
        if (view == mBind.imageProfile) {
            showFileChooser();
        }

    }


    /********************
     * Listener Zone
     ********************/

    /********************
     * Inner Class
     ********************/
}
