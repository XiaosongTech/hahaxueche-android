package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.model.user.coach.PartnerPrice;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PartnerDetailView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/20.
 */

public class PartnerDetailPresenter extends HHBasePresenter implements Presenter<PartnerDetailView> {
    private PartnerDetailView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Partner mPartner;
    private User mUser;
    private boolean isApplaud;

    @Override
    public void attachView(PartnerDetailView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mPartner = null;
        mUser = null;
    }

    public void setPartner(Partner Partner) {
        this.mPartner = Partner;
        if (mPartner == null) return;
        this.mView.showPartnerDetail(mPartner);
        int pos = 1;
        boolean addC1Label = false;
        boolean addC2Label = false;
        for (PartnerPrice price : mPartner.prices) {
            if (price.license_type == 1 && !addC1Label) {
                mView.addC1Label(pos++);
                addC1Label = true;
            }
            if (price.license_type == 2 && !addC2Label) {
                mView.addC2Label(pos++);
                addC2Label = true;
            }
            mView.addPrice(pos++, price.price, price.duration, price.description);
        }
        this.mView.initShareData(mPartner);
        loadApplaud();
        pageStartCount();
    }

    public void setPartner(final String partnerId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getPartner(partnerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Partner>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Partner partner) {
                        setPartner(partner);
                    }
                });
    }


    public Partner getPartner() {
        return mPartner;
    }

    private void loadApplaud() {
        isApplaud = (mPartner.liked == 1);
        mView.showApplaud(isApplaud);
        mView.setApplaudCount(mPartner.like_count);
    }

    public void applaud() {
        if (mUser == null || !mUser.isLogin()) {
            mView.alertToLogin("注册登录后,才可以点赞教练哦～\n注册获得更多学车咨询!～");
            return;
        }
        //like unlike 点击
        HashMap<String, String> countMap = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            countMap.put("student_id", mUser.student.id);
        }
        countMap.put("partner_id", mPartner.id);
        countMap.put("like", isApplaud ? "0" : "1");
        MobclickAgent.onEvent(mView.getContext(), "personal_coach_detail_page_like_unlike_tapped", countMap);
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mView.enableApplaud(false);
        if (isApplaud) {
            mapParam.put("like", 0);
            subscription = apiService.isValidToken(mUser.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Partner>>() {
                        @Override
                        public Observable<Partner> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.likePartner(mUser.student.id, mPartner.id, mapParam, mUser.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Partner>() {
                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Partner Partner) {
                            mPartner = Partner;
                        }
                    });
        } else {
            mapParam.put("like", 1);
            subscription = apiService.isValidToken(mUser.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Partner>>() {
                        @Override
                        public Observable<Partner> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.likePartner(mUser.student.id, mPartner.id, mapParam, mUser.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Partner>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            mView.startApplaudAnimation();
                        }

                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Partner Partner) {
                            mPartner = Partner;
                        }
                    });
        }
    }

    public void clickContactCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("partner_id", mPartner.id);
        MobclickAgent.onEvent(mView.getContext(), "personal_coach_detail_page_call_coach_tapped", map);
    }

    public void clickShareCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("partner_id", mPartner.id);
        MobclickAgent.onEvent(mView.getContext(), "personal_coach_detail_page_share_coach_tapped", map);
    }

    public void clickShareSuccessCount(String shareChannel) {
        //分享成功
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("partner_id", mPartner.id);
        map.put("share_channel", shareChannel);
        MobclickAgent.onEvent(mView.getContext(), "personal_coach_detail_page_share_coach_succeed", map);
    }

    public void pageStartCount() {
        //陪练详情展现
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("partner_id", mPartner.id);
        MobclickAgent.onEvent(mView.getContext(), "personal_coach_detail_page_viewed", map);
    }
}
