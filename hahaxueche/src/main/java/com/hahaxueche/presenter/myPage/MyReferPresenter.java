package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.responseList.ReferrerResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyReferView;
import com.hahaxueche.util.Common;
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
 * Created by wangshirui on 2016/12/13.
 */

public class MyReferPresenter extends HHBasePresenter implements Presenter<MyReferView> {
    private MyReferView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;

    public void attachView(MyReferView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            if (user.student.is_sales_agent) {
                mView.showWithdraw(true);
                mView.setWithdrawMoney(Utils.getMoney(user.student.bonus_balance));
            }
        }
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchReferrers() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.showProgressDialog();
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<ReferrerResponseList>>() {
                        @Override
                        public Observable<ReferrerResponseList> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.getReferrers(user.student.id, Common.START_PAGE, Common.PER_PAGE,
                                        user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<ReferrerResponseList>() {
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
                        public void onNext(ReferrerResponseList referrerResponseList) {
                            if (referrerResponseList.data != null) {
                                mView.refreshReferrerList(referrerResponseList.data);
                                nextLink = referrerResponseList.links.next;
                                mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            }
                        }
                    });
        } else {
            mView.alertToLogin();
        }
    }

    public void addMoreReferrers() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.showProgressDialog();
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<ReferrerResponseList>>() {
                        @Override
                        public Observable<ReferrerResponseList> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.getReferrers(nextLink, user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<ReferrerResponseList>() {
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
                        public void onNext(ReferrerResponseList referrerResponseList) {
                            if (referrerResponseList.data != null) {
                                mView.addMoreReferrerList(referrerResponseList.data);
                                nextLink = referrerResponseList.links.next;
                                mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            }
                        }
                    });
        } else {
            mView.alertToLogin();
        }
    }

    public void refreshBonus() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mView.setWithdrawMoney(Utils.getMoney(student.bonus_balance));
                    }
                });
    }

    public void clickWithdraw() {
        addDataTrack("refer_page_cash_tapped", mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.navigateToWithdraw();
        } else {
            mView.alertToLogin();
        }
    }

    public boolean isAgent() {
        User user = application.getSharedPrefUtil().getUser();
        return user != null && user.isLogin() && user.student.is_sales_agent;
    }
}
