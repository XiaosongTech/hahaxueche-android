package com.hahaxueche.ui.dialog.findCoach;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/2/24.
 */

public class SelectInsuranceDialog extends Dialog {
    private Context mContext;
    //默认选择
    private boolean mSelect = true;

    public interface OnSelectListener {
        void select(boolean isSelect);
    }

    private OnSelectListener mOnSelectListener;

    public SelectInsuranceDialog(Context context, OnSelectListener onSelectListener) {
        super(context);
        mContext = context;
        mOnSelectListener = onSelectListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_select_insurance, null);
        setContentView(view);
        initView(view);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    private void initView(View view) {
        final ImageView mIvSelect = ButterKnife.findById(view, R.id.iv_select_insurance);
        mIvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvSelect.setClickable(false);
                mSelect = !mSelect;
                mIvSelect.setImageDrawable(ContextCompat.getDrawable(mContext,
                        mSelect ? R.drawable.ic_cashout_chack_btn : R.drawable.ic_cashout_unchack_btn));
                mIvSelect.setClickable(true);
            }
        });
        TextView mTvCancel = ButterKnife.findById(view, R.id.tv_cancel);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView mTvSure = ButterKnife.findById(view, R.id.tv_sure);
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSelectListener.select(mSelect);
                dismiss();
            }
        });
    }
}
