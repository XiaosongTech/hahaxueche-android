package com.hahaxueche.model.course;

/**
 * Created by gibxin on 2016/4/12.
 */
public class Course {
    private String id;
    private String course;
    private String display_name;
    private boolean coach_requried;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public boolean isCoach_requried() {
        return coach_requried;
    }

    public void setCoach_requried(boolean coach_requried) {
        this.coach_requried = coach_requried;
    }
}
