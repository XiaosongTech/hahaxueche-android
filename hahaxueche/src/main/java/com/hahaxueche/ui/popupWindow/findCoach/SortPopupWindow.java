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
import android.widget.TextView;

import com.hahaxueche.R;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/4/29.
 */

public class SortPopupWindow extends PopupWindow {
    private Activity mActivity;
    private View contentView;
    private TextView mTvAutoSort;
    private TextView mTvDistanceSort;
    private TextView mTvCommentSort;
    private TextView mTvPriceSort;
    private View mVwAutoSort;
    private View mVwDistanceSort;
    private View mVwCommentSort;
    private View mVwPriceSort;
    private OnSortListener mOnSortListener;

    public interface OnSortListener {
        void sort(int sortBy);

        void dismiss();
    }

    public SortPopupWindow(Activity activity, OnSortListener onSortListener) {
        super(activity);
        this.mActivity = activity;
        this.mOnSortListener = onSortListener;
        initPopupWindow();
        initEvent();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mOnSortListener.dismiss();
            }
        });
    }

    private void initPopupWindow() {

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_driving_school_coach_sort, null);
        setContentView(contentView);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), (Bitmap) null));
        mTvAutoSort = ButterKnife.findById(contentView, R.id.tv_auto_sort);
        mTvDistanceSort = ButterKnife.findById(contentView, R.id.tv_distance_sort);
        mTvCommentSort = ButterKnife.findById(contentView, R.id.tv_comment_sort);
        mTvPriceSort = ButterKnife.findById(contentView, R.id.tv_price_sort);
        mVwAutoSort = ButterKnife.findById(contentView, R.id.vw_auto_sort);
        mVwDistanceSort = ButterKnife.findById(contentView, R.id.vw_distance_sort);
        mVwCommentSort = ButterKnife.findById(contentView, R.id.vw_comment_sort);
        mVwPriceSort = ButterKnife.findById(contentView, R.id.vw_price_sort);
    }

    private void initEvent() {
        mTvAutoSort.setOnClickListener(mClickListener);
        mTvDistanceSort.setOnClickListener(mClickListener);
        mTvCommentSort.setOnClickListener(mClickListener);
        mTvPriceSort.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_auto_sort:
                    disAllViews();
                    dismiss();
                    setSortBy(0);
                    break;
                case R.id.tv_distance_sort:
                    disAllViews();
                    dismiss();
                    setSortBy(1);
                    break;
                case R.id.tv_comment_sort:
                    disAllViews();
                    dismiss();
                    setSortBy(5);
                    break;
                case R.id.tv_price_sort:
                    disAllViews();
                    dismiss();
                    setSortBy(3);
                    break;
                default:
                    break;
            }
        }
    };

    private void disAllViews() {
        mTvAutoSort.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvDistanceSort.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvCommentSort.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvPriceSort.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        mTvAutoSort.setTypeface(Typeface.DEFAULT);
        mTvDistanceSort.setTypeface(Typeface.DEFAULT);
        mTvCommentSort.setTypeface(Typeface.DEFAULT);
        mTvPriceSort.setTypeface(Typeface.DEFAULT);
        mVwAutoSort.setVisibility(View.INVISIBLE);
        mVwDistanceSort.setVisibility(View.INVISIBLE);
        mVwCommentSort.setVisibility(View.INVISIBLE);
        mVwPriceSort.setVisibility(View.INVISIBLE);
    }

    private void setSortBy(int sortBy) {
        if (sortBy == 0) {
            mTvAutoSort.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            mTvAutoSort.setTypeface(Typeface.DEFAULT_BOLD);
            mVwAutoSort.setVisibility(View.VISIBLE);
        } else if (sortBy == 1) {
            mTvDistanceSort.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            mTvDistanceSort.setTypeface(Typeface.DEFAULT_BOLD);
            mVwDistanceSort.setVisibility(View.VISIBLE);
        } else if (sortBy == 5) {
            mTvCommentSort.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            mTvCommentSort.setTypeface(Typeface.DEFAULT_BOLD);
            mVwCommentSort.setVisibility(View.VISIBLE);
        } else if (sortBy == 3) {
            mTvPriceSort.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            mTvPriceSort.setTypeface(Typeface.DEFAULT_BOLD);
            mVwPriceSort.setVisibility(View.VISIBLE);
        }
        mOnSortListener.sort(sortBy);
    }
}
