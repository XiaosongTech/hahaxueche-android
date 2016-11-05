package com.hahaxueche.presenter.myPage;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.ReferFriendsView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

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
        if (user == null || !user.isLogin()) return;
        getQrCodeUrl(user);
        mReferFriendsView.setWithdrawMoney(Utils.getMoney(user.student.bonus_balance));
        String eventDetailTips = mReferFriendsView.getContext().getResources().getString(R.string.eventDetailsTips);
        City myCity = application.getConstants().getCity(user.student.city_id);
        mReferFriendsView.setReferRules(String.format(eventDetailTips, Utils.getMoney(myCity.referer_bonus)));
        mReferFriendsView.setMyCityReferImage(myCity.referral_banner);

    }

    public void detachView() {
        this.mReferFriendsView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mQrCodeUrl = null;
    }

    public void refreshBonus() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getStudent(user.student.id, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Student>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mReferFriendsView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mReferFriendsView.setWithdrawMoney(Utils.getMoney(student.bonus_balance));
                    }
                });
    }

    private void getQrCodeUrl(User user) {
        subscription = redirectUrl(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        mReferFriendsView.setQrCodeImage(mQrCodeUrl);
                        pageStartCount();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        mQrCodeUrl = s;
                        HHLog.v("QrCodeUrl -> " + mQrCodeUrl);
                    }
                });

    }

    private Observable<String> redirectUrl(final User user) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient client = new OkHttpClient();
                String url = BuildConfig.SERVER_URL + "/share/students/" + user.student.id + "/image";
                HHLog.v("url -> " + url);
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response.request().url().toString());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public String getQrCodeUrl() {
        return mQrCodeUrl;
    }

    public void clickShareCount() {
        //分享点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_share_pic_tapped", map);
        } else {
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_share_pic_tapped");
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
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_cash_tapped", map);
            mReferFriendsView.navigateToWithdraw();
        } else {
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_cash_tapped");
        }
    }

    public void clickReferrerList() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_check_balance_tapped", map);
        } else {
            MobclickAgent.onEvent(mReferFriendsView.getContext(), "refer_page_check_balance_tapped");
        }
    }
}
