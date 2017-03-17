package com.hahaxueche.presenter.myPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.SoftwareInfoView;
import com.hahaxueche.util.UpdateManager;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/21.
 */

public class SoftwareInfoPresenter extends HHBasePresenter implements Presenter<SoftwareInfoView> {
    private SoftwareInfoView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private PackageInfo pi;

    public void attachView(SoftwareInfoView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        PackageManager pm = mView.getContext().getPackageManager();
        try {
            pi = pm.getPackageInfo(mView.getContext().getPackageName(), 0);
            mView.setVersionCode("当前版本： " + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        pi = null;
    }

    /**
     * 版本检测
     */
    public void doVersionCheck() {
        Constants constants = application.getConstants();
        if (!super.doVersionCheck(mView.getContext(), constants.version_code)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
            builder.setTitle("提醒");
            builder.setMessage("您已经是最新版本了!");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
        }
    }

}
