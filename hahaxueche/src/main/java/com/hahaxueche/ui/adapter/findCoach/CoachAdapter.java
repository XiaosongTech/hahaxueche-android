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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.dialog.findCoach.MapDialog;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.DistanceUtil;
import com.hahaxueche.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/10/1.
 */

public class CoachAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Coach> mCoachList;
    private HHBaseApplication application;

    public CoachAdapter(Context context, ArrayList<Coach> coachList) {
        inflater = LayoutInflater.from(context);
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
        final CoachAdapter.ViewHolder holder;

        if (convertView != null) {
            holder = (CoachAdapter.ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_news, parent, false);
            holder = new CoachAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Coach coach = mCoachList.get(position);
        holder.tvCoachName.setText(coach.name);
        holder.ivCoachAvatar.setImageURI(coach.avatar);
        double coachExperiences = 0d;
        if (!TextUtils.isEmpty(coach.experiences)) {
            coachExperiences = Double.parseDouble(coach.experiences);
        }
        DecimalFormat dfInt = new DecimalFormat("#####");
        holder.tvCoachTeachTime.setText(dfInt.format(coachExperiences) + "年教龄");
        holder.tvCoachPoints.setText(coach.average_rating + " (" + coach.review_count + ")");
        holder.tvCoachActualPrice.setText(Utils.getMoney(coach.coach_group.training_cost));
        //vip价格
        if (coach.vip == 0) {
            holder.rlyVipPrice.setVisibility(View.GONE);
        } else {
            holder.rlyVipPrice.setVisibility(View.VISIBLE);
            holder.tvVIPPrice.setText(Utils.getMoney(coach.coach_group.vip_price));
        }
        if (coach.skill_level.equals("1")) {
            holder.ivIsGoldenCoach.setVisibility(View.VISIBLE);
        } else {
            holder.ivIsGoldenCoach.setVisibility(View.GONE);
        }
        float score = Float.parseFloat(coach.average_rating);
        if (score > 5) {
            score = 5;
        }
        holder.svCoachScore.setScore(score, false);
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
                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        p.setMargins(Utils.instence(mContext).dip2px(100), 0, 0, 0);
                        holder.rlyActualPrice.setLayoutParams(p);
                        return true;
                    }
                });
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(Utils.instence(mContext).dip2px(100), Utils.instence(mContext).dip2px(200), 0, 0);
                holder.rlyActualPrice.setLayoutParams(p);
                mapDialog.show();
            }
        });
        holder.tvApplaudCount.setText(String.valueOf(coach.like_count));
        if (!TextUtils.isEmpty(coach.driving_school)) {
            holder.llyTrainSchool.setVisibility(View.VISIBLE);
            holder.tvTrainSchoolName.setText(coach.driving_school);
        } else {
            holder.llyTrainSchool.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_coach_name)
        TextView tvCoachName;
        @BindView(R.id.tv_coach_teach_time)
        TextView tvCoachTeachTime;
        @BindView(R.id.tv_coach_points)
        TextView tvCoachPoints;
        @BindView(R.id.tv_coach_actual_price)
        TextView tvCoachActualPrice;
        @BindView(R.id.tv_vip_price)
        TextView tvVIPPrice;
        @BindView(R.id.iv_coach_avatar)
        SimpleDraweeView ivCoachAvatar;
        @BindView(R.id.iv_is_golden_coach)
        SimpleDraweeView ivIsGoldenCoach;
        @BindView(R.id.sv_coach_score)
        ScoreView svCoachScore;
        @BindView(R.id.tv_coach_location)
        TextView tvCoachLocation;
        @BindView(R.id.rly_third_line)
        RelativeLayout rlyCoachLocation;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.tv_applaud_count)
        TextView tvApplaudCount;
        @BindView(R.id.rly_vip_price)
        RelativeLayout rlyVipPrice;
        @BindView(R.id.tv_train_school)
        TextView tvTrainSchoolName;
        @BindView(R.id.lly_train_school)
        LinearLayout llyTrainSchool;
        @BindView(R.id.rly_actual_price)
        RelativeLayout rlyActualPrice;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
