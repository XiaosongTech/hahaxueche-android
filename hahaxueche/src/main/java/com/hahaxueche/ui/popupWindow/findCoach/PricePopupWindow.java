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
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/4/29.
 */

public class PricePopupWindow extends PopupWindow {
    private Activity mActivity;
    private View contentView;
    private LinearLayout mLlyPrices;
    private TextView mTvNoLimit;
    private View mVwNoLimit;
    private OnPriceClickListener mOnPriceClickListener;
    private List<TextView> mTvList = new ArrayList<>();
    private List<View> mVwList = new ArrayList<>();

    public interface OnPriceClickListener {
        void selectNoLimit();

        void selectPrice(int[] priceRange);

        void selectMaxPrice(int endMoney);

        void dismiss();
    }

    public PricePopupWindow(Activity activity, OnPriceClickListener listener, int[][] priceRanges) {
        super(activity);
        this.mActivity = activity;
        this.mOnPriceClickListener = listener;
        initPopupWindow();
        addPriceView(priceRanges);
        initEvent();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mOnPriceClickListener.dismiss();
            }
        });
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_driving_school_coach_price, null);
        setContentView(contentView);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), (Bitmap) null));
        mLlyPrices = ButterKnife.findById(contentView, R.id.lly_prices);
        mTvNoLimit = ButterKnife.findById(contentView, R.id.tv_no_limit);
        mVwNoLimit = ButterKnife.findById(contentView, R.id.vw_no_limit);
    }

    private void addPriceView(int[][] priceRanges) {
        int startLine = 3;
        int margin20dp = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp);
        int padding15dp = Utils.instence(mActivity).dip2px(15);
        int endMoney = 0;
        for (int i = 0; i < priceRanges.length; i++) {
            int[] priceRange = priceRanges[i];
            if (i == priceRanges.length - 1) {
                endMoney = priceRange[1];
            }
            TextView tvPrice = new TextView(mActivity);
            LinearLayout.LayoutParams tvPriceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvPriceParam.setMargins(margin20dp, 0, margin20dp, 0);
            tvPrice.setLayoutParams(tvPriceParam);
            tvPrice.setPadding(0, padding15dp, 0, padding15dp);
            tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
            if (i == 0) {
                tvPrice.setText(priceRange[1] + "元以下");
            } else {
                tvPrice.setText(priceRange[0] + "-" + priceRange[1] + "元");
            }
            tvPrice.setTag(priceRange);
            tvPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disAllViews();
                    TextView tvPrice = (TextView) view;
                    tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                    tvPrice.setTypeface(Typeface.DEFAULT_BOLD);
                    for (View vwPrice : mVwList) {
                        if (vwPrice.getTag() == tvPrice.getTag()) {
                            vwPrice.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    int[] priceRange = (int[]) tvPrice.getTag();
                    mOnPriceClickListener.selectPrice(priceRange);
                    dismiss();
                }
            });
            mTvList.add(tvPrice);
            mLlyPrices.addView(tvPrice, startLine++);

            View vwPrice = new View(mActivity);
            LinearLayout.LayoutParams vwPriceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mActivity.getResources().getDimensionPixelSize(R.dimen.divider_width));
            vwPriceParam.setMargins(margin20dp, 0, margin20dp, 0);
            vwPrice.setLayoutParams(vwPriceParam);
            vwPrice.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
            vwPrice.setVisibility(View.INVISIBLE);
            vwPrice.setTag(priceRange);
            mVwList.add(vwPrice);
            mLlyPrices.addView(vwPrice, startLine++);
        }

        TextView tvPrice = new TextView(mActivity);
        LinearLayout.LayoutParams tvPriceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPriceParam.setMargins(margin20dp, 0, margin20dp, 0);
        tvPrice.setLayoutParams(tvPriceParam);
        tvPrice.setPadding(0, padding15dp, 0, padding15dp);
        tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.haha_gray));
        tvPrice.setText(endMoney + "元以上");
        tvPrice.setTag(endMoney);
        final int finalEndMoney = endMoney;
        tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disAllViews();
                TextView tvPrice = (TextView) view;
                tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                tvPrice.setTypeface(Typeface.DEFAULT_BOLD);
                for (View vwPrice : mVwList) {
                    if (vwPrice.getTag() == tvPrice.getTag()) {
                        vwPrice.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                mOnPriceClickListener.selectMaxPrice(finalEndMoney);
                dismiss();
            }
        });
        mTvList.add(tvPrice);
        mLlyPrices.addView(tvPrice, startLine++);

        View vwPrice = new View(mActivity);
        LinearLayout.LayoutParams vwPriceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mActivity.getResources().getDimensionPixelSize(R.dimen.divider_width));
        vwPriceParam.setMargins(margin20dp, 0, margin20dp, 0);
        vwPrice.setLayoutParams(vwPriceParam);
        vwPrice.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
        vwPrice.setVisibility(View.INVISIBLE);
        vwPrice.setTag(endMoney);
        mVwList.add(vwPrice);
        mLlyPrices.addView(vwPrice, startLine++);
    }

    private void initEvent() {
        mTvNoLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disAllViews();
                mTvNoLimit.setTextColor(ContextCompat.getColor(mActivity, R.color.app_theme_color));
                mTvNoLimit.setTypeface(Typeface.DEFAULT_BOLD);
                mVwNoLimit.setVisibility(View.VISIBLE);
                mOnPriceClickListener.selectNoLimit();
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
