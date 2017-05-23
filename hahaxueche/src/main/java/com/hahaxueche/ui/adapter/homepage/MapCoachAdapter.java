package com.hahaxueche.ui.adapter.homepage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/4/17.
 */

public class MapCoachAdapter extends RecyclerView.Adapter<MapCoachAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Coach> mCoachList;
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private Context mContext;
    private int[] mDrivingSchoolIds;
    private HHBaseApplication application;

    public interface OnRecyclerViewItemClickListener {
        void onDrivingSchoolClick(int drivingSchoolId);

        void onCoachDetailClick(Coach coach);

        void onCheckFieldClick(Coach coach);

        void onSendLocationClick(Coach coach);

        void onContactCoachClick(Coach coach);
    }

    public MapCoachAdapter(Context context, List<Coach> coachList, int[] drivingSchoolIds, OnRecyclerViewItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        mCoachList = coachList;
        mOnItemClickListener = listener;
        mContext = context;
        mDrivingSchoolIds = drivingSchoolIds;
        application = HHBaseApplication.get(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_map_coach, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coach coach = mCoachList.get(position);
        holder.tvCoachName.setText(coach.name);
        if (mDrivingSchoolIds.length > 1) {
            List<DrivingSchool> drivingSchools = application.getCityConstants().driving_schools;
            for (DrivingSchool drivingSchool : drivingSchools) {
                if (drivingSchool.id == coach.driving_school_id) {
                    holder.ivCoachAvatar.setImageURI(drivingSchool.avatar);
                }
            }
        } else {
            holder.ivCoachAvatar.setImageURI(coach.avatar);
        }
        holder.tvCoachPoints.setText(coach.average_rating + " (" + coach.review_count + ")");
        if (coach.coach_group != null) {
            holder.tvCoachActualPrice.setText(Utils.getMoney(coach.coach_group.training_cost));
        }
        holder.ivIsGoldenCoach.setVisibility(coach.skill_level == Common.COACH_SKILL_LEVEL_GOLDEN ? View.VISIBLE : View.GONE);
        holder.ivCashPledge.setVisibility(coach.has_cash_pledge == 1 ? View.VISIBLE : View.GONE);
        float score = Float.parseFloat(coach.average_rating);
        if (score > 5) {
            score = 5;
        }
        holder.rbCoachScore.setRating(score);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mCoachList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_coach_name)
        TextView tvCoachName;
        @BindView(R.id.iv_coach_avatar)
        SimpleDraweeView ivCoachAvatar;
        @BindView(R.id.iv_is_golden_coach)
        ImageView ivIsGoldenCoach;
        @BindView(R.id.iv_is_cash_pledge)
        ImageView ivCashPledge;
        @BindView(R.id.rb_coach_score)
        RatingBar rbCoachScore;
        @BindView(R.id.tv_coach_points)
        TextView tvCoachPoints;
        @BindView(R.id.tv_coach_actual_price)
        TextView tvCoachActualPrice;
        @BindView(R.id.tv_check_field)
        TextView tvCheckField;
        @BindView(R.id.tv_send_location)
        TextView tvSendLocation;
        @BindView(R.id.fly_contact_coach)
        FrameLayout flyContactCoach;
        @BindView(R.id.tv_more)
        TextView tvMore;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            tvCheckField.setOnClickListener(this);
            tvSendLocation.setOnClickListener(this);
            flyContactCoach.setOnClickListener(this);
            tvMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Coach coach = mCoachList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.tv_check_field:
                    mOnItemClickListener.onCheckFieldClick(coach);
                    break;
                case R.id.tv_send_location:
                    mOnItemClickListener.onSendLocationClick(coach);
                    break;
                case R.id.fly_contact_coach:
                    mOnItemClickListener.onContactCoachClick(coach);
                    break;
                case R.id.tv_more:
                    mOnItemClickListener.onCoachDetailClick(coach);
                    break;

                default:
                    break;
            }
        }
    }
}
