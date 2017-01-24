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
import com.oldster.swiftmovedriver.adapter.CardCommentAdapter;
import com.oldster.swiftmovedriver.constants.Config;
import com.oldster.swiftmovedriver.dao.CommentDataCollectionDao;
import com.oldster.swiftmovedriver.dao.RatingDataCollectionDao;
import com.oldster.swiftmovedriver.databinding.FragmentCommentBinding;
import com.oldster.swiftmovedriver.manager.DriverDataManager;
import com.oldster.swiftmovedriver.manager.HttpManager;
import com.oldster.swiftmovedriver.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */

public class CommentFragment extends Fragment {

    /********************
     * Variable
     ********************/
    private String TAG = CommentFragment.class.getSimpleName();

    private FragmentCommentBinding mBind;
    private CardCommentAdapter commentAdapter;
    private DriverDataManager driverDataManager;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public CommentFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        commentAdapter = new CardCommentAdapter(getContext());
        driverDataManager = new DriverDataManager();
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


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBind.recyclerView.setLayoutManager(layoutManager);
        mBind.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBind.recyclerView.setAdapter(commentAdapter);
        mBind.recyclerView.setVisibility(View.GONE);
        mBind.progressBar.setVisibility(View.VISIBLE);
        mBind.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
        mBind.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataComment();

            }
        });
        loadDataComment();
        loadDataAvgRating();
    }

    private void loadDataAvgRating() {
        Call<RatingDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataRating(driverDataManager.getDao().getData().get(0).getDriverId());
        call.enqueue(new Callback<RatingDataCollectionDao>() {
            @Override
            public void onResponse(Call<RatingDataCollectionDao> call, Response<RatingDataCollectionDao> response) {
                if (response.isSuccessful()){
                    RatingDataCollectionDao dao = response.body();
                    DecimalFormat df = new DecimalFormat("0.0");
                    mBind.tvAvgRating.setText(df.format(dao.getData().get(0).getRatingAvg()));
                    mBind.rtbProductRating.setRating(Float.parseFloat(dao.getData().get(0).getRatingAvg()+""));
                    mBind.tvCountRating.setText(dao.getData().get(0).getRatingCount()+"");
                }
            }

            @Override
            public void onFailure(Call<RatingDataCollectionDao> call, Throwable t) {

            }
        });
    }

    private void loadDataComment() {
        Call<CommentDataCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadDataComment(driverDataManager.getDao().getData().get(0).getDriverId());
        call.enqueue(new Callback<CommentDataCollectionDao>() {
            @Override
            public void onResponse(Call<CommentDataCollectionDao> call, Response<CommentDataCollectionDao> response) {
                mBind.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    mBind.recyclerView.removeAllViews();
                    CommentDataCollectionDao dao = response.body();
                    if (dao.getData().size() > 0) {
                        mBind.recyclerView.setVisibility(View.VISIBLE);
                        mBind.tvNoComment.setVisibility(View.GONE);
                        mBind.progressBar.setVisibility(View.GONE);
                        commentAdapter.setData(dao);
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        mBind.recyclerView.setVisibility(View.GONE);
                        mBind.tvNoComment.setVisibility(View.VISIBLE);
                        mBind.progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG + " onResponse ELSE", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CommentDataCollectionDao> call, Throwable t) {
                mBind.swipeRefreshLayout.setRefreshing(false);
                mBind.tvNoComment.setVisibility(View.VISIBLE);
                mBind.progressBar.setVisibility(View.GONE);
                Log.e(TAG + " onFailure", t.toString());
            }
        });

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

    /********************
     * Inner Class
     ********************/
}
