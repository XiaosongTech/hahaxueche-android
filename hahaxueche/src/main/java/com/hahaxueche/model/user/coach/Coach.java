package com.hahaxueche.model.user.coach;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/27.
 */

public class Coach {
    public String id;
    public String cell_phone;
    public String name;
    public String city_id;
    public String user_id;
    public String avatar;
    public String bio;
    public String review_count;
    public String average_rating;
    public String total_student_count;
    public String active_student_count;
    public String account_balance;
    public String commission_ratio;
    public String experiences;
    public String skill_level;
    public String license_type;
    public String service_type;
    public String satisfaction_rate;
    public String consultant;
    public CoachGroup coach_group;
    public ArrayList<Coach> peer_coaches;
    public ArrayList<String> images;
    public String assigned_coaches;
    public int vip;
    public int like_count;
    public String liked;
    public String driving_school;
}
