package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.employee.Adviser;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.PhotoUtil;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 16/9/19.
 */
public class MyPagePresenter extends HHBasePresenter implements Presenter<MyPageView> {
    private static final MediaType MULTIPART_FORM_DATA = MediaType.parse("multipart/form-data; boundary=__X_PAW_BOUNDARY__");
    private MyPageView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Student mStudent;
    private Adviser mAdviser;

    public void attachView(MyPageView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user.isLogin()) {
            mView.showLogin();
            mStudent = user.student;
            mView.loadStudentInfo(mStudent);
            showVoucherBadge(mStudent);
            setContractBadge();
            setPassEnsuranceBadge();
            fetchAdviser();
            if (user.student.is_sales_agent) {
                //代理文字
                mView.setReferText("邀请好友平分¥400！邀请越多，奖励越多！");
            }
        } else {
            mView.showNotLogin();
        }
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mStudent = null;
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void fetchStudent() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Student>>() {
                        @Override
                        public Observable<Student> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.getStudent(user.student.id, user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Student>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            mStudent = student;
                            application.getSharedPrefUtil().updateStudent(student);
                            mView.loadStudentInfo(student);
                            showVoucherBadge(student);
                        }
                    });
        }

    }

    public void logOut() {
        HHApiService apiService = application.getApiService();
        String sessionId = application.getSharedPrefUtil().getUser().session.id;
        String accessToken = application.getSharedPrefUtil().getUser().session.access_token;
        subscription = apiService.logOut(sessionId, accessToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        application.getSharedPrefUtil().setUser(null);//清空用户
                        mView.finishToStartLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {

                    }
                });
    }

    public void uploadAvatar() {
        HHApiService apiService = application.getApiService();
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        String filePath = PhotoUtil.IMGPATH + "/" + PhotoUtil.IMAGE_FILE_NAME;
        if (TextUtils.isEmpty(filePath)) {

            return;
        }
        File file = new File(filePath);
        String fileName = filePath.split("/")[filePath.split("/").length - 1];
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, RequestBody.create(MULTIPART_FORM_DATA, file));
        subscription = apiService.uploadAvatar(user.student.id, user.session.access_token, body).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Student>() {
                    @Override
                    public void onCompleted() {
                        mView.showMessage("头像修改成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mStudent = student;
                        mView.loadStudentInfo(student);
                        showVoucherBadge(student);
                    }
                });

    }

    private void fetchAdviser() {
        HHApiService apiService = application.getApiService();
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        subscription = apiService.getAdviser(user.student.id)
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Adviser>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Adviser adviser) {
                        mAdviser = adviser;
                    }
                });
    }

    public Adviser getAdviser() {
        return mAdviser;
    }

    public void toMyCoach() {
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            //我的教练点击
            HashMap<String, String> map = new HashMap();
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_coach_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_coach_tapped");
        }
        if (user == null || !user.isLogin()) {
            mView.alertToLogin();
        } else if (!user.student.isPurchasedService()) {
            mView.showMessage("您还没有购买教练");
        } else {
            mView.toMyCoach(user.student.current_coach_id);
        }
    }

    public void clickPaymentStage() {
        //打款状态点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_pay_coach_status_tapped", map);
            if (user.student.isPurchasedService()) {
                mView.navigateToPaymentStage();
            }
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_pay_coach_status_tapped");
            mView.alertToLogin();
        }
    }

    public void clickMyFollowCount() {
        //我关注教练点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_followed_coach_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_followed_coach_tapped");
        }
    }

    public void clickMyAdviserCount() {
        //我的顾问点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_advisor_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_advisor_tapped");
        }
    }

    public void clickFAQCount() {
        //常见问题点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_FAQ_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_FAQ_tapped");
        }
    }

    public void clickSupportHahaCount() {
        //支持小哈点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_rate_us_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_rate_us_tapped");
        }
    }

    public void clickSoftwareInfoCount() {
        //软件信息点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_version_check_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_version_check_tapped");
        }
    }

    public void clickReferCount() {
        //推荐有奖点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_refer_tapped", map);
            if (user.student.is_sales_agent) {
                mView.navigateToReferFriends();
                return;
            }
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_refer_tapped");
        }
        mView.navigateToStudentRefer();
    }

    public void clickMyCourse() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_course_tapped", map);
            if (user.student.isPurchasedService()) {
                mView.navigateToMyCourse();
            } else {
                mView.navigateToNoCourse();
            }
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_my_course_tapped");
            mView.alertToLogin();
        }
    }

    public void clickMyVoucher() {
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.navigateToMyVoucher();
        } else {
            mView.navigateToNotLoginVoucher();
        }
    }

    public void editUsername(final String username) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("name", username);
        mapParam.put("city_id", user.student.city_id);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.completeUserInfo(user.student.id, user.session.access_token, mapParam);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Student>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mView.editUsername(student.name);
                    }
                });

    }

    private void showVoucherBadge(Student student) {
        if (student == null || student.vouchers == null || student.vouchers.size() < 1) return;
        boolean hasUnUsedVoucher = false;
        for (Voucher voucher : student.vouchers) {
            if (voucher.status == 0) {
                hasUnUsedVoucher = true;
                break;
            }
        }
        mView.setVoucherBadge(hasUnUsedVoucher);
    }

    public void clickMyContract() {
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "my_page_contract_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "my_page_contract_tapped");
        }
        if (user == null || !user.isLogin()) {
            mView.alertToLogin();
        } else if (!user.student.isPurchasedService()) {
            mView.alertToFindCoach();
        } else if (!user.student.isUploadedIdInfo()) {
            mView.navigateToUploadIdCard();
        } else if (!user.student.isSigned()) {
            mView.navigateToSignContract();
        } else {
            mView.navigateToMyContract();
        }
    }

    public void setContractBadge() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (user.student.isPurchasedService() && (!user.student.isUploadedIdInfo() || !user.student.isSigned())) {
            //已购买但是未签订协议或者上传资料
            mView.setContractBadge(true);
        } else {
            mView.setContractBadge(false);
        }
    }

    private void setPassEnsuranceBadge() {
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin() && user.student.isPurchasedService()) {
            mView.setPassEnsuranceBadge(false);
        } else {
            mView.setPassEnsuranceBadge(true);
        }
    }

    public boolean isLogin() {
        User user = application.getSharedPrefUtil().getUser();
        return user != null && user.isLogin();
    }

    public void openFindAdviser() {
        int cityId = 0;
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId > -1) {
            cityId = localSettings.cityId;
        }
        mView.openWebView(WebViewUrl.WEB_URL_FIND_ADVISER + "?city_id=" + cityId);
    }

    public void clickPassEnsurance() {
        mView.navigateToPassEnsurance();
    }

    public void clickMyInsurance() {
        mView.navigateToMyInsurance();
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
}
