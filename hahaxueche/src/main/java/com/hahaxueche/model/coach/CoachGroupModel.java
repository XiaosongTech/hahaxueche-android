package com.hahaxueche.model.coach;

import java.io.Serializable;

/**
 * Created by gibxin on 2016/2/21.
 */
public class CoachGroupModel implements Serializable {
    private String id;
    private String field_id;
    private String unit_training_cost;
    private String training_cost;
    private String other_fee;
    private String deleted_at;
    private String active;
    private String created_at;
    private String updated_at;
    private String name;
    private String market_price;
    private String vip_price;
    private String vip_market_price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getField_id() {
        return field_id;
    }

    public void setField_id(String field_id) {
        this.field_id = field_id;
    }

    public String getUnit_training_cost() {
        return unit_training_cost;
    }

    public void setUnit_training_cost(String unit_training_cost) {
        this.unit_training_cost = unit_training_cost;
    }

    public String getTraining_cost() {
        return training_cost;
    }

    public void setTraining_cost(String training_cost) {
        this.training_cost = training_cost;
    }

    public String getOther_fee() {
        return other_fee;
    }

    public void setOther_fee(String other_fee) {
        this.other_fee = other_fee;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }

    public String getVip_price() {
        return vip_price;
    }

    public void setVip_price(String vip_price) {
        this.vip_price = vip_price;
    }

    public String getVip_market_price() {
        return vip_market_price;
    }

    public void setVip_market_price(String vip_market_price) {
        this.vip_market_price = vip_market_price;
    }
}
