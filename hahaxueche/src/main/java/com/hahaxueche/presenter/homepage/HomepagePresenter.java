package com.hahaxueche.presenter.homepage;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Statistics;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.UpdateManager;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.Subscription;

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
            mView.initBanners(constants.new_home_page_banners);
            loadStatistics();
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
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        //顾问页面点击
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "homepage_advisor_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_advisor_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_FIND_ADVISER + "?city_id=" + cityId);
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
        super.onlineAsk(user,mView.getContext());
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

    private void loadStatistics() {
        Statistics statistics = application.getConstants().statistics;
        String text = "已入驻" + Utils.getCount(statistics.driving_school_count) + "所";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.haha_orange_text)), text.indexOf("驻") + 1, text.indexOf("所"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(mView.getContext()).sp2px(14)), text.indexOf("驻") + 1, text.indexOf("所"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setDrivingSchoolCountDisplay(ss);

        text = "已签约" + Utils.getCount(statistics.coach_count) + "名";
        ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.haha_blue_text)), text.indexOf("约") + 1, text.indexOf("名"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(mView.getContext()).sp2px(14)), text.indexOf("约") + 1, text.indexOf("名"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setCoachCountDisplay(ss);

        text = "已帮助学员" + Utils.getCount(statistics.paid_student_count) + "名";
        ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.haha_red_text)), text.indexOf("员") + 1, text.indexOf("名"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(mView.getContext()).sp2px(14)), text.indexOf("员") + 1, text.indexOf("名"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.setPaidStudentCountDisplay(ss);

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

    /**
     * 版本检测
     */
    public void doVersionCheck() {
        PackageManager pm = mView.getContext().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mView.getContext().getPackageName(), 0);
            int versioncode = pi.versionCode;
            Constants constants = application.getConstants();
            if (constants.version_code > versioncode) {
                //有版本更新时
                UpdateManager updateManager = new UpdateManager(mView.getContext());
                updateManager.checkUpdateInfo();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
}