package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.util.Utils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/5/3.
 */

public class DrivingSchoolAdapter extends BaseAdapter {
    private Context mContext;
    private List<DrivingSchool> mDrivingSchoolList;
    private HHBaseApplication application;
    private OnDrivingSchoolClickListener mOnDrivingSchoolClickListener;

    public interface OnDrivingSchoolClickListener {
        void callCoach(String phone);
    }

    public DrivingSchoolAdapter(Context context, List<DrivingSchool> drivingSchools, OnDrivingSchoolClickListener listener) {
        mContext = context;
        mDrivingSchoolList = drivingSchools;
        application = HHBaseApplication.get(mContext);
        mOnDrivingSchoolClickListener = listener;
    }

    @Override
    public int getCount() {
        return mDrivingSchoolList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDrivingSchoolList.get(position);
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
            view = inflator.inflate(R.layout.adapter_driving_school, null);
            holder = new ViewHolder();
            holder.mLlyMain = ButterKnife.findById(view, R.id.lly_main);
            holder.mRlyAdapter = ButterKnife.findById(view, R.id.rly_adapter);
            holder.mIvAvatar = ButterKnife.findById(view, R.id.iv_avatar);
            holder.mFrlCall = ButterKnife.findById(view, R.id.frl_call);
            holder.mTvConsultantCount = ButterKnife.findById(view, R.id.tv_consultant_count);
            holder.mTvName = ButterKnife.findById(view, R.id.tv_name);
            holder.mTvPrice = ButterKnife.findById(view, R.id.tv_price);
            holder.mRbScore = ButterKnife.findById(view, R.id.rb_score);
            holder.mTvPoints = ButterKnife.findById(view, R.id.tv_points);
            holder.mTvDistance = ButterKnife.findById(view, R.id.tv_distance);
            holder.mTvZone = ButterKnife.findById(view, R.id.tv_zone);
            holder.mRlyGroupBuy = ButterKnife.findById(view, R.id.rly_group_buy);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final DrivingSchool drivingSchool = mDrivingSchoolList.get(position);
        holder.mIvAvatar.setImageURI(drivingSchool.avatar);
        holder.mTvName.setText(drivingSchool.name);
        holder.mTvPrice.setText(Utils.getMoney(drivingSchool.lowest_price));
        holder.mTvPoints.setText(drivingSchool.rating + " (" + drivingSchool.review_count + ")");
        holder.mRbScore.setRating(drivingSchool.rating > 5 ? 5 : drivingSchool.rating);
        String text = Utils.getCount(drivingSchool.consult_count) + "人已咨询";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), 0, text.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.mTvConsultantCount.setText(ss);
        if (!TextUtils.isEmpty(drivingSchool.distance)) {
            String infoText = "最近训练场距您" + Utils.getDistance(Double.parseDouble(drivingSchool.distance));
            SpannableStringBuilder style = new SpannableStringBuilder(infoText);
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), infoText.indexOf("您") + 1, infoText.indexOf("KM"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.mTvDistance.setText(style);
            if (drivingSchool.zones != null && drivingSchool.zones.size() > 1) {
                holder.mTvZone.setText(drivingSchool.zones.get(0));
            }
        } else {
            holder.mTvDistance.setText("共有" + drivingSchool.field_count + "个训练场 点击查看最近 >");
            holder.mTvZone.setVisibility(View.GONE);
        }

        holder.mFrlCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnDrivingSchoolClickListener != null && !TextUtils.isEmpty(drivingSchool.consult_phone)) {
                    mOnDrivingSchoolClickListener.callCoach(drivingSchool.consult_phone);
                }
            }
        });
        return view;
    }

    static class ViewHolder {
        LinearLayout mLlyMain;
        RelativeLayout mRlyAdapter;
        SimpleDraweeView mIvAvatar;
        FrameLayout mFrlCall;
        TextView mTvConsultantCount;
        TextView mTvName;
        TextView mTvPrice;
        RatingBar mRbScore;
        TextView mTvPoints;
        TextView mTvDistance;
        TextView mTvZone;
        RelativeLayout mRlyGroupBuy;
    }

}
