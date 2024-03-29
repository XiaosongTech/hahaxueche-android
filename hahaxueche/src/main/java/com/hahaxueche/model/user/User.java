package com.hahaxueche.model.user;

import android.text.TextUtils;

import com.hahaxueche.model.user.student.Student;

/**
 * Created by wangshirui on 16/9/10.
 */
public class User {
    public String id;
    public String cell_phone;
    public Session session;
    public Student student;

    public boolean isCompleted() {
        return (student != null && student.city_id >= 0 && !TextUtils.isEmpty(student.name));
    }

    public boolean isLogin() {
        return (session != null && !TextUtils.isEmpty(session.access_token) && student != null && !TextUtils.isEmpty(student.id));
    }
}
