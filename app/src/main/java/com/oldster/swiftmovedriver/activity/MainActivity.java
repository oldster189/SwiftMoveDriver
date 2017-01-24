package com.oldster.swiftmovedriver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.constants.EndPoints;
import com.oldster.swiftmovedriver.dao.DriverDataDao;
import com.oldster.swiftmovedriver.dao.JobDataWithImageDao;
import com.oldster.swiftmovedriver.dao.TemplatesMessageDado;
import com.oldster.swiftmovedriver.fcm.MyFirebaseInstanceIDService;
import com.oldster.swiftmovedriver.fragment.MainFragment;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.DriverUpdateFcmManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.manager.SharedPreferencesJobProcess;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private TextView textViewUsername;
    private CircleImageView imageProfileNav;
    private DriverUpdateFcmManager updateFcmManager;
    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    private SharedPreferencesJobProcess mPref;
    private DriverDataManager driverDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverDataManager = new DriverDataManager();
        if (driverDataManager.getCount() == 0) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);
                      if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, MainFragment.newInstance())
                        .commit();
            }
            initInstances();
        }
    }

    private void initInstances() {
        mPref = new SharedPreferencesJobProcess();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("รายการขนย้ายสิ่งของ");
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        imageProfileNav = (CircleImageView) headerLayout.findViewById(R.id.imageProfileNav);
        textViewUsername = (TextView) headerLayout.findViewById(R.id.textViewUsername);

        updateFcmManager = new DriverUpdateFcmManager();
        if (updateFcmManager.getFcmToken() != null) {
            sendFcmToServer();
        } else {
            MyFirebaseInstanceIDService mTokenID = new MyFirebaseInstanceIDService();
            mTokenID.onTokenRefresh();
            sendFcmToServer();
        }

        loadDriverData();

    }

    private void loadDriverData() {
        driverDataManager = new DriverDataManager();
        DriverDataDao driver = driverDataManager.getDao().getData().get(0);

        //SET DISPLAY NAME
        textViewUsername.setText(driver.getDriverFname() + " " + driver.getDriverLname());

        //SET DISPLAY IMAGE
        if (driver.getDriverImgName() != null && !driver.getDriverImgName().equals("")) {
            Glide.with(this)
                    .load(EndPoints.BASE_URL_IMG_DRIVER + driver.getDriverImgName())
                    .fitCenter()
                    .crossFade()
                    .into(imageProfileNav);
        }
    }

    private void sendFcmToServer() {
        String fcmToken = updateFcmManager.getFcmToken();
        int did = driverDataManager.getDao().getData().get(0).getDriverId();
        Call<TemplatesMessageDado> call = HttpManager.getInstance()
                .getService()
                .driverUpdateFcm(did, fcmToken);
        call.enqueue(new Callback<TemplatesMessageDado>() {
            @Override
            public void onResponse(Call<TemplatesMessageDado> call, Response<TemplatesMessageDado> response) {
                if (response.isSuccessful()) {
                    TemplatesMessageDado fcmDao = response.body();
                    if (fcmDao.isSuccess()) {
                        Log.d(TAG + " sendFcmToServer", "Successful!!!");
                    } else {
                        Log.d(TAG + " sendFcmToServer", "Unsuccessful!!!");
                    }
                } else {
                    Log.d(TAG + " sendFcmToServer", "Unsuccessful!!!");
                }
            }

            @Override
            public void onFailure(Call<TemplatesMessageDado> call, Throwable t) {
                Log.d(TAG + " sendFcmToServer", t.toString());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_process_job) {
            Intent intent = new Intent(this, ProcessJobActivity.class);
            JobDataWithImageDao jobDataDao = mPref.getJobDataDao();
            intent.putExtra("jobData", jobDataDao);
            startActivity(intent);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_list_job) {
            startActivity(new Intent(this, HistoryJobActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_comment) {
            startActivity(new Intent(this, CommentActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.nav_logout) {
            new DriverDataManager().clear();
            new DriverUpdateFcmManager().clear();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.click_agian_exit_application, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDriverData();
    }
}
