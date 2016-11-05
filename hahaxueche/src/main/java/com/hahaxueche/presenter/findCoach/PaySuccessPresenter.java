package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PaySuccessView;
import com.hahaxueche.util.HHLog;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/10/13.
 */

public class PaySuccessPresenter implements Presenter<PaySuccessView> {
    private PaySuccessView mPaySuccessView;
    private Subscription subscription;

    @Override
    public void attachView(PaySuccessView view) {
        this.mPaySuccessView = view;
        HHBaseApplication application = HHBaseApplication.get(mPaySuccessView.getContext());
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || user.student == null || !user.student.hasPurchasedService()) return;
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCoach(user.student.current_coach_id, user.student.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Coach>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Coach coach) {
                        mPaySuccessView.loadPayInfo(coach,user.student.purchased_services.get(0));
                    }
                });
    }

    @Override
    public void detachView() {
        this.mPaySuccessView = null;
        if (subscription != null) subscription.unsubscribe();
    }
}
