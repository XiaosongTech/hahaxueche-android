package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.identity.MarketingInfo;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.StudentReferView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2017/2/18.
 */

public class StudentReferPresenter implements Presenter<StudentReferView> {
    private StudentReferView mStudentReferView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(StudentReferView view) {
        this.mStudentReferView = view;
        application = HHBaseApplication.get(mStudentReferView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            //原url地址
            String url = WebViewUrl.WEB_URL_REFER_FRIENDS + "&referrer_id=" + user.student.user_identity_id;
            mStudentReferView.initShareData(url);
        }
    }

    public void detachView() {
        this.mStudentReferView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void clickShareCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mStudentReferView.getContext(), "refer_page_share_pic_tapped", map);
            mStudentReferView.showShareDialog();
        } else {
            MobclickAgent.onEvent(mStudentReferView.getContext(), "refer_page_share_pic_tapped");
            mStudentReferView.alertToLogin();
        }

    }

    public void clickShareSuccessCount(String shareChannel) {
        //分享成功
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("share_channel", shareChannel);
        MobclickAgent.onEvent(mStudentReferView.getContext(), "refer_page_share_pic_succeed", map);
    }

    public void convertUrlForShare(final String url, final int shareType) {
        if (TextUtils.isEmpty(url)) return;
        if (shareType < 0 || shareType > 5) return;
        HHApiService apiService = application.getApiService();
        String promoCode = Utils.getUrlValueByName(url, "promo_code");
        String channelId = application.getConstants().getChannelIdByShareType(shareType);
        if (!TextUtils.isEmpty(promoCode)) {
            subscription = apiService.convertPromoCode(channelId, promoCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<MarketingInfo>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            shortenUrl(url, shareType);
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(MarketingInfo marketingInfo) {
                            shortenUrl(Utils.replaceUrlParam(url, "promo_code", marketingInfo.promo_code), shareType);
                        }
                    });
        } else {
            shortenUrl(url, shareType);
        }
    }

    public boolean isLogin() {
        User user = application.getSharedPrefUtil().getUser();
        return user != null && user.isLogin();
    }

    private void shortenUrl(String url, final int shareType) {
        if (TextUtils.isEmpty(url)) return;
        String longUrl = null;
        HHApiService apiService = application.getApiService();
        try {
            longUrl = " https://api.t.sina.com.cn/short_url/shorten.json?source=4186780524&url_long=" +
                    URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                            mStudentReferView.startToShare(shareType, shortenUrls.get(0).url_short);
                        }
                    }
                });
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(mStudentReferView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }
}
