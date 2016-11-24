package com.hahaxueche.ui.fragment.homepage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Banner;
import com.hahaxueche.model.base.City;
import com.hahaxueche.presenter.homepage.HomepagePresenter;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.dialog.login.CityChoseDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class HomepageFragment extends HHBaseFragment implements ViewPager.OnPageChangeListener, OnItemClickListener, HomepageView {
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;
    private MainActivity mActivity;
    private HomepagePresenter mPresenter;

    @BindView(R.id.banner_homepage)
    ConvenientBanner mHomepageBanner;
    @BindView(R.id.crl_main)
    CoordinatorLayout mClyMain;
    @BindView(R.id.tv_driving_school_count)
    TextView mTvDrivingSchoolCount;
    @BindView(R.id.tv_coach_count)
    TextView mTvCoachCount;
    @BindView(R.id.tv_paid_student_count)
    TextView mTvPaidStudentCount;

    private CityChoseDialog mCityChoseDialog;
    private static final int PERMISSIONS_REQUEST_SDCARD = 600;
    private static final int REQUEST_CODE_WEBVIEW = 14;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new HomepagePresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_SDCARD);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            mPresenter.doVersionCheck();
        }
        return view;
    }

    @Override
    public void initBanners(ArrayList<Banner> bannerArrayList) {
        if (bannerArrayList != null && bannerArrayList.size() > 0) {
            int screenWidth = Utils.instence(getContext()).getDm().widthPixels;
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(screenWidth, screenWidth / 5 * 2);
            mHomepageBanner.setLayoutParams(p);
            ArrayList<String> networkImages = new ArrayList<>();
            for (Banner banner : bannerArrayList) {
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
    }

    @OnClick({R.id.fly_my_strength,
            R.id.fly_procedure,
            R.id.frl_tel_ask,
            R.id.cv_coach,
            R.id.frl_online_ask,
            R.id.iv_free_try,
            R.id.cv_adviser,
            R.id.cv_driving_school,
            R.id.frl_group_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fly_my_strength:
                mPresenter.openMyStrengths();
                break;
            case R.id.fly_procedure:
                mPresenter.openProcedure();
                break;
            case R.id.frl_tel_ask:
                mPresenter.phoneSupportCount();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    contactService();
                }
                break;
            case R.id.cv_coach:
                mPresenter.openBestCoaches();
                break;
            case R.id.frl_online_ask:
                mPresenter.onlineAsk();
                break;
            case R.id.iv_free_try:
                mPresenter.freeTry();
                break;
            case R.id.cv_adviser:
                mPresenter.openFindAdviser();
                break;
            case R.id.cv_driving_school:
                mPresenter.openFindDrivingSchool();
                break;
            case R.id.frl_group_buy:
                mPresenter.openGroupBuy();
                break;
            default:
                break;
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mPresenter.doVersionCheck();
            } else {
                showMessage("请允许读写sdcard权限，不然无法下载最新的安装包");
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mClyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setDrivingSchoolCountDisplay(SpannableString ss) {
        mTvDrivingSchoolCount.setText(ss);
    }

    @Override
    public void setCoachCountDisplay(SpannableString ss) {
        mTvCoachCount.setText(ss);
    }

    @Override
    public void setPaidStudentCountDisplay(SpannableString ss) {
        mTvPaidStudentCount.setText(ss);
    }

    @Override
    public void showCityChoseDialog() {
        if (mCityChoseDialog == null) {
            mCityChoseDialog = new CityChoseDialog(getContext(), new CityChoseDialog.onConfirmListener() {
                @Override
                public boolean selectCity(City city) {
                    if (city != null) {
                        mPresenter.selectCity(city.id);
                    }
                    return true;
                }
            });
        }
        mCityChoseDialog.show();
    }

    @Override
    public void openWebView(String url) {
        this.openWebView(url, "", false);
    }

    @Override
    public void openWebView(String url, String title, boolean isShowShare) {
        Intent intent = new Intent(getContext(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        HHLog.v("webview url -> " + url);
        bundle.putString("url", url);
        bundle.putString("title", title);
        bundle.putBoolean("isShowShare", isShowShare);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_WEBVIEW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WEBVIEW) {
            if (resultCode == RESULT_OK && null != data) {
                int tab = data.getIntExtra("showTab", 1);
                mActivity.selectTab(tab);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        mHomepageBanner.stopTurning();
    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        mHomepageBanner.startTurning(2500);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
