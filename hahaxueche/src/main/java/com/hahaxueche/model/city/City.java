package com.hahaxueche.model.city;

import com.hahaxueche.model.course.Course;
import com.hahaxueche.model.student.StudentPhase;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市数据模型
 * Created by gibxin on 2016/1/23.
 */
public class City {
    private String id;
    private String name;
    private String zip_code;
    private String available;
    private FilterModel filters;
    private List<CostItem> fixed_cost_itemizer;
    private ArrayList<StudentPhase> student_phases;
    private ArrayList<Course> courses;
    private ArrayList<Bonus> referal_bonus;
    private String referral_banner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public FilterModel getFilters() {
        return filters;
    }

    public void setFilters(FilterModel filters) {
        this.filters = filters;
    }

    public List<CostItem> getFixed_cost_itemizer() {
        return fixed_cost_itemizer;
    }

    public void setFixed_cost_itemizer(List<CostItem> fixed_cost_itemizer) {
        this.fixed_cost_itemizer = fixed_cost_itemizer;
    }

    public ArrayList<StudentPhase> getStudent_phases() {
        return student_phases;
    }

    public void setStudent_phases(ArrayList<StudentPhase> student_phases) {
        this.student_phases = student_phases;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public ArrayList<Bonus> getReferal_bonus() {
        return referal_bonus;
    }

    public void setReferal_bonus(ArrayList<Bonus> referal_bonus) {
        this.referal_bonus = referal_bonus;
    }

    public String getReferral_banner() {
        return referral_banner;
    }

    public void setReferral_banner(String referral_banner) {
        this.referral_banner = referral_banner;
    }
}
