package com.hahaxueche.presenter.findCoach;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.DrivingSchoolView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2017/5/7.
 */

public class DrivingSchoolPresenter extends HHBasePresenter implements Presenter<DrivingSchoolView> {
    private DrivingSchoolView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private DrivingSchool mDrivingSchool;

    @Override
    public void attachView(DrivingSchoolView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mDrivingSchool = null;
    }

    public void getDrivingSchoolDetail(int drivingSchoolId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getDrivingSchoolDetail(drivingSchoolId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<DrivingSchool>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        setDetailViews();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DrivingSchool drivingSchool) {
                        mDrivingSchool = drivingSchool;
                    }
                });
        subscription = apiService.getDrivingSchoolReviews(drivingSchoolId, Common.START_PAGE, Common.PER_PAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ReviewResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ReviewResponseList reviewResponseList) {
                        if (reviewResponseList != null && reviewResponseList.data != null && reviewResponseList.data.size() > 0) {
                            for (int i = 0; i < reviewResponseList.data.size() && i < 3; i++) {
                                //最多三条评论纪录
                                mView.addReview(reviewResponseList.data.get(i));
                            }
                        } else {
                            mView.showNoReview();
                        }
                    }
                });
    }

    private void setDetailViews() {
        if (mDrivingSchool.images != null && mDrivingSchool.images.size() > 0) {
            mView.setImage(Uri.parse(mDrivingSchool.images.get(0)));
        }
        mView.setName(mDrivingSchool.name);
        String consultantCountText = "已有" + Utils.getCount(mDrivingSchool.consult_count) + "人咨询";
        SpannableString ssConsultantCount = new SpannableString(consultantCountText);
        ssConsultantCount.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.app_theme_color)),
                consultantCountText.indexOf("有") + 1, consultantCountText.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setConsultantCount(ssConsultantCount);
        String lowestPriceText = "班别费用：" + Utils.getMoney(mDrivingSchool.lowest_price) + "起";
        SpannableString ssLowestPrice = new SpannableString(lowestPriceText);
        ssLowestPrice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.haha_orange)),
                consultantCountText.indexOf("：") + 1, consultantCountText.indexOf("起"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssLowestPrice.setSpan(new AbsoluteSizeSpan(Utils.instence(mView.getContext()).sp2px(12)),
                lowestPriceText.indexOf("起"), lowestPriceText.indexOf("起") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssLowestPrice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.haha_gray_text)),
                consultantCountText.indexOf("起"), consultantCountText.indexOf("起") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setLowestPrice(ssLowestPrice);
        String fieldCountText = "服务范围：共有" + mDrivingSchool.field_count + "个训练场地";
        SpannableString ssFieldCount = new SpannableString(fieldCountText);
        ssFieldCount.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.app_theme_color)),
                consultantCountText.indexOf("有") + 1, consultantCountText.indexOf("个"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setFieldCount(ssFieldCount);
        String passRateText = "通过率：" + mDrivingSchool.pass_rate;
        SpannableString ssPassRate = new SpannableString(passRateText);
        ssPassRate.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.app_theme_color)),
                passRateText.indexOf("：") + 1, passRateText.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setPassRate(ssPassRate);
        String satisfactionRateText = "满意度：100%";
        SpannableString ssSatisfactionRate = new SpannableString(satisfactionRateText);
        ssSatisfactionRate.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.app_theme_color)),
                satisfactionRateText.indexOf("：") + 1, satisfactionRateText.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setSatisfactionRate(ssSatisfactionRate);
        String coachCountText = "教练人数：" + mDrivingSchool.coach_count + "人";
        SpannableString ssCoachCount = new SpannableString(coachCountText);
        ssCoachCount.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.app_theme_color)),
                coachCountText.indexOf("：") + 1, coachCountText.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setCoachCount(ssCoachCount);
        mView.setBio(mDrivingSchool.bio);
        String groupBuyCountText = 42 + "人已参与";
        SpannableString ssGroupBuyCount = new SpannableString(groupBuyCountText);
        ssGroupBuyCount.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.app_theme_color)),
                0, groupBuyCountText.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setGroupBuyCount(ssGroupBuyCount);
        if (mDrivingSchool.lowest_price != 0) {
            mView.addClassType(new ClassType(Common.CLASS_TYPE_NORMAL_NAME, Common.CLASS_TYPE_NORMAL_C1,
                    mDrivingSchool.lowest_price, false, Common.CLASS_TYPE_NORMAL_DESC, Common.LICENSE_TYPE_C1));
        }
        if (mDrivingSchool.lowest_vip_price != 0) {
            mView.addClassType(new ClassType(Common.CLASS_TYPE_VIP_NAME, Common.CLASS_TYPE_VIP_C1,
                    mDrivingSchool.lowest_vip_price, false, Common.CLASS_TYPE_VIP_DESC, Common.LICENSE_TYPE_C1));
        }
        if (mDrivingSchool.lowest_price != 0) {
            int insuranceWithNewCoachPrice = application.getConstants().insurance_prices.pay_with_new_coach_price;
            mView.addClassType(new ClassType(Common.CLASS_TYPE_WUYOU_NAME, Common.CLASS_TYPE_WUYOU_C1,
                    mDrivingSchool.lowest_price + insuranceWithNewCoachPrice, true,
                    Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C1));
        }
        if (mDrivingSchool.fields != null) {
            for (int i = 0; i < mDrivingSchool.fields.size() && i < 3; i++) {
                //最多三条训练场纪录
                mView.addFieldView(mDrivingSchool.fields.get(i));
            }
        }
    }
}
