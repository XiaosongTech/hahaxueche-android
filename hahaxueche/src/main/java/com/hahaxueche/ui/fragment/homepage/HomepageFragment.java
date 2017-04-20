package com.hahaxueche.ui.fragment.homepage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.student.Contact;
import com.hahaxueche.presenter.homepage.HomepagePresenter;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.FieldFilterActivity;
import com.hahaxueche.ui.activity.findCoach.PaySuccessActivity;
import com.hahaxueche.ui.activity.findCoach.SearchCoachActivity;
import com.hahaxueche.ui.activity.myPage.MyInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.PurchaseInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.StudentReferActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.adapter.homepage.HotDrivingSchoolAdapter;
import com.hahaxueche.ui.adapter.homepage.NearCoachAdapter;
import com.hahaxueche.ui.dialog.login.CityChoseDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class HomepageFragment extends HHBaseFragment implements ViewPager.OnPageChangeListener, OnItemClickListener, HomepageView {
    private MainActivity mActivity;
    private HomepagePresenter mPresenter;

    @BindView(R.id.crl_main)
    CoordinatorLayout mClyMain;
    @BindView(R.id.iv_find_driving_school)
    SimpleDraweeView mIvFindDrivingSchool;
    @BindView(R.id.iv_find_coach)
    SimpleDraweeView mIvFindCoach;
    @BindView(R.id.rcy_hot_driving_school)
    RecyclerView mRcyHotDrivingSchool;
    @BindView(R.id.rcy_near_coach)
    RecyclerView mRcyNearCoach;
    @BindView(R.id.tv_city)
    TextView mTvCityName;

    private CityChoseDialog mCityChoseDialog;
    private HotDrivingSchoolAdapter mDrivingSchoolAdapter;
    private NearCoachAdapter mNearCoachAdapter;

    //定位client
    public AMapLocationClient mLocationClient;
    //定位回调监听器
    public AMapLocationListener mLocationListener;

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
        Uri uriFindSchool = Uri.parse("res://com.hahaxueche)/" + R.drawable.bt_chooseschool);
        DraweeController dcFindSchool =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uriFindSchool)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        mIvFindDrivingSchool.setController(dcFindSchool);
        GenericDraweeHierarchy hyFindDrivingSchool = mIvFindDrivingSchool.getHierarchy();
        hyFindDrivingSchool.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        Uri uriFindCoach = Uri.parse("res://com.hahaxueche)/" + R.drawable.bt_choosecoach);
        DraweeController dcFindCoach =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uriFindCoach)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        mIvFindCoach.setController(dcFindCoach);
        GenericDraweeHierarchy hyFindCoach = mIvFindCoach.getHierarchy();
        hyFindCoach.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

        mPresenter.getHotDrivingSchools();

        if (mPresenter.isNeedUpdate()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || mActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                            || mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        RequestCode.PERMISSIONS_REQUEST_SDCARD_CONTACTS_LOCATIONS_HOMEPAGE);
            } else {
                mPresenter.alertToUpdate(getContext());
                readContacts();
                startLocation();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && (mActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION},
                        RequestCode.PERMISSIONS_REQUEST_READ_CONTACTS_AND_LOCATIONS);
            } else {
                readContacts();
                startLocation();
            }
        }

        return view;
    }

    @Override
    public void navigateToReferFriends() {
        startActivity(new Intent(getContext(), ReferFriendsActivity.class));
    }

    @Override
    public void navigateToExamLibrary() {
        startActivityForResult(new Intent(getContext(), ExamLibraryActivity.class), RequestCode.REQUEST_CODE_EXAM_LIBRARY);
    }

    @Override
    public void navigateToStudentRefer() {
        startActivity(new Intent(getContext(), StudentReferActivity.class));
    }

    @Override
    public void navigateToMyInsurance() {
        startActivityForResult(new Intent(getContext(), MyInsuranceActivity.class), RequestCode.REQUEST_CODE_MY_INSURANCE);
    }

    @Override
    public void loadHotDrivingSchools(final List<DrivingSchool> drivingSchoolList) {
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        mRcyHotDrivingSchool.setLayoutManager(layoutManager);
        mDrivingSchoolAdapter = new HotDrivingSchoolAdapter(getContext(), drivingSchoolList, new HotDrivingSchoolAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (drivingSchoolList != null && drivingSchoolList.size() > 0 && position > -1 && position < drivingSchoolList.size()) {
                    mPresenter.clickHotDrivingSchool(position);
                    openWebView(WebViewUrl.WEB_URL_JIAXIAO + "/" + drivingSchoolList.get(position).id);
                }
            }
        });
        mRcyHotDrivingSchool.setAdapter(mDrivingSchoolAdapter);
    }

    @Override
    public void loadNearCoaches(final ArrayList<Coach> coaches) {
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        mRcyNearCoach.setLayoutManager(layoutManager);
        mNearCoachAdapter = new NearCoachAdapter(getContext(), coaches, new NearCoachAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (coaches != null && coaches.size() > 0 && position > -1 && position < coaches.size()) {
                    mPresenter.clickNearCoach(position);
                    Intent intent = new Intent(getContext(), CoachDetailActivity.class);
                    intent.putExtra("coach", coaches.get(position));
                    startActivity(intent);
                }
            }
        });
        mRcyNearCoach.setAdapter(mNearCoachAdapter);
    }

    @Override
    public void setCityName(String cityName) {
        mTvCityName.setText(cityName);
    }

    @OnClick({R.id.cv_procedure,
            R.id.tv_online_ask,
            R.id.tv_group_buy,
            R.id.tv_test_lib,
            R.id.cv_new_policy,
            R.id.cv_enroll,
            R.id.cv_driving_school_sort,
            R.id.lly_xuechebao,
            R.id.lly_fenqibao,
            R.id.lly_peifubao,
            R.id.tv_more_hot_driving_school,
            R.id.iv_find_driving_school,
            R.id.iv_find_coach,
            R.id.tv_more_near_coach,
            R.id.tv_city,
            R.id.tv_map,
            R.id.tv_map_find,
            R.id.fly_search
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_procedure:
                mPresenter.openProcedure();
                break;
            case R.id.tv_online_ask:
                mPresenter.onlineAsk();
                break;
            case R.id.tv_group_buy:
                mPresenter.openGroupBuy();
                break;
            case R.id.tv_test_lib:
                mPresenter.clickTestLib();
                break;
            case R.id.cv_new_policy:
                mPresenter.addDataTrack("home_page_new_policy_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_ZHENGCE);
                break;
            case R.id.cv_enroll:
                mPresenter.addDataTrack("home_page_application_notice_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_BAOMING);
                break;
            case R.id.cv_driving_school_sort:
                openWebView(WebViewUrl.WEB_URL_JIAXIAO);
                break;
            case R.id.lly_xuechebao:
                mPresenter.addDataTrack("home_page_assurance_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_XUECHEBAO);
                break;
            case R.id.lly_fenqibao:
                mPresenter.addDataTrack("home_page_installment_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_FENQIBAO);
                break;
            case R.id.lly_peifubao:
                mPresenter.addDataTrack("home_page_compensate_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_PEIFUBAO);
                break;
            case R.id.tv_more_hot_driving_school:
                mPresenter.addDataTrack("home_page_hot_school_more_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_JIAXIAO);
                break;
            case R.id.iv_find_driving_school:
                mPresenter.addDataTrack("home_page_select_school_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_JIAXIAO);
                break;
            case R.id.iv_find_coach:
                mPresenter.addDataTrack("home_page_select_coach_tapped", getContext());
                mActivity.selectTab(1);
                break;
            case R.id.tv_more_near_coach:
                mPresenter.addDataTrack("home_page_hot_coach_more_tapped", getContext());
                mActivity.selectTab(1);
                break;
            case R.id.tv_city:
                mPresenter.addDataTrack("home_navigation_city_tapped", getContext());
                showCityChoseDialog();
                break;
            case R.id.tv_map:
                mPresenter.addDataTrack("home_navigation_map_tapped", getContext());
                startActivity(new Intent(getContext(), FieldFilterActivity.class));
                break;
            case R.id.tv_map_find:
                mPresenter.addDataTrack("home_page_map_view_tapped", getContext());
                startActivity(new Intent(getContext(), FieldFilterActivity.class));
                break;
            case R.id.fly_search:
                mPresenter.addDataTrack("home_navigation_search_tapped", getContext());
                startActivity(new Intent(getContext(), SearchCoachActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_READ_CONTACTS_AND_LOCATIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startLocation();
            } else {
                mPresenter.getNearCoaches();
                showMessage("请允许使用定位权限，不然我们无法为您推荐附近的教练");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SDCARD_CONTACTS_LOCATIONS_HOMEPAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.alertToUpdate(getContext());
            } else {
                showMessage("请允许读写sdcard权限，不然无法下载最新的安装包");
            }
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            }
            if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                startLocation();
            } else {
                showMessage("请允许使用定位权限，不然我们无法为您推荐附近的教练");
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mClyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showCityChoseDialog() {
        if (mCityChoseDialog == null) {
            mCityChoseDialog = new CityChoseDialog(getContext(), new CityChoseDialog.onConfirmListener() {
                @Override
                public boolean selectCity(City city) {
                    mPresenter.addDataTrack("home_navigation_city_selected", getContext());
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
        Intent intent = new Intent(getContext(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        HHLog.v("webview url -> " + url);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivityForResult(intent, RequestCode.REQUEST_CODE_WEBVIEW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_WEBVIEW) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("peifubao", false)) {
                    navigateToMyInsurance();
                } else {
                    int tab = data.getIntExtra("showTab", 1);
                    mActivity.selectTab(tab);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_EXAM_LIBRARY) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("toFindCoach", false)) {
                    mActivity.selectTab(1);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_MY_INSURANCE) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("toUploadInfo", false)) {
                    Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
                    intent.putExtra("isFromPaySuccess", false);
                    intent.putExtra("isInsurance", true);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
                } else if (data.getBooleanExtra("toFindCoach", false)) {
                    mActivity.selectTab(1);
                } else {
                    Intent intent = new Intent(getContext(), PurchaseInsuranceActivity.class);
                    intent.putExtra("insuranceType", data.getIntExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_WITHOUT_COACH));
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_PURCHASE_INSURANCE);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PURCHASE_INSURANCE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), PaySuccessActivity.class);
                intent.putExtra("isPurchasedInsurance", true);
                intent.putExtra("isFromPurchaseInsurance", true);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PAY_SUCCESS) {
            Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
            intent.putExtra("isFromPaySuccess", false);
            intent.putExtra("isInsurance", true);
            startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
        } else if (requestCode == RequestCode.REQUEST_CODE_UPLOAD_ID_CARD) {
            mPresenter.toReferFriends();
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
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();//销毁定位客户端。
        }
    }

    /**
     * 读取通讯录
     */
    private void readContacts() {
        Cursor cursor = null;
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            cursor = mActivity.getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Contact contact = new Contact();
                    contact.name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contact.number = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("-", "");
                    contacts.add(contact);
                }
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            mPresenter.uploadContacts(contacts);
        }
    }

    private void startLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext());
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        mPresenter.setLocation(amapLocation.getLatitude(), amapLocation.getLongitude());
                    } else {
                        String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                        HHLog.e(errText);
                        mPresenter.getNearCoaches();
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000 * 60);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        HHLog.v("create location service");
        mLocationClient.startLocation();
    }
}
