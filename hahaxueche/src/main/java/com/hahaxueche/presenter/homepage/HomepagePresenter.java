package com.hahaxueche.presenter.homepage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.UpdateManager;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/17.
 */
public class HomepagePresenter implements Presenter<HomepageView> {
    private HomepageView mHomepageView;
    private Subscription subscription;
    private static final String WEB_URL_ABOUT_HAHA = "http://staging.hahaxueche.net/#/student";
    private static final String WEB_URL_ABOUT_COACH = "http://staging.hahaxueche.net/#/coach";
    private static final String WEB_URL_MY_STRENGTHS = "http://activity.hahaxueche.com/share/features";
    private static final String WEB_URL_PROCEDURE = "http://activity.hahaxueche.com/share/steps";
    private static final String WEB_URL_FREE_TRY = "http://m.hahaxueche.com/free_trial?promo_code=553353";
    private static final String WEB_URL_BEST_COACHES = "http://m.hahaxueche.com/share/best-coaches";

    private HHBaseApplication application;
    private Constants constants;

    public void attachView(HomepageView view) {
        this.mHomepageView = view;
        application = HHBaseApplication.get(mHomepageView.getContext());
        constants = application.getConstants();
        mHomepageView.initBanners(constants.new_home_page_banners);
        loadStatistics();
        loadCityChoseDialog();
        doVersionCheck();
    }

    public void detachView() {
        this.mHomepageView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void openAboutHaha() {
        mHomepageView.openWebView(WEB_URL_ABOUT_HAHA);
    }

    public void openAboutCoach() {
        mHomepageView.openWebView(WEB_URL_ABOUT_COACH);
    }

    public void openMyStrengths() {
        mHomepageView.openWebView(WEB_URL_MY_STRENGTHS);
    }

    public void openBestCoaches(){
        mHomepageView.openWebView(WEB_URL_BEST_COACHES);
    }

    public void openProcedure() {
        mHomepageView.openWebView(WEB_URL_PROCEDURE);
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(mHomepageView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }

    public void freeTry() {
        //免费试学URL
        String url = WEB_URL_FREE_TRY;
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
        HHLog.v("free try url -> " + url);
        mHomepageView.openWebView(url);
    }

    private void loadStatistics() {
        Statistics statistics = application.getConstants().statistics;
        int drivingSchoolCount = statistics.driving_school_count * 3;
        String text = "已入驻" + Utils.getCount(drivingSchoolCount) + "所";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mHomepageView.getContext(), R.color.haha_orange_text)), text.indexOf("驻") + 1, text.indexOf("所"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(mHomepageView.getContext()).sp2px(14)), text.indexOf("驻") + 1, text.indexOf("所"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mHomepageView.setDrivingSchoolCountDisplay(ss);

        int coachCount = statistics.coach_count * 10;
        text = "已签约" + Utils.getCount(coachCount) + "名";
        ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mHomepageView.getContext(), R.color.haha_blue_text)), text.indexOf("约") + 1, text.indexOf("名"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(mHomepageView.getContext()).sp2px(14)), text.indexOf("约") + 1, text.indexOf("名"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mHomepageView.setCoachCountDisplay(ss);

        int paidStudentCount = statistics.paid_student_count * 10;
        text = "已帮助学员" + Utils.getCount(paidStudentCount) + "名";
        ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mHomepageView.getContext(), R.color.haha_red_text)), text.indexOf("员") + 1, text.indexOf("名"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(mHomepageView.getContext()).sp2px(14)), text.indexOf("员") + 1, text.indexOf("名"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mHomepageView.setPaidStudentCountDisplay(ss);

    }

    public void loadCityChoseDialog() {
        User user = application.getSharedPrefUtil().getUser();
        if (user.student.city_id < 0) {
            selectCity(0);//先默认为武汉
            mHomepageView.showCityChoseDialog();
        }
    }

    public void selectCity(int cityId) {
        application.getSharedPrefUtil().setUserCity(cityId);
    }

    public void bannerClick(int i) {
        try {
            if (!TextUtils.isEmpty(constants.new_home_page_banners.get(i).target_url)) {
                mHomepageView.openWebView(constants.new_home_page_banners.get(i).target_url);
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
    }

    /**
     * 版本检测
     */
    public void doVersionCheck() {
        PackageManager pm = mHomepageView.getContext().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mHomepageView.getContext().getPackageName(), 0);
            int versioncode = pi.versionCode;
            Constants constants = application.getConstants();
            if (constants.version_code > versioncode) {
                //有版本更新时
                UpdateManager updateManager = new UpdateManager(mHomepageView.getContext());
                updateManager.checkUpdateInfo();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}