package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/10/1.
 */

public class CoachAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Coach> mCoachList;
    private HHBaseApplication application;
    private OnCoachClickListener mOnCoachClickListener;

    public interface OnCoachClickListener {
        void callCoach(String phone);
    }

    public CoachAdapter(Context context, ArrayList<Coach> coachList, OnCoachClickListener listener) {
        mContext = context;
        mCoachList = coachList;
        application = HHBaseApplication.get(mContext);
        mOnCoachClickListener = listener;
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
            holder.tvCoachPoints = ButterKnife.findById(view, R.id.tv_coach_points);
            holder.tvCoachActualPrice = ButterKnife.findById(view, R.id.tv_coach_actual_price);
            holder.ivCoachAvatar = ButterKnife.findById(view, R.id.iv_coach_avatar);
            holder.ivIsGoldenCoach = ButterKnife.findById(view, R.id.iv_is_golden_coach);
            holder.ivCashPledge = ButterKnife.findById(view, R.id.iv_is_cash_pledge);
            holder.rbCoachScore = ButterKnife.findById(view, R.id.rb_coach_score);
            holder.tvCoachLocation = ButterKnife.findById(view, R.id.tv_coach_location);
            holder.rlyCoachLocation = ButterKnife.findById(view, R.id.rly_third_line);
            holder.tvDistance = ButterKnife.findById(view, R.id.tv_distance);
            holder.tvTrainSchoolName = ButterKnife.findById(view, R.id.tv_train_school);
            holder.llyTrainSchool = ButterKnife.findById(view, R.id.lly_train_school);
            holder.rlyActualPrice = ButterKnife.findById(view, R.id.rly_actual_price);
            holder.frlCall = ButterKnife.findById(view, R.id.frl_call);
            holder.tvConsultantCount = ButterKnife.findById(view, R.id.tv_consultant_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final Coach coach = mCoachList.get(position);
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
        String text = Utils.getCount(coach.consult_count) + "人已咨询";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), 0, text.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvConsultantCount.setText(ss);
        if (coach.coach_group != null) {
            final Field field = application.getFieldResponseList().getFieldById(coach.coach_group.field_id);
            if (field != null) {
                holder.tvCoachLocation.setText(field.zone + " | " + field.name);
            }
            if (!TextUtils.isEmpty(coach.distance)) {
                String infoText = "距您" + Utils.getDistance(Double.parseDouble(coach.distance));
                SpannableStringBuilder style = new SpannableStringBuilder(infoText);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), 2, infoText.indexOf("KM"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.tvDistance.setText(style);
            }
        }
        if (!TextUtils.isEmpty(coach.driving_school)) {
            holder.llyTrainSchool.setVisibility(View.VISIBLE);
            holder.tvTrainSchoolName.setText(coach.driving_school);
        } else {
            holder.llyTrainSchool.setVisibility(View.GONE);
        }
        holder.llyTrainSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView(WebViewUrl.WEB_URL_JIAXIAO + "/" + coach.driving_school_id);
            }
        });
        holder.frlCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnCoachClickListener != null && !TextUtils.isEmpty(coach.consult_phone)) {
                    mOnCoachClickListener.callCoach(coach.consult_phone);
                }
            }
        });
        return view;
    }

    public void openWebView(String originUrl) {
        Intent intent = new Intent(mContext, BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        HHLog.v("webview url -> " + originUrl);
        bundle.putString("url", originUrl);
        bundle.putString("shareUrl", originUrl);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    static class ViewHolder {
        TextView tvCoachName;
        TextView tvCoachPoints;
        TextView tvCoachActualPrice;
        SimpleDraweeView ivCoachAvatar;
        ImageView ivIsGoldenCoach;
        ImageView ivCashPledge;
        RatingBar rbCoachScore;
        TextView tvCoachLocation;
        RelativeLayout rlyCoachLocation;
        TextView tvDistance;
        TextView tvTrainSchoolName;
        LinearLayout llyTrainSchool;
        RelativeLayout rlyActualPrice;
        FrameLayout frlCall;
        TextView tvConsultantCount;
    }
}
