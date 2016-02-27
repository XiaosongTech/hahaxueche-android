package com.hahaxueche.model.findCoach;

import java.io.Serializable;

/**
 * 评论信息
 * Created by gibxin on 2016/2/27.
 */
public class ReviewInfo implements Serializable {
    private String id;
    private Reviewer reviewer;
    private Reviewee reviewee;
    private String comment;
    private String rating;
    private String active;
    private String created_at;
    private String updated_at;
    private String service_type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

    public Reviewee getReviewee() {
        return reviewee;
    }

    public void setReviewee(Reviewee reviewee) {
        this.reviewee = reviewee;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }
}
