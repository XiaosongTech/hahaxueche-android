package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.UploadIdCardView;
import com.hahaxueche.util.Utils;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/11/25.
 */

public class UploadIdCardPresenter implements Presenter<UploadIdCardView> {
    private UploadIdCardView mUploadIdCardView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(UploadIdCardView view) {
        this.mUploadIdCardView = view;
        application = HHBaseApplication.get(mUploadIdCardView.getContext());
    }

    public void detachView() {
        this.mUploadIdCardView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void uploadIdCard() {
        mUploadIdCardView.navigateToUserContract();
    }

    public String getShareText() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return "";
        String shareText = mUploadIdCardView.getContext().getResources().getString(R.string.upload_share_dialog_text);
        City myCity = application.getConstants().getCity(user.student.city_id);
        return String.format(shareText, Utils.getMoney(myCity.referer_bonus), Utils.getMoney(myCity.referee_bonus));
    }
}
