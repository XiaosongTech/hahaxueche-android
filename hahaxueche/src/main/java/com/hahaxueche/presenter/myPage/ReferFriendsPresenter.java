package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.identity.MarketingInfo;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.ReferFriendsView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/21.
 */

public class ReferFriendsPresenter implements Presenter<ReferFriendsView> {
    private ReferFriendsView mReferFriendsView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String mQrCodeUrl;

    public void attachView(ReferFriendsView view) {
        this.mReferFriendsView = view;
        application = HHBaseApplication.get(mReferFriendsView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            //原url地址
            String url = WebViewUrl.WEB_URL_REFER_FRIENDS + "&referrer_id=" + user.student.user_identity_id;
            mReferFriendsView.initShareData(url);
        }
    }

    public void detachView() {
        this.mReferFriendsView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mQrCodeUrl = null;
    }

    public void clickShareCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_share_pic_tapped", map);
            mReferFriendsView.showShareDialog();
        } else {
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_share_pic_tapped");
            mReferFriendsView.alertToLogin();
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
        MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_share_pic_succeed", map);
    }

    public void pageStartCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_viewed");
        }
    }

    public void clickWithdraw() {
        if (isLogin()) {
            mReferFriendsView.navigateToMyRefer();
        } else {
            mReferFriendsView.alertToLogin();
        }
    }

    public boolean isLogin() {
        User user = application.getSharedPrefUtil().getUser();
        return user != null && user.isLogin();
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
                        mReferFriendsView.startToShare(shareType);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ShortenUrl> shortenUrls) {
                        if (shortenUrls != null && shortenUrls.size() > 0) {
                            mReferFriendsView.initShareData(shortenUrls.get(0).url_short);
                        }
                    }
                });
    }
}
