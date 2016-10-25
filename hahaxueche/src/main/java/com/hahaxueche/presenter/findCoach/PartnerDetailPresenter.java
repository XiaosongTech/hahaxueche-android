package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.model.user.coach.PartnerPrice;
import com.hahaxueche.model.user.coach.ProductType;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PartnerDetailView;
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
 * Created by wangshirui on 2016/10/20.
 */

public class PartnerDetailPresenter implements Presenter<PartnerDetailView> {
    private PartnerDetailView mPartnerDetailView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Partner mPartner;
    private User mUser;
    private boolean isApplaud;

    @Override
    public void attachView(PartnerDetailView view) {
        this.mPartnerDetailView = view;
        application = HHBaseApplication.get(mPartnerDetailView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mPartnerDetailView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mPartner = null;
        mUser = null;
    }

    public void setPartner(Partner Partner) {
        this.mPartner = Partner;
        if (mPartner == null) return;
        this.mPartnerDetailView.showPartnerDetail(mPartner);
        this.mPartnerDetailView.addPrices(getPrices());
        loadApplaud();
    }

    private ArrayList<ProductType> getPrices() {
        ArrayList<ProductType> productTypes = new ArrayList<>();
        for (PartnerPrice price : mPartner.prices) {
            String name = price.license_type == 1 ? "C1手动档" : "C2自动档";
            String label = price.duration + "h";
            int nameBackgroundResId = price.license_type == 1 ? R.drawable.rect_bg_orange_ssm : R.drawable.rect_bg_yellow_ssm;
            String remark = price.duration == 9 ? "短期速成，性价比高" : "基础强化，全面巩固";
            ProductType productType = new ProductType(price.price, name, label, nameBackgroundResId, remark);
            productTypes.add(productType);
        }
        return productTypes;
    }

    public Partner getPartner() {
        return mPartner;
    }

    private void loadApplaud() {
        isApplaud = (mPartner.liked == 1);
        mPartnerDetailView.showApplaud(isApplaud);
        mPartnerDetailView.setApplaudCount(mPartner.like_count);
    }

    public void applaud() {
        if (mUser == null || !mUser.isLogin()) {
            mPartnerDetailView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mPartnerDetailView.enableApplaud(false);
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
                            mPartnerDetailView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mPartnerDetailView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mPartnerDetailView.forceOffline();
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
                            mPartnerDetailView.startApplaudAnimation();
                        }

                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mPartnerDetailView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mPartnerDetailView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mPartnerDetailView.forceOffline();
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
}
