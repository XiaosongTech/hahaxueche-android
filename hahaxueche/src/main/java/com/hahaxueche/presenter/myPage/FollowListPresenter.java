package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.FollowListView;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/9.
 */

public class FollowListPresenter implements Presenter<FollowListView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private FollowListView mFollowListView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    private User mUser;

    public void attachView(FollowListView view) {
        this.mFollowListView = view;
        application = HHBaseApplication.get(mFollowListView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    public void detachView() {
        this.mFollowListView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mUser = null;
    }

    public void fetchCoaches() {
        if (mUser == null || !mUser.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<CoachResponseList>>() {
                    @Override
                    public Observable<CoachResponseList> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getFollowList(PAGE, PER_PAGE, mUser.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CoachResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(CoachResponseList coachResponseList) {
                        if (coachResponseList.data != null) {
                            mFollowListView.refreshCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mFollowListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }

                    }
                });
    }

    public void addMoreCoaches() {
        if (TextUtils.isEmpty(nextLink)) return;
        if (mUser == null || !mUser.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<CoachResponseList>>() {
                    @Override
                    public Observable<CoachResponseList> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getFollowList(PAGE, PER_PAGE, mUser.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CoachResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(CoachResponseList coachResponseList) {
                        if (coachResponseList.data != null) {
                            mFollowListView.addMoreCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mFollowListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }
}
