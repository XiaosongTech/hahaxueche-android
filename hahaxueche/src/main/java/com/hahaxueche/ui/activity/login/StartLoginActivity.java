package com.hahaxueche.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Banner;
import com.hahaxueche.presenter.login.StartLoginPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.view.login.StartLoginView;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.util.Common;
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

    @OnClick({R.id.tv_start_login,
            R.id.tv_start_register,
            R.id.tv_tourist_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start_login:
                mPresenter.navigateToLogin();
                break;
            case R.id.tv_start_register:
                mPresenter.navigateToRegister();
                break;
            case R.id.tv_tourist_login:
                mPresenter.touristLogin();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(int i) {
        mPresenter.clickBanner(i);
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
    public void onResume() {
        super.onResume();
        //开始自动翻页
        mLoginBanner.startTurning(Common.BANNER_TURNING_TIME);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

}
