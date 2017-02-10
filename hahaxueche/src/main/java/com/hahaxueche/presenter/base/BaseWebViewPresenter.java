package com.hahaxueche.presenter.base;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.user.identity.MarketingInfo;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.BaseWebViewView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/12/20.
 */

public class BaseWebViewPresenter implements Presenter<BaseWebViewView> {
    private BaseWebViewView mView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(BaseWebViewView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
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
                        mView.startToShare(shareType);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ShortenUrl> shortenUrls) {
                        if (shortenUrls != null && shortenUrls.size() > 0) {
                            mView.setShareUrl(shortenUrls.get(0).url_short);
                        }
                    }
                });
    }
}
