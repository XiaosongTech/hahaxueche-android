package com.hahaxueche.ui.dialog.findCoach;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hahaxueche.R;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerSortDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private TextView mTvSortPrice;
    private TextView mTvSortLike;
    private OnSortListener mOnSortListener;

    public interface OnSortListener {
        void sort(int sortBy);
    }

    public PartnerSortDialog(Context context, OnSortListener onSortListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mOnSortListener = onSortListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_partner_sort, null);
        mTvSortPrice = ButterKnife.findById(contentView, R.id.tv_sort_price);
        mTvSortLike = ButterKnife.findById(contentView, R.id.tv_sort_like);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSortPrice.setOnClickListener(mClickListener);
        mTvSortLike.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_sort_price:
                    disAllViews();
                    mDialog.dismiss();
                    setSortBy(0);
                    break;
                case R.id.tv_sort_like:
                    disAllViews();
                    mDialog.dismiss();
                    setSortBy(1);
                    break;
                default:
                    break;
            }
        }
    };

    private void disAllViews() {
        mTvSortPrice.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray));
        mTvSortLike.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray));
        mTvSortPrice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_sort_price_normal_btn), null, null, null);
        mTvSortLike.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_sort_zan_normal_btn), null, null, null);
    }

    private void setSortBy(int sortBy) {
        if (sortBy == 0) {
            mTvSortPrice.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
            mTvSortPrice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_sort_price_hold_btn), null, null, null);
        } else if (sortBy == 1) {
            mTvSortLike.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
            mTvSortLike.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_sort_zan_hold_btn), null, null, null);
        }
        mOnSortListener.sort(sortBy);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }


    private void setDialogParams() {
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.x = 20; // 新位置X坐标
        lp.y = 180; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }
}
