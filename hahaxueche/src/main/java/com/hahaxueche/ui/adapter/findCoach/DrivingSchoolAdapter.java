package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.ui.activity.findCoach.DrivingSchoolDetailDetailActivity;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/5/3.
 */

public class DrivingSchoolAdapter extends BaseAdapter {
    private Context mContext;
    private List<DrivingSchool> mDrivingSchoolList;
    private OnDrivingSchoolClickListener mOnDrivingSchoolClickListener;
    private List<DrivingSchool> mHotDrivingSchoolList;
    private boolean mIsHotViewAdded = false;
    private int mInsertHotViewPosition = -1;

    public interface OnDrivingSchoolClickListener {
        void callCoach(String phone);

        void clickDrivingSchool(DrivingSchool drivingSchool);
    }

    public DrivingSchoolAdapter(Context context, List<DrivingSchool> drivingSchools, List<DrivingSchool> hotDrivingSchools, OnDrivingSchoolClickListener listener) {
        mContext = context;
        mDrivingSchoolList = drivingSchools;
        mHotDrivingSchoolList = hotDrivingSchools;
        mOnDrivingSchoolClickListener = listener;
        mInsertHotViewPosition = drivingSchools.size() > 3 ? 3 : drivingSchools.size() - 1;
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
        } else {
            holder.mTvDistance.setText("共有" + drivingSchool.field_count + "个训练场 点击查看最近 >");
        }
        if (!TextUtils.isEmpty(drivingSchool.closest_zone)) {
            holder.mTvZone.setText(drivingSchool.closest_zone);
        } else {
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
        holder.mRlyAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnDrivingSchoolClickListener != null) {
                    mOnDrivingSchoolClickListener.clickDrivingSchool(drivingSchool);
                }
            }
        });
        if (position == mInsertHotViewPosition) {
            if (!mIsHotViewAdded) {
                holder.mLlyMain.addView(getHotDrivingSchoolView());
                mIsHotViewAdded = true;
            }
            if (holder.mLlyMain.getChildCount() == 2) {
                holder.mLlyMain.getChildAt(1).setVisibility(View.VISIBLE);
            }
        } else {
            if (holder.mLlyMain.getChildCount() == 2) {
                holder.mLlyMain.getChildAt(1).setVisibility(View.GONE);
            }
        }
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

    private LinearLayout getHotDrivingSchoolView() {
        int margin5dp = Utils.instence(mContext).dip2px(5);
        int margin8dp = Utils.instence(mContext).dip2px(8);
        int margin10dp = Utils.instence(mContext).dip2px(10);
        int margin15dp = Utils.instence(mContext).dip2px(15);
        int margin20dp = Utils.instence(mContext).dip2px(20);
        int padding3dp = Utils.instence(mContext).dip2px(3);

        LinearLayout llyHotDrivingSchool = new LinearLayout(mContext);
        LinearLayout.LayoutParams llyHotDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llyHotDrivingSchoolParam.setMargins(0, margin10dp, 0, margin10dp);
        llyHotDrivingSchool.setLayoutParams(llyHotDrivingSchoolParam);
        llyHotDrivingSchool.setBackgroundResource(R.color.haha_white);
        llyHotDrivingSchool.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout rlyHotSearch = new RelativeLayout(mContext);
        rlyHotSearch.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tvHotSearch = new TextView(mContext);
        RelativeLayout.LayoutParams tvHotSearchParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotSearchParam.setMargins(margin20dp, margin15dp, 0, margin15dp);
        tvHotSearch.setLayoutParams(tvHotSearchParam);
        tvHotSearch.setText("大家都在搜");
        tvHotSearch.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray_dark));
        tvHotSearch.setTextSize(16);
        int tvHotSearchId = Utils.generateViewId();
        tvHotSearch.setId(tvHotSearchId);
        rlyHotSearch.addView(tvHotSearch);
        TextView tvHot = new TextView(mContext);
        RelativeLayout.LayoutParams tvHotParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotParam.addRule(RelativeLayout.RIGHT_OF, tvHotSearchId);
        tvHotParam.setMargins(margin5dp, margin10dp, 0, 0);
        tvHot.setLayoutParams(tvHotParam);
        tvHot.setText("hot!");
        tvHot.setTextColor(ContextCompat.getColor(mContext, R.color.haha_red));
        rlyHotSearch.addView(tvHot);
        llyHotDrivingSchool.addView(rlyHotSearch);

        View vwDivider = new View(mContext);
        vwDivider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.divider_width)));
        vwDivider.setBackgroundResource(R.color.haha_gray_divider);
        llyHotDrivingSchool.addView(vwDivider);

        TableLayout tbDrivingSchool = new TableLayout(mContext);
        LinearLayout.LayoutParams tbDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tbDrivingSchoolParam.setMargins(0, 0, 0, margin15dp);
        tbDrivingSchool.setLayoutParams(tbDrivingSchoolParam);
        tbDrivingSchool.setStretchAllColumns(true);

        int maxColCount = 4;
        for (int row = 0; row < mHotDrivingSchoolList.size() / maxColCount; row++) {
            TableRow tr = new TableRow(mContext);
            TableLayout.LayoutParams trParam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trParam.setMargins(0, margin10dp, 0, 0);
            tr.setLayoutParams(trParam);
            for (int col = 0; col < maxColCount; col++) {
                if (row * maxColCount + col > mHotDrivingSchoolList.size() - 1) {
                    break;
                }
                final DrivingSchool drivingSchool = mHotDrivingSchoolList.get(row * maxColCount + col);
                TextView tvDrivingSchool = new TextView(mContext);
                TableRow.LayoutParams tvDrivingSchoolParam = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvDrivingSchoolParam.setMargins(margin8dp, 0, margin8dp, 0);
                tvDrivingSchool.setLayoutParams(tvDrivingSchoolParam);
                tvDrivingSchool.setBackgroundResource(R.drawable.rect_bg_gray_bd_gray_corner);
                tvDrivingSchool.setGravity(Gravity.CENTER);
                tvDrivingSchool.setPadding(0, padding3dp, 0, padding3dp);
                tvDrivingSchool.setText(drivingSchool.name);
                tvDrivingSchool.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
                tvDrivingSchool.setTextSize(12);
                tvDrivingSchool.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnDrivingSchoolClickListener != null) {
                            mOnDrivingSchoolClickListener.clickDrivingSchool(drivingSchool);
                        }
                    }
                });
                tr.addView(tvDrivingSchool);
            }
            tbDrivingSchool.addView(tr);
        }
        llyHotDrivingSchool.addView(tbDrivingSchool);
        return llyHotDrivingSchool;
    }
}
