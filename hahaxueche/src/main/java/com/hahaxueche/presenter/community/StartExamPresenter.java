package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.ExamResult;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.HHBaseView;
import com.hahaxueche.ui.view.community.StartExamView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.ExamLib;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class StartExamPresenter implements Presenter<StartExamView> {
    private StartExamView mStartExamView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(StartExamView view) {
        this.mStartExamView = view;
        application = HHBaseApplication.get(mStartExamView.getContext());
    }

    public void detachView() {
        this.mStartExamView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public int getBonus() {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        return application.getConstants().getCity(cityId).referer_bonus;
    }

    public void submitExamResults(String examType, String score) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        if (examType.equals(ExamLib.EXAM_TYPE_1)) {
            mapParam.put("course", 0);
        } else {
            mapParam.put("course", 1);
        }
        mapParam.put("score", Integer.parseInt(score.substring(0, score.indexOf("分"))));
        //mapParam.put("score", 91);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ExamResult>>() {
                    @Override
                    public Observable<ExamResult> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.submitExamResult(user.student.id, mapParam, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ExamResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mStartExamView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ExamResult examResult) {
                    }
                });
    }
}
