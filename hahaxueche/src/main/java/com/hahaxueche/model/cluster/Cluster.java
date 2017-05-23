package com.hahaxueche.model.cluster;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshirui on 2017/5/17.
 */

public class Cluster {
    //中心点坐标
    private LatLng mLatLng;
    //聚合点名称（行政区名）
    private String mZoneName;
    //包含的坐标点
    private List<FieldItem> mFieldItems;
    //是否为训练场坐标
    private boolean mIsFieldPoint;
    private Marker mMarker;

    Cluster(FieldItem fieldItem, boolean isFieldPoint) {
        if (isFieldPoint) {
            mLatLng = new LatLng(fieldItem.getField().lat, fieldItem.getField().lng);
        } else {
            mLatLng = new LatLng(fieldItem.getField().zone_center_lat, fieldItem.getField().zone_center_lng);
        }
        mZoneName = fieldItem.getField().zone;
        mIsFieldPoint = isFieldPoint;
        mFieldItems = new ArrayList<>();
    }

    void addFieldItem(FieldItem fieldItem) {
        mFieldItems.add(fieldItem);
    }

    int getFieldCount() {
        return mFieldItems.size();
    }

    LatLng getCenterLatLng() {
        return mLatLng;
    }

    void setMarker(Marker marker) {
        mMarker = marker;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public List<FieldItem> getFieldItems() {
        return mFieldItems;
    }

    public String getZoneName() {
        return mZoneName;
    }

    public boolean isFieldPoint() {
        return mIsFieldPoint;
    }
}
