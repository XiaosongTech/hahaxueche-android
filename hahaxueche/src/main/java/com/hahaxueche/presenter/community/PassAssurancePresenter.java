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
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.ExamResult;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.PassAssuranceView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
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

public class PassAssurancePresenter extends HHBasePresenter implements Presenter<PassAssuranceView> {
    private PassAssuranceView mPassAssuranceView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String mQrCodeUrl;

    public void attachView(PassAssuranceView view) {
        this.mPassAssuranceView = view;
        application = HHBaseApplication.get(mPassAssuranceView.getContext());
        fetchScores();
        String text = Utils.getCount(application.getConstants().statistics.student_count) + "人已获得挂科险";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mPassAssuranceView.getContext(), R.color.app_theme_color)), 0, text.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPassAssuranceView.setInsuranceCount(ss);

    }

    public void detachView() {
        this.mPassAssuranceView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchScores() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mPassAssuranceView.showNotLogin();
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
                                mPassAssuranceView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(ArrayList<ExamResult> examResults) {
                            if (examResults != null) {
                                mPassAssuranceView.showScores(examResults.size());
                            }
                        }
                    });
        }
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
                        mPassAssuranceView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mPassAssuranceView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPassAssuranceView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(String s) {
                        mQrCodeUrl = s;
                        mPassAssuranceView.share(shareType);
                        HHLog.v("QrCodeUrl -> " + mQrCodeUrl);
                    }
                });
    }
}
