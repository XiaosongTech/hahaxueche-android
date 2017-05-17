package com.hahaxueche.model.cluster;

import android.graphics.drawable.Drawable;

/**
 * Created by wangshirui on 2017/5/16.
 */

public interface ClusterRender {
    /**
     * 根据聚合点的元素数目返回渲染背景样式
     *
     * @param clusterNum
     * @return
     */
    Drawable getDrawAble(int clusterNum, String clusterName, boolean isFieldPoint);
}
