package com.hahaxueche.model.base;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/8.
 */
public class Constants {
    public ArrayList<City> cities;
    public ArrayList<Field> fields;
    public ArrayList<BaseItemType> license_types;
    public ArrayList<BaseItemType> service_types;
    public ArrayList<BaseItemType> product_types;
    public ArrayList<BaseItemType> skill_levels;
    public ArrayList<SortType> sort_by_keywords;
    public ArrayList<Banner> new_login_banners;
    public ArrayList<Banner> new_home_page_banners;
    public ArrayList<BaseItemType> schedule_course_status;
    public String version_name;
    public int version_code;
    public ArrayList<Bank> banks;
    public Statistics statistics;

    public City getMyCity(int cityId) {
        City myCity = cities.get(0);
        for (City city : cities) {
            if (city.id == cityId) {
                myCity = city;
                break;
            }
        }
        return myCity;
    }
}
