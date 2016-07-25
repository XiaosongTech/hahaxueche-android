package com.hahaxueche.ui.activity.mySetting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.UpdateManager;
import com.hahaxueche.utils.Util;

/**
 * Created by Administrator on 2016/5/9.
 */
public class SoftwareInfoActivity extends MSBaseActivity {
    private ImageButton mIbtnBack;
    private RelativeLayout mRlyVersionCheck;
    private TextView mTvCurrentVersion;
    private PackageManager pm;
    private PackageInfo pi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_info);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mRlyVersionCheck = Util.instence(this).$(this, R.id.rly_version_check);
        mTvCurrentVersion = Util.instence(this).$(this, R.id.tv_current_version);
    }

    private void initEvent() {
        mIbtnBack.setOnClickListener(mClickListener);
        mRlyVersionCheck.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        pm = this.getPackageManager();
        try {
            pi = pm.getPackageInfo(this.getPackageName(), 0);
            mTvCurrentVersion.setText("当前版本： " + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ibtn_back:
                    SoftwareInfoActivity.this.finish();
                    break;
                case R.id.rly_version_check:
                    doVersionCheck();
                default:
                    break;
            }
        }
    };

    /**
     * 版本检测
     */
    private void doVersionCheck() {
        if (pi != null) {
            int versioncode = pi.versionCode;
            SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
            Constants constants = spUtil.getConstants();
            if (constants == null)
                return;
            if (constants.getVersion_code() > versioncode) {
                //有版本更新时
                UpdateManager updateManager = new UpdateManager(SoftwareInfoActivity.this);
                updateManager.checkUpdateInfo();
            } else {
                BaseAlertDialog baseAlertDialog = new BaseAlertDialog(this, "", "提醒", "您已经是最新版本了！");
                baseAlertDialog.show();
            }
        }
    }
}
