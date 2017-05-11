package com.hahaxueche.presenter.myPage;

import android.text.Html;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.PaymentStage;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.PaymentStageView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class PaymentStagePresenter extends HHBasePresenter implements Presenter<PaymentStageView> {
    private PaymentStageView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;

    public void attachView(PaymentStageView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchCoachInfo() {
        HHApiService apiService = application.getApiService();
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.isPurchasedService()) return;
        subscription = apiService.getCoach(user.student.current_coach_id, user.student.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Coach>() {
                    @Override
                    public void onCompleted() {
                        mView.setCoachInfo(mCoach);
                        refreshData();
                        addDataTrack("pay_coach_status_page_viewed", mView.getContext());
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
            mView.showProgressDialog();
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
                            mView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.dismissProgressDialog();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            application.getSharedPrefUtil().updateStudent(student);
                            if (student.isPurchasedService()) {
                                PurchasedService ps = student.purchased_services.get(0);
                                mView.showPs(ps);
                                mView.enablePayStage(ps.current_payment_stage != ps.payment_stages.size() + 1);
                                PaymentStage currentPaymentStage = null;
                                for (PaymentStage paymentStage : ps.payment_stages) {
                                    if (paymentStage.stage_number == ps.current_payment_stage) {
                                        currentPaymentStage = paymentStage;
                                        break;
                                    }
                                }
                                if (currentPaymentStage != null) {
                                    mView.setCurrentPayAmountText(Html.fromHtml("<font color=\"#929292\">本期打款金额</font><font color=\"#ff9e00\">" + Utils.getMoneyYuan(currentPaymentStage.stage_amount) + "</font>"));
                                }
                            }
                        }
                    });
        }

    }

    public void makeReview(String paymentStage, String rating, String comment) {
        addDataTrack("pay_coach_status_page_comment_tapped", mView.getContext());
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
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
                        addDataTrack("pay_coach_status_page_comment_succeed", mView.getContext());
                        refreshData();
                        mView.showShareAppDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
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
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        addDataTrack("pay_coach_status_page_pay_coach_succeed", mView.getContext());
                        if (getCurrentPaymentStage().reviewable) {
                            mView.showReview(true, getCurrentPaymentStage(), true);
                        }
                        refreshData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(PurchasedService ps) {
                    }
                });
    }

    public PaymentStage getCurrentPaymentStage() {
        PaymentStage paymentStage = new PaymentStage();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin() && user.student.isPurchasedService()) {
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
}
