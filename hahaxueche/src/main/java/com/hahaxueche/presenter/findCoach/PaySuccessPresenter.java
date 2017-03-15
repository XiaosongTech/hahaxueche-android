package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PaySuccessView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/10/13.
 */

public class PaySuccessPresenter extends HHBasePresenter implements Presenter<PaySuccessView> {
    private PaySuccessView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    //是否来源于购买保险页面
    public boolean isFromPurchaseInsurance;
    //是否购买保险
    public boolean isPurchasedInsurance;

    @Override
    public void attachView(PaySuccessView view) {
        this.mView = view;
        application = HHBaseApplication.get(view.getContext());
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || user.student == null) return;
        if (isFromPurchaseInsurance) {
            mView.showInsurancePayView();
            mView.setInsuranceAmount(user.student.insurance_order.total_amount);
            mView.setInsurancePaidAt(user.student.insurance_order.paid_at);
        } else {
            mView.showCoachPayView();
            HHApiService apiService = application.getApiService();
            subscription = apiService.getCoach(user.student.current_coach_id, user.student.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Coach>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            mView.showProgressDialog();
                        }

                        @Override
                        public void onCompleted() {
                            mView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.dismissProgressDialog();
                            HHLog.e(e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Coach coach) {
                            mView.setPayCoachName(coach.name);
                            PurchasedService ps = user.student.purchased_services.get(0);
                            mView.setPayTime(Utils.getDateFromUTC(ps.paid_at));
                            int insuranceWithNewCoachPrice = application.getConstants().insurance_prices.pay_with_new_coach_price;
                            int payAmount = ps.actual_amount +
                                    ((ps.product_type == Common.CLASS_TYPE_WUYOU_C1 || ps.product_type == Common.CLASS_TYPE_WUYOU_C2) ?
                                            insuranceWithNewCoachPrice : 0);
                            mView.setPayAmount(Utils.getMoney(payAmount));
                            mView.setPayOrderNo(ps.order_no);
                        }
                    });
        }
        if (isPurchasedInsurance) {
            mView.setSignText("上传投保信息");
        } else {
            mView.setSignText("签订专属协议");
        }
    }

    @Override
    public void detachView() {
        this.mView = null;
        application = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }
}
