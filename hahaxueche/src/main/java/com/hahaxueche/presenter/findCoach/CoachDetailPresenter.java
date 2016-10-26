package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseBoolean;
import com.hahaxueche.model.base.BaseItemType;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Follow;
import com.hahaxueche.model.user.coach.ProductType;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.CoachDetailView;
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
 * Created by wangshirui on 16/10/5.
 */

public class CoachDetailPresenter implements Presenter<CoachDetailView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private CoachDetailView mCoachDetailView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;
    private User mUser;
    private boolean isFollow;
    private boolean isApplaud;

    @Override
    public void attachView(CoachDetailView view) {
        this.mCoachDetailView = view;
        application = HHBaseApplication.get(mCoachDetailView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mCoachDetailView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mCoach = null;
        mUser = null;
    }

    public void setCoach(Coach coach) {
        this.mCoach = coach;
        if (mCoach == null) return;
        setCoachLabel();
        this.mCoachDetailView.showCoachDetail(mCoach);
        this.mCoachDetailView.addPrices(getPrices());
        loadReviews();
        loadFollow();
        loadApplaud();
    }

    private ArrayList<ProductType> getPrices() {
        ArrayList<ProductType> productTypes = new ArrayList<>();
        if (mCoach.coach_group.training_cost != 0) {
            ProductType price = new ProductType(mCoach.coach_group.market_price, "C1手动档", "超值", R.drawable.rect_bg_orange_ssm, "四人一车，性价比高");
            productTypes.add(price);
        }
        if (mCoach.coach_group.vip_price != 0) {
            ProductType price = new ProductType(mCoach.coach_group.vip_price, "C1手动档", "VIP", R.drawable.rect_bg_orange_ssm, "一人一车，极速拿证");
            productTypes.add(price);
        }
        if (mCoach.coach_group.c2_price != 0) {
            ProductType price = new ProductType(mCoach.coach_group.c2_price, "C2自动档", "超值", R.drawable.rect_bg_yellow_ssm, "四人一车，性价比高");
            productTypes.add(price);
        }
        if (mCoach.coach_group.c2_vip_price != 0) {
            ProductType price = new ProductType(mCoach.coach_group.c2_vip_price, "C2自动档", "VIP", R.drawable.rect_bg_yellow_ssm, "一人一车，极速拿证");
            productTypes.add(price);
        }
        return productTypes;
    }

    public Coach getCoach() {
        return mCoach;
    }

    private void setCoachLabel() {
        Constants constants = application.getConstants();
        for (BaseItemType skillLevel : constants.skill_levels) {
            if (String.valueOf(skillLevel.id).equals(mCoach.skill_level)) {
                mCoach.skill_level_label = skillLevel.readable_name;
                break;
            }
        }
    }

    /**
     * 训练场地址
     *
     * @return
     */
    public String getTrainingFieldName() {
        String ret = "";
        if (mCoach == null) return ret;
        Constants constants = application.getConstants();
        Field field = constants.getField(mCoach.coach_group.field_id);
        City city = constants.getCity(field.city_id);
        return city.name + field.street + field.section;
    }

    public Field getTrainingField() {
        if (mCoach == null) return null;
        Constants constants = application.getConstants();
        Field field = constants.getField(mCoach.coach_group.field_id);
        return field;
    }

    public void setCoach(String coachId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCoach(coachId, (mUser != null && mUser.isLogin()) ? mUser.student.id : null)
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
                        setCoach(coach);
                    }
                });
    }

    private void loadReviews() {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getReviews(mCoach.user_id, PAGE, PER_PAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ReviewResponseList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ReviewResponseList reviewResponseList) {
                        if (reviewResponseList != null && reviewResponseList.data != null && reviewResponseList.data.size() > 0) {
                            mCoachDetailView.showReviews(reviewResponseList);
                        } else {
                            mCoachDetailView.showNoReview(mCoach.name);
                        }
                    }
                });
    }

    private void loadFollow() {
        if (mUser == null || !mUser.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        mCoachDetailView.enableFollow(false);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<BaseBoolean>>() {
                    @Override
                    public Observable<BaseBoolean> call(BaseValid baseValid) {
                        if (baseValid.valid) {//session验证
                            return apiService.isFollow(mCoach.user_id, mUser.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseBoolean>() {
                    @Override
                    public void onCompleted() {
                        mCoachDetailView.enableFollow(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCoachDetailView.enableFollow(true);
                        isFollow = false;
                        mCoachDetailView.showFollow(isFollow);
                        if (ErrorUtil.isInvalidSession(e)) {
                            mCoachDetailView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseBoolean baseBoolean) {
                        isFollow = baseBoolean.result;
                        mCoachDetailView.showFollow(isFollow);

                    }
                });
    }

    public void follow() {
        if (mUser == null || !mUser.isLogin()) {
            mCoachDetailView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        mCoachDetailView.enableFollow(false);
        if (isFollow) {//当前关注状态，取消关注
            subscription = apiService.isValidToken(mUser.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<BaseModel>>() {
                        @Override
                        public Observable<BaseModel> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.cancelFollow(mCoach.user_id, mUser.session.access_token);
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
                            mCoachDetailView.enableFollow(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mCoachDetailView.enableFollow(true);
                            isFollow = false;
                            mCoachDetailView.showFollow(isFollow);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mCoachDetailView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(BaseModel baseModel) {
                            isFollow = false;
                            mCoachDetailView.showFollow(isFollow);
                        }
                    });
        } else {//关注
            subscription = apiService.isValidToken(mUser.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Follow>>() {
                        @Override
                        public Observable<Follow> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.follow(mCoach.user_id, mUser.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Follow>() {
                        @Override
                        public void onCompleted() {
                            mCoachDetailView.enableFollow(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mCoachDetailView.enableFollow(true);
                            isFollow = false;
                            mCoachDetailView.showFollow(isFollow);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mCoachDetailView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Follow follow) {
                            isFollow = true;
                            mCoachDetailView.showFollow(isFollow);
                        }
                    });
        }
    }

    private void loadApplaud() {
        isApplaud = (mCoach.liked == 1);
        mCoachDetailView.showApplaud(isApplaud);
        mCoachDetailView.setApplaudCount(mCoach.like_count);
    }

    public void applaud() {
        if (mUser == null || !mUser.isLogin()) {
            mCoachDetailView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mCoachDetailView.enableApplaud(false);
        if (isApplaud) {
            mapParam.put("like", 0);
            subscription = apiService.isValidToken(mUser.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Coach>>() {
                        @Override
                        public Observable<Coach> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.like(mUser.student.id, mCoach.id, mapParam, mUser.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Coach>() {
                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mCoachDetailView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mCoachDetailView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mCoachDetailView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Coach coach) {
                            mCoach = coach;
                        }
                    });
        } else {
            mapParam.put("like", 1);
            subscription = apiService.isValidToken(mUser.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Coach>>() {
                        @Override
                        public Observable<Coach> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.like(mUser.student.id, mCoach.id, mapParam, mUser.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Coach>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            mCoachDetailView.startApplaudAnimation();
                        }

                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mCoachDetailView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mCoachDetailView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mCoachDetailView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Coach coach) {
                            mCoach = coach;
                        }
                    });
        }
    }

    public void purchaseCoach() {
        if (mUser == null || !mUser.isLogin()) {
            mCoachDetailView.alertToLogin();
            return;
        }
        mCoachDetailView.navigateToPurchaseCoach(mCoach);
    }

}
