package com.hahaxueche.model.util;

import com.hahaxueche.model.findCoach.FieldModel;
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
    private List<FieldModel> fields;

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
}
