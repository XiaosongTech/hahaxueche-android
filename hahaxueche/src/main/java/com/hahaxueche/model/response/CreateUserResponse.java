package com.hahaxueche.model.response;

import com.hahaxueche.model.user.SessionModel;
import com.hahaxueche.model.student.StudentModel;

/**
 * Created by gibxin on 2016/1/22.
 */
public class CreateUserResponse {
    private String id;
    private String cell_phone;
    private SessionModel session;
    private StudentModel student;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCell_phone() {
        return cell_phone;
    }

    public void setCell_phone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    public SessionModel getSession() {
        return session;
    }

    public void setSession(SessionModel session) {
        this.session = session;
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
