package com.hahaxueche.presenter.homepage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.responseList.FieldResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.BookAddress;
import com.hahaxueche.model.user.student.Contact;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.HahaCache;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/17.
 */
public class HomepagePresenter extends HHBasePresenter implements Presenter<HomepageView> {
    private HomepageView mView;
    private Subscription subscription;

    private HHBaseApplication application;
    private Constants constants;

    public void attachView(HomepageView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        constants = application.getConstants();
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId < 0) {
            //本地化设置没有城市，默认用户的
            User user = application.getSharedPrefUtil().getUser();
            if (user != null && user.student != null && user.student.city_id > -1) {
                localSettings.cityId = user.student.city_id;
            } else {
                //用户也没有城市信息，默认武汉
                localSettings.cityId = 0;
            }
            application.getSharedPrefUtil().setLocalSettings(localSettings);
        }
        initCityConstants(localSettings.cityId);
        mView.setCityName(application.getConstants().getCityName(localSettings.cityId));
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    /**
     * 初始化城市常量
     *
     * @param cityId
     */
    private void initCityConstants(final int cityId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCityConstant(cityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CityConstants>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        initCityFields(cityId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(CityConstants cityConstants) {
                        application.setCityConstants(cityConstants);
                        mView.loadHotDrivingSchools(cityConstants.driving_schools.subList(0, Common.MAX_DRIVING_SCHOOL_COUNT));
                    }
                });
    }

    private void initCityFields(int cityId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getFields(cityId, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<FieldResponseList>() {
                    @Override
                    public void onCompleted() {
                        mView.readyToLoadViews();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(FieldResponseList fieldResponseList) {
                        application.setFieldResponseList(fieldResponseList);
                    }
                });
    }

    private void getCityFields(int cityId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getFields(cityId, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<FieldResponseList>() {
                    @Override
                    public void onCompleted() {
                        mView.onCityChange();
                        getNearCoaches();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(FieldResponseList fieldResponseList) {
                        application.setFieldResponseList(fieldResponseList);
                    }
                });
    }

    public void openProcedure() {
        //学车流程点击
        addDataTrack("homepage_process_tapped", mView.getContext());
        mView.openWebView(WebViewUrl.WEB_URL_PROCEDURE);
    }

    public void openGroupBuy() {
        //团购点击
        addDataTrack("homepage_group_purchase_tapped", mView.getContext());
        mView.openWebView(WebViewUrl.WEB_URL_GROUP_BUY);
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }


    public void selectCity(int cityId) {
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        localSettings.cityId = cityId;
        application.getSharedPrefUtil().setLocalSettings(localSettings);
        mView.setCityName(application.getConstants().getCityName(cityId));
        getNearCoaches();
        getCityConstants(cityId);
    }

    public void bannerClick(int i) {
        try {
            HashMap<String, String> map = new HashMap();
            if (!TextUtils.isEmpty(constants.new_home_page_banners.get(i).target_url)) {
                map.put("URL", constants.new_home_page_banners.get(i).target_url);
                addDataTrack("home_page_banner_tapped", mView.getContext(), map);
                mView.openWebView(constants.new_home_page_banners.get(i).target_url);
            } else {
                addDataTrack("home_page_banner_tapped", mView.getContext());
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
    }

    public boolean isNeedUpdate() {
        Constants constants = application.getConstants();
        return super.isNeedUpdate(mView.getContext(), constants.version_code);
    }

    /**
     * 推荐有奖跳转逻辑
     */
    public void toReferFriends() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
            //非代理
            mView.navigateToStudentRefer();
        } else {
            mView.navigateToReferFriends();
        }
    }

    /**
     * 上传通讯录
     *
     * @param contacts
     */
    public void uploadContacts(ArrayList<Contact> contacts) {
        if (contacts == null || contacts.size() < 1) return;
        BookAddress bookAddress = new BookAddress();
        bookAddress.device_id = HahaCache.deviceId;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            bookAddress.phone = user.cell_phone;
        }
        bookAddress.address_book = contacts;
        HHApiService apiService = application.getApiService();
        subscription = apiService.uploadContacts(bookAddress)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String ret) {

                    }
                });
    }

    public void getCityConstants(final int cityId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCityConstant(cityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CityConstants>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        getCityFields(cityId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(CityConstants cityConstants) {
                        application.setCityConstants(cityConstants);
                        mView.loadHotDrivingSchools(cityConstants.driving_schools.subList(0, Common.MAX_DRIVING_SCHOOL_COUNT));
                    }
                });
    }

    public void getNearCoaches() {
        HHApiService apiService = application.getApiService();
        int cityId = 0;
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId > -1) {
            cityId = localSettings.cityId;
        }
        if (application.getMyLocation() != null) {
            ArrayList<String> locations = new ArrayList<>();
            locations.add(String.valueOf(application.getMyLocation().lat));
            locations.add(String.valueOf(application.getMyLocation().lng));
            subscription = apiService.getNearCoaches(Common.START_PAGE, Common.MAX_NEAR_COACH_COUNT, cityId, locations, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<CoachResponseList>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            HHLog.e(e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(CoachResponseList coachResponseList) {
                            if (coachResponseList.data != null) {
                                mView.loadNearCoaches(coachResponseList.data);
                            }

                        }
                    });
        } else {
            subscription = apiService.getNearCoaches(Common.START_PAGE, Common.MAX_NEAR_COACH_COUNT, cityId, null, 5)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<CoachResponseList>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            HHLog.e(e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(CoachResponseList coachResponseList) {
                            if (coachResponseList.data != null) {
                                mView.loadNearCoaches(coachResponseList.data);
                            }

                        }
                    });
        }
    }

    public void setLocation(double lat, double lng) {
        application.setMyLocation(lat, lng);
        getNearCoaches();
    }

    public void clickHotDrivingSchool(int index) {
        HashMap<String, String> map = new HashMap();
        map.put("index", String.valueOf(index));
        addDataTrack("home_page_hot_school_tapped", mView.getContext(), map);
    }

    public void clickNearCoach(int index) {
        HashMap<String, String> map = new HashMap();
        map.put("index", String.valueOf(index));
        addDataTrack("home_page_hot_coach_tapped", mView.getContext(), map);
    }
}