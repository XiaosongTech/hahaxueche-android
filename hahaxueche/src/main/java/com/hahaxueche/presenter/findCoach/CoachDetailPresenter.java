package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
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
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Follow;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.CoachDetailView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private List<ClassType> mClassTypeList;

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
        if (mCoach.coach_group.c2_price != 0 || mCoach.coach_group.c2_vip_price != 0) {
            mCoachDetailView.setLicenseTab(true, true);
        } else {
            mCoachDetailView.setLicenseTab(true, false);
        }
        mClassTypeList = new ArrayList<>();
        if (mCoach.coach_group.training_cost != 0) {
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_NORMAL_NAME, Common.CLASS_TYPE_NORMAL_C1,
                    mCoach.coach_group.training_cost, false, Common.CLASS_TYPE_NORMAL_DESC, Common.LICENSE_TYPE_C1));
        }
        if (mCoach.coach_group.vip_price != 0) {
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_VIP_NAME, Common.CLASS_TYPE_VIP_C1,
                    mCoach.coach_group.vip_price, false, Common.CLASS_TYPE_VIP_DESC, Common.LICENSE_TYPE_C1));
        }
        if (mCoach.coach_group.training_cost != 0) {
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_WUYOU_NAME, Common.CLASS_TYPE_WUYOU_C1,
                    mCoach.coach_group.training_cost + Common.INSURANCE_PRICE_TOGETHER, true,
                    Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C1));
        }
        if (mCoach.coach_group.c2_price != 0) {
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_NORMAL_NAME, Common.CLASS_TYPE_NORMAL_C2,
                    mCoach.coach_group.c2_price, false, Common.CLASS_TYPE_NORMAL_DESC, Common.LICENSE_TYPE_C2));
        }
        if (mCoach.coach_group.c2_vip_price != 0) {
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_VIP_NAME, Common.CLASS_TYPE_VIP_C2,
                    mCoach.coach_group.c2_vip_price, false, Common.CLASS_TYPE_VIP_DESC, Common.LICENSE_TYPE_C2));
        }
        if (mCoach.coach_group.c2_price != 0) {
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_WUYOU_NAME, Common.CLASS_TYPE_WUYOU_C2,
                    mCoach.coach_group.c2_price + Common.INSURANCE_PRICE_TOGETHER, true,
                    Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C2));
        }
        //车友无忧班是特殊教练，只有无忧班
        if (mCoach.coach_group.group_type == Common.GROUP_TYPE_CHEYOU_WUYOU) {
            mClassTypeList = new ArrayList<>();
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_WUYOU_NAME, Common.CLASS_TYPE_WUYOU_C1,
                    mCoach.coach_group.training_cost + Common.INSURANCE_PRICE_TOGETHER, true,
                    Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C1));
            mCoachDetailView.setLicenseTab(true, false);
        }
        //默认选择C1
        selectLicenseType(Common.LICENSE_TYPE_C1);
        mCoachDetailView.setCoachBadge(coach.skill_level.equals("1"));
        mCoachDetailView.setPayBadge(coach.has_cash_pledge == 1);
        this.mCoachDetailView.initShareData(mCoach);
        loadReviews();
        loadFollow();
        loadApplaud();
        pageStartCount();
    }

    public void selectLicenseType(int licenseType) {
        mCoachDetailView.clearClassType();
        for (ClassType classType : mClassTypeList) {
            if (classType.licenseType == licenseType) {
                mCoachDetailView.addClassType(classType);
            }
        }
        if (licenseType == Common.LICENSE_TYPE_C1) {
            mCoachDetailView.showC1Tab(true);
            mCoachDetailView.showC2Tab(false);
        } else {
            mCoachDetailView.showC1Tab(false);
            mCoachDetailView.showC2Tab(true);
        }
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
        return constants.getField(mCoach.coach_group.field_id);
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
            mCoachDetailView.alertToLogin("注册登录后,才可以关注教练哦～\n注册获得更多学车咨询!～");
            return;
        }
        //follow unfollow 点击
        HashMap<String, String> countMap = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            countMap.put("student_id", mUser.student.id);
        }
        countMap.put("coach_id", mCoach.id);
        countMap.put("follow", isFollow ? "0" : "1");
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_follow_unfollow_tapped", countMap);
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
        //like unlike 点击
        HashMap<String, String> countMap = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            countMap.put("student_id", mUser.student.id);
        }
        countMap.put("coach_id", mCoach.id);
        countMap.put("like", isApplaud ? "0" : "1");
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_like_unlike_tapped", countMap);
        if (mUser == null || !mUser.isLogin()) {
            mCoachDetailView.alertToLogin("注册登录后,才可以点赞教练哦～\n注册获得更多学车咨询!～");
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

    public void purchaseCoach(ClassType classType) {
        if (mUser == null || !mUser.isLogin()) {
            mCoachDetailView.alertToLogin("注册登录后,才可以购买教练哦～\n注册获得更多学车咨询!～");
            return;
        } else if (mUser.student.hasPurchasedService()) {
            mCoachDetailView.showMessage("该学员已经购买过教练");
            return;
        }
        mCoachDetailView.navigateToPurchaseCoach(mCoach, classType);
    }

    public void freeTry() {
        if (mCoach == null) return;
        //免费试学URL
        String url = WebViewUrl.WEB_URL_FREE_TRY;
        String shareUrl = url;
        url += "&coach_id=" + mCoach.id;
        if (mUser != null && mUser.isLogin()) {
            if (mUser.student.city_id >= 0) {
                url += "&city_id=" + mUser.student.city_id;
            }
            if (!TextUtils.isEmpty(mUser.student.name)) {
                url += "&name=" + mUser.student.name;
            }
            if (!TextUtils.isEmpty(mUser.student.cell_phone)) {
                url += "&phone=" + mUser.student.cell_phone;
            }

        }
        //免费试学点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_free_trial_tapped", map);
        HHLog.v("free try url -> " + url);
        HHLog.v("free try share url -> " + shareUrl);
        mCoachDetailView.openWebView(url, shareUrl);
    }

    public void clickCommentsCount() {
        //学员评价点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_comment_tapped", map);
    }

    public void clickShareCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_share_coach_tapped", map);
    }

    public void clickShareSuccessCount(String shareChannel) {
        //分享成功
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        map.put("share_channel", shareChannel);
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_share_coach_succeed", map);
    }

    public void clickTrainFieldCount() {
        //训练场地址点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_field_tapped", map);
    }

    public void pageStartCount() {
        //教练详情展现
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mCoachDetailView.getContext(), "coach_detail_page_viewed", map);
    }

    public void clickPlatformAssurance() {
        mCoachDetailView.navigationToPlatformAssurance(mCoach.skill_level.equals("1"), mCoach.has_cash_pledge == 1);
    }

    /**
     * 推荐有奖跳转逻辑
     */
    public void toReferFriends() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
            //非代理
            mCoachDetailView.navigateToStudentRefer();
        } else {
            mCoachDetailView.navigateToReferFriends();
        }
    }
}
