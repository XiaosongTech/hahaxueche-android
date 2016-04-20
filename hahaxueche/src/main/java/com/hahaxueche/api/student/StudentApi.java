package com.hahaxueche.api.student;


import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.response.ScheduleEventListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;

/**
 * Created by gibxin on 2016/2/29.
 */
public interface StudentApi {
    /**
     * @param student_id
     * @param access_token
     * @return
     */
    public Student getStudent(String student_id, String access_token);

    public Student getStudentForever(String student_id, String access_token);

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

    /**
     * 学生预约训练
     *
     * @param studentId
     * @param courseScheduleEventId
     * @param accessToken
     * @return
     */
    public ScheduleEvent bookScheduleEvent(String studentId, String courseScheduleEventId, String accessToken);

    /**
     * 学员提取自己教练的课程
     *
     * @param studentId
     * @param page
     * @param perPage
     * @param booked    0:教练将来的；1:自己已经booked的了 default to 0
     * @return
     */
    public ScheduleEventListResponse fetchCourseSchedule(String studentId, String page, String perPage, String booked, String accessToken);

    public ScheduleEventListResponse fetchCourseSchedule(String url, String accessToken);

    /**
     * 学员取消自己预定的课程
     *
     * @param studentId
     * @param scheduleEventId
     * @param accessToken
     * @return
     */
    public BaseApiResponse cancelCourseSchedule(String studentId, String scheduleEventId, String accessToken);

    /**
     * 学员评价课程
     *
     * @param studentId
     * @param scheduleEventId
     * @param rating
     * @param accessToken
     * @return
     */
    public ReviewInfo reviewSchedule(String studentId, String scheduleEventId, String rating, String accessToken);

}