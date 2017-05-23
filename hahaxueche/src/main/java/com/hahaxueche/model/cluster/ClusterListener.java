package com.hahaxueche.model.cluster;

import com.amap.api.maps.model.Marker;
import com.hahaxueche.model.base.Field;

/**
 * Created by wangshirui on 2017/5/16.
 */

public interface ClusterListener {
    /**
     * 点击聚合点的回调处理函数
     */
    void onClickCluster(Marker marker, Cluster cluster);

    /**
     * 点击训练场坐标点回调处理函数
     *
     * @param field
     */
    void onClickField(Field field, boolean isSelect);

    /**
     * 放大到全城
     */
    void onZoomToCity();

    /**
     * 显示info window
     *
     * @param marker
     */
    void showInfoWindow(Marker marker);

    void setInfoDrivingSchoolNull();
}
