package com.hahaxueche.presenter.myPage;

import android.text.Html;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.PaymentStage;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
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
}
