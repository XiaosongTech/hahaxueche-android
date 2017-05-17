package com.hahaxueche.model.cluster;

import com.amap.api.maps.model.LatLng;
import com.hahaxueche.model.base.Field;

/**
 * Created by wangshirui on 2017/5/17.
 */

public class FieldItem {
    private LatLng mLatLng;
    private Field mField;

    public FieldItem(LatLng latLng, Field field) {
        mLatLng = latLng;
        mField = field;
    }

    public LatLng getPosition() {
        return mLatLng;
    }

    public Field getField() {
        return mField;
    }
}
