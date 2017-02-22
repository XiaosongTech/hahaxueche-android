package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseSuccess;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.ErrorResponse;
import com.hahaxueche.model.user.IdCardUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.UploadIdCardView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/25.
 */

public class UploadIdCardPresenter implements Presenter<UploadIdCardView> {
    private static final MediaType MULTIPART_FORM_DATA = MediaType.parse("multipart/form-data; boundary=__X_PAW_BOUNDARY__");
    private UploadIdCardView mUploadIdCardView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(UploadIdCardView view) {
        this.mUploadIdCardView = view;
        application = HHBaseApplication.get(mUploadIdCardView.getContext());
        pageStartCount();
    }

    public void detachView() {
        this.mUploadIdCardView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void generateAgreement() {
        HHApiService apiService = application.getApiService();
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        subscription = apiService.createAgreement(user.student.id, user.session.access_token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Response<IdCardUrl>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mUploadIdCardView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mUploadIdCardView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUploadIdCardView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<IdCardUrl> response) {
                        if (response.isSuccessful()) {
                            mUploadIdCardView.navigateToUserContract(response.body().agreement_url, user.student.id);
                        } else {
                            Retrofit retrofit = HHApiService.Factory.getRetrofit();
                            Converter<ResponseBody, ErrorResponse> errorConverter = retrofit.responseBodyConverter(ErrorResponse.class,
                                    new Annotation[0]);
                            try {
                                ErrorResponse error = errorConverter.convert(response.errorBody());
                                if (error.code == 40026) {
                                    mUploadIdCardView.showMessage("身份证正面识别失败，请重新拍摄并上传！");
                                } else if (error.code == 400028) {
                                    mUploadIdCardView.showMessage("身份证信息无效, 请确保使用真实的第二代身份证!");
                                } else {
                                    mUploadIdCardView.showMessage("上传失败, 请重试!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                mUploadIdCardView.showMessage("上传失败, 请重试!");
                            }
                        }
                    }
                });
    }

    public String getShareText() {
        return mUploadIdCardView.getContext().getResources().getString(R.string.upload_share_dialog_text);
    }

    public void uploadIdCard(String filePath, final int side) {
        HHApiService apiService = application.getApiService();
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        String fileName = filePath.split("/")[filePath.split("/").length - 1];
        HashMap<String, Object> map = new HashMap<>();
        map.put("side", side);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, RequestBody.create(MULTIPART_FORM_DATA, file));
        subscription = apiService.uploadIdCard(user.student.id, user.session.access_token, body, map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<IdCardUrl>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mUploadIdCardView.showProgressDialog("图片上传中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mUploadIdCardView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUploadIdCardView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(IdCardUrl idCardUrl) {
                        if (side == 0) {
                            mUploadIdCardView.setFaceImage(idCardUrl.url);
                        } else {
                            mUploadIdCardView.setFaceBackImage(idCardUrl.url);
                        }
                    }
                });

    }

    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
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
        Unicorn.openServiceActivity(mUploadIdCardView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }

    public void pageStartCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mUploadIdCardView.getContext(), "upload_id_page_viewed", map);
    }

    public void clickLaterSubmit() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mUploadIdCardView.getContext(), "upload_id_page_cancel_tapped", map);
    }

    public void clickCancelPop() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mUploadIdCardView.getContext(), "upload_id_page_popup_cancel_tapped", map);
    }

    public void clickConfirmPop() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mUploadIdCardView.getContext(), "upload_id_page_popup_confirm_tapped", map);
    }

    public void clickUploadInfo() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mUploadIdCardView.getContext(), "upload_id_page_confirm_tapped", map);
    }

    public void manualUpload(String name, String idCardNumber) {
        final HHApiService apiService = application.getApiService();
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("name", name);
        mapParam.put("number", idCardNumber);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<BaseSuccess>>() {
                    @Override
                    public Observable<BaseSuccess> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.uploadIdCard(user.student.id, mapParam, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseSuccess>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mUploadIdCardView.showProgressDialog("数据验证中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mUploadIdCardView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUploadIdCardView.dismissProgressDialog();
                        mUploadIdCardView.showMessage("身份验证失败，请确认姓名和身份证号码后重试！");
                        if (ErrorUtil.isInvalidSession(e)) {
                            mUploadIdCardView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BaseSuccess baseSuccess) {
                        generateAgreement();
                    }
                });
    }
}
