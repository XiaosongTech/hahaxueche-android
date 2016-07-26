package com.hahaxueche.model.activity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangshirui on 16/7/26.
 */
public class Event implements Serializable {
    private String city_id;
    private String event_type;
    private String title;
    private String icon;
    private String url;
    private String end_date;
    private String countDownText;

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCountDownText() {
        return countDownText;
    }

    public void setCountDownText(String countDownText) {
        this.countDownText = countDownText;
    }

    public void parseCountDownText() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ret = "";
        try {
            Date endTime = dayFormat.parse(end_date + " 23:59:59");
            long diff = endTime.getTime() - new Date().getTime();
            if (diff < 0) {
                ret = "已截止";
            } else {
                long day = diff / (24 * 60 * 60 * 1000);
                long hour = (diff / (60 * 60 * 1000) - day * 24);
                long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                ret = day + "天" + hour + "时" + min + "分" + s + "秒";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            countDownText = ret;
        }
    }
}
