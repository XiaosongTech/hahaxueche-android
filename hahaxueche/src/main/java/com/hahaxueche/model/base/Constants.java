package com.hahaxueche.model.base;

import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.city.City;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量数据模型
 * Created by gibxin on 2016/2/3.
 */
public class Constants {
    private List<City> cities;
    private List<FieldModel> fields;
    private List<BaseKeyValue> skill_levels;
    private ArrayList<String> login_banners;
    private ArrayList<String> home_page_banners;
    private List<BaseKeyValue> service_types;
    private ArrayList<BannerHighlight> banner_highlights;
    private String message;
    private String code;
    private boolean isSuccess = true;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<FieldModel> getFields() {
        return fields;
    }

    public void setFields(List<FieldModel> fields) {
        this.fields = fields;
    }

    public List<BaseKeyValue> getSkill_levels() {
        return skill_levels;
    }

    public void setSkill_levels(List<BaseKeyValue> skill_levels) {
        this.skill_levels = skill_levels;
    }

    public ArrayList<String> getLogin_banners() {
        return login_banners;
    }

    public void setLogin_banners(ArrayList<String> login_banners) {
        this.login_banners = login_banners;
    }

    public ArrayList<String> getHome_page_banners() {
        return home_page_banners;
    }

    public void setHome_page_banners(ArrayList<String> home_page_banners) {
        this.home_page_banners = home_page_banners;
    }

    public List<BaseKeyValue> getService_types() {
        return service_types;
    }

    public void setService_types(List<BaseKeyValue> service_types) {
        this.service_types = service_types;
    }

    public ArrayList<BannerHighlight> getBanner_highlights() {
        return banner_highlights;
    }

    public void setBanner_highlights(ArrayList<BannerHighlight> banner_highlights) {
        this.banner_highlights = banner_highlights;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
