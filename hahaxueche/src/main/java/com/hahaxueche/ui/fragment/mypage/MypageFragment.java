package com.hahaxueche.ui.fragment.myPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.presenter.myPage.MyPagePresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.activity.myPage.FAQActivity;
import com.hahaxueche.ui.activity.myPage.SoftwareInfoActivity;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/13.
 */
public class MyPageFragment extends HHBaseFragment implements MyPageView, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.lly_not_login)
    LinearLayout mLlyNotLogin;
    @BindView(R.id.srl_my_page)
    SwipeRefreshLayout mSrlMyPage;
    @BindView(R.id.iv_my_avatar)
    SimpleDraweeView mIvMyAvatar;
    @BindView(R.id.tv_student_name)
    TextView mTvStudentName;
    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;
    @BindView(R.id.tv_payment_stage)
    TextView mTvPaymentStage;
    @BindView(R.id.tv_student_phase)
    TextView mTvStudentPhase;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;

    private MyPagePresenter mPresenter;
    private MainActivity mActivity;
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;
    private static final String WEB_URL_ABOUT_HAHA = "http://staging.hahaxueche.net/#/student";
    private static final String URL_APP_STORE = "http://a.app.qq.com/o/simple.jsp?pkgname=com.hahaxueche";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new MyPagePresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        mSrlMyPage.setOnRefreshListener(this);
        mSrlMyPage.setColorSchemeResources(R.color.app_theme_color);
        return view;
    }

    @OnClick(R.id.tv_logout)
    public void logOut() {
        mPresenter.logOut();
    }

    @Override
    public void showNotLoginView() {
        mLlyNotLogin.setVisibility(View.VISIBLE);
        mSrlMyPage.setVisibility(View.GONE);
    }

    @Override
    public void showLoggedInView() {
        mLlyNotLogin.setVisibility(View.GONE);
        mSrlMyPage.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadStudentInfo(Student student) {
        mIvMyAvatar.setImageURI(student.avatar);
        mTvStudentName.setText(student.name);
        mTvAccountBalance.setText(Utils.getMoney(student.getAccountBalance()));
        mTvPaymentStage.setText(student.getPaymentStageLabel());
        mTvStudentPhase.setText(student.getStudentPhaseLabel());
    }

    @Override
    public void finishToStartLogin() {
        startActivity(new Intent(getContext(), StartLoginActivity.class));
        mActivity.finish();
    }

    @Override
    public void startRefresh() {
        mSrlMyPage.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        mSrlMyPage.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchStudent();
    }

    @OnClick(R.id.rly_online_service)
    public void onlineAsk() {
        mPresenter.onlineAsk();
    }

    @OnClick(R.id.rly_tel_service)
    public void clickTelContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            contactService();
        }
    }

    @OnClick(R.id.rly_FAQ)
    public void navigateToFAQ() {
        startActivity(new Intent(getContext(), FAQActivity.class));
    }

    @OnClick(R.id.rly_about_haha)
    public void navigateToAboutHaha() {
        mActivity.openWebView(WEB_URL_ABOUT_HAHA);
    }

    @OnClick(R.id.rly_support_haha)
    public void supportHaha() {
        Uri uri = Uri.parse(URL_APP_STORE);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.rly_software_info)
    public void navigateToSoftwareInfo() {
        startActivity(new Intent(getContext(), SoftwareInfoActivity.class));
    }

    /**
     * 联系客服
     */
    private void contactService() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        }
    }
}
