package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.responseList.ReferrerResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyReferView;
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
 * Created by wangshirui on 2016/12/13.
 */

public class MyReferPresenter implements Presenter<MyReferView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private MyReferView mMyReferView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;

    public void attachView(MyReferView view) {
        this.mMyReferView = view;
        application = HHBaseApplication.get(mMyReferView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mMyReferView.setWithdrawMoney(Utils.getMoney(user.student.bonus_balance));
        }
    }

    public void detachView() {
        this.mMyReferView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchReferrers() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mMyReferView.showProgressDialog();
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<ReferrerResponseList>>() {
                        @Override
                        public Observable<ReferrerResponseList> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.getReferrers(user.student.id, PAGE, PER_PAGE, user.session.access_token);
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
                            mMyReferView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyReferView.dismissProgressDialog();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mMyReferView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(ReferrerResponseList referrerResponseList) {
                            if (referrerResponseList.data != null) {
                                mMyReferView.refreshReferrerList(referrerResponseList.data);
                                nextLink = referrerResponseList.links.next;
                                mMyReferView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            }
                        }
                    });
        } else {
            mMyReferView.alertToLogin();
        }
    }

    public void addMoreReferrers() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mMyReferView.showProgressDialog();
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
                            mMyReferView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyReferView.dismissProgressDialog();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mMyReferView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(ReferrerResponseList referrerResponseList) {
                            if (referrerResponseList.data != null) {
                                mMyReferView.addMoreReferrerList(referrerResponseList.data);
                                nextLink = referrerResponseList.links.next;
                                mMyReferView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            }
                        }
                    });
        } else {
            mMyReferView.alertToLogin();
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
                            mMyReferView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mMyReferView.setWithdrawMoney(Utils.getMoney(student.bonus_balance));
                    }
                });
    }

    public void clickWithdraw() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyReferView.getContext(), "refer_page_cash_tapped", map);
            mMyReferView.navigateToWithdraw();
        } else {
            MobclickAgent.onEvent(mMyReferView.getContext(), "refer_page_cash_tapped");
            mMyReferView.alertToLogin();
        }
    }
}
