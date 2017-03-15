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
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
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

public class UploadIdCardPresenter extends HHBasePresenter implements Presenter<UploadIdCardView> {
    private static final MediaType MULTIPART_FORM_DATA = MediaType.parse("multipart/form-data; boundary=__X_PAW_BOUNDARY__");
    private UploadIdCardView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private boolean mIsInsurance;

    public void attachView(UploadIdCardView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        pageStartCount();
        if (mIsInsurance) {
            mView.setUploadHints(mView.getContext().getResources()
                    .getString(R.string.upload_id_card_hints_insurance));
        } else {
            mView.setUploadHints(mView.getContext().getResources()
                    .getString(R.string.upload_id_card_hints));
        }
        User user = application.getSharedPrefUtil().getUser();
        if (user.student.isUploadedIdInfo()) {
            //如果已经上传过用户信息，直接提示提交
            mView.confirmToSubmit(user.student.identity_card.name, user.student.identity_card.num);
        }
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setIsInsurance(boolean isInsurance) {
        mIsInsurance = isInsurance;
    }

    /**
     * 生成协议
     */
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
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<IdCardUrl> response) {
                        if (response.isSuccessful()) {
                            mView.navigateToUserContract(response.body().agreement_url, user.student.id);
                        } else {
                            Retrofit retrofit = HHApiService.Factory.getRetrofit();
                            Converter<ResponseBody, ErrorResponse> errorConverter = retrofit.responseBodyConverter(ErrorResponse.class,
                                    new Annotation[0]);
                            try {
                                ErrorResponse error = errorConverter.convert(response.errorBody());
                                if (error.code == 40034) {
                                    mView.showMessage("未上传身份信息，请上传身份信息后重试！");
                                } else {
                                    mView.showMessage("上传失败, 请重试!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                mView.showMessage("上传失败, 请重试!");
                            }
                        }
                    }
                });
    }

    /**
     * 投保
     */
    public void claimInsurance() {
        HHApiService apiService = application.getApiService();
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        subscription = apiService.claimInsurance(user.student.id, user.session.access_token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Response<Student>>() {
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
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<Student> response) {
                        if (response.isSuccessful()) {
                            application.getSharedPrefUtil().updateStudent(response.body());
                            mView.showShareDialog();
                        } else {
                            Retrofit retrofit = HHApiService.Factory.getRetrofit();
                            Converter<ResponseBody, ErrorResponse> errorConverter = retrofit.responseBodyConverter(ErrorResponse.class,
                                    new Annotation[0]);
                            try {
                                ErrorResponse error = errorConverter.convert(response.errorBody());
                                if (error.code == 40034) {
                                    mView.showMessage("未上传身份信息，请上传身份信息后重试！");
                                } else if (error.code == 40036) {
                                    mView.showMessage("该用户已投保!");
                                } else {
                                    mView.showMessage("投保失败, 请重试!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                mView.showMessage("投保失败, 请重试!");
                            }
                        }
                    }
                });
    }

    public String getShareText() {
        return mView.getContext().getResources().getString(R.string.upload_share_dialog_text);
    }

    /**
     * 直接确认上传
     */
    public void clickSureToSubmit() {
        if (mIsInsurance) {
            claimInsurance();
        } else {
            generateAgreement();
        }
    }

    public void uploadIdCard(String filePath) {
        HHApiService apiService = application.getApiService();
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        String fileName = filePath.split("/")[filePath.split("/").length - 1];
        HashMap<String, Object> map = new HashMap<>();
        map.put("side", 0);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, RequestBody.create(MULTIPART_FORM_DATA, file));
        subscription = apiService.uploadIdCard(user.student.id, user.session.access_token, body, map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Response<IdCardUrl>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog("图片上传中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<IdCardUrl> response) {
                        if (response.isSuccessful()) {
                            mView.setFaceImage(response.body().url);
                        } else {
                            Retrofit retrofit = HHApiService.Factory.getRetrofit();
                            Converter<ResponseBody, ErrorResponse> errorConverter = retrofit.responseBodyConverter(ErrorResponse.class,
                                    new Annotation[0]);
                            try {
                                ErrorResponse error = errorConverter.convert(response.errorBody());
                                if (error.code == 40022) {
                                    mView.showMessage("已上传过身份信息，请直接提交！");
                                } else if (error.code == 40026 || error.code == 40029) {
                                    mView.showMessage("身份证识别失败，请重新拍摄上传或者点击左上角的手动填写！");

                                } else if (error.code == 40028) {
                                    mView.showMessage("身份证信息无效，请确认上传或填写正确后重试!");
                                } else {
                                    mView.showMessage("上传失败, 请重试!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                mView.showMessage("上传失败, 请重试!");
                            }
                        }


                    }
                });

    }

    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void pageStartCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mView.getContext(), "upload_id_page_viewed", map);
    }

    public void clickLaterSubmit() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mView.getContext(), "upload_id_page_cancel_tapped", map);
    }

    public void clickCancelPop() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mView.getContext(), "upload_id_page_popup_cancel_tapped", map);
    }

    public void clickConfirmPop() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mView.getContext(), "upload_id_page_popup_confirm_tapped", map);
    }

    public void clickUploadInfo() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mView.getContext(), "upload_id_page_confirm_tapped", map);
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
                        mView.showProgressDialog("数据验证中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        mView.showMessage("身份验证失败，请确认姓名和身份证号码后重试！");
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BaseSuccess baseSuccess) {
                        if (mIsInsurance) {
                            claimInsurance();
                        } else {
                            generateAgreement();
                        }
                    }
                });
    }
}
