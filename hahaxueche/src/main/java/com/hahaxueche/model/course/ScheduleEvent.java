package com.hahaxueche.model.course;

import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.coach.Coach;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class ScheduleEvent {
    public String id;
    public String start_time;
    public String end_time;
    public int max_st_count;
    public int registered_st_count;
    public int reviewed_st_count;
    public ArrayList<Student> registered_students;
    public int service_type;
    public int student_phase;
    public String status;
    public Coach coach;
    public boolean isShowDay;
}
