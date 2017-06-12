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
import com.hahaxueche.model.base.ZoneDetail;
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
    private LinearLayout mLlyZone;
    private LinearLayout mLlyDistance;
    private TextView mTvNear;
    private List<TextView> mTvZoneList = new ArrayList<>();
    private List<TextView> mTVDistanceList = new ArrayList<>();
    private List<View> mVwDistanceList = new ArrayList<>();
    private List<TextView> mTvBusinessAreaList = new ArrayList<>();
    private List<View> mVwBusinessAreaList = new ArrayList<>();
    private OnZoneClickListener mOnZoneClickListener;
    private int[] mRadius;

    public interface OnZoneClickListener {
        void selectNoLimit();

        void selectBusinessArea(String businessArea);

        void selectDistance(int distance);

        void selectZone(String zone);

        void dismiss();
    }

    public ZonePopupWindow(Activity activity, OnZoneClickListener listener, List<ZoneDetail> zoneDetails, int[] radius) {
        super(activity);
        this.mActivity = activity;
        this.mOnZoneClickListener = listener;
        mRadius = radius;
        initPopupWindow();
        addZoneView(zoneDetails);
        addDistanceView();
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
        setHeight(Utils.instence(mActivity).dip2px(300));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), (Bitmap) null));
        mLlyZone = ButterKnife.findById(contentView, R.id.lly_zone);
        mLlyDistance = ButterKnife.findById(contentView, R.id.lly_distance);
        mTvNear = ButterKnife.findById(contentView, R.id.tv_near);
    }

    private void addZoneView(List<ZoneDetail> zoneDetails) {
        int startLine = 1;
        int margin20dp = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp);
        int padding15dp = Utils.instence(mActivity).dip2px(15);
        for (ZoneDetail zoneDetail : zoneDetails) {
            TextView tvZone = new TextView(mActivity);
            tvZone.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tvZone.setPadding(margin20dp, padding15dp, margin20dp, padding15dp);
            tvZone.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvZone.setText(zoneDetail.zone);
            tvZone.setTag(zoneDetail);
            tvZone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disAllZoneViews();
                    TextView tvZone = (TextView) view;
                    tvZone.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                    tvZone.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_white));
                    tvZone.setTypeface(Typeface.DEFAULT_BOLD);
                    ZoneDetail zoneDetail = (ZoneDetail) tvZone.getTag();
                    addBusinessAreaView(zoneDetail.zone, zoneDetail.business_areas);
                }
            });
            mTvZoneList.add(tvZone);
            mLlyZone.addView(tvZone, startLine++);
        }
    }

    private void addBusinessAreaView(String zone, String[] businessAreas) {
        mLlyDistance.removeAllViews();
        int margin20dp = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp);
        int padding15dp = Utils.instence(mActivity).dip2px(15);

        //添加全区
        TextView tvAllZone = new TextView(mActivity);
        LinearLayout.LayoutParams tvAllZoneParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvAllZoneParam.setMargins(margin20dp, 0, margin20dp, 0);
        tvAllZone.setLayoutParams(tvAllZoneParam);
        tvAllZone.setPadding(0, padding15dp, 0, padding15dp);
        tvAllZone.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        tvAllZone.setText("全部" + zone);
        tvAllZone.setTag(zone);
        tvAllZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disAllBusinessAreaViews();
                TextView tvAllZone = (TextView) view;
                tvAllZone.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                tvAllZone.setTypeface(Typeface.DEFAULT_BOLD);
                for (View vwBusinessArea : mVwBusinessAreaList) {
                    if (vwBusinessArea.getTag() == tvAllZone.getTag()) {
                        vwBusinessArea.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                String zone = (String) tvAllZone.getTag();
                mOnZoneClickListener.selectZone(zone);
                dismiss();
            }
        });
        mTvBusinessAreaList.add(tvAllZone);
        mLlyDistance.addView(tvAllZone);

        View vwAllZone = new View(mActivity);
        LinearLayout.LayoutParams vwAllZoneParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mActivity.getResources().getDimensionPixelSize(R.dimen.divider_width));
        vwAllZoneParam.setMargins(margin20dp, 0, margin20dp, 0);
        vwAllZone.setLayoutParams(vwAllZoneParam);
        vwAllZone.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
        vwAllZone.setVisibility(View.INVISIBLE);
        vwAllZone.setTag(zone);
        mVwBusinessAreaList.add(vwAllZone);
        mLlyDistance.addView(vwAllZone);

        //添加商圈
        for (int i = 0; i < businessAreas.length; i++) {
            String businessArea = businessAreas[i];

            TextView tvBusinessArea = new TextView(mActivity);
            LinearLayout.LayoutParams tvBusinessAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvBusinessAreaParam.setMargins(margin20dp, 0, margin20dp, 0);
            tvBusinessArea.setLayoutParams(tvBusinessAreaParam);
            tvBusinessArea.setPadding(0, padding15dp, 0, padding15dp);
            tvBusinessArea.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvBusinessArea.setText(businessArea);
            tvBusinessArea.setTag(businessArea);
            tvBusinessArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disAllBusinessAreaViews();
                    TextView tvBusinessArea = (TextView) view;
                    tvBusinessArea.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                    tvBusinessArea.setTypeface(Typeface.DEFAULT_BOLD);
                    for (View vwBusinessArea : mVwBusinessAreaList) {
                        if (vwBusinessArea.getTag() == tvBusinessArea.getTag()) {
                            vwBusinessArea.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    String businessArea = (String) tvBusinessArea.getTag();
                    mOnZoneClickListener.selectBusinessArea(businessArea);
                    dismiss();
                }
            });
            mTvBusinessAreaList.add(tvBusinessArea);
            mLlyDistance.addView(tvBusinessArea);

            View vwBusinessArea = new View(mActivity);
            LinearLayout.LayoutParams vwBusinessAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mActivity.getResources().getDimensionPixelSize(R.dimen.divider_width));
            vwBusinessAreaParam.setMargins(margin20dp, 0, margin20dp, 0);
            vwBusinessArea.setLayoutParams(vwBusinessAreaParam);
            vwBusinessArea.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            vwBusinessArea.setVisibility(View.INVISIBLE);
            vwBusinessArea.setTag(businessArea);
            mVwBusinessAreaList.add(vwBusinessArea);
            mLlyDistance.addView(vwBusinessArea);
        }
    }

    private void disAllZoneViews() {
        mTvNear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_gray_background));
        for (TextView tvZone : mTvZoneList) {
            tvZone.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_gray_background));
            tvZone.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvZone.setTypeface(Typeface.DEFAULT);
        }
    }

    private void addDistanceView() {
        mLlyDistance.removeAllViews();
        int margin20dp = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp);
        int padding15dp = Utils.instence(mActivity).dip2px(15);
        for (int i = 0; i < mRadius.length; i++) {
            int distance = mRadius[i];

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

    private void disAllBusinessAreaViews() {
        for (TextView tvBusinessArea : mTvBusinessAreaList) {
            tvBusinessArea.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvBusinessArea.setTypeface(Typeface.DEFAULT);
        }
        for (View vwBusinessArea : mVwBusinessAreaList) {
            vwBusinessArea.setVisibility(View.INVISIBLE);
        }
    }

    private void initEvent() {
        mTvNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disAllZoneViews();
                mTvNear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.haha_white));
                addDistanceView();
            }
        });
    }
}
