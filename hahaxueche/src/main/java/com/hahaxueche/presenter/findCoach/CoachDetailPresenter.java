package com.hahaxueche.presenter.findCoach;

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
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.UserIdentityInfo;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Follow;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.CoachDetailView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
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

public class CoachDetailPresenter extends HHBasePresenter implements Presenter<CoachDetailView> {
    private CoachDetailView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;
    private User mUser;
    private boolean isFollow;
    private boolean isApplaud;
    private List<ClassType> mClassTypeList;

    @Override
    public void attachView(CoachDetailView view) {
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

    public void setCoach(Coach coach) {
        this.mCoach = coach;
        if (mCoach == null) return;
        mView.setCoachName(mCoach.name);
        mView.setCoachBio(mCoach.bio);
        mView.setCoachAvatar(mCoach.avatar);
        mView.setCoachImages(mCoach.images);
        mView.setCoachGolden(mCoach.skill_level == Common.COACH_SKILL_LEVEL_GOLDEN);
        mView.setCoachPledge(mCoach.has_cash_pledge == 1);
        mView.setCoachSatisfaction(Utils.getRate(mCoach.satisfaction_rate));
        mView.setCoachSkillLevel(getSkillLevelLabel());
        mView.setCoachAveragePassDays(mCoach.average_pass_days + "天");
        mView.setCoachPassRate(Utils.getRate(mCoach.stage_three_pass_rate));
        mView.setTrainingLocation(getTrainingFieldName());
        mView.setPeerCoaches(mCoach.peer_coaches);
        mView.setCommentCount("学员评价（" + mCoach.review_count + "）");
        mView.setCoachAverageRating(mCoach.average_rating);
        mView.setDrivingSchool(mCoach.driving_school);
        if (mCoach.coach_group.c2_price != 0 || mCoach.coach_group.c2_vip_price != 0) {
            mView.setLicenseTab(true, true);
        } else {
            mView.setLicenseTab(true, false);
        }
        int insuranceWithNewCoachPrice = application.getConstants().insurance_prices.pay_with_new_coach_price;
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
                    mCoach.coach_group.training_cost + insuranceWithNewCoachPrice, true,
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
                    mCoach.coach_group.c2_price + insuranceWithNewCoachPrice, true,
                    Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C2));
        }
        //车友无忧班是特殊教练，只有无忧班
        if (mCoach.coach_group.group_type == Common.GROUP_TYPE_CHEYOU_WUYOU) {
            mClassTypeList = new ArrayList<>();
            mClassTypeList.add(new ClassType(Common.CLASS_TYPE_WUYOU_NAME, Common.CLASS_TYPE_WUYOU_C1,
                    mCoach.coach_group.training_cost + insuranceWithNewCoachPrice, true,
                    Common.CLASS_TYPE_WUYOU_DESC, Common.LICENSE_TYPE_C1));
            mView.setLicenseTab(true, false);
        }
        //默认选择C1
        selectLicenseType(Common.LICENSE_TYPE_C1);
        mView.setCoachBadge(mCoach.skill_level == Common.COACH_SKILL_LEVEL_GOLDEN);
        mView.setPayBadge(coach.has_cash_pledge == 1);
        this.mView.initShareData(mCoach);
        loadReviews();
        loadFollow();
        loadApplaud();
        pageStartCount();
    }

    public void selectLicenseType(int licenseType) {
        mView.clearClassType();
        for (ClassType classType : mClassTypeList) {
            if (classType.licenseType == licenseType) {
                mView.addClassType(classType);
            }
        }
        if (licenseType == Common.LICENSE_TYPE_C1) {
            mView.showC1Tab(true);
            mView.showC2Tab(false);
        } else {
            mView.showC1Tab(false);
            mView.showC2Tab(true);
        }
    }

    public Coach getCoach() {
        return mCoach;
    }

    private String getSkillLevelLabel() {
        String ret = "";
        Constants constants = application.getConstants();
        for (BaseItemType skillLevel : constants.skill_levels) {
            if (skillLevel.id == mCoach.skill_level) {
                ret = skillLevel.readable_name;
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
        Field field = constants.getField(mCoach.coach_group.field_id);
        return field != null ? field.display_address : "";
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
        subscription = apiService.getReviews(mCoach.user_id, Common.START_PAGE, Common.PER_PAGE)
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
                            mView.showReviews(reviewResponseList);
                        } else {
                            mView.showNoReview(mCoach.name);
                        }
                    }
                });
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
            mView.alertToLogin("注册登录后,才可以关注教练哦～\n注册获得更多学车咨询!～");
            return;
        }
        //follow unfollow 点击
        HashMap<String, String> countMap = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            countMap.put("student_id", mUser.student.id);
        }
        countMap.put("coach_id", mCoach.id);
        countMap.put("follow", isFollow ? "0" : "1");
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_follow_unfollow_tapped", countMap);
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
        //like unlike 点击
        HashMap<String, String> countMap = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            countMap.put("student_id", mUser.student.id);
        }
        countMap.put("coach_id", mCoach.id);
        countMap.put("like", isApplaud ? "0" : "1");
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_like_unlike_tapped", countMap);
        if (mUser == null || !mUser.isLogin()) {
            mView.alertToLogin("注册登录后,才可以点赞教练哦～\n注册获得更多学车咨询!～");
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

    public void purchaseCoach(ClassType classType) {
        if (mUser == null || !mUser.isLogin()) {
            mView.alertToLogin("注册登录后,才可以购买教练哦～\n注册获得更多学车咨询!～");
            return;
        } else if (mUser.student.isPurchasedService()) {
            mView.showMessage("该学员已经购买过教练");
            return;
        }
        mView.navigateToPurchaseCoach(mCoach, classType);
    }

    public void freeTry() {
        if (mCoach == null) return;
        //免费试学点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_free_trial_tapped", map);
    }

    public void clickCommentsCount() {
        //学员评价点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_comment_tapped", map);
    }

    public void clickShareCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_share_coach_tapped", map);
    }

    public void clickShareSuccessCount(String shareChannel) {
        //分享成功
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        map.put("share_channel", shareChannel);
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_share_coach_succeed", map);
    }

    public void clickTrainFieldCount() {
        //训练场地址点击
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_field_tapped", map);
    }

    public void pageStartCount() {
        //教练详情展现
        HashMap<String, String> map = new HashMap();
        if (mUser != null && mUser.isLogin()) {
            map.put("student_id", mUser.student.id);
        }
        map.put("coach_id", mCoach.id);
        MobclickAgent.onEvent(mView.getContext(), "coach_detail_page_viewed", map);
    }

    public void clickPlatformAssurance() {
        mView.navigationToPlatformAssurance(mCoach.skill_level == Common.COACH_SKILL_LEVEL_GOLDEN, mCoach.has_cash_pledge == 1);
    }

    /**
     * 推荐有奖跳转逻辑
     */
    public void toReferFriends() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
            //非代理
            mView.navigateToStudentRefer();
        } else {
            mView.navigateToReferFriends();
        }
    }

    public boolean isPurchasedService() {
        User user = application.getSharedPrefUtil().getUser();
        return user != null && user.isLogin() && user.student.isPurchasedService();
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void getUserIdentity(String cellPhone) {
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", cellPhone);
        map.put("promo_code", "921434");
        subscription = apiService.getUserIdentity(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<UserIdentityInfo>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UserIdentityInfo userIdentityInfo) {
                    }
                });
    }

    public void shortenUrl(String url, final int shareType) {
        if (TextUtils.isEmpty(url)) return;
        HHApiService apiService = application.getApiService();
        String longUrl = getShortenUrlAddress(url);
        if (TextUtils.isEmpty(longUrl)) return;
        subscription = apiService.shortenUrl(longUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<ShortenUrl>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ShortenUrl> shortenUrls) {
                        if (shortenUrls != null && shortenUrls.size() > 0) {
                            mView.startToShare(shareType, shortenUrls.get(0).url_short);
                        }
                    }
                });
    }
}
