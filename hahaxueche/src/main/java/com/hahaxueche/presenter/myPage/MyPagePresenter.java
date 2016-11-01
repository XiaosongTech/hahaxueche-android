package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.employee.Adviser;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.PhotoUtil;
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
            mMyPageView.showLoggedInView();
            mStudent = user.student;
            mMyPageView.loadStudentInfo(mStudent);
            fetchAdviser();
        } else {
            mMyPageView.showNotLoginView();
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
            mMyPageView.startRefresh();
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
                            mMyPageView.stopRefresh();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyPageView.stopRefresh();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mMyPageView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            application.getSharedPrefUtil().updateStudent(student);
                            mMyPageView.loadStudentInfo(student);
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
                        mMyPageView.loadStudentInfo(student);
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
        }
    }

    public void clickMyFollowCount(){
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

    public void clickMyAdviserCount(){
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

    public void clickFAQCount(){
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

    public void clickSupportHahaCount(){
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

    public void clickSoftwareInfoCount(){
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

    public void clickReferCount(){
        //推荐有奖点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_refer_tapped", map);
        } else {
            MobclickAgent.onEvent(mMyPageView.getContext(), "my_page_refer_tapped");
        }
    }
}
