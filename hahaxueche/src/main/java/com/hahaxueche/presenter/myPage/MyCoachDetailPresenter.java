package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseBoolean;
import com.hahaxueche.model.base.BaseItemType;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Follow;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyCoachDetailView;
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
 * Created by wangshirui on 2016/10/26.
 */

public class MyCoachDetailPresenter extends HHBasePresenter implements Presenter<MyCoachDetailView> {
    private MyCoachDetailView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;
    private User mUser;
    private boolean isFollow;
    private boolean isApplaud;

    @Override
    public void attachView(MyCoachDetailView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mView = null;
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
        mView.setCoachName(mCoach.name);
        mView.setCoachBio(mCoach.bio);
        mView.setCoachAvatar(mCoach.avatar);
        mView.setCoachImages(mCoach.images);
        mView.setCoachPhone(mCoach.cell_phone);
        mView.setTrainingLocation(getTrainingFieldName());
        mView.setPeerCoaches(mCoach.peer_coaches);
        mView.setDrivingSchool(mCoach.driving_school);
        mView.setCourseName(getServiceTypeLevel());
        loadFollow();
        loadApplaud();
        pageStartCount();
    }

    public Coach getCoach() {
        return mCoach;
    }

    private String getServiceTypeLevel() {
        String ret = "";
        Constants constants = application.getConstants();
        for (BaseItemType serviceType : constants.service_types) {
            if (serviceType.id == mCoach.service_type) {
                ret = serviceType.readable_name;
                break;
            }
        }
        return ret;
    }

    /**
     * 训练场地址
     *
     * @return
     */
    private String getTrainingFieldName() {
        String ret = "";
        if (mCoach == null) return ret;
        Constants constants = application.getConstants();
        Field field = application.getFieldResponseList().getFieldById(mCoach.coach_group.field_id);
        if (field != null) {
            City city = constants.getCity(field.city_id);
            return city.name + field.street + (TextUtils.isEmpty(field.section) ? field.zone : field.section);
        } else {
            return "";
        }
    }

    public Field getTrainingField() {
        if (mCoach == null) return null;
        return application.getFieldResponseList().getFieldById(mCoach.coach_group.field_id);
    }

    private void loadFollow() {
        if (mUser == null || !mUser.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        mView.enableFollow(false);
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
                        mView.enableFollow(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.enableFollow(true);
                        isFollow = false;
                        mView.showFollow(isFollow);
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseBoolean baseBoolean) {
                        isFollow = baseBoolean.result;
                        mView.showFollow(isFollow);

                    }
                });
    }

    public void follow() {
        if (mUser == null || !mUser.isLogin()) {
            mView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        mView.enableFollow(false);
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
                            mView.enableFollow(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.enableFollow(true);
                            isFollow = false;
                            mView.showFollow(isFollow);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(BaseModel baseModel) {
                            isFollow = false;
                            mView.showFollow(isFollow);
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
                            mView.enableFollow(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.enableFollow(true);
                            isFollow = false;
                            mView.showFollow(isFollow);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Follow follow) {
                            isFollow = true;
                            mView.showFollow(isFollow);
                        }
                    });
        }
    }

    private void loadApplaud() {
        isApplaud = (mCoach.liked == 1);
        mView.showApplaud(isApplaud);
        mView.setApplaudCount(mCoach.like_count);
    }

    public void applaud() {
        if (mUser == null || !mUser.isLogin()) {
            mView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mView.enableApplaud(false);
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
                        public void onNext(Coach coach) {
                            mCoach = coach;
                        }
                    });
        }
    }

    public void clickShareCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        map.put("coach_id", mCoach.id);
        addDataTrack("my_coach_page_share_coach_tapped", mView.getContext(), map);
    }

    public void clickShareSuccessCount(String shareChannel) {
        //分享成功
        HashMap<String, String> map = new HashMap();
        map.put("coach_id", mCoach.id);
        map.put("share_channel", shareChannel);
        addDataTrack("my_coach_page_share_coach_succeed", mView.getContext(), map);
    }

    public void pageStartCount() {
        //教练详情展现
        HashMap<String, String> map = new HashMap();
        map.put("coach_id", mCoach.id);
        addDataTrack("my_coach_page_viewed", mView.getContext(), map);
    }

    public ClassType getClassTypeByPs() {
        PurchasedService ps = mUser.student.purchased_services.get(0);
        int insuranceWithNewCoachPrice = application.getConstants().insurance_prices.pay_with_new_coach_price;
        if (ps.product_type == Common.CLASS_TYPE_NORMAL_C1) {
            return new ClassType(Common.CLASS_TYPE_NORMAL_NAME, Common.CLASS_TYPE_NORMAL_C1,
                    mCoach.coach_group.training_cost, false, Common.CLASS_TYPE_NORMAL_DESC, Common.LICENSE_TYPE_C1);
        } else if (ps.product_type == Common.CLASS_TYPE_VIP_C1) {
            return new ClassType(Common.CLASS_TYPE_VIP_NAME, Common.CLASS_TYPE_VIP_C1,
                    mCoach.coach_group.vip_price, false, Common.CLASS_TYPE_VIP_DESC, Common.LICENSE_TYPE_C1);
        } else if (ps.product_type == Common.CLASS_TYPE_WUYOU_C1) {
            return new ClassType(Common.CLASS_TYPE_WUYOU_NAME, Common.CLASS_TYPE_WUYOU_C1,
                    mCoach.coach_group.training_cost + insuranceWithNewCoachPrice, true,
                    Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C1);
        } else if (ps.product_type == Common.CLASS_TYPE_NORMAL_C2) {
            new ClassType(Common.CLASS_TYPE_NORMAL_NAME, Common.CLASS_TYPE_NORMAL_C2,
                    mCoach.coach_group.c2_price, false, Common.CLASS_TYPE_NORMAL_DESC, Common.LICENSE_TYPE_C2);
        } else if (ps.product_type == Common.CLASS_TYPE_VIP_C2) {
            return new ClassType(Common.CLASS_TYPE_VIP_NAME, Common.CLASS_TYPE_VIP_C2,
                    mCoach.coach_group.c2_vip_price, false, Common.CLASS_TYPE_VIP_DESC, Common.LICENSE_TYPE_C2);
        }
        return new ClassType(Common.CLASS_TYPE_WUYOU_NAME, Common.CLASS_TYPE_WUYOU_C2,
                mCoach.coach_group.c2_price + insuranceWithNewCoachPrice, true,
                Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C2);
    }
}
