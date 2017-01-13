package com.hahaxueche.presenter.community;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.ExamResult;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ExamLibraryView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
 * Created by wangshirui on 2016/12/1.
 */

public class ExamLibraryPresenter implements Presenter<ExamLibraryView> {
    private ExamLibraryView mExamLibraryView;
    private Subscription subscription;
    private HHBaseApplication application;
    private static final String WEB_URL_GROUP_BUY = BuildConfig.MOBILE_URL + "/share/baoguoka";
    private String mQrCodeUrl;

    public void attachView(ExamLibraryView view) {
        this.mExamLibraryView = view;
        application = HHBaseApplication.get(mExamLibraryView.getContext());
        fetchScores();
        String text = Utils.getCount(application.getConstants().statistics.student_count) + "人已获得保过卡";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mExamLibraryView.getContext(), R.color.app_theme_color)), 0, text.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mExamLibraryView.setInsuranceCount(ss);

    }

    public void detachView() {
        this.mExamLibraryView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchScores() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mExamLibraryView.showNotLogin();
        } else {
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<ArrayList<ExamResult>>>() {
                        @Override
                        public Observable<ArrayList<ExamResult>> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                //90分以上 科目一
                                return apiService.getExamResults(user.student.id, 90, 0, user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<ArrayList<ExamResult>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (ErrorUtil.isInvalidSession(e)) {
                                mExamLibraryView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(ArrayList<ExamResult> examResults) {
                            if (examResults != null) {
                                mExamLibraryView.showScores(examResults.size());
                            }
                        }
                    });
        }
    }

    public int getBonus() {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        return application.getConstants().getCity(cityId).referer_bonus;
    }

    private String getShareDescription() {
        ArrayList<String> codeList = new ArrayList<>();
        codeList.add("AZ-521");
        codeList.add("MDS-339");
        codeList.add("TEK-071");
        codeList.add("MIDE-295");
        codeList.add("IDBD-692");
        codeList.add("MIMK-039");
        Collections.shuffle(codeList);//打乱顺序
        return "科一保过卡免费送！考不过现金赔！【" + codeList.get(0) + "】哈哈老司机要开车了，捂脸~~内有惊喜";
    }

    private Observable<String> redirectUrl(final User user, int shareType) {
        final String channelId = application.getConstants().getChannelIdByShareType(shareType);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient client = new OkHttpClient();
                String url = BuildConfig.SERVER_URL + "/share/students/" + user.student.id + "/exam_result" +
                        "?channel_id=" + channelId + "&promo_code=328170";
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

    public void generateQrCodeUrl(final int shareType) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        redirectUrl(user, shareType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mExamLibraryView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mExamLibraryView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mExamLibraryView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(String s) {
                        mQrCodeUrl = s;
                        mExamLibraryView.share(shareType);
                        HHLog.v("QrCodeUrl -> " + mQrCodeUrl);
                    }
                });
    }
}
