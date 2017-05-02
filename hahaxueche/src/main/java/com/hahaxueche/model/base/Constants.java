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
