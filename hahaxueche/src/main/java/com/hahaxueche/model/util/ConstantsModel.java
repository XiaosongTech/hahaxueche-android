package com.hahaxueche.model.util;

import com.hahaxueche.model.findCoach.FieldsModel;
import com.hahaxueche.model.signupLogin.CitiesModel;
import com.hahaxueche.model.signupLogin.CityModel;

import java.util.List;

/**
 * 常量数据模型
 * Created by gibxin on 2016/2/3.
 */
public class ConstantsModel {
    private List<CityModel> cities;
    private List<FieldsModel> fields;

    public List<CityModel> getCities() {
        return cities;
    }

    public void setCities(List<CityModel> cities) {
        this.cities = cities;
    }

    public List<FieldsModel> getFields() {
        return fields;
    }

    public void setFields(List<FieldsModel> fields) {
        this.fields = fields;
    }
}
