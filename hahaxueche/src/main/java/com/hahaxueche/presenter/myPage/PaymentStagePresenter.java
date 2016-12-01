package com.hahaxueche.presenter.myPage;

import android.text.Html;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.PaymentStage;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.PaymentStageView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class PaymentStagePresenter implements Presenter<PaymentStageView> {
    private PaymentStageView mPaymentStageView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;

    public void attachView(PaymentStageView view) {
        this.mPaymentStageView = view;
        application = HHBaseApplication.get(mPaymentStageView.getContext());
    }

    public void detachView() {
        this.mPaymentStageView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchCoachInfo() {
        HHApiService apiService = application.getApiService();
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.hasPurchasedService()) return;
        subscription = apiService.getCoach(user.student.current_coach_id, user.student.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Coach>() {
                    @Override
                    public void onCompleted() {
                        mPaymentStageView.setCoachInfo(mCoach);
                        refreshData();
                        pageStartCount();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Coach coach) {
                        mCoach = coach;
                    }
                });
    }

    public void refreshData() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mPaymentStageView.showProgressDialog();
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Student>>() {
                        @Override
                        public Observable<Student> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.getStudent(user.student.id, user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Student>() {
                        @Override
                        public void onCompleted() {
                            mPaymentStageView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mPaymentStageView.dismissProgressDialog();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mPaymentStageView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            application.getSharedPrefUtil().updateStudent(student);
                            if (student.hasPurchasedService()) {
                                PurchasedService ps = student.purchased_services.get(0);
                                mPaymentStageView.showPs(ps);
                                mPaymentStageView.enablePayStage(ps.current_payment_stage != ps.payment_stages.size() + 1);
                                PaymentStage currentPaymentStage = null;
                                for (PaymentStage paymentStage : ps.payment_stages) {
                                    if (paymentStage.stage_number == ps.current_payment_stage) {
                                        currentPaymentStage = paymentStage;
                                        break;
                                    }
                                }
                                if (currentPaymentStage != null) {
                                    mPaymentStageView.setCurrentPayAmountText(Html.fromHtml("<font color=\"#929292\">本期打款金额</font><font color=\"#ff9e00\">" + Utils.getMoneyYuan(currentPaymentStage.stage_amount) + "</font>"));
                                }
                            }
                        }
                    });
        }

    }

    public void makeReview(String paymentStage, String rating, String comment) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HashMap<String, String> countMap = new HashMap();
        if (user != null && user.isLogin()) {
            countMap.put("student_id", user.student.id);
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_comment_tapped", countMap);
        } else {
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_comment_tapped");
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("payment_stage", paymentStage);
        paramMap.put("rating", rating);
        paramMap.put("comment", comment);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Review>>() {
                    @Override
                    public Observable<Review> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.reviewCoach(mCoach.user_id, paramMap, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Review>() {
                    @Override
                    public void onCompleted() {
                        MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_comment_succeed", countMap);
                        refreshData();
                        mPaymentStageView.showShareAppDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mPaymentStageView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Review review) {
                    }
                });
    }

    public void pay() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("payment_stage", user.student.purchased_services.get(0).current_payment_stage);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<PurchasedService>>() {
                    @Override
                    public Observable<PurchasedService> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.payStage(paramMap, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<PurchasedService>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mPaymentStageView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        clickPaySuccessCount();
                        if (getCurrentPaymentStage().reviewable) {
                            mPaymentStageView.showReview(true, getCurrentPaymentStage(), true);
                        }
                        refreshData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPaymentStageView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mPaymentStageView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(PurchasedService ps) {
                    }
                });
    }

    public int getBonus() {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        return application.getConstants().getCity(cityId).referer_bonus;
    }

    public void clickICount() {
        final User user = application.getSharedPrefUtil().getUser();
        final HashMap<String, String> countMap = new HashMap();
        if (user != null && user.isLogin()) {
            countMap.put("student_id", user.student.id);
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_i_tapped", countMap);
        } else {
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_i_tapped");
        }
    }

    public void clickPayCount() {
        final User user = application.getSharedPrefUtil().getUser();
        final HashMap<String, String> countMap = new HashMap();
        if (user != null && user.isLogin()) {
            countMap.put("student_id", user.student.id);
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_pay_coach_tapped", countMap);
        } else {
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_pay_coach_tapped");
        }
    }

    public void clickPaySuccessCount() {
        final User user = application.getSharedPrefUtil().getUser();
        final HashMap<String, String> countMap = new HashMap();
        if (user != null && user.isLogin()) {
            countMap.put("student_id", user.student.id);
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_pay_coach_succeed", countMap);
        } else {
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_pay_coach_succeed");
        }
    }

    public PaymentStage getCurrentPaymentStage() {
        PaymentStage paymentStage = new PaymentStage();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin() && user.student.hasPurchasedService()) {
            int currentStageNum = user.student.purchased_services.get(0).current_payment_stage;
            for (PaymentStage ps : user.student.purchased_services.get(0).payment_stages) {
                if (ps.stage_number == currentStageNum) {
                    paymentStage = ps;
                    break;
                }
            }
        }
        return paymentStage;
    }

    public void pageStartCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mPaymentStageView.getContext(), "pay_coach_status_page_viewed");
        }
    }
}
