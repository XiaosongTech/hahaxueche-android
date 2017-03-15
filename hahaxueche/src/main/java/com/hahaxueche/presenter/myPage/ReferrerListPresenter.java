package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.responseList.ReferrerResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.ReferrerListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class ReferrerListPresenter extends HHBasePresenter implements Presenter<ReferrerListView> {
    private ReferrerListView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;

    public void attachView(ReferrerListView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
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

    public boolean isAgent() {
        User user = application.getSharedPrefUtil().getUser();
        return user != null && user.isLogin() && user.student.is_sales_agent;
    }
}
