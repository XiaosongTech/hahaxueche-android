package com.hahaxueche.ui.popupWindow.homepage;

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
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/5/16.
 */

public class DrivingSchoolPopupWindow extends PopupWindow {
    private Activity mActivity;
    private View contentView;
    private LinearLayout mLlyDrivingSchools;
    private TextView mTvNoLimit;
    private View mVwNoLimit;
    private OnDrivingSchoolClickListener mOnDrivingSchoolClickListener;
    private List<TextView> mTvList = new ArrayList<>();
    private List<View> mVwList = new ArrayList<>();

    public interface OnDrivingSchoolClickListener {
        void selectNoLimit();

        void selectDrivingSchool(int drivingSchoolId);

        void dismiss();
    }

    public DrivingSchoolPopupWindow(Activity activity, OnDrivingSchoolClickListener listener, List<DrivingSchool> drivingSchools) {
        super(activity);
        this.mActivity = activity;
        this.mOnDrivingSchoolClickListener = listener;
        initPopupWindow();
        addDrivingSchoolView(drivingSchools);
        initEvent();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mOnDrivingSchoolClickListener.dismiss();
            }
        });
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_map_search_driving_school, null);
        setContentView(contentView);
        setHeight(Utils.instence(mActivity).dip2px(300));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), (Bitmap) null));
        mLlyDrivingSchools = ButterKnife.findById(contentView, R.id.lly_driving_school);
        mTvNoLimit = ButterKnife.findById(contentView, R.id.tv_no_limit);
        mVwNoLimit = ButterKnife.findById(contentView, R.id.vw_no_limit);
    }

    private void addDrivingSchoolView(List<DrivingSchool> drivingSchools) {
        int startLine = 3;
        int margin20dp = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp);
        int padding15dp = Utils.instence(mActivity).dip2px(15);
        int endMoney = 0;
        for (DrivingSchool drivingSchool : drivingSchools) {
            TextView tvDrivingSchool = new TextView(mActivity);
            LinearLayout.LayoutParams tvDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvDrivingSchoolParam.setMargins(margin20dp, 0, margin20dp, 0);
            tvDrivingSchool.setLayoutParams(tvDrivingSchoolParam);
            tvDrivingSchool.setPadding(0, padding15dp, 0, padding15dp);
            tvDrivingSchool.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvDrivingSchool.setText(drivingSchool.name);
            tvDrivingSchool.setTag(drivingSchool);
            tvDrivingSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disAllViews();
                    TextView tvDrivingSchool = (TextView) view;
                    DrivingSchool tvSchool = (DrivingSchool) tvDrivingSchool.getTag();
                    tvDrivingSchool.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                    tvDrivingSchool.setTypeface(Typeface.DEFAULT_BOLD);
                    for (View vwDrivingSchool : mVwList) {
                        DrivingSchool vwSchool = (DrivingSchool) vwDrivingSchool.getTag();
                        if (vwSchool.id == tvSchool.id) {
                            vwDrivingSchool.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    mOnDrivingSchoolClickListener.selectDrivingSchool(tvSchool.id);
                    dismiss();
                }
            });
            mTvList.add(tvDrivingSchool);
            mLlyDrivingSchools.addView(tvDrivingSchool, startLine++);

            View vwDrivingSchool = new View(mActivity);
            LinearLayout.LayoutParams vwDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mActivity.getResources().getDimensionPixelSize(R.dimen.divider_width));
            vwDrivingSchoolParam.setMargins(margin20dp, 0, margin20dp, 0);
            vwDrivingSchool.setLayoutParams(vwDrivingSchoolParam);
            vwDrivingSchool.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            vwDrivingSchool.setVisibility(View.INVISIBLE);
            vwDrivingSchool.setTag(drivingSchool);
            mVwList.add(vwDrivingSchool);
            mLlyDrivingSchools.addView(vwDrivingSchool, startLine++);
        }
    }

    private void initEvent() {
        mTvNoLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disAllViews();
                mTvNoLimit.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                mTvNoLimit.setTypeface(Typeface.DEFAULT_BOLD);
                mVwNoLimit.setVisibility(View.VISIBLE);
                mOnDrivingSchoolClickListener.selectNoLimit();
                dismiss();
            }
        });
    }

    private void disAllViews() {
        mTvNoLimit.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvNoLimit.setTypeface(Typeface.DEFAULT);
        mVwNoLimit.setVisibility(View.INVISIBLE);
        for (TextView tvPrice : mTvList) {
            tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            tvPrice.setTypeface(Typeface.DEFAULT);
        }
        for (View vwPrice : mVwList) {
            vwPrice.setVisibility(View.INVISIBLE);
        }
    }
}
