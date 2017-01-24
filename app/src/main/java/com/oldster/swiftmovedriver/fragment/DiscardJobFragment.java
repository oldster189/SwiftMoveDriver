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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.adapter.CardHistoryJobAdapter;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.dao.JobDataWithImageCollectionDao;
import com.oldster.swiftmovedriver.databinding.FragmentDiscardJobBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.manager.JobDiscardManager;
import com.oldster.swiftmovedriver.util.NotificationUtils;
import com.oldster.swiftmovedriver.util.VerticalSpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class DiscardJobFragment extends Fragment {

    /********************
     * Variable
     ********************/
    private String TAG = DiscardJobFragment.class.getSimpleName();
    private FragmentDiscardJobBinding mBind;
    private CardHistoryJobAdapter cardHistoryJobAdapter;
    private JobDiscardManager jobDiscardManager;
    private JobDataWithImageCollectionDao jobDataCollectionDao;
    private DriverDataManager driverDataManager;

    /********************
     * Functions
     ********************/
    public DiscardJobFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static DiscardJobFragment newInstance() {
        DiscardJobFragment fragment = new DiscardJobFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_discard_job, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        cardHistoryJobAdapter = new CardHistoryJobAdapter(getContext());
        jobDiscardManager = new JobDiscardManager();
        driverDataManager =new DriverDataManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {



        // Init 'View' instance(s) with rootView.findViewById here
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Contextor.getInstance().getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));
        //  cardProcessJobAdapter.setJobData(jobProcessManager.getJobDataCollectionDao());
        mBind.recyclerView.setAdapter(cardHistoryJobAdapter);
        mBind.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
        mBind.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataJob();

            }
        });
        loadDataJob();
    }
    private void loadDataJob() {
        Call<JobDataWithImageCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataJobDiscard(driverDataManager.getDao().getData().get(0).getDriverId());
        call.enqueue(new Callback<JobDataWithImageCollectionDao>() {
            @Override
            public void onResponse(Call<JobDataWithImageCollectionDao> call, Response<JobDataWithImageCollectionDao> response) {
                if (response.isSuccessful()) {
                    mBind.recyclerView.removeAllViews();
                    jobDataCollectionDao = response.body();
                    mBind.progressBar.setVisibility(View.GONE);
                    if (jobDataCollectionDao.getData().size() == 0) {
                        jobDiscardManager.setJobDataCollectionDao(jobDataCollectionDao);
                        cardHistoryJobAdapter.setJobData(jobDiscardManager.getJobDataCollectionDao());
                        mBind.textViewMessage.setVisibility(View.VISIBLE);
                        mBind.imgNo.setVisibility(View.VISIBLE);
                    } else {
                        jobDiscardManager.setJobDataCollectionDao(jobDataCollectionDao);
                        cardHistoryJobAdapter.setJobData(jobDiscardManager.getJobDataCollectionDao());
                        mBind.textViewMessage.setVisibility(View.GONE);
                        mBind.recyclerView.setVisibility(View.VISIBLE);
                        mBind.imgNo.setVisibility(View.GONE);
                    }
                    cardHistoryJobAdapter.notifyDataSetChanged();
                    Log.e(TAG + " isSuccessful true", "true");
                } else {
                    Log.e(TAG + " isSuccessful else", response.errorBody().toString());
                }
                mBind.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JobDataWithImageCollectionDao> call, Throwable t) {
                mBind.progressBar.setVisibility(View.GONE);
                mBind.textViewMessage.setVisibility(View.VISIBLE);
                Log.e(TAG + " onFailure", t.toString());
                mBind.swipeRefreshLayout.setRefreshing(false);
                mBind.imgNo.setVisibility(View.VISIBLE);
            }
        });
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

    /********************
     * Inner Class
     ********************/
}
