package com.hahaxueche.model.coach;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gibxin on 2016/2/13.
 */
public class CoachModel implements Serializable {
    private String id;
    private String cell_phone;
    private String name;
    private String city_id;
    private String user_id;
    private String avatar;
    private String bio;
    private String review_count;
    private String average_rating;
    private String total_student_count;
    private String active_student_count;
    private String account_balance;
    private String commission_ratio;
    private String experiences;
    private String skill_level;
    private String license_type;
    private String service_type;
    private String satisfaction_rate;
    private String consultant;
    private CoachGroupModel coach_group;
    private List<BriefCoachInfo> peer_coaches;
    private List<String> images;
    private String assigned_coaches;
    private String code;
    private String message;
    private boolean isSuccess;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public String getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(String average_rating) {
        this.average_rating = average_rating;
    }

    public String getTotal_student_count() {
        return total_student_count;
    }

    public void setTotal_student_count(String total_student_count) {
        this.total_student_count = total_student_count;
    }

    public String getActive_student_count() {
        return active_student_count;
    }

    public void setActive_student_count(String active_student_count) {
        this.active_student_count = active_student_count;
    }

    public String getAccount_balance() {
        return account_balance;
    }

    public void setAccount_balance(String account_balance) {
        this.account_balance = account_balance;
    }

    public String getCommission_ratio() {
        return commission_ratio;
    }

    public void setCommission_ratio(String commission_ratio) {
        this.commission_ratio = commission_ratio;
    }

    public String getExperiences() {
        return experiences;
    }

    public void setExperiences(String experiences) {
        this.experiences = experiences;
    }

    public String getSkill_level() {
        return skill_level;
    }

    public void setSkill_level(String skill_level) {
        this.skill_level = skill_level;
    }

    public String getLicense_type() {
        return license_type;
    }

    public void setLicense_type(String license_type) {
        this.license_type = license_type;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getSatisfaction_rate() {
        return satisfaction_rate;
    }

    public void setSatisfaction_rate(String satisfaction_rate) {
        this.satisfaction_rate = satisfaction_rate;
    }

    public String getConsultant() {
        return consultant;
    }

    public void setConsultant(String consultant) {
        this.consultant = consultant;
    }

    public CoachGroupModel getCoach_group() {
        return coach_group;
    }

    public void setCoach_group(CoachGroupModel coach_group) {
        this.coach_group = coach_group;
    }

    public List<BriefCoachInfo> getPeer_coaches() {
        return peer_coaches;
    }

    public void setPeer_coaches(List<BriefCoachInfo> peer_coaches) {
        this.peer_coaches = peer_coaches;
    }

    public String getAssigned_coaches() {
        return assigned_coaches;
    }

    public void setAssigned_coaches(String assigned_coaches) {
        this.assigned_coaches = assigned_coaches;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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
