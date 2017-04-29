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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Common;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/4/29.
 */

public class TypePopupWindow extends PopupWindow {
    private Activity mActivity;
    private View contentView;
    private TextView mTvNoLimit;
    private TextView mTvC1;
    private TextView mTvWhatC1;
    private TextView mTvC2;
    private TextView mTvWhatC2;
    private RelativeLayout mRlyC1;
    private RelativeLayout mRlyC2;
    private View mVwNoLimit;
    private View mVwC1;
    private View mVwC2;
    private OnTypeClickListener mOnTypeClickListener;

    public interface OnTypeClickListener {
        void selectType(int licenseType);

        void clickQuestion(int licenseType);

        void dismiss();
    }

    public TypePopupWindow(Activity activity, OnTypeClickListener listener) {
        super(activity);
        this.mActivity = activity;
        this.mOnTypeClickListener = listener;
        initPopupWindow();
        initEvent();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mOnTypeClickListener.dismiss();
            }
        });
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_driving_school_coach_type, null);
        setContentView(contentView);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), (Bitmap) null));
        mTvNoLimit = ButterKnife.findById(contentView, R.id.tv_no_limit);
        mTvC1 = ButterKnife.findById(contentView, R.id.tv_C1_label);
        mTvWhatC1 = ButterKnife.findById(contentView, R.id.tv_what_is_C1);
        mTvC2 = ButterKnife.findById(contentView, R.id.tv_C2_label);
        mTvWhatC2 = ButterKnife.findById(contentView, R.id.tv_what_is_C2);
        mRlyC1 = ButterKnife.findById(contentView, R.id.rly_C1);
        mRlyC2 = ButterKnife.findById(contentView, R.id.rly_C2);
        mVwNoLimit = ButterKnife.findById(contentView, R.id.vw_no_limit);
        mVwC1 = ButterKnife.findById(contentView, R.id.vw_C1);
        mVwC2 = ButterKnife.findById(contentView, R.id.vw_C2);
        mTvWhatC1.setText("?");
        mTvWhatC2.setText("?");
    }

    private void initEvent() {
        mTvNoLimit.setOnClickListener(mClickListener);
        mRlyC1.setOnClickListener(mClickListener);
        mRlyC2.setOnClickListener(mClickListener);
        mTvWhatC1.setOnClickListener(mClickListener);
        mTvWhatC2.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_no_limit:
                    disAllViews();
                    selectType(Common.NO_LIMIT);
                    dismiss();
                    break;
                case R.id.rly_C1:
                    disAllViews();
                    selectType(Common.LICENSE_TYPE_C1);
                    dismiss();
                    break;
                case R.id.rly_C2:
                    disAllViews();
                    selectType(Common.LICENSE_TYPE_C2);
                    dismiss();
                    break;
                case R.id.tv_what_is_C1:
                    mOnTypeClickListener.clickQuestion(Common.LICENSE_TYPE_C1);
                    break;
                case R.id.tv_what_is_C2:
                    mOnTypeClickListener.clickQuestion(Common.LICENSE_TYPE_C2);
                    break;
                default:
                    break;
            }
        }
    };

    private void disAllViews() {
        mTvNoLimit.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvC1.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvC2.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvNoLimit.setTypeface(Typeface.DEFAULT);
        mTvC1.setTypeface(Typeface.DEFAULT);
        mTvC2.setTypeface(Typeface.DEFAULT);
        mVwNoLimit.setVisibility(View.INVISIBLE);
        mVwC1.setVisibility(View.INVISIBLE);
        mVwC2.setVisibility(View.INVISIBLE);
    }

    private void selectType(int licenseType) {
        if (licenseType == Common.NO_LIMIT) {
            mTvNoLimit.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            mTvNoLimit.setTypeface(Typeface.DEFAULT_BOLD);
            mVwNoLimit.setVisibility(View.VISIBLE);
        } else if (licenseType == Common.LICENSE_TYPE_C1) {
            mTvC1.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            mTvC1.setTypeface(Typeface.DEFAULT_BOLD);
            mVwC1.setVisibility(View.VISIBLE);
        } else if (licenseType == Common.LICENSE_TYPE_C2) {
            mTvC2.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            mTvC2.setTypeface(Typeface.DEFAULT_BOLD);
            mVwC2.setVisibility(View.VISIBLE);
        }
        mOnTypeClickListener.selectType(licenseType);
    }

}
