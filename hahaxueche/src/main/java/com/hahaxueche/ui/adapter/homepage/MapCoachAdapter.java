package com.hahaxueche.ui.adapter.homepage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
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

    public interface OnRecyclerViewItemClickListener {
        void onDrivingSchoolClick(String drivingSchoolId);

        void onCoachDetailClick(Coach coach);

        void onCheckFieldClick(Coach coach);

        void onCustomerServiceClick();

        void onContactCoachClick(Coach coach);
    }

    public MapCoachAdapter(Context context, List<Coach> coachList, OnRecyclerViewItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        mCoachList = coachList;
        mOnItemClickListener = listener;
        mContext = context;
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
        holder.ivCoachAvatar.setImageURI(coach.avatar);
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
        if (!TextUtils.isEmpty(coach.driving_school)) {
            holder.llyTrainSchool.setVisibility(View.VISIBLE);
            holder.tvTrainSchoolName.setText(coach.driving_school);
        } else {
            holder.llyTrainSchool.setVisibility(View.GONE);
        }
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
        @BindView(R.id.tv_train_school)
        TextView tvTrainSchoolName;
        @BindView(R.id.lly_train_school)
        LinearLayout llyTrainSchool;
        @BindView(R.id.rb_coach_score)
        RatingBar rbCoachScore;
        @BindView(R.id.tv_coach_points)
        TextView tvCoachPoints;
        @BindView(R.id.tv_coach_actual_price)
        TextView tvCoachActualPrice;
        @BindView(R.id.tv_check_field)
        TextView tvCheckField;
        @BindView(R.id.tv_online_ask)
        TextView tvOnlineAsk;
        @BindView(R.id.fly_contact_coach)
        FrameLayout flyContactCoach;
        @BindView(R.id.tv_more)
        TextView tvMore;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            llyTrainSchool.setOnClickListener(this);
            tvCheckField.setOnClickListener(this);
            tvOnlineAsk.setOnClickListener(this);
            flyContactCoach.setOnClickListener(this);
            tvMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Coach coach = mCoachList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.lly_train_school:
                    mOnItemClickListener.onDrivingSchoolClick(coach.driving_school_id);
                    break;
                case R.id.tv_check_field:
                    mOnItemClickListener.onCheckFieldClick(coach);
                    break;
                case R.id.tv_online_ask:
                    mOnItemClickListener.onCustomerServiceClick();
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
