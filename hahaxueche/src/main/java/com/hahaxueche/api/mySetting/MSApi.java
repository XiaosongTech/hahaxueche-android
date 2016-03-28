package com.hahaxueche.api.mySetting;


import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.ReviewInfo;
import com.hahaxueche.model.signupLogin.StudentModel;
import com.hahaxueche.model.util.BaseApiResponse;

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

    public StudentModel getStudentForever(String student_id, String access_token );

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

    /**
     * @param coach_user_id
     * @param payment_stage
     * @param rating
     * @param comment
     * @param access_token
     * @return
     */
    public ReviewInfo makeReview(String coach_user_id, String payment_stage, String rating, String comment, String access_token);

    /**
     * @param session_id
     * @param access_token
     * @return
     */
    public BaseApiResponse loginOff(String session_id, String access_token);
}
