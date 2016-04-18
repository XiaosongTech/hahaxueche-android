package com.hahaxueche.ui.adapter.mySetting;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.PaymentStage;
import com.hahaxueche.ui.activity.mySetting.PaymentStageActivity;
import com.hahaxueche.ui.dialog.PaymentStageInfoDialog;
import com.hahaxueche.utils.Util;

import java.util.List;

/**
 * Created by gibxin on 2016/3/2.
 */
public class PaymentStageAdapter extends BaseAdapter {
    private List<PaymentStage> mPaymentStageList;
    private int mResource;   //item的布局
    private PaymentStageActivity mContext;
    private LayoutInflater inflator;
    private TextView tvStageNumber;
    private TextView tvStageName;
    private TextView tvStageAmount;
    private TextView tvPaidAt;
    private TextView tvPaymentState;
    private ImageView ivPsMessage;
    private TextView tvReviewed;
    private TextView tvReadyForReview;
    private String mCurrentPaymentStage;
    private PaymentStageInfoDialog paymentStageInfoDialog;

    public PaymentStageAdapter(Context context, List<PaymentStage> paymentStageList, String currentPaymentStage, int resource) {
        mContext = (PaymentStageActivity) context;
        mPaymentStageList = paymentStageList;
        mResource = resource;
        mCurrentPaymentStage = currentPaymentStage;
    }

    @Override
    public int getCount() {
        return mPaymentStageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPaymentStageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(mResource, null);
            tvStageNumber = (TextView) convertView.findViewById(R.id.tv_stage_number);
            tvStageName = (TextView) convertView.findViewById(R.id.tv_stage_name);
            tvStageAmount = (TextView) convertView.findViewById(R.id.tv_stage_amount);
            tvPaidAt = (TextView) convertView.findViewById(R.id.tv_paid_at);
            tvPaymentState = (TextView) convertView.findViewById(R.id.tv_payment_state_label);
            ivPsMessage = (ImageView) convertView.findViewById(R.id.iv_ps_message);
            tvReviewed = (TextView) convertView.findViewById(R.id.tv_reviewed);
            tvReadyForReview = (TextView) convertView.findViewById(R.id.tv_ready_for_review);
        }
        final PaymentStage paymentStage = mPaymentStageList.get(position);
        tvStageNumber.setText(paymentStage.getStage_number());
        tvStageName.setText(paymentStage.getStage_name());
        tvStageAmount.setText(Util.getMoney(paymentStage.getStage_amount()));
        if (TextUtils.isEmpty(paymentStage.getPaid_at())) {
            //待打款
            tvPaidAt.setVisibility(View.INVISIBLE);//不显示打款日期
            tvPaymentState.setText("待打款");
            //点击显示待打款dialog
            ivPsMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paymentStageInfoDialog = new PaymentStageInfoDialog(mContext, paymentStage.getStage_name(), paymentStage.getDescription(), false);
                    paymentStageInfoDialog.show();
                }
            });
            if (mCurrentPaymentStage.equals(paymentStage.getStage_number())) {
                //当前状态
                //变橘黄色
                tvStageNumber.setBackgroundResource(R.drawable.circle_stage_number_orange);
                tvStageNumber.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                tvStageName.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                tvStageAmount.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                tvPaymentState.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
            }
        } else {
            tvPaidAt.setText(Util.getMonthDayFromUTC(paymentStage.getPaid_at()));
            tvPaymentState.setText("已打款");
            tvPaidAt.setVisibility(View.VISIBLE);//显示打款日期
            //变浅灰色
            tvStageNumber.setBackgroundResource(R.drawable.circle_stage_number_white);
            tvStageNumber.setTextColor(mContext.getResources().getColor(R.color.fCTxtGrayFade));
            tvStageName.setTextColor(mContext.getResources().getColor(R.color.fCTxtGrayFade));
            tvStageAmount.setTextColor(mContext.getResources().getColor(R.color.fCTxtGrayFade));
            tvPaymentState.setTextColor(mContext.getResources().getColor(R.color.fCTxtGrayFade));
            tvPaidAt.setTextColor(mContext.getResources().getColor(R.color.fCTxtGrayFade));
            //已打款，不可以评论的，如资料费、考试费
            if (paymentStage.getReviewable().equals("false")) {
                //点击显示已打款dialog
                tvReviewed.setVisibility(View.GONE);
                tvReadyForReview.setVisibility(View.GONE);
                ivPsMessage.setVisibility(View.VISIBLE);
                ivPsMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paymentStageInfoDialog = new PaymentStageInfoDialog(mContext, paymentStage.getStage_name(), paymentStage.getDescription(), true);
                        paymentStageInfoDialog.show();
                    }
                });
            } else {
                ivPsMessage.setVisibility(View.GONE);
                //已打款，已评论
                if (paymentStage.getReviewed().equals("true")) {
                    //已评论
                    tvReviewed.setVisibility(View.VISIBLE);
                } else {
                    if (paymentStage.getReady_for_review().equals("true")) {
                        //待评价
                        tvReadyForReview.setVisibility(View.VISIBLE);
                        tvReadyForReview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.showReview(false,paymentStage);
                            }
                        });
                    }
                }
            }
        }

        return convertView;
    }
}
