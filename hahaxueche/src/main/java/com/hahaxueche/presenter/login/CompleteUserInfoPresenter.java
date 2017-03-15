package com.hahaxueche.presenter.login;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.login.CompleteUserInfoView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/10.
 */
public class CompleteUserInfoPresenter extends HHBasePresenter implements Presenter<CompleteUserInfoView> {
    private CompleteUserInfoView mView;
    private Subscription subscription;

    public void attachView(CompleteUserInfoView view) {
        this.mView = view;
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void completeUserInfo(String username, int cityId, String promoCode) {
        if (TextUtils.isEmpty(username)) {
            mView.showMessage("用户名不能为空");
            return;
        }
        if (cityId < 0) {
            mView.showMessage("请选择所在城市");
            return;
        }
        mView.disableButtons();
        mView.showProgressDialog();
        final HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", username);
        map.put("city_id", cityId);
        if (!TextUtils.isEmpty(promoCode)) {
            map.put("promo_code", promoCode);
        }
        subscription = apiService.completeUserInfo(application.getSharedPrefUtil().getStudentId(), application.getSharedPrefUtil().getAccessToken(), map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Student>() {
                    @Override
                    public void onCompleted() {
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        mView.navigateToHomepage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp422(e)) {
                            mView.showMessage("您的优惠码有误");
                        }
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                    }
                });

    }
}
