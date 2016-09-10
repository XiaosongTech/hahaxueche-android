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
import com.hahaxueche.ui.activity.HHBaseActivity;
import com.hahaxueche.ui.model.base.Banner;
import com.hahaxueche.ui.model.base.Constants;
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
public class StartLoginActivity extends HHBaseActivity implements ViewPager.OnPageChangeListener, OnItemClickListener {
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_start_login)
    TextView mTvStartLogin;
    @BindView(R.id.tv_start_register)
    TextView mTvStartRegister;
    @BindView(R.id.tv_tourist_login)
    TextView mTvTouristLogin;
    @BindView(R.id.banner_login)
    ConvenientBanner mLoginBanner;
    private Constants mConstants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.acitivity_start_login);
        ButterKnife.bind(this);
        initBanners();
    }

    private void initBanners() {
        HHBaseApplication application = HHBaseApplication.get(getContext());
        mConstants = application.getConstants();
        if (mConstants == null) return;

        int screenWidth = Utils.instence(this).getDm().widthPixels;
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(screenWidth, screenWidth);
        mLoginBanner.setLayoutParams(p);
        ArrayList<String> networkImages = new ArrayList<>();
        for (Banner banner : mConstants.new_login_banners) {
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

    @OnClick(R.id.tv_start_login)
    public void startLogin() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    @OnClick(R.id.tv_start_register)
    public void startRegister() {
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_back)
    public void back() {
        HHLog.v("click back");
    }

    @OnClick(R.id.tv_tourist_login)
    public void touristLogin() {
        HHLog.v("click tourist login");
    }

    @Override
    public void onItemClick(int i) {
        if (mConstants == null) return;
        if (!TextUtils.isEmpty(mConstants.new_login_banners.get(i).target_url)) {
            /*Intent intent = new Intent(getApplication(), BaseWebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", mConstants.getNew_login_banners().get(i).getTarget_url());
            intent.putExtras(bundle);
            startActivity(intent);*/
            HHLog.v("target url -> " + mConstants.new_login_banners.get(i).target_url);
        }
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
}
