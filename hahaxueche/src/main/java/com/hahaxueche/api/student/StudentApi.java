package com.hahaxueche.api.student;


import com.hahaxueche.model.activity.Event;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.response.GroupBuyResponse;
import com.hahaxueche.model.response.ReferalHistoryResponse;
import com.hahaxueche.model.response.RefereeListResponse;
import com.hahaxueche.model.response.ScheduleEventListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.BankCard;
import com.hahaxueche.model.student.ReferalBonusSummary;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;

import java.util.ArrayList;

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

    /**
     * 学员提取自己推荐的人
     *
     * @param studentId
     * @param page
     * @param perPage
     * @param accessToken
     * @return
     */
    public RefereeListResponse fetchRefereeList(String studentId, String page, String perPage, String accessToken);

    /**
     * @param url
     * @param accessToken
     * @return
     */
    public RefereeListResponse fetchRefereeList(String url, String accessToken);

    /**
     * 学员提取自己提取奖金的历史
     *
     * @param studentId
     * @param page
     * @param perPage
     * @param accessToken
     * @return
     */
    public ReferalHistoryResponse fetchReferalHistoryList(String studentId, String page, String perPage, String accessToken);

    /**
     * @param url
     * @param accessToken
     * @return
     */
    public ReferalHistoryResponse fetchReferalHistoryList(String url, String accessToken);

    /**
     * 学员提取自己提取奖金的总结
     *
     * @param studentId
     * @param accessToken
     * @return
     */
    public ReferalBonusSummary fetchBonusSummary(String studentId, String accessToken);

    /**
     * 学员提取奖金
     *
     * @param studentId
     * @param account
     * @param accountOwnerName
     * @param amount
     * @return
     */
    public ReferalBonusTransaction withdrawBonus(String studentId, String account, String accountOwnerName, String amount, String accessToken);

    /**
     * 团购报名
     *
     * @param name
     * @param phone
     * @return
     */
    public GroupBuyResponse createGroupBuy(String name, String phone);

    /**
     * 获取活动列表
     *
     * @param cityId
     * @return
     */
    public ArrayList<Event> fetchEventList(String cityId);

    /**
     * 添加银行卡
     *
     * @param name
     * @param cardNumber
     * @param openBankCode
     * @param studentId
     * @param accessToken
     * @return
     */
    public BankCard addBankCard(String name, String cardNumber, String openBankCode, String studentId, String accessToken);

}
