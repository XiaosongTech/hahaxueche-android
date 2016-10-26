package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseBoolean;
import com.hahaxueche.model.base.BaseItemType;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Follow;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyCoachDetailView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class MyCoachDetailPresenter implements Presenter<MyCoachDetailView> {
    private MyCoachDetailView mMyCoachDetailView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;
    private User mUser;
    private boolean isFollow;
    private boolean isApplaud;

    @Override
    public void attachView(MyCoachDetailView view) {
        this.mMyCoachDetailView = view;
        application = HHBaseApplication.get(mMyCoachDetailView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mMyCoachDetailView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mCoach = null;
        mUser = null;
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

    public void setCoach(Coach coach) {
        this.mCoach = coach;
        if (mCoach == null) return;
        setCoachLabel();
        this.mMyCoachDetailView.showCoachDetail(mCoach);
        loadFollow();
        loadApplaud();
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
        for (BaseItemType serviceType : constants.service_types) {
            if (String.valueOf(serviceType.id).equals(mCoach.service_type)) {
                mCoach.service_type_label = serviceType.readable_name;
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

    private void loadFollow() {
        if (mUser == null || !mUser.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        mMyCoachDetailView.enableFollow(false);
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
                        mMyCoachDetailView.enableFollow(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMyCoachDetailView.enableFollow(true);
                        isFollow = false;
                        mMyCoachDetailView.showFollow(isFollow);
                        if (ErrorUtil.isInvalidSession(e)) {
                            mMyCoachDetailView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseBoolean baseBoolean) {
                        isFollow = baseBoolean.result;
                        mMyCoachDetailView.showFollow(isFollow);

                    }
                });
    }

    public void follow() {
        if (mUser == null || !mUser.isLogin()) {
            mMyCoachDetailView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        mMyCoachDetailView.enableFollow(false);
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
                            mMyCoachDetailView.enableFollow(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyCoachDetailView.enableFollow(true);
                            isFollow = false;
                            mMyCoachDetailView.showFollow(isFollow);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mMyCoachDetailView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(BaseModel baseModel) {
                            isFollow = false;
                            mMyCoachDetailView.showFollow(isFollow);
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
                            mMyCoachDetailView.enableFollow(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyCoachDetailView.enableFollow(true);
                            isFollow = false;
                            mMyCoachDetailView.showFollow(isFollow);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mMyCoachDetailView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Follow follow) {
                            isFollow = true;
                            mMyCoachDetailView.showFollow(isFollow);
                        }
                    });
        }
    }

    private void loadApplaud() {
        isApplaud = (mCoach.liked == 1);
        mMyCoachDetailView.showApplaud(isApplaud);
        mMyCoachDetailView.setApplaudCount(mCoach.like_count);
    }

    public void applaud() {
        if (mUser == null || !mUser.isLogin()) {
            mMyCoachDetailView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mMyCoachDetailView.enableApplaud(false);
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
                            mMyCoachDetailView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyCoachDetailView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mMyCoachDetailView.forceOffline();
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
                            mMyCoachDetailView.startApplaudAnimation();
                        }

                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mMyCoachDetailView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyCoachDetailView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mMyCoachDetailView.forceOffline();
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
}
