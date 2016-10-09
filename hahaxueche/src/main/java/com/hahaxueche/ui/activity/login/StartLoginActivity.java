package com.hahaxueche.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.presenter.login.StartLoginPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.model.base.Banner;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.view.login.StartLoginView;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/9.
 */
public class StartLoginActivity extends HHBaseActivity implements ViewPager.OnPageChangeListener, OnItemClickListener, StartLoginView {
    @BindView(R.id.banner_login)
    ConvenientBanner mLoginBanner;
    private StartLoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new StartLoginPresenter();
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.acitivity_start_login);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
    }

    @Override
    public void initBanners(ArrayList<Banner> bannerArrayList) {
        int screenWidth = Utils.instence(this).getDm().widthPixels;
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(screenWidth, screenWidth);
        mLoginBanner.setLayoutParams(p);
        ArrayList<String> networkImages = new ArrayList<>();
        for (Banner banner : bannerArrayList) {
            networkImages.add(banner.image_url);
        }
        mLoginBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, networkImages)
                .setPageIndicator(new int[]{R.drawable.icon_point, R.drawable.icon_point_pre})
                .setOnItemClickListener(this);
        mLoginBanner.notifyDataSetChanged();
    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    @Override
    public void navigateToRegister() {
        startActivity(new Intent(getContext(), RegisterActivity.class));
    }

    @Override
    public void navigateToHomepage() {
        startActivity(new Intent(getContext(), MainActivity.class));
        StartLoginActivity.this.finish();
    }

    @OnClick(R.id.tv_start_login)
    public void startLogin() {
        mPresenter.navigateToLogin();
    }

    @OnClick(R.id.tv_start_register)
    public void startRegister() {
        mPresenter.navigateToRegister();
    }

    @OnClick(R.id.tv_tourist_login)
    public void touristLogin() {
        mPresenter.touristLogin();
    }

    @Override
    public void onItemClick(int i) {
        mPresenter.bannerClick(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        mLoginBanner.stopTurning();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始自动翻页
        mLoginBanner.startTurning(2500);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

}
