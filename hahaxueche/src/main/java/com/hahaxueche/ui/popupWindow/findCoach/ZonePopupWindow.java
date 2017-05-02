package com.hahaxueche.ui.popupWindow.findCoach;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/5/2.
 */

public class ZonePopupWindow extends PopupWindow {
    private Activity mActivity;
    private View contentView;
    private ScrollView mSvDistance;
    private LinearLayout mLlyZone;
    private LinearLayout mLlyDistance;
    private TextView mTvNear;
    private List<TextView> mTvZoneList = new ArrayList<>();
    private List<TextView> mTVDistanceList = new ArrayList<>();
    private List<View> mVwDistanceList = new ArrayList<>();
    private OnZoneClickListener mOnZoneClickListener;

    public interface OnZoneClickListener {
        void selectNoLimit();

        void selectZone(String zone);

        void selectDistance(int distance);

        void dismiss();
    }

    public ZonePopupWindow(Activity activity, OnZoneClickListener listener, String[] zones, int[] radius) {
        super(activity);
        this.mActivity = activity;
        this.mOnZoneClickListener = listener;
        initPopupWindow();
        addZoneView(zones);
        addDistanceView(radius);
        initEvent();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mOnZoneClickListener.dismiss();
            }
        });
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_driving_school_coach_zone, null);
        setContentView(contentView);
        //setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), (Bitmap) null));
        mSvDistance = ButterKnife.findById(contentView, R.id.sv_distance);
        mLlyZone = ButterKnife.findById(contentView, R.id.lly_zone);
        mLlyDistance = ButterKnife.findById(contentView, R.id.lly_distance);
        mTvNear = ButterKnife.findById(contentView, R.id.tv_near);
    }

    private void addZoneView(String[] zones) {
        int startLine = 1;
        int margin20dp = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp);
        int padding15dp = Utils.instence(mActivity).dip2px(15);
        for (int i = 0; i < zones.length; i++) {
            String zone = zones[i];
            TextView tvZone = new TextView(mActivity);
            tvZone.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tvZone.setPadding(margin20dp, padding15dp, margin20dp, padding15dp);
            tvZone.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvZone.setText(zone);
            tvZone.setTag(zone);
            tvZone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disAllZoneViews();
                    TextView tvZone = (TextView) view;
                    tvZone.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                    tvZone.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_white));
                    tvZone.setTypeface(Typeface.DEFAULT_BOLD);
                    String zone = (String) tvZone.getTag();
                    mOnZoneClickListener.selectZone(zone);
                    dismiss();
                }
            });
            mTvZoneList.add(tvZone);
            mLlyZone.addView(tvZone, startLine++);
        }
    }

    private void disAllZoneViews() {
        mSvDistance.setVisibility(View.GONE);
        mTvNear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_gray_background));
        for (TextView tvZone : mTvZoneList) {
            tvZone.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_gray_background));
            tvZone.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvZone.setTypeface(Typeface.DEFAULT);
        }
    }

    private void addDistanceView(int[] radius) {
        int margin20dp = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp);
        int padding15dp = Utils.instence(mActivity).dip2px(15);
        for (int i = 0; i < radius.length; i++) {
            int distance = radius[i];

            TextView tvDistance = new TextView(mActivity);
            LinearLayout.LayoutParams tvDistanceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvDistanceParam.setMargins(margin20dp, 0, margin20dp, 0);
            tvDistance.setLayoutParams(tvDistanceParam);
            tvDistance.setPadding(0, padding15dp, 0, padding15dp);
            tvDistance.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvDistance.setText(distance + "KM");
            tvDistance.setTag(distance);
            tvDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disAllDistanceViews();
                    TextView tvDistance = (TextView) view;
                    tvDistance.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                    tvDistance.setTypeface(Typeface.DEFAULT_BOLD);
                    for (View vwDistance : mVwDistanceList) {
                        if (vwDistance.getTag() == tvDistance.getTag()) {
                            vwDistance.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    int distance = (int) tvDistance.getTag();
                    mOnZoneClickListener.selectDistance(distance);
                    dismiss();
                }
            });
            mTVDistanceList.add(tvDistance);
            mLlyDistance.addView(tvDistance);

            View vwDistance = new View(mActivity);
            LinearLayout.LayoutParams vwDistanceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mActivity.getResources().getDimensionPixelSize(R.dimen.divider_width));
            vwDistanceParam.setMargins(margin20dp, 0, margin20dp, 0);
            vwDistance.setLayoutParams(vwDistanceParam);
            vwDistance.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            vwDistance.setVisibility(View.INVISIBLE);
            vwDistance.setTag(distance);
            mVwDistanceList.add(vwDistance);
            mLlyDistance.addView(vwDistance);
        }

        TextView tvDistance = new TextView(mActivity);
        LinearLayout.LayoutParams tvDistanceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvDistanceParam.setMargins(margin20dp, 0, margin20dp, 0);
        tvDistance.setLayoutParams(tvDistanceParam);
        tvDistance.setPadding(0, padding15dp, 0, padding15dp);
        tvDistance.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        tvDistance.setText("全城");
        tvDistance.setTag(Common.NO_LIMIT);
        tvDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disAllDistanceViews();
                TextView tvDistance = (TextView) view;
                tvDistance.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                tvDistance.setTypeface(Typeface.DEFAULT_BOLD);
                for (View vwDistance : mVwDistanceList) {
                    if (vwDistance.getTag() == tvDistance.getTag()) {
                        vwDistance.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                mOnZoneClickListener.selectNoLimit();
                dismiss();
            }
        });
        mTVDistanceList.add(tvDistance);
        mLlyDistance.addView(tvDistance);

        View vwDistance = new View(mActivity);
        LinearLayout.LayoutParams vwDistanceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mActivity.getResources().getDimensionPixelSize(R.dimen.divider_width));
        vwDistanceParam.setMargins(margin20dp, 0, margin20dp, 0);
        vwDistance.setLayoutParams(vwDistanceParam);
        vwDistance.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
        vwDistance.setVisibility(View.INVISIBLE);
        vwDistance.setTag(Common.NO_LIMIT);
        mVwDistanceList.add(vwDistance);
        mLlyDistance.addView(vwDistance);
    }

    private void disAllDistanceViews() {
        for (TextView tvDistance : mTVDistanceList) {
            tvDistance.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvDistance.setTypeface(Typeface.DEFAULT);
        }
        for (View vwDistance : mVwDistanceList) {
            vwDistance.setVisibility(View.INVISIBLE);
        }
    }

    private void initEvent() {
        mTvNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disAllZoneViews();
                mSvDistance.setVisibility(View.VISIBLE);
                mTvNear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_white));
            }
        });
    }
}
