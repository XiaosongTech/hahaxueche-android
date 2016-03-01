package com.hahaxueche.api.mySetting;


import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.signupLogin.StudentModel;

/**
 * Created by gibxin on 2016/2/29.
 */
public interface MSApi {
    /**
     * @param student_id
     * @param access_token
     * @return
     */
    public StudentModel getStudent(String student_id, String access_token);

    /**
     * @param page
     * @param per_page
     * @param access_token
     * @return
     */
    public CoachListResponse getFollowCoachList(String page, String per_page, String access_token);

    /**
     * @param url
     * @param access_token
     * @return
     */
    public CoachListResponse getFollowCoachList(String url, String access_token);
}
