package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.employee.Adviser;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.PhotoUtil;
import com.hahaxueche.util.WebViewUrl;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;
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
public class MyPagePresenter implements Presenter<MyPageView> {
    private static final MediaType MULTIPART_FORM_DATA = MediaType.parse("multipart/form-data; boundary=__X_PAW_BOUNDARY__");
    private MyPageView mMyPageView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Student mStudent;
    private Adviser mAdviser;

    public void attachView(MyPageView view) {
        this.mMyPageView = view;
        application = HHBaseApplication.get(mMyPageView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user.isLogin()) {
            mMyPageView.showLogin();
            mStudent = user.student;
            mMyPageView.loadStudentInfo(mStudent);
            showVoucherBadge(mStudent);
            setContractBadge();
            setPassEnsuranceBadge();
            fetchAdviser();
            if (user.student.is_sales_agent) {
                //代理文字
                mMyPageView.setReferText("邀请好友平分¥400！邀请越多，奖励越多！");
            }
        } else {
            mMyPageView.showNotLogin();
        }
    }

    public void detachView() {
        this.mMyPageView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mStudent = null;
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        //在线客服点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_online_support_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_online_support_tapped");
        }
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(mMyPageView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
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
                                mMyPageView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            mStudent = student;
                            application.getSharedPrefUtil().updateStudent(student);
                            mMyPageView.loadStudentInfo(student);
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
                        mMyPageView.finishToStartLogin();
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
                        mMyPageView.showMessage("头像修改成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mStudent = student;
                        mMyPageView.loadStudentInfo(student);
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
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_coach_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_coach_tapped");
        }
        if (user == null || !user.isLogin()) {
            mMyPageView.alertToLogin();
        } else if (!user.student.hasPurchasedService()) {
            mMyPageView.showMessage("您还没有购买教练");
        } else {
            mMyPageView.toMyCoach(user.student.current_coach_id);
        }
    }

    public void clickPaymentStage() {
        //打款状态点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_pay_coach_status_tapped", map);
            if (user.student.hasPurchasedService()) {
                mMyPageView.navigateToPaymentStage();
            }
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_pay_coach_status_tapped");
            mMyPageView.alertToLogin();
        }
    }

    public void clickMyFollowCount() {
        //我关注教练点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_followed_coach_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_followed_coach_tapped");
        }
    }

    public void clickMyAdviserCount() {
        //我的顾问点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_advisor_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_advisor_tapped");
        }
    }

    public void clickFAQCount() {
        //常见问题点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_FAQ_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_FAQ_tapped");
        }
    }

    public void clickSupportHahaCount() {
        //支持小哈点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_rate_us_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_rate_us_tapped");
        }
    }

    public void clickSoftwareInfoCount() {
        //软件信息点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_version_check_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_version_check_tapped");
        }
    }

    public void clickReferCount() {
        //推荐有奖点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_refer_tapped", map);
            if (user.student.is_sales_agent) {
                mMyPageView.navigateToReferFriends();
                return;
            }
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_refer_tapped");
        }
        mMyPageView.navigateToStudentRefer();
    }

    public void clickMyCourse() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_course_tapped", map);
            if (user.student.hasPurchasedService()) {
                mMyPageView.navigateToMyCourse();
            } else {
                mMyPageView.navigateToNoCourse();
            }
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_my_course_tapped");
            mMyPageView.alertToLogin();
        }
    }

    public void clickMyVoucher() {
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mMyPageView.navigateToMyVoucher();
        } else {
            mMyPageView.navigateToNotLoginVoucher();
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
                        mMyPageView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mMyPageView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMyPageView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mMyPageView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mMyPageView.editUsername(student.name);
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
        mMyPageView.setVoucherBadge(hasUnUsedVoucher);
    }

    public void clickMyContract() {
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_contract_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_contract_tapped");
        }
        if (user == null || !user.isLogin()) {
            mMyPageView.alertToLogin();
        } else if (!user.student.hasPurchasedService()) {
            mMyPageView.alertToFindCoach();
        } else if (!user.student.isUploadedIdInfo()) {
            mMyPageView.navigateToUploadIdCard();
        } else if (!user.student.isSigned()) {
            mMyPageView.navigateToSignContract();
        } else {
            mMyPageView.navigateToMyContract();
        }
    }

    public void setContractBadge() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (user.student.hasPurchasedService() && (!user.student.isUploadedIdInfo() || !user.student.isSigned())) {
            //已购买但是未签订协议或者上传资料
            mMyPageView.setContractBadge(true);
        } else {
            mMyPageView.setContractBadge(false);
        }
    }

    private void setPassEnsuranceBadge() {
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin() && user.student.hasPurchasedService()) {
            mMyPageView.setPassEnsuranceBadge(false);
        } else {
            mMyPageView.setPassEnsuranceBadge(true);
        }
    }

    public boolean isLogin() {
        User user = application.getSharedPrefUtil().getUser();
        return user != null && user.isLogin();
    }

    public void openFindAdviser() {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        mMyPageView.openWebView(WebViewUrl.WEB_URL_FIND_ADVISER + "?city_id=" + cityId);
    }

    public void clickPassEnsurance() {
        mMyPageView.navigateToPassEnsurance();
    }

    public void clickMyInsurance() {
        mMyPageView.navigateToMyInsurance();
    }

    /**
     * 推荐有奖跳转逻辑
     */
    public void toReferFriends() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
            //非代理
            mMyPageView.navigateToStudentRefer();
        } else {
            mMyPageView.navigateToReferFriends();
        }
    }
}
