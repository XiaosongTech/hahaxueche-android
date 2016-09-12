package com.hahaxueche.model.user;

import android.text.TextUtils;

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
}
