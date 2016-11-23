package com.hahaxueche.presenter.login;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.user.Student;
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
public class CompleteUserInfoPresenter implements Presenter<CompleteUserInfoView> {
    private CompleteUserInfoView mCompleteUserInfoView;
    private Subscription subscription;

    public void attachView(CompleteUserInfoView view) {
        this.mCompleteUserInfoView = view;
    }

    public void detachView() {
        this.mCompleteUserInfoView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void completeUserInfo(String username, int cityId, String promoCode) {
        if (TextUtils.isEmpty(username)) {
            mCompleteUserInfoView.showMessage("用户名不能为空");
            return;
        }
        if (cityId < 0) {
            mCompleteUserInfoView.showMessage("请选择所在城市");
            return;
        }
        mCompleteUserInfoView.disableButtons();
        mCompleteUserInfoView.showProgressDialog("数据上传中,请稍后...");
        final HHBaseApplication application = HHBaseApplication.get(mCompleteUserInfoView.getContext());
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
                        mCompleteUserInfoView.enableButtons();
                        mCompleteUserInfoView.dismissProgressDialog();
                        mCompleteUserInfoView.navigateToHomepage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp422(e)) {
                            mCompleteUserInfoView.showMessage("您的优惠码有误");
                        }
                        mCompleteUserInfoView.enableButtons();
                        mCompleteUserInfoView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                    }
                });

    }
}
