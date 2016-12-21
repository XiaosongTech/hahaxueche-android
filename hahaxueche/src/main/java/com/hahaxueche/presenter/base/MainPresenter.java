package com.hahaxueche.presenter.base;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Contact;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.HHBaseView;
import com.hahaxueche.ui.view.base.MainView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

public class MainPresenter implements Presenter<MainView> {
    private MainView mBaseView;
    private HHBaseApplication application;
    private Subscription subscription;

    public void attachView(MainView view) {
        this.mBaseView = view;
        application = HHBaseApplication.get(mBaseView.getContext());
        loadVoucherShare();
    }

    public void detachView() {
        this.mBaseView = null;
        if (subscription != null) subscription.unsubscribe();
        this.application = null;
    }

    public void viewHomepageCount() {
        //首页展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "home_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "home_page_viewed");
        }
    }

    public void viewFindCoachCount() {
        //寻找教练展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "find_coach_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "find_coach_page_viewed");
        }
    }

    public void viewCommunityCount() {
        //俱乐部展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "club_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "club_page_viewed");
        }
    }

    public void viewMyPageCount() {
        //我的页面展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "my_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "my_page_viewed");
        }
    }

    public void setMyPageBadge() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (user.student.hasPurchasedService() && (!user.student.isUploadedIdInfo() || !user.student.isSigned())) {
            mBaseView.setMyPageBadge(true);
        } else {
            mBaseView.setMyPageBadge(false);
        }
    }

    public void controlSignDialog() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (user.student.hasPurchasedService() && (!user.student.isUploadedIdInfo() || !user.student.isSigned())) {
            mBaseView.showSignDialog();
        }
    }

    public void clickMyContract() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (!user.student.hasPurchasedService()) {
            return;
        } else if (!user.student.isUploadedIdInfo()) {
            mBaseView.navigateToUploadIdCard();
        } else if (!user.student.isSigned()) {
            mBaseView.navigateToSignContract();
        } else {
            mBaseView.navigateToMyContract();
        }
    }

    private void loadVoucherShare() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (!user.student.hasPurchasedService() && user.student.vouchers != null && user.student.vouchers.size() > 0) {
            Voucher maxVoucher = getMaxVoucher(user.student.vouchers);
            if (maxVoucher != null) {
                //原url地址
                String url = WebViewUrl.WEB_URL_DALIBAO;
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
                                    mBaseView.initShareData(shortenUrls.get(0).url_short);
                                }
                            }
                        });
                mBaseView.showVoucherDialog(user.student.id, maxVoucher);
            }
        }
    }

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
            MobclickAgent.onEvent(mBaseView.getContext(), "home_page_voucher_popup_share_tapped", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "home_page_voucher_popup_share_tapped");
        }
    }

    public void clickShareSuccessCount(String shareChannel) {
        HashMap<String, String> map = new HashMap();
        map.put("share_channel", shareChannel);
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);

        }
        MobclickAgent.onEvent(mBaseView.getContext(), "home_page_voucher_popup_share_succeed", map);
    }

    public void uploadContacts(ArrayList<Contact> contacts) {
        if (contacts == null || contacts.size() < 1) return;
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;//未登录用户不上传通讯录
        HHLog.v(new Gson().toJson(contacts));
    }
}
