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

    public City getCity(int cityId) {
        City myCity = cities.get(0);
        for (City city : cities) {
            if (city.id == cityId) {
                myCity = city;
                break;
            }
        }
        return myCity;
    }

    public String getSectionName(String fieldId) {
        String ret = "";
        if (fields == null || fields.size() < 1) return ret;
        for (Field field : fields) {
            if (field.id.equals(fieldId)) {
                ret = getCityName(field.city_id) + field.section;
                break;
            }
        }
        return ret;
    }

    public String getCityName(int cityId) {
        String ret = "";
        if (cities == null || cities.size() < 1) return ret;
        for (City city : cities) {
            if (city.id == cityId) {
                ret = city.name;
                break;
            }
        }
        return ret;
    }

    public Field getField(String fieldId) {
        Field ret = null;
        if (fields != null && fields.size() > 0) {
            for (Field field : fields) {
                if (field.id.equals(fieldId)) {
                    ret = field;
                    break;
                }
            }
        }
        return ret;
    }

    public ArrayList<Field> getFields(int cityId) {
        ArrayList<Field> retFields = new ArrayList<>();
        for (Field field : fields) {
            if (field.city_id == cityId) {
                retFields.add(field);
            }
        }
        return retFields;
    }
}
