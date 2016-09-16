package com.hahaxueche.ui.fragment.homepage;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Banner;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/13.
 */
public class HomepageFragment extends Fragment implements ViewPager.OnPageChangeListener, OnItemClickListener {
    @BindView(R.id.banner_homepage)
    ConvenientBanner mHomepageBanner;
    private Constants mConstants;

    public static HomepageFragment newInstance() {
        HomepageFragment fragment = new HomepageFragment();
        return fragment;
    }

    public HomepageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        ButterKnife.bind(this, view);
        initBanners();
        return view;
    }

    private void initBanners() {
        HHBaseApplication application = HHBaseApplication.get(getContext());
        mConstants = application.getConstants();
        if (mConstants == null) return;

        int screenWidth = Utils.instence(getContext()).getDm().widthPixels;
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(screenWidth, screenWidth / 2);
        mHomepageBanner.setLayoutParams(p);
        ArrayList<String> networkImages = new ArrayList<>();
        for (Banner banner : mConstants.new_home_page_banners) {
            networkImages.add(banner.image_url);
        }
        mHomepageBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, networkImages)
                .setPageIndicator(new int[]{R.drawable.icon_point, R.drawable.icon_point_pre})
                .setOnItemClickListener(this);
        mHomepageBanner.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int i) {
        if (mConstants == null) return;
        if (!TextUtils.isEmpty(mConstants.new_home_page_banners.get(i).target_url)) {
            /*Intent intent = new Intent(getApplication(), BaseWebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", mConstants.getNew_login_banners().get(i).getTarget_url());
            intent.putExtras(bundle);
            startActivity(intent);*/
            HHLog.v("target url -> " + mConstants.new_home_page_banners.get(i).target_url);
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
        mHomepageBanner.stopTurning();
    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        mHomepageBanner.startTurning(2500);
    }
}
