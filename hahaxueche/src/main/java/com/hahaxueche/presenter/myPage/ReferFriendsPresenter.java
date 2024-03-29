package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.identity.MarketingInfo;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.ReferFriendsView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/21.
 */

public class ReferFriendsPresenter extends HHBasePresenter implements Presenter<ReferFriendsView> {
    private ReferFriendsView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String mQrCodeUrl;

    public void attachView(ReferFriendsView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            //原url地址
            String url = WebViewUrl.WEB_URL_REFER_FRIENDS + "&referrer_id=" + user.student.user_identity_id;
            mView.initShareData(url);
        }
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mQrCodeUrl = null;
    }

    public void clickShareCount() {
        //分享点击
        addDataTrack("refer_page_share_pic_tapped", mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.showShareDialog();
        } else {
            mView.alertToLogin();
        }
    }

    public void clickShareSuccessCount(String shareChannel) {
        //分享成功
        HashMap<String, String> map = new HashMap();
        map.put("share_channel", shareChannel);
        addDataTrack("refer_page_share_pic_succeed", mView.getContext(), map);
    }

    public void clickWithdraw() {
        if (isLogin()) {
            mView.navigateToMyRefer();
        } else {
            mView.alertToLogin();
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

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }
}
