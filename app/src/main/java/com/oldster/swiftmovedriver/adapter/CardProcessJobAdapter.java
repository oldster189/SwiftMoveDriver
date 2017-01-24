package com.oldster.swiftmovedriver.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.activity.DetailJobActivity;
import com.oldster.swiftmovedriver.activity.ProcessJobActivity;
import com.oldster.swiftmovedriver.dao.JobDataWithImageCollectionDao;
import com.oldster.swiftmovedriver.manager.SharedPreferencesJobProcess;
import com.oldster.swiftmovedriver.util.DateTimeValue;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Old'ster on 15/9/2559.
 */

public class CardProcessJobAdapter extends RecyclerView.Adapter<CardProcessJobAdapter.MyViewHolder> {
    private static final long TIME_SET_COOLDOWN = 1500000;
    private String TAG = CardProcessJobAdapter.class.getSimpleName();
    private int lastPosition = -1;
    private Context mContext;
    private JobDataWithImageCollectionDao jobData;
    private double total;
    private SharedPreferencesJobProcess mPref;

    public void setJobData(JobDataWithImageCollectionDao jobData) {
        this.jobData = jobData;
    }

    public CardProcessJobAdapter(Context mContext) {
        this.mContext = mContext;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.job_card, parent, false);

        return new MyViewHolder(itemView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CountDownTimer timer;
        TextView tv_timer;
        CardView cardView1;
        LinearLayout linStatusJob, linExpTime;
        TextView tvIdJob, tvStatusJob, tvDateTime, tvPositionFrom, tvPositionTo, tvServicePrice;
        FancyButton btnDetail, btnGo;

        MyViewHolder(View itemView) {
            super(itemView);
            linStatusJob = (LinearLayout) itemView.findViewById(R.id.linStatusJob);
            linExpTime = (LinearLayout) itemView.findViewById(R.id.linExpTime);
            tvIdJob = (TextView) itemView.findViewById(R.id.tvIdJob);
            tv_timer = (TextView) itemView.findViewById(R.id.tvExpTime);
            tvStatusJob = (TextView) itemView.findViewById(R.id.tvStatusJob);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            tvPositionFrom = (TextView) itemView.findViewById(R.id.tvPositionFrom);
            tvPositionTo = (TextView) itemView.findViewById(R.id.tvPositionTo);
            tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
            cardView1 = (CardView) itemView.findViewById(R.id.cardView1);
            btnDetail = (FancyButton) itemView.findViewById(R.id.btnDetail);
            btnGo = (FancyButton) itemView.findViewById(R.id.btnGo);

            btnDetail.setOnClickListener(this);
            btnGo.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == btnDetail.getId()) {
                String id = String.valueOf(jobData.getData().get(getAdapterPosition()).getJobId());
                Intent intent = new Intent(mContext, DetailJobActivity.class);
                intent.putExtra("jid", id);
                mContext.startActivity(intent);
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.from_right, R.anim.to_left);
            }
            if (v.getId() == btnGo.getId()) {
                mPref = new SharedPreferencesJobProcess();

                if (mPref.getJobDataDao() == null || (jobData.getData().get(getAdapterPosition()).getJobId() == mPref.getJobDataDao().getJobId())) {
                    Intent intent = new Intent(mContext, ProcessJobActivity.class);
                    intent.putExtra("jobData", jobData.getData().get(getAdapterPosition()));
                    mContext.startActivity(intent);
                    ((AppCompatActivity) mContext).overridePendingTransition(R.anim.from_right, R.anim.to_left);
                } else {
                    DecimalFormat df = new DecimalFormat("000000");
                    new MaterialDialog.Builder(mContext)
                            .content("มีรายการหมายเลข #" + df.format(mPref.getJobDataDao().getJobId()) + " กำลังดำเนินการอยู่")
                            .positiveText("ตกลง")
                            .show();
                }
            }
        }
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (position < getItemCount()) {
            final int idJob = jobData.getData().get(position).getJobId();
            String dateCurrent = getDateCurrent();
            String dateJob = jobData.getData().get(position).getJobDate() + " " + jobData.getData().get(position).getJobTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final DecimalFormat df = new DecimalFormat("000000");
            DecimalFormat df2 = new DecimalFormat("00.00");
            final DecimalFormat df3 = new DecimalFormat("00");
            Date d1 = null;
            Date d2 = null;
            Date d3 = null;
            try {
                d1 = format.parse(dateCurrent);
                d2 = format.parse(dateJob);
                d3 = format.parse(jobData.getData().get(position).getJobCreatedAt());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = (d2 != null ? d2.getTime() : 0) - (d1 != null ? d1.getTime() : 0);
            long diff2 = (d3 != null ? d3.getTime() : 0) - (d1 != null ? d1.getTime() : 0);
            long t1 = diff2 + TIME_SET_COOLDOWN;
            if (t1 > 0 && t1 < TIME_SET_COOLDOWN) {
                holder.timer = new CountDownTimer(t1, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000) % 60;
                        int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                        int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                        holder.tv_timer.setText(" "+df3.format(minutes) + ":" + df3.format(seconds)+ " นาที");
                    }

                    public void onFinish() {
                        holder.tv_timer.setText("หมดเวลาการรับงาน");
                    }
                }.start();

            } else {
                holder.tv_timer.setText("หมดเวลาการรับงาน");
            }
            Log.e(TAG, "\n");
            long diffHours = diff / (60 * 60 * 1000);

            if (diffHours > 48) {
                holder.tvStatusJob.setText("งานในอนาคต");
                holder.linStatusJob.setBackgroundResource(R.drawable.bg_oval_red_pink);
            } else if (diffHours > 24) {
                holder.tvStatusJob.setText("งานพรุ่งนี้");
                holder.linStatusJob.setBackgroundResource(R.drawable.bg_oval_purple);
            } else if (diffHours > 0) {
                holder.tvStatusJob.setText("งานวันนี้");
                holder.linStatusJob.setBackgroundResource(R.drawable.bg_oval);
            }

            if (jobData.getData().get(position).getJobStatusName().equals("รอการยืนยัน")) {
                holder.btnGo.setVisibility(View.GONE);
            } else {
                holder.btnGo.setVisibility(View.VISIBLE);
                holder.linExpTime.setVisibility(View.GONE);
            }


            holder.tvIdJob.setText("#" + df.format(idJob) + "");
            DateTimeValue date = new DateTimeValue(jobData.getData().get(position).getJobDate(), jobData.getData().get(position).getJobTime());
            holder.tvDateTime.setText(date.getDate() + " " + date.getMount() + " " + date.getYear() + " | " + date.getTime() + " น.");

            holder.tvPositionFrom.setText(jobData.getData().get(position).getJobFromNameAddress());
            holder.tvPositionTo.setText(jobData.getData().get(position).getJobToNameAddress());

            int chargeStartPrice = jobData.getData().get(position).getJobChargeStartPrice();
            int chargeStartKm = jobData.getData().get(position).getJobChargeStartKm();
            int chargeRate = jobData.getData().get(position).getJobCharge();


            double distance = jobData.getData().get(position).getJobDistance();

            String liftStatus = jobData.getData().get(position).getJobServiceLiftStatus();
            int liftPrice;
            if (liftStatus.equals("t")) {
                liftPrice = jobData.getData().get(position).getJobServiceLiftPrice();
            } else {
                liftPrice = 0;
            }


            String liftPlusStatus = jobData.getData().get(position).getJobServiceLiftPlusStatus();

            int liftPlusPrice;
            if (liftPlusStatus.equals("t")) {
                liftPlusPrice = jobData.getData().get(position).getJobServiceLiftPlusPrice();
            } else {
                liftPlusPrice = 0;
            }

            String cartStatus = jobData.getData().get(position).getJobServiceCartStatus();
            int cartPrice;
            if (cartStatus.equals("t")) {
                cartPrice = jobData.getData().get(position).getJobServiceCartPrice();
            } else {
                cartPrice = 0;
            }
            double dis2 = distance - chargeStartKm;
            if (dis2 <= 0) {
                dis2 = 0;
            }

            total = (dis2 * chargeRate) + liftPrice + liftPlusPrice + cartPrice + chargeStartPrice;

            holder.tvServicePrice.setText("ค่าบริการ ฿ " + df2.format(total) + "");


            setAnimation(holder.itemView, position);
        }


    }


    @Override
    public int getItemCount() {
        if (jobData == null) {
            return 0;
        }
        if (jobData.getData() == null) {
            return 0;
        }
        return jobData.getData().size();

    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext
                    , R.anim.up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
