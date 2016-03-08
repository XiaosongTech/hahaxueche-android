package com.hahaxueche.ui.activity.mySetting;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.ReviewInfo;
import com.hahaxueche.model.findCoach.StuPurchaseResponse;
import com.hahaxueche.model.mySetting.PaymentStage;
import com.hahaxueche.model.mySetting.PurchasedService;
import com.hahaxueche.model.signupLogin.StudentModel;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.adapter.mySetting.PaymentStageAdapter;
import com.hahaxueche.ui.dialog.ReviewDialog;
import com.hahaxueche.ui.dialog.TransferConfirmDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

/**
 * Created by gibxin on 2016/3/2.
 */
public class PaymentStageActivity extends MSBaseActivity {
    private ImageButton ibtnPsBack;
    private TextView tvPsCoachName;
    private CircleImageView civPsCoachAvatar;
    private TextView tvPaidTime;
    private TextView tvOrderCode;
    private TextView tvPsTotalAmount;
    private TextView tvPsPaidAmount;
    private TextView tvPsUnPaidAmount;
    private TextView tvCurrentPayAmount;
    private TextView tvSureTransfer;
    private TextView tvCongratulation;
    private ListView lvPurchasedServices;
    private PaymentStageAdapter mPaymentStageAdapter;
    private String access_token;
    private StudentModel mStudent;
    private PurchasedService mPurchasedService;
    private CoachModel mCurrentCoach;
    private PaymentStage mPaymentStage;
    private TransferConfirmDialog transferConfirmDialog;
    private ReviewDialog reviewDialog;
    private ProgressDialog pd;//进度框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_stage);
        initViews();
        refreshUI();
    }

    private void initViews() {
        ibtnPsBack = Util.instence(this).$(this, R.id.ibtn_ps_back);
        tvPsCoachName = Util.instence(this).$(this, R.id.tv_ps_coach_name);
        civPsCoachAvatar = Util.instence(this).$(this, R.id.cir_ps_coach_avatar);
        tvPaidTime = Util.instence(this).$(this, R.id.tv_paid_time);
        tvOrderCode = Util.instence(this).$(this, R.id.tv_order_code);
        tvPsTotalAmount = Util.instence(this).$(this, R.id.tv_ps_total_amount);
        tvPsPaidAmount = Util.instence(this).$(this, R.id.tv_ps_paid_amount);
        tvPsUnPaidAmount = Util.instence(this).$(this, R.id.tv_ps_unpaid_amount);
        tvCurrentPayAmount = Util.instence(this).$(this, R.id.tv_current_pay_amount);
        tvSureTransfer = Util.instence(this).$(this, R.id.tv_sure_transfer);
        tvCongratulation = Util.instence(this).$(this, R.id.tv_congratulation);
        lvPurchasedServices = Util.instence(this).$(this, R.id.lv_purchased_services);
    }

    private void refreshUI() {
        if (pd != null) {
            pd.dismiss();
        }
        SharedPreferences spSession = getSharedPreferences("session", Activity.MODE_PRIVATE);
        access_token = spSession.getString("access_token", "");
        Type stuType = new TypeToken<StudentModel>() {
        }.getType();
        mStudent = JsonUtils.deserialize(spSession.getString("student", ""), stuType);
        Type coachType = new TypeToken<CoachModel>() {
        }.getType();
        mCurrentCoach = JsonUtils.deserialize(spSession.getString("current_coach", ""), coachType);
        mPurchasedService = mStudent.getPurchased_services().get(0);
        for (PaymentStage paymentStage : mPurchasedService.getPayment_stages()) {
            if (paymentStage.getStage_number().equals(mPurchasedService.getCurrent_payment_stage())) {
                mPaymentStage = paymentStage;
                break;
            }
        }
        if(null!=mCurrentCoach) {
            //教练姓名
            tvPsCoachName.setText(mCurrentCoach.getName());
            //头像
            final int iconWidth = Util.instence(this).dip2px(50);
            final int iconHeight = iconWidth;
            Picasso.with(this).load(mCurrentCoach.getAvatar()).resize(iconWidth, iconHeight)
                    .into(civPsCoachAvatar);
        }
        //支付时间
        tvPaidTime.setText(Util.getDateFromUTC(mPurchasedService.getPaid_at()));
        //订单编号
        tvOrderCode.setText(mPurchasedService.getCharge_id());
        //总金额
        tvPsTotalAmount.setText(Util.getMoney(mPurchasedService.getTotal_amount()));
        //已打款
        tvPsPaidAmount.setText(Util.getMoney(mPurchasedService.getPaid_amount()));
        //待打款
        tvPsUnPaidAmount.setText(Util.getMoney(mPurchasedService.getUnpaid_amount()));
        //listview
        mPaymentStageAdapter = new PaymentStageAdapter(PaymentStageActivity.this, mPurchasedService.getPayment_stages(),
                mPurchasedService.getCurrent_payment_stage(), R.layout.view_payment_stage_list_item);
        lvPurchasedServices.setAdapter(mPaymentStageAdapter);
        //已全部打款
        if (Integer.parseInt(mPurchasedService.getCurrent_payment_stage()) == mPurchasedService.getPayment_stages().size()+1) {
            tvCurrentPayAmount.setVisibility(View.GONE);
            tvSureTransfer.setVisibility(View.GONE);
            tvCongratulation.setVisibility(View.VISIBLE);
            tvSureTransfer.setClickable(false);
        } else {
            //本期打款金额
            tvCurrentPayAmount.setVisibility(View.VISIBLE);
            tvSureTransfer.setVisibility(View.VISIBLE);
            tvCongratulation.setVisibility(View.GONE);
            tvCurrentPayAmount.setText(Html.fromHtml("<font color=\"#929292\">本期打款金额</font><font color=\"#ff9e00\">" + Util.getMoneyYuan(mPaymentStage.getStage_amount()) + "</font>"));
            tvSureTransfer.setClickable(true);
            tvSureTransfer.setOnClickListener(mClickListener);
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //确认打款
                case R.id.tv_sure_transfer:
                    transferConfirmDialog = new TransferConfirmDialog(PaymentStageActivity.this, mPaymentStage.getDescription(), new TransferConfirmDialog.OnBtnClickListener() {
                        @Override
                        public void onTransfer() {
                            transferConfirmDialog.dismiss();
                            if (pd != null) {
                                pd.dismiss();
                            }
                            pd = ProgressDialog.show(PaymentStageActivity.this, null, "付款中，请稍后……");
                            fcPresenter.purchasedService(mPaymentStage.getStage_number(), access_token, new FCCallbackListener<PurchasedService>() {
                                @Override
                                public void onSuccess(PurchasedService purchasedService) {
                                    /**
                                     * 付款成功后
                                     * 1.当前阶段可评论
                                     * 1.2.加载评论dialog
                                     * 1.3.刷新缓存
                                     * 1.4.refreshUI
                                     *
                                     * 2.当前阶段不可评论
                                     * 2.1刷新缓存
                                     * 2.2加载评论dialog
                                     */
                                    if (mPaymentStage.getReviewable().equals("true")) {
                                        showReview(true,mPaymentStage);
                                    } else {
                                        refreshStuCache();
                                        refreshUI();
                                    }
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                    if (pd != null) {
                                        pd.dismiss();
                                    }
                                    Toast.makeText(PaymentStageActivity.this, "打款失败", Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    });
                    transferConfirmDialog.show();
                    break;
            }
        }
    };

    /**
     * 更新学生缓存信息
     */
    private void refreshStuCache() {
        //更新SharedPreferences中的student
        msPresenter.getStudent(mStudent.getId(), access_token, new MSCallbackListener<StudentModel>() {
            @Override
            public void onSuccess(StudentModel data) {
                mStudent = data;
                SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("student", JsonUtils.serialize(mStudent));
                editor.commit();
                if (!TextUtils.isEmpty(data.getCurrent_coach_id())) {
                    fcPresenter.getCoach(data.getCurrent_coach_id(), new FCCallbackListener<CoachModel>() {
                        @Override
                        public void onSuccess(CoachModel coachModel) {
                            SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("current_coach", JsonUtils.serialize(coachModel));
                            editor.commit();
                            refreshUI();
                        }

                        @Override
                        public void onFailure(String errorEvent, String message) {

                        }
                    });
                } else {
                    refreshUI();
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    /**
     * 显示评价dialog
     * @param isShowTitle
     */
    public void showReview(boolean isShowTitle,PaymentStage paymentStage) {
        reviewDialog = new ReviewDialog(PaymentStageActivity.this, isShowTitle, paymentStage.getStage_number(), paymentStage.getCoach_user_id(), paymentStage.getStage_name(), new ReviewDialog.OnBtnClickListener() {
            @Override
            public void onReview(String review, float score, String paymentStageNumber, String coachUserId) {
                reviewDialog.dismiss();
                msPresenter.makeReview(coachUserId, paymentStageNumber, score + "", review, access_token, new MSCallbackListener<ReviewInfo>() {
                    @Override
                    public void onSuccess(ReviewInfo reviewInfo) {
                        Toast.makeText(PaymentStageActivity.this, "评论成功", Toast.LENGTH_SHORT);
                        refreshStuCache();
                        refreshUI();
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        Toast.makeText(PaymentStageActivity.this, "评论失败", Toast.LENGTH_SHORT);
                        refreshStuCache();
                        refreshUI();
                    }
                });
            }
        }) {
            @Override
            public void dismiss() {
                super.dismiss();
                Toast.makeText(PaymentStageActivity.this, "取消评论", Toast.LENGTH_SHORT);
                refreshStuCache();
                refreshUI();
            }
        };
        reviewDialog.show();
    }
}
