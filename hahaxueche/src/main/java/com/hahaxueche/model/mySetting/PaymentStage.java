package com.hahaxueche.model.mySetting;

/**
 * Created by gibxin on 2016/2/29.
 */
public class PaymentStage {
    private String id;
    private String stage_number;
    private String stage_name;
    private String description;
    private String stage_amount;
    private String reviewable;
    private String active;
    private String stage_fee_type;
    private String reviewed;
    private String paid_at;
    private String deleted_at;
    private String ready_for_review;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStage_number() {
        return stage_number;
    }

    public void setStage_number(String stage_number) {
        this.stage_number = stage_number;
    }

    public String getStage_name() {
        return stage_name;
    }

    public void setStage_name(String stage_name) {
        this.stage_name = stage_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStage_amount() {
        return stage_amount;
    }

    public void setStage_amount(String stage_amount) {
        this.stage_amount = stage_amount;
    }

    public String getReviewable() {
        return reviewable;
    }

    public void setReviewable(String reviewable) {
        this.reviewable = reviewable;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getStage_fee_type() {
        return stage_fee_type;
    }

    public void setStage_fee_type(String stage_fee_type) {
        this.stage_fee_type = stage_fee_type;
    }

    public String getReviewed() {
        return reviewed;
    }

    public void setReviewed(String reviewed) {
        this.reviewed = reviewed;
    }

    public String getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(String paid_at) {
        this.paid_at = paid_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getReady_for_review() {
        return ready_for_review;
    }

    public void setReady_for_review(String ready_for_review) {
        this.ready_for_review = ready_for_review;
    }
}
