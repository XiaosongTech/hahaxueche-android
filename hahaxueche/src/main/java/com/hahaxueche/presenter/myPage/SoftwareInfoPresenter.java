package com.hahaxueche.presenter.myPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.SoftwareInfoView;
import com.hahaxueche.util.UpdateManager;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/21.
 */

public class SoftwareInfoPresenter implements Presenter<SoftwareInfoView> {
    private SoftwareInfoView mSoftwareInfoView;
    private Subscription subscription;
    private HHBaseApplication application;
    private PackageInfo pi;

    public void attachView(SoftwareInfoView view) {
        this.mSoftwareInfoView = view;
        application = HHBaseApplication.get(mSoftwareInfoView.getContext());
        PackageManager pm = mSoftwareInfoView.getContext().getPackageManager();
        try {
            pi = pm.getPackageInfo(mSoftwareInfoView.getContext().getPackageName(), 0);
            mSoftwareInfoView.setVersionCode("当前版本： " + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void detachView() {
        this.mSoftwareInfoView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        pi = null;
    }

    /**
     * 版本检测
     */
    public void doVersionCheck() {
        if (pi != null) {
            int versioncode = pi.versionCode;
            Constants constants = application.getConstants();
            if (constants.version_code > versioncode) {
                //有版本更新时
                UpdateManager updateManager = new UpdateManager(mSoftwareInfoView.getContext());
                updateManager.checkUpdateInfo();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mSoftwareInfoView.getContext());
                builder.setTitle("提醒");
                builder.setMessage("您已经是最新版本了!");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing
                    }
                });
                builder.create().show();
            }
        }
    }

}
