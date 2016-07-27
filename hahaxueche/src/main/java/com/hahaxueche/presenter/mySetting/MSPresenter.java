package com.hahaxueche.presenter.mySetting;

import com.hahaxueche.model.activity.Event;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.response.GroupBuyResponse;
import com.hahaxueche.model.response.ReferalHistoryResponse;
import com.hahaxueche.model.response.RefereeListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.ReferalBonusSummary;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/2/29.
 */
public interface MSPresenter {
    /**
     * @param student_id
     * @param access_token
     * @param listener
     */
    public void getStudent(String student_id, String access_token, MSCallbackListener<Student> listener);

    public void getStudentForever(String student_id, String access_token, MSCallbackListener<Student> listener);

    /**
     * @param page
     * @param per_page
     * @param access_token
     * @param listener
     */
    public void getFollowCoachList(String page, String per_page, String access_token, MSCallbackListener<CoachListResponse> listener);

    /**
     * @param url
     * @param access_token
     * @param listener
     */
    public void getFollowCoachList(String url, String access_token, MSCallbackListener<CoachListResponse> listener);

    /**
     * @param coach_user_id
     * @param payment_stage
     * @param rating
     * @param comment
     * @param access_token
     * @param listener
     */
    public void makeReview(String coach_user_id, String payment_stage, String rating, String comment,
                           String access_token, MSCallbackListener<ReviewInfo> listener);

    /**
     * @param session_id
     * @param access_token
     * @param listener
     */
    public void loginOff(String session_id, String access_token, MSCallbackListener<BaseApiResponse> listener);

    /**
     * @param studentId
     * @param accessToken
     * @param filePath
     * @param listener
     */
    public void uploadAvatar(String studentId, String accessToken, String filePath, MSCallbackListener<Student> listener);

    /**
     * @param studentId
     * @param page
     * @param perPage
     * @param accessToken
     * @param listener
     */
    public void fetchRefereeList(String studentId, String page, String perPage, String accessToken, MSCallbackListener<RefereeListResponse> listener);

    /**
     * @param url
     * @param accessToken
     * @param listener
     */
    public void fetchRefereeList(String url, String accessToken, MSCallbackListener<RefereeListResponse> listener);

    /**
     * @param studentId
     * @param page
     * @param perPage
     * @param accessToken
     * @param listener
     */
    public void fetchReferalHistoryList(String studentId, String page, String perPage, String accessToken, MSCallbackListener<ReferalHistoryResponse> listener);

    /**
     * @param url
     * @param accessToken
     * @param listener
     */
    public void fetchReferalHistoryList(String url, String accessToken, MSCallbackListener<ReferalHistoryResponse> listener);

    /**
     * @param studentId
     * @param accessToken
     */
    public void fetchBonusSummary(String studentId, String accessToken, MSCallbackListener<ReferalBonusSummary> listener);

    /**
     * @param studentId
     * @param account
     * @param accountOwnerName
     * @param amount
     * @param accessToken
     * @param listener
     */
    public void withdrawBonus(String studentId, String account, String accountOwnerName, String amount, String accessToken, MSCallbackListener<ReferalBonusTransaction> listener);

    /**
     * @param name
     * @param phone
     * @param listener
     */
    public void createGroupBuy(String name, String phone, MSCallbackListener<GroupBuyResponse> listener);

    public void editUsername(String studentId, String cityId, String username, String accessToken, MSCallbackListener<Student> listener);

    /**
     * 获取活动列表
     *
     * @param cityId
     * @param listener
     */
    public void fetchEventList(String cityId, MSCallbackListener<ArrayList<Event>> listener);
}
