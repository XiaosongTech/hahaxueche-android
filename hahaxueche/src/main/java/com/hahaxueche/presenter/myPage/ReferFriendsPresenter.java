package com.hahaxueche.presenter.myPage;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.ReferFriendsView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.io.IOException;

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
        if (user == null || !user.isLogin()) return;
        getQrCodeUrl(user);
        mReferFriendsView.setWithdrawMoney(Utils.getMoney(user.student.bonus_balance));
        String eventDetailTips = mReferFriendsView.getContext().getResources().getString(R.string.eventDetailsTips);
        City myCity = application.getConstants().getMyCity(user.student.city_id);
        mReferFriendsView.setReferRules(String.format(eventDetailTips, Utils.getMoney(myCity.referer_bonus)));
        mReferFriendsView.setMyCityReferImage(myCity.referral_banner);

    }

    public void detachView() {
        this.mReferFriendsView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mQrCodeUrl = null;
    }

    private void getQrCodeUrl(User user) {
        redirectUrl(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        mReferFriendsView.setQrCodeImage(mQrCodeUrl);
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
}
