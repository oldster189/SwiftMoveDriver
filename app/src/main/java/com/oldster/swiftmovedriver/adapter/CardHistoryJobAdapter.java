package com.oldster.swiftmovedriver.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.oldster.swiftmovedriver.R;
import com.oldster.swiftmovedriver.dao.JobDataWithImageCollectionDao;
import com.oldster.swiftmovedriver.util.DateTimeValue;

import java.text.DecimalFormat;

/**
 * Created by Old'ster on 15/9/2559.
 */

public class CardHistoryJobAdapter extends RecyclerView.Adapter<CardHistoryJobAdapter.MyViewHolder> {

    private int lastPosition = -1;

    private Context mContext;

    private JobDataWithImageCollectionDao jobData;

    private CardView cardView1;
    private TextView tvIdJob, tvStatusJob, tvDateTime, tvPositionFrom, tvTypeCar, tvPositionTo, tvTotalPrice;
    private ImageView imgStatus;

    public void setJobData(JobDataWithImageCollectionDao jobData) {
        this.jobData = jobData;
    }

    public CardHistoryJobAdapter(Context mContext) {
        this.mContext = mContext;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CountDownTimer timer;

        MyViewHolder(View itemView) {
            super(itemView);
            tvIdJob = (TextView) itemView.findViewById(R.id.tvIdJob);
            tvStatusJob = (TextView) itemView.findViewById(R.id.tvStatusJob);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            tvPositionFrom = (TextView) itemView.findViewById(R.id.tvPositionFrom);
            tvPositionTo = (TextView) itemView.findViewById(R.id.tvPositionTo);
            tvTotalPrice = (TextView) itemView.findViewById(R.id.tvTotalPrice);
            tvTypeCar = (TextView) itemView.findViewById(R.id.tvTypeCar);
            cardView1 = (CardView) itemView.findViewById(R.id.cardView1);
            imgStatus = (ImageView) itemView.findViewById(R.id.imgStatus);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.history_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (position < getItemCount()) {
            DecimalFormat df = new DecimalFormat("000000");
            tvIdJob.setText("#" + df.format(jobData.getData().get(position).getJobId()));
            DecimalFormat df2 = new DecimalFormat("00.00");
            DateTimeValue date = new DateTimeValue(jobData.getData().get(position).getJobDate(), jobData.getData().get(position).getJobTime());
            tvDateTime.setText(date.getDate() + " " + date.getMount() + " " + date.getYear() + " | " + date.getTime() + " น.");
            String typeCar;
            switch (jobData.getData().get(position).getDriverDetailType()) {
                case "Pickup":
                    typeCar = "รถกระบะ";
                    break;
                case "Truck":
                    typeCar = "รถกระบะตู้ทึบ";
                    break;
                default:
                    typeCar = "รถ 5 ประตู";
                    break;
            }
            tvTypeCar.setText(typeCar);
            tvPositionFrom.setText(jobData.getData().get(position).getJobFromNameAddress());
            tvPositionTo.setText(jobData.getData().get(position).getJobToNameAddress());

            String jobStatus = jobData.getData().get(position).getJobStatusName();
            tvStatusJob.setText(jobStatus);
            if (jobStatus.equals("เสร็จสิ้น")) {
                tvStatusJob.setBackgroundResource(R.drawable.bg_oval);
                imgStatus.setImageResource(R.drawable.ic_correct);
            } else {
                tvStatusJob.setBackgroundResource(R.drawable.bg_oval_red_pink);
                imgStatus.setImageResource(R.drawable.ic_multiply);
            }


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

            double total = (dis2 * chargeRate) + liftPrice + liftPlusPrice + cartPrice + chargeStartPrice;


            tvTotalPrice.setText("฿ " + df2.format(total));


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
