package com.hahaxueche.presenter.mySetting;

import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.signupLogin.StudentModel;

/**
 * Created by gibxin on 2016/2/29.
 */
public interface MSPresenter {
    /**
     * @param student_id
     * @param access_token
     * @param listener
     */
    public void getStudent(String student_id, String access_token, MSCallbackListener<StudentModel> listener);

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
}
