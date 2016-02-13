package com.hahaxueche.model.signupLogin;

/**
 * 城市数据模型
 * Created by gibxin on 2016/1/23.
 */
public class CityModel {
    private String id;
    private String name;
    private String zip_code;
    private String available;
    private FilterModel filters;

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
}
