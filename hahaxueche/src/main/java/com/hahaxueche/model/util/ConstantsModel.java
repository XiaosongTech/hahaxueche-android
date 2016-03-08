package com.hahaxueche.model.util;

import com.hahaxueche.model.findCoach.FieldModel;
import com.hahaxueche.model.findCoach.FieldsModel;
import com.hahaxueche.model.signupLogin.CitiesModel;
import com.hahaxueche.model.signupLogin.CityModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量数据模型
 * Created by gibxin on 2016/2/3.
 */
public class ConstantsModel {
    private List<CityModel> cities;
    private List<FieldModel> fields;
    private List<BaseKeyValue> skill_levels;
    private ArrayList<String> login_banners;
    private ArrayList<String> home_page_banners;
    private List<BaseKeyValue> service_types;

    public List<CityModel> getCities() {
        return cities;
    }

    public void setCities(List<CityModel> cities) {
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
}
