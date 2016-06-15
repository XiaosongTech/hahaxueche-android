package com.hahaxueche.ui.activity.mySetting;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.PaymentStage;
import com.hahaxueche.model.student.PurchasedService;
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.adapter.mySetting.PaymentStageAdapter;
import com.hahaxueche.ui.dialog.ReviewDialog;
import com.hahaxueche.ui.dialog.TransferConfirmDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;


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
    private Session mSession;
    private Student mStudent;
    private PurchasedService mPurchasedService;
    private Coach mCurrentCoach;
    private PaymentStage mPaymentStage;
    private TransferConfirmDialog transferConfirmDialog;
    private ReviewDialog reviewDialog;
    private ProgressDialog pd;//进度框
    private SharedPreferencesUtil spUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_stage);
        spUtil = new SharedPreferencesUtil(this);
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
        ibtnPsBack.setOnClickListener(mClickListener);
    }

    private void refreshUI() {
        if (pd != null) {
            pd.dismiss();
        }
        mSession = spUtil.getUser().getSession();
        mStudent = spUtil.getUser().getStudent();
        mCurrentCoach = spUtil.getCurrentCoach();
        mPurchasedService = mStudent.getPurchased_services().get(0);
        for (PaymentStage paymentStage : mPurchasedService.getPayment_stages()) {
            if (paymentStage.getStage_number().equals(mPurchasedService.getCurrent_payment_stage())) {
                mPaymentStage = paymentStage;
                break;
            }
        }
        if (null != mCurrentCoach) {
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
        tvOrderCode.setText(mPurchasedService.getOrder_no());
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
        setListViewHeightBasedOnChildren(lvPurchasedServices);
        //已全部打款
        if (Integer.parseInt(mPurchasedService.getCurrent_payment_stage()) == mPurchasedService.getPayment_stages().size() + 1) {
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
                            fcPresenter.purchasedService(mPaymentStage.getStage_number(), mSession.getAccess_token(), new FCCallbackListener<PurchasedService>() {
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
                                        showReview(true, mPaymentStage);
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
                                    Toast.makeText(PaymentStageActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    transferConfirmDialog.show();
                    break;
                case R.id.ibtn_ps_back:
                    PaymentStageActivity.this.finish();
                    break;
            }
        }
    };

    /**
     * 更新学生缓存信息
     */
    private void refreshStuCache() {
        //更新SharedPreferences中的student
        msPresenter.getStudent(mStudent.getId(), mSession.getAccess_token(), new MSCallbackListener<Student>() {
            @Override
            public void onSuccess(Student data) {
                mStudent = data;
                User user = spUtil.getUser();
                user.setStudent(mStudent);
                spUtil.setUser(user);
                if (!TextUtils.isEmpty(data.getCurrent_coach_id())) {
                    fcPresenter.getCoach(data.getCurrent_coach_id(), new FCCallbackListener<Coach>() {
                        @Override
                        public void onSuccess(Coach coach) {
                            spUtil.setCurrentCoach(coach);
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
     *
     * @param isShowTitle
     */
    public void showReview(boolean isShowTitle, PaymentStage paymentStage) {
        reviewDialog = new ReviewDialog(PaymentStageActivity.this, isShowTitle, paymentStage.getStage_number(), paymentStage.getCoach_user_id(), paymentStage.getStage_name(), new ReviewDialog.OnBtnClickListener() {
            @Override
            public void onReview(String review, float score, String paymentStageNumber, String coachUserId) {
                reviewDialog.dismiss();
                msPresenter.makeReview(coachUserId, paymentStageNumber, score + "", review, mSession.getAccess_token(), new MSCallbackListener<ReviewInfo>() {
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //params.height = Util.instence(this).dip2px(height) * listAdapter.getCount() + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
