package com.hahaxueche.model.cluster;

import com.amap.api.maps.model.Marker;

/**
 * Created by wangshirui on 2017/5/16.
 */

public interface ClusterClickListener {
    /**
     * 点击聚合点的回调处理函数
     */
    public void onClick(Marker marker, Cluster cluster);
}
