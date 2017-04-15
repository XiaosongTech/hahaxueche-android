package com.hahaxueche.presenter.homepage;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Statistics;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.BookAddress;
import com.hahaxueche.model.user.student.Contact;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.HahaCache;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/17.
 */
public class HomepagePresenter extends HHBasePresenter implements Presenter<HomepageView> {
    private HomepageView mView;
    private Subscription subscription;

    private HHBaseApplication application;
    private Constants constants;

    public void attachView(HomepageView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        constants = application.getConstants();
        if (constants != null) {
            loadCityChoseDialog();
        }
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void openAboutHaha() {
        mView.openWebView(WebViewUrl.WEB_URL_ABOUT_HAHA);
    }

    public void openAboutCoach() {
        mView.openWebView(WebViewUrl.WEB_URL_ABOUT_COACH);
    }

    public void openMyStrengths() {
        //我的优势点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_strength_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_strength_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_MY_STRENGTHS);
    }

    public void openBestCoaches() {
        //教练页面点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_coach_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_coach_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_BEST_COACHES);
    }

    public void openProcedure() {
        //学车流程点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_process_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_process_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_PROCEDURE);
    }

    public void openFindAdviser() {
        User user = application.getSharedPrefUtil().getUser();
        //顾问页面点击
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_advisor_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_advisor_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_XUECHEBAO);
    }

    public void openFindDrivingSchool() {
        //驾校页面点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_driving_school_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_driving_school_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_FIND_DRIVING_SCHOOL);
    }

    public void openGroupBuy() {
        //团购点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_group_purchase_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_group_purchase_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_GROUP_BUY);
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void phoneSupportCount() {
        //客服电话点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_phone_support_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_phone_support_tapped");
        }
    }

    public void freeTry() {
        //免费试学URL
        String url = WebViewUrl.WEB_URL_FREE_TRY;
        String shareUrl = url;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            if (user.student.city_id >= 0) {
                url += "&city_id=" + user.student.city_id;
            }
            if (!TextUtils.isEmpty(user.student.name)) {
                url += "&name=" + user.student.name;
            }
            if (!TextUtils.isEmpty(user.student.cell_phone)) {
                url += "&phone=" + user.student.cell_phone;
            }

        }
        //免费试学点击
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_free_trial_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_free_trial_tapped");
        }
        HHLog.v("free try url -> " + url);
        HHLog.v("free try share url -> " + shareUrl);
        mView.openWebView(url, shareUrl);
    }

    public void loadCityChoseDialog() {
        User user = application.getSharedPrefUtil().getUser();
        if (user.student.city_id < 0) {
            selectCity(0);//先默认为武汉
            mView.showCityChoseDialog();
        }
    }

    public void selectCity(int cityId) {
        application.getSharedPrefUtil().setUserCity(cityId);
    }

    public void bannerClick(int i) {
        try {
            User user = application.getSharedPrefUtil().getUser();
            HashMap<String, String> map = new HashMap();
            if (!TextUtils.isEmpty(constants.new_home_page_banners.get(i).target_url)) {
                map.put("URL", constants.new_home_page_banners.get(i).target_url);
                if (user != null && user.isLogin()) {
                    map.put("student_id", user.student.id);
                    MobclickAgent.onEvent(mView.getContext(), "home_page_banner_tapped", map);
                }
                MobclickAgent.onEvent(mView.getContext(), "home_page_banner_tapped", map);
                mView.openWebView(constants.new_home_page_banners.get(i).target_url);
            } else {
                if (user != null && user.isLogin()) {
                    map.put("student_id", user.student.id);
                    MobclickAgent.onEvent(mView.getContext(), "home_page_banner_tapped", map);
                } else {
                    MobclickAgent.onEvent(mView.getContext(), "home_page_banner_tapped");
                }
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
    }

    public boolean isNeedUpdate() {
        Constants constants = application.getConstants();
        return super.isNeedUpdate(mView.getContext(), constants.version_code);
    }

    public void clickTestLib() {
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "home_page_online_test_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "home_page_online_test_tapped");
        }
        mView.navigateToExamLibrary();
    }

    public void clickInsurance() {
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "home_page_course_one_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "home_page_course_one_tapped");
        }
        mView.navigateToMyInsurance();
    }

    public void clickPlatformGuard() {
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "home_page_platform_guard_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "home_page_platform_guard_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_PLATFORM_GUARD);
    }

    public void clickReferFriends() {
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "home_page_refer_friends_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "home_page_refer_friends_tapped");
        }
        if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
            //非代理
            mView.navigateToStudentRefer();
        } else {
            mView.navigateToReferFriends();
        }
    }

    public String getShareText() {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        String shareText = mView.getContext().getResources().getString(R.string.homepage_share_dialog_text);
        City myCity = application.getConstants().getCity(cityId);
        return String.format(shareText, Utils.getMoney(myCity.referer_bonus), Utils.getMoney(myCity.referee_bonus));
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

    /**
     * 上传通讯录
     *
     * @param contacts
     */
    public void uploadContacts(ArrayList<Contact> contacts) {
        if (contacts == null || contacts.size() < 1) return;
        BookAddress bookAddress = new BookAddress();
        bookAddress.device_id = HahaCache.deviceId;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            bookAddress.phone = user.cell_phone;
        }
        bookAddress.address_book = contacts;
        HHApiService apiService = application.getApiService();
        subscription = apiService.uploadContacts(bookAddress)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String ret) {

                    }
                });
    }

    public void getCityConstants() {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCityConstant("0")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CityConstants>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CityConstants cityConstants) {
                        mView.loadHotDrivingSchools(cityConstants.driving_schools.subList(0, 8));
                    }
                });
    }

    public void getNearCoaches() {
        HHApiService apiService = application.getApiService();
        ArrayList<String> locations = null;
        if (application.getMyLocation() != null) {
            locations = new ArrayList<>();
            locations.add(String.valueOf(application.getMyLocation().lat));
            locations.add(String.valueOf(application.getMyLocation().lng));
            subscription = apiService.getCoaches(Common.START_PAGE, Common.MAX_NEAR_COACH_COUNT,
                    null, null, null, 0, null, null, locations, 1, 0, null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<CoachResponseList>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            HHLog.e(e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(CoachResponseList coachResponseList) {
                            if (coachResponseList.data != null) {
                                mView.loadNearCoaches(coachResponseList.data);
                            }

                        }
                    });
        } else {
            subscription = apiService.getCoaches(Common.START_PAGE, Common.MAX_NEAR_COACH_COUNT,
                    null, null, null, 0, null, null, null, 5, 0, null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<CoachResponseList>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            HHLog.e(e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(CoachResponseList coachResponseList) {
                            if (coachResponseList.data != null) {
                                mView.loadNearCoaches(coachResponseList.data);
                            }

                        }
                    });
        }
    }

    public void setLocation(double lat, double lng) {
        application.setMyLocation(lat, lng);
        getNearCoaches();
    }
}