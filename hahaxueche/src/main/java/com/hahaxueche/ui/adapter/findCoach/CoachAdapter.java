package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.dialog.findCoach.MapDialog;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.DistanceUtil;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/10/1.
 */

public class CoachAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Coach> mCoachList;
    private HHBaseApplication application;

    public CoachAdapter(Context context, ArrayList<Coach> coachList) {
        mContext = context;
        mCoachList = coachList;
        application = HHBaseApplication.get(mContext);
    }

    @Override
    public int getCount() {
        return mCoachList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCoachList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.adapter_coach, null);
            holder = new ViewHolder();
            holder.tvCoachName = ButterKnife.findById(view, R.id.tv_coach_name);
            holder.tvCoachTeachTime = ButterKnife.findById(view, R.id.tv_coach_teach_time);
            holder.tvCoachPoints = ButterKnife.findById(view, R.id.tv_coach_points);
            holder.tvCoachActualPrice = ButterKnife.findById(view, R.id.tv_coach_actual_price);
            holder.ivCoachAvatar = ButterKnife.findById(view, R.id.iv_coach_avatar);
            holder.ivIsGoldenCoach = ButterKnife.findById(view, R.id.iv_is_golden_coach);
            holder.ivCashPledge = ButterKnife.findById(view, R.id.iv_is_cash_pledge);
            holder.rbCoachScore = ButterKnife.findById(view, R.id.rb_coach_score);
            holder.tvCoachLocation = ButterKnife.findById(view, R.id.tv_coach_location);
            holder.rlyCoachLocation = ButterKnife.findById(view, R.id.rly_third_line);
            holder.tvDistance = ButterKnife.findById(view, R.id.tv_distance);
            holder.tvApplaudCount = ButterKnife.findById(view, R.id.tv_applaud_count);
            holder.tvTrainSchoolName = ButterKnife.findById(view, R.id.tv_train_school);
            holder.llyTrainSchool = ButterKnife.findById(view, R.id.lly_train_school);
            holder.rlyActualPrice = ButterKnife.findById(view, R.id.rly_actual_price);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Coach coach = mCoachList.get(position);
        holder.tvCoachName.setText(coach.name);
        holder.ivCoachAvatar.setImageURI(coach.avatar);
        holder.tvCoachTeachTime.setText(coach.experiences + "年教龄");
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
        if (coach.coach_group != null) {
            holder.tvCoachLocation.setText(application.getConstants().getSectionName(coach.coach_group.field_id));

            final Field myField = application.getConstants().getField(coach.coach_group.field_id);
            if (application.getMyLocation() != null && myField != null) {
                String kmString = DistanceUtil.getDistanceKm(application.getMyLocation().lng, application.getMyLocation().lat, myField.lng, myField.lat);
                String infoText = "距您" + kmString + "km";
                SpannableStringBuilder style = new SpannableStringBuilder(infoText);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), 2, 2 + kmString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.tvDistance.setText(style);
            }
            holder.rlyCoachLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapDialog mapDialog = new MapDialog(mContext, R.style.map_dialog, myField, v, new MapDialog.MapDialogDismissListener() {
                        @Override
                        public boolean dialogDismiss() {
                            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            p.setMargins(0, Utils.instence(mContext).dip2px(6), 0, 0);
                            p.addRule(RelativeLayout.ALIGN_LEFT, R.id.rly_third_line);
                            p.addRule(RelativeLayout.BELOW, R.id.rly_third_line);
                            holder.rlyActualPrice.setLayoutParams(p);
                            return true;
                        }
                    });
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    p.setMargins(0, Utils.instence(mContext).dip2px(200), 0, 0);
                    p.addRule(RelativeLayout.ALIGN_LEFT, R.id.rly_third_line);
                    p.addRule(RelativeLayout.BELOW, R.id.rly_third_line);
                    holder.rlyActualPrice.setLayoutParams(p);
                    mapDialog.show();
                }
            });
        }
        holder.tvApplaudCount.setText(String.valueOf(coach.like_count));
        if (!TextUtils.isEmpty(coach.driving_school)) {
            holder.llyTrainSchool.setVisibility(View.VISIBLE);
            holder.tvTrainSchoolName.setText(coach.driving_school);
        } else {
            holder.llyTrainSchool.setVisibility(View.GONE);
        }
        return view;
    }

    static class ViewHolder {
        TextView tvCoachName;
        TextView tvCoachTeachTime;
        TextView tvCoachPoints;
        TextView tvCoachActualPrice;
        SimpleDraweeView ivCoachAvatar;
        ImageView ivIsGoldenCoach;
        ImageView ivCashPledge;
        RatingBar rbCoachScore;
        TextView tvCoachLocation;
        RelativeLayout rlyCoachLocation;
        TextView tvDistance;
        TextView tvApplaudCount;
        TextView tvTrainSchoolName;
        LinearLayout llyTrainSchool;
        RelativeLayout rlyActualPrice;
    }
}
