package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.WithdrawRecord;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.WithdrawRecordsView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/1.
 */

public class WithdrawRecordsPresenter implements Presenter<WithdrawRecordsView> {
    private WithdrawRecordsView mWithdrawRecordsView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(WithdrawRecordsView view) {
        this.mWithdrawRecordsView = view;
        application = HHBaseApplication.get(mWithdrawRecordsView.getContext());
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            mWithdrawRecordsView.showProgressDialog();
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<ArrayList<WithdrawRecord>>>() {
                        @Override
                        public Observable<ArrayList<WithdrawRecord>> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.getWithdrawRecords(user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<ArrayList<WithdrawRecord>>() {
                        @Override
                        public void onCompleted() {
                            mWithdrawRecordsView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mWithdrawRecordsView.dismissProgressDialog();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mWithdrawRecordsView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(ArrayList<WithdrawRecord> withdrawRecords) {
                            mWithdrawRecordsView.loadWithdrawRecords(withdrawRecords);
                        }
                    });
        }
    }

    public void detachView() {
        this.mWithdrawRecordsView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }
}
