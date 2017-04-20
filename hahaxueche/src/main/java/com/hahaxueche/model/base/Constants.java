package com.hahaxueche.model.base;

import android.text.TextUtils;

import com.hahaxueche.model.payment.InsurancePrices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangshirui on 16/9/8.
 */
public class Constants {
    public ArrayList<City> cities;
    public List<Field> fields;
    public ArrayList<BaseItemType> license_types;
    public ArrayList<BaseItemType> service_types;
    public ArrayList<BaseItemType> product_types;
    public ArrayList<BaseItemType> skill_levels;
    public ArrayList<SortType> sort_by_keywords;
    public ArrayList<Banner> new_login_banners;
    public ArrayList<Banner> new_home_page_banners;
    public ArrayList<BaseItemType> schedule_course_status;
    public ArrayList<BannerHighlight> banner_highlights;
    public String version_name;
    public int version_code;
    public ArrayList<Bank> banks;
    public Statistics statistics;
    public ArrayList<ArticleCategory> article_categories;
    public String apk_download_url;
    public ArrayList<HashMap<String, String>> marketing_channels;
    public InsurancePrices insurance_prices;

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

    public String getCitySectionName(String fieldId) {
        String ret = "";
        if (fields == null || fields.size() < 1) return ret;
        for (Field field : fields) {
            if (field.id.equals(fieldId)) {
                ret = getCityName(field.city_id) +
                        (TextUtils.isEmpty(field.section) ? field.zone : field.section);
                break;
            }
        }
        return ret;
    }

    public String getSectionName(String fieldId) {
        String ret = "";
        if (fields == null || fields.size() < 1) return ret;
        for (Field field : fields) {
            if (field.id.equals(fieldId)) {
                ret = (TextUtils.isEmpty(field.section) ? field.zone : field.section);
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

    public String getCourseName(int cityId, int courseId) {
        String ret = "";
        City city = getCity(cityId);
        for (Course course : city.courses) {
            if (course.id == courseId) {
                ret = course.display_name;
                break;
            }
        }
        return ret;
    }

    public String getChannelIdByShareType(int shareType) {
        String channelName = "APP";
        if (shareType == 0 || shareType == 1) {
            channelName = "微信";
        } else if (shareType == 2 || shareType == 4) {
            channelName = "QQ";
        } else if (shareType == 3) {
            channelName = "微博";
        } else if (shareType == 5) {
            channelName = "短信";
        }
        String ret = "";
        for (HashMap<String, String> map : marketing_channels) {
            if (map.containsKey(channelName)) {
                ret = map.get(channelName);
                break;
            }
        }
        return ret;
    }
}
