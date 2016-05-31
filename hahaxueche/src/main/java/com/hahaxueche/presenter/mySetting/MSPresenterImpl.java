package com.hahaxueche.presenter.mySetting;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hahaxueche.api.auth.AuthApi;
import com.hahaxueche.api.auth.AuthApiImpl;
import com.hahaxueche.api.student.StudentApi;
import com.hahaxueche.api.student.StudentApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.response.GroupBuyResponse;
import com.hahaxueche.model.response.ReferalHistoryResponse;
import com.hahaxueche.model.response.RefereeListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.ReferalBonusSummary;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.presenter.util.ErrorEvent;

/**
 * Created by gibxin on 2016/2/29.
 */
public class MSPresenterImpl implements MSPresenter {
    private Context context;
    private StudentApi studentApi;
    private AuthApi authApi;

    public MSPresenterImpl(Context context) {
        this.context = context;
        this.studentApi = new StudentApiImpl();
        this.authApi = new AuthApiImpl();
    }

    @Override
    public void getStudent(final String student_id, final String access_token, final MSCallbackListener<Student> listener) {
        new AsyncTask<Void, Void, Student>() {

            @Override
            protected Student doInBackground(Void... params) {
                return studentApi.getStudent(student_id, access_token);
            }

            @Override
            protected void onPostExecute(Student student) {
                if (student != null) {
                    if (student.isSuccess()) {
                        listener.onSuccess(student);
                    } else {
                        listener.onFailure(student.getCode(), student.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getStudentForever(final String student_id, final String access_token, final MSCallbackListener<Student> listener) {
        new AsyncTask<Void, Void, Student>() {

            @Override
            protected Student doInBackground(Void... params) {
                return studentApi.getStudentForever(student_id, access_token);
            }

            @Override
            protected void onPostExecute(Student student) {
                if (student != null) {
                    if (student.isSuccess()) {
                        listener.onSuccess(student);
                    } else {
                        listener.onFailure(student.getCode(), student.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getFollowCoachList(final String page, final String per_page, final String access_token, final MSCallbackListener<CoachListResponse> listener) {
        new AsyncTask<Void, Void, CoachListResponse>() {

            @Override
            protected CoachListResponse doInBackground(Void... params) {
                return studentApi.getFollowCoachList(page, per_page, access_token);
            }

            @Override
            protected void onPostExecute(CoachListResponse coachListResponse) {
                if (listener != null) {
                    if (coachListResponse.isSuccess()) {
                        listener.onSuccess(coachListResponse);
                    } else {
                        listener.onFailure(coachListResponse.getCode(), coachListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getFollowCoachList(final String url, final String access_token, final MSCallbackListener<CoachListResponse> listener) {
        new AsyncTask<Void, Void, CoachListResponse>() {

            @Override
            protected CoachListResponse doInBackground(Void... params) {
                return studentApi.getFollowCoachList(url, access_token);
            }

            @Override
            protected void onPostExecute(CoachListResponse coachListResponse) {
                if (listener != null) {
                    if (coachListResponse.isSuccess()) {
                        listener.onSuccess(coachListResponse);
                    } else {
                        listener.onFailure(coachListResponse.getCode(), coachListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void makeReview(final String coach_user_id, final String payment_stage, final String rating, final String comment, final String access_token,
                           final MSCallbackListener<ReviewInfo> listener) {
        new AsyncTask<Void, Void, ReviewInfo>() {

            @Override
            protected ReviewInfo doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);//ping++有延迟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return studentApi.makeReview(coach_user_id, payment_stage, rating, comment, access_token);
            }

            @Override
            protected void onPostExecute(ReviewInfo reviewInfo) {
                if (listener != null) {
                    if (reviewInfo.isSuccess()) {
                        listener.onSuccess(reviewInfo);
                    } else {
                        listener.onFailure(reviewInfo.getCode(), reviewInfo.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void loginOff(final String session_id, final String access_token, final MSCallbackListener<BaseApiResponse> listener) {
        new AsyncTask<Void, Void, BaseApiResponse>() {

            @Override
            protected BaseApiResponse doInBackground(Void... params) {
                return studentApi.loginOff(session_id, access_token);
            }

            @Override
            protected void onPostExecute(BaseApiResponse baseApiResponse) {
                if (listener != null) {
                    if (baseApiResponse.isSuccess()) {
                        listener.onSuccess(baseApiResponse);
                    } else {
                        listener.onFailure(baseApiResponse.getCode(), baseApiResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void uploadAvatar(final String studentId, final String accessToken, final String filePath, final MSCallbackListener<Student> listener) {
        if (TextUtils.isEmpty(filePath)) {
            listener.onFailure("000", "亲，请上传头像哦~");
            return;
        }
        new AsyncTask<Void, Void, Student>() {

            @Override
            protected Student doInBackground(Void... params) {
                return authApi.uploadAvatar(accessToken, filePath, studentId);
            }

            @Override
            protected void onPostExecute(Student student) {
                if (listener != null) {
                    if (student.isSuccess()) {
                        listener.onSuccess(student);
                    } else {
                        listener.onFailure(student.getCode(), student.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void fetchRefereeList(final String studentId, final String page, final String perPage, final String accessToken, final MSCallbackListener<RefereeListResponse> listener) {
        new AsyncTask<Void, Void, RefereeListResponse>() {

            @Override
            protected RefereeListResponse doInBackground(Void... params) {
                return studentApi.fetchRefereeList(studentId, page, perPage, accessToken);
            }

            @Override
            protected void onPostExecute(RefereeListResponse refereeListResponse) {
                if (listener != null) {
                    if (refereeListResponse.isSuccess()) {
                        listener.onSuccess(refereeListResponse);
                    } else {
                        listener.onFailure(refereeListResponse.getCode(), refereeListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void fetchRefereeList(final String url, final String accessToken, final MSCallbackListener<RefereeListResponse> listener) {
        new AsyncTask<Void, Void, RefereeListResponse>() {

            @Override
            protected RefereeListResponse doInBackground(Void... params) {
                return studentApi.fetchRefereeList(url, accessToken);
            }

            @Override
            protected void onPostExecute(RefereeListResponse refereeListResponse) {
                if (listener != null) {
                    if (refereeListResponse.isSuccess()) {
                        listener.onSuccess(refereeListResponse);
                    } else {
                        listener.onFailure(refereeListResponse.getCode(), refereeListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void fetchReferalHistoryList(final String studentId, final String page, final String perPage, final String accessToken, final MSCallbackListener<ReferalHistoryResponse> listener) {
        new AsyncTask<Void, Void, ReferalHistoryResponse>() {

            @Override
            protected ReferalHistoryResponse doInBackground(Void... params) {
                return studentApi.fetchReferalHistoryList(studentId, page, perPage, accessToken);
            }

            @Override
            protected void onPostExecute(ReferalHistoryResponse referalHistoryResponse) {
                if (listener != null) {
                    if (referalHistoryResponse.isSuccess()) {
                        listener.onSuccess(referalHistoryResponse);
                    } else {
                        listener.onFailure(referalHistoryResponse.getCode(), referalHistoryResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void fetchReferalHistoryList(final String url, final String accessToken, final MSCallbackListener<ReferalHistoryResponse> listener) {
        new AsyncTask<Void, Void, ReferalHistoryResponse>() {

            @Override
            protected ReferalHistoryResponse doInBackground(Void... params) {
                return studentApi.fetchReferalHistoryList(url, accessToken);
            }

            @Override
            protected void onPostExecute(ReferalHistoryResponse referalHistoryResponse) {
                if (listener != null) {
                    if (referalHistoryResponse.isSuccess()) {
                        listener.onSuccess(referalHistoryResponse);
                    } else {
                        listener.onFailure(referalHistoryResponse.getCode(), referalHistoryResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void fetchBonusSummary(final String studentId, final String accessToken, final MSCallbackListener<ReferalBonusSummary> listener) {
        new AsyncTask<Void, Void, ReferalBonusSummary>() {

            @Override
            protected ReferalBonusSummary doInBackground(Void... params) {
                return studentApi.fetchBonusSummary(studentId, accessToken);
            }

            @Override
            protected void onPostExecute(ReferalBonusSummary referalBonusSummary) {
                if (listener != null) {
                    if (referalBonusSummary.isSuccess()) {
                        listener.onSuccess(referalBonusSummary);
                    } else {
                        listener.onFailure(referalBonusSummary.getCode(), referalBonusSummary.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void withdrawBonus(final String studentId, final String account, final String accountOwnerName, final String amount, final String accessToken, final MSCallbackListener<ReferalBonusTransaction> listener) {
        if (TextUtils.isEmpty(account)) {
            listener.onFailure(ErrorEvent.PARAM_NULL, "支付宝账号不能为空！");
            return;
        } else if (TextUtils.isEmpty(accountOwnerName)) {
            listener.onFailure(ErrorEvent.PARAM_NULL, "姓名不能为空！");
            return;
        } else if (TextUtils.isEmpty(amount)) {
            listener.onFailure(ErrorEvent.PARAM_NULL, "提现金额不能为空！");
            return;
        } else if (Double.parseDouble(amount) <= 0d) {
            listener.onFailure(ErrorEvent.PARAM_NULL, "提现金额必须大于0！");
            return;
        }
        new AsyncTask<Void, Void, ReferalBonusTransaction>() {

            @Override
            protected ReferalBonusTransaction doInBackground(Void... params) {
                return studentApi.withdrawBonus(studentId, account, accountOwnerName, amount, accessToken);
            }

            @Override
            protected void onPostExecute(ReferalBonusTransaction referalBonusTransaction) {
                if (listener != null) {
                    if (referalBonusTransaction.isSuccess()) {
                        listener.onSuccess(referalBonusTransaction);
                    } else {
                        listener.onFailure(referalBonusTransaction.getCode(), referalBonusTransaction.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void createGroupBuy(final String name, final String phone, final MSCallbackListener<GroupBuyResponse> listener) {
        new AsyncTask<Void, Void, GroupBuyResponse>() {

            @Override
            protected GroupBuyResponse doInBackground(Void... params) {
                return studentApi.createGroupBuy(name, phone);
            }

            @Override
            protected void onPostExecute(GroupBuyResponse groupBuyResponse) {
                if (listener != null) {
                    if (groupBuyResponse.isSuccess()) {
                        listener.onSuccess(groupBuyResponse);
                    } else {
                        listener.onFailure(groupBuyResponse.getCode(), groupBuyResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void editUsername(final String studentId, final String cityId, final String username, final String accessToken, final MSCallbackListener<Student> listener) {
        new AsyncTask<Void, Void, Student>() {

            @Override
            protected Student doInBackground(Void... params) {
                return authApi.completeStuInfo(studentId, cityId, username, accessToken);
            }

            @Override
            protected void onPostExecute(Student student) {
                if (listener != null) {
                    if (student.isSuccess()) {
                        listener.onSuccess(student);
                    } else {
                        listener.onFailure(student.getCode(), student.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }
}
