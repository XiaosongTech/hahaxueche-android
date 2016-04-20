package com.hahaxueche.model.coach;

import com.hahaxueche.model.student.Student;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gibxin on 2016/4/13.
 */
public class ScheduleEvent implements Serializable{
    private String id;
    private String start_time;
    private String end_time;
    private int max_st_count;
    private int registered_st_count;
    private int reviewed_st_count;
    private ArrayList<Student> registered_students;
    private int service_type;
    private int student_phase;
    private Coach coach;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getMax_st_count() {
        return max_st_count;
    }

    public void setMax_st_count(int max_st_count) {
        this.max_st_count = max_st_count;
    }

    public int getRegistered_st_count() {
        return registered_st_count;
    }

    public void setRegistered_st_count(int registered_st_count) {
        this.registered_st_count = registered_st_count;
    }

    public int getReviewed_st_count() {
        return reviewed_st_count;
    }

    public void setReviewed_st_count(int reviewed_st_count) {
        this.reviewed_st_count = reviewed_st_count;
    }

    public ArrayList<Student> getRegistered_students() {
        return registered_students;
    }

    public void setRegistered_students(ArrayList<Student> registered_students) {
        this.registered_students = registered_students;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public int getStudent_phase() {
        return student_phase;
    }

    public void setStudent_phase(int student_phase) {
        this.student_phase = student_phase;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
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
