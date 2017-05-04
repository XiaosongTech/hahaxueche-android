package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.FollowListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/9.
 */

public class FollowListPresenter extends HHBasePresenter implements Presenter<FollowListView> {
    private FollowListView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    private User mUser;

    public void attachView(FollowListView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    public void detachView() {
        this.mView = null;
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
                            return apiService.getFollowList(Common.START_PAGE, Common.PER_PAGE,
                                    mUser.session.access_token);
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
                            mView.refreshCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
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
                            return apiService.getFollowList(Common.START_PAGE, Common.PER_PAGE,
                                    mUser.session.access_token);
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
                            mView.addMoreCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }

    public List<DrivingSchool> getHotDrivingSchools() {
        return application.getCityConstants().driving_schools.subList(0, 8);
    }
}
