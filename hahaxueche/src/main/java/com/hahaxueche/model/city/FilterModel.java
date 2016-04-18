package com.hahaxueche.model.city;

import java.util.List;

/**
 * 筛选条件数据模型
 * Created by gibxin on 2016/2/13.
 */
public class FilterModel {
    List<String> prices;
    List<String> radius;

    public List<String> getPrices() {
        return prices;
    }

    public void setPrices(List<String> prices) {
        this.prices = prices;
    }

    public List<String> getRadius() {
        return radius;
    }

    public void setRadius(List<String> radius) {
        this.radius = radius;
    }
}
