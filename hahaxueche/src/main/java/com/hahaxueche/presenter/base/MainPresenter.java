package com.hahaxueche.presenter.base;

import android.text.TextUtils;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.identity.MarketingInfo;
import com.hahaxueche.model.user.student.BookAddress;
import com.hahaxueche.model.user.student.Contact;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.MainView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.HahaCache;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/11/1.
 */

public class MainPresenter extends HHBasePresenter implements Presenter<MainView> {
    private MainView mView;
    private HHBaseApplication application;
    private Subscription subscription;

    public void attachView(MainView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        loadVoucherShare();
        bindAliyun();
    }

    /**
     * 绑定阿里云推送的标签
     */
    private void bindAliyun() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (user.student.isPurchasedService()) {
            CloudPushService pushService = PushServiceFactory.getCloudPushService();
            pushService.bindTag(1, new String[]{"purchased"}, "", new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                }

                @Override
                public void onFailed(String s, String s1) {
                }
            });
        }
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        this.application = null;
    }

    public void viewHomepageCount() {
        //首页展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "home_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "home_page_viewed");
        }
    }

    public void viewFindCoachCount() {
        //寻找教练展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_viewed");
        }
    }

    public void viewCommunityCount() {
        //俱乐部展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "club_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "club_page_viewed");
        }
    }

    public void viewMyPageCount() {
        //我的页面展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_viewed");
        }
    }

    public void setMyPageBadge() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {//未登录没有红点
            mView.setMyPageBadge(false);
            return;
        }
        if (!user.student.isPurchasedService()) {//未购买教练，挂科险的红点
            mView.setMyPageBadge(true);
            return;
        }
        if (!user.student.isUploadedIdInfo() || !user.student.isSigned()) {//购买了，没有上传协议的
            mView.setMyPageBadge(true);
        } else {
            mView.setMyPageBadge(false);
        }
    }

    public void controlSignDialog() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (user.student.isPurchasedService() && (!user.student.isUploadedIdInfo() || !user.student.isSigned())) {
            mView.showSignDialog();
        }
    }

    public void clickMyContract() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (!user.student.isPurchasedService()) {
            return;
        } else if (!user.student.isUploadedIdInfo()) {
            mView.navigateToUploadIdCard();
        } else if (!user.student.isSigned()) {
            mView.navigateToSignContract();
        } else {
            mView.navigateToMyContract();
        }
    }

    private void loadVoucherShare() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (!user.student.isPurchasedService() && user.student.vouchers != null
                && user.student.vouchers.size() > 0) {
            Voucher maxVoucher = getMaxVoucher(user.student.vouchers);
            if (maxVoucher != null) {
                //原url地址
                String url = WebViewUrl.WEB_URL_DALIBAO;
                mView.initShareData(url);
                mView.showVoucherDialog(user.student.id, maxVoucher);
            }
        }
    }

    /**
     * 获得最大代金券
     *
     * @param vouchers
     * @return
     */
    private Voucher getMaxVoucher(ArrayList<Voucher> vouchers) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Voucher maxVoucher = null;
        try {
            for (Voucher voucher : vouchers) {
                if (voucher.status != 0) continue;//未使用的
                if (!TextUtils.isEmpty(voucher.expired_at)) {
                    Date expiredAtDate = format.parse(voucher.expired_at);
                    if (expiredAtDate.getTime() - new Date().getTime() < 0) continue;//已过期的
                }
                if (maxVoucher == null) {
                    maxVoucher = voucher;
                } else if (maxVoucher.amount < voucher.amount) {
                    maxVoucher = voucher;
                }
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
            return null;
        }
        return maxVoucher;

    }

    public void clickPopShareCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "home_page_voucher_popup_share_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "home_page_voucher_popup_share_tapped");
        }
    }

    public void clickShareSuccessCount(String shareChannel) {
        HashMap<String, String> map = new HashMap();
        map.put("share_channel", shareChannel);
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);

        }
        MobclickAgent.onEvent(mView.getContext(), "home_page_voucher_popup_share_succeed", map);
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
                        mView.startToShare(shareType);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<ShortenUrl> shortenUrls) {
                        if (shortenUrls != null && shortenUrls.size() > 0) {
                            mView.initShareData(shortenUrls.get(0).url_short);
                        }
                    }
                });
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
