package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.WithdrawView;
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
 * Created by wangshirui on 2016/11/1.
 */

public class WithdrawPresenter extends HHBasePresenter implements Presenter<WithdrawView> {
    private WithdrawView mView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(WithdrawView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            refreshStudent(user.student);
        } else {
            mView.alertToLogin();
        }
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchStudent() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.startRefresh();
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
                            mView.stopRefresh();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.stopRefresh();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            application.getSharedPrefUtil().updateStudent(student);
                            refreshStudent(student);
                        }
                    });
        }
    }

    private void refreshStudent(Student student) {
        mView.setAvailableAmount(Utils.getMoney(student.bonus_balance));
        mView.setBankInfo(student.bank_card);
    }

    public void withdraw(String withdrawAmount) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (TextUtils.isEmpty(withdrawAmount)) {
            mView.showMessage("提现金额不能为空！");
            return;
        }
        final double withdrawMoney = Double.parseDouble(withdrawAmount) * 100;
        if (Double.compare(withdrawMoney, 10000d) < 0) {
            mView.showMessage("提现金额不能低于100元");
            return;
        } else {
            int interval = Double.compare(withdrawMoney, user.student.bonus_balance);
            if (interval > 0) {
                mView.showMessage("提现金额不能大于可提现金额");
                return;
            }
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("amount", withdrawMoney);
        mView.showProgressDialog();
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<BaseModel>>() {
                    @Override
                    public Observable<BaseModel> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.withdrawBonus(mapParam, user.student.id, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                        mView.back(true);
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
                    public void onNext(BaseModel baseModel) {
                    }
                });

    }

}
