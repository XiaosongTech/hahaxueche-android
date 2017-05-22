package com.hahaxueche.model.cluster;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshirui on 2017/5/17.
 */

public class ClusterOverlay implements AMap.OnCameraChangeListener,
        AMap.OnMarkerClickListener, AMap.OnMapLoadedListener {
    private AMap mAMap;
    private Context mContext;
    private List<FieldItem> mFieldItems;
    private List<Cluster> mClusters;
    private ClusterListener mClusterListener;
    private ClusterRender mClusterRender;
    private List<Marker> mAddMarkers = new ArrayList<>();
    private float mCurrentZoomLevel = -1;
    private Field mSelectField;
    private Marker mSelectFieldMarker;
    private boolean mIsPoint = false;

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param amap
     * @param fieldItems
     * @param context
     */
    public ClusterOverlay(AMap amap, List<FieldItem> fieldItems, Context context, boolean isInitLoadPoint) {
        if (fieldItems != null) {
            mFieldItems = fieldItems;
        } else {
            mFieldItems = new ArrayList<>();
        }
        mContext = context;
        mClusters = new ArrayList<>();
        this.mAMap = amap;
        mIsPoint = isInitLoadPoint;
        amap.clear();
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        amap.setOnMapLoadedListener(this);
    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterListener
     */
    public void setClusterListener(ClusterListener clusterListener) {
        mClusterListener = clusterListener;
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    public void onDestroy() {
        for (Marker marker : mAddMarkers) {
            marker.remove();

        }
        mAddMarkers.clear();
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
        //zoom改变时，再计算clusters
        if (mCurrentZoomLevel != mAMap.getCameraPosition().zoom) {
            if (mIsPoint && mAMap.getCameraPosition().zoom <= Common.CLUSTER_MAX_ZOOM_LEVEL) {
                //当前是点状态，并且zoom到小于聚合的范围了，进行聚合
                if (mSelectFieldMarker != null) {
                    selectMarker(mSelectFieldMarker, false);
                    Cluster cluster = (Cluster) mSelectFieldMarker.getObject();
                    Field field = cluster.getFieldItems().get(0).getField();
                    mClusterListener.onClickField(field, false);
                    mSelectFieldMarker = null;
                    mSelectField = null;
                }
                mIsPoint = false;
                //calculateClusters();
                mClusterListener.onZoomToCity();
            } else if (!mIsPoint && mAMap.getCameraPosition().zoom > Common.CLUSTER_MAX_ZOOM_LEVEL) {
                //当前是聚合状态，并且放大到聚合范围了，显示成点
                mIsPoint = true;
                calculateClusters();
            }
            mCurrentZoomLevel = mAMap.getCameraPosition().zoom;
        }
    }

    public void calculateClusters() {
        mClusters.clear();
        for (FieldItem fieldItem : mFieldItems) {
            Cluster cluster;
            if (mIsPoint) {
                cluster = new Cluster(fieldItem, true);
                mClusters.add(cluster);
            } else {
                cluster = getCluster(fieldItem, mClusters);
                if (cluster == null) {
                    cluster = new Cluster(fieldItem, false);
                    mClusters.add(cluster);
                }
            }
            cluster.addFieldItem(fieldItem);
        }
        //1.先删掉之前的marker点
        removeAllMarkers();
        //2.再添加新的marker
        for (Cluster cluster : mClusters) {
            addSingleClusterToMap(cluster);
        }
    }

    public void moveCamera() {
        if (mClusters.size() < 1) return;
        if (mClusters.size() == 1) {
            //只有一个点的时候
            Field field = mClusters.get(0).getFieldItems().get(0).getField();
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(field.lat, field.lng), 14));
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Cluster cluster : mClusters) {
            if (mIsPoint) {
                Field field = cluster.getFieldItems().get(0).getField();
                builder.include(new LatLng(field.lat, field.lng));
            } else {
                builder.include(cluster.getCenterLatLng());
            }
        }
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), Utils.instence(mContext).dip2px(20)));
    }

    private void removeAllMarkers() {
        if (mAddMarkers == null || mAddMarkers.size() < 1)
            return;
        for (Marker marker : mAddMarkers) {
            marker.remove();
        }
    }

    private AlphaAnimation mADDAnimation = new AlphaAnimation(0, 1);

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster) {
        LatLng latlng = cluster.getCenterLatLng();
        MarkerOptions markerOptions = new MarkerOptions();
        boolean isSelectedField = false;
        if (cluster.isFieldPoint()) {
            Field field = cluster.getFieldItems().get(0).getField();
            if (mSelectField != null && mSelectField.id.equals(field.id)) {
                isSelectedField = true;
            }
            markerOptions.anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(mContext.getResources(),
                                    R.drawable.ic_map_local_choseon))).position(latlng);
            markerOptions.title(field.name).snippet(field.display_address);
        } else {
            markerOptions.anchor(0.5f, 0.5f)
                    .icon(getBitmapDes(cluster)).position(latlng);
        }
        Marker marker = mAMap.addMarker(markerOptions);
        marker.setAnimation(mADDAnimation);
        marker.setObject(cluster);
        marker.startAnimation();
        cluster.setMarker(marker);
        if (isSelectedField) {
            selectMarker(marker, true);
        }
        mAddMarkers.add(marker);
    }

    private void selectMarker(Marker marker, boolean isSelect) {
        if (isSelect) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(mContext.getResources(),
                            R.drawable.ic_map_local_choseonly)));
            mClusterListener.showInfoWindow(marker);
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(mContext.getResources(),
                            R.drawable.ic_map_local_choseon)));
            marker.hideInfoWindow();
        }
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param fieldItem
     * @return
     */
    private Cluster getCluster(FieldItem fieldItem, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            if (!cluster.isFieldPoint() && cluster.getZoneName().contains(fieldItem.getField().zone)) {
                return cluster;
            }
        }
        return null;
    }

    /**
     * 获取每个聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(Cluster cluster) {
        String zoneName = cluster.getZoneName();
        int num = cluster.getFieldCount();
        TextView textView = new TextView(mContext);
        textView.setText(zoneName + "\n" + num);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.haha_white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        if (mClusterRender != null && mClusterRender.getDrawAble(num, zoneName, cluster.isFieldPoint()) != null) {
            textView.setBackgroundDrawable(mClusterRender.getDrawAble(num, zoneName, cluster.isFieldPoint()));
        }
        return BitmapDescriptorFactory.fromView(textView);
    }

    //点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        Cluster cluster = (Cluster) marker.getObject();
        if (cluster != null) {
            if (cluster.isFieldPoint()) {
                Field field = cluster.getFieldItems().get(0).getField();
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(field.lat, field.lng), 14));
                if (mSelectField != null && mSelectField.id.equals(field.id)) {
                    mSelectField = null;
                } else {
                    mSelectField = field;
                    if (mSelectFieldMarker != null) {
                        mSelectFieldMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeResource(mContext.getResources(),
                                        R.drawable.ic_map_local_choseon)));
                    }
                    mSelectFieldMarker = marker;
                }
                boolean isSelect = mSelectField != null && mSelectField.id.equals(field.id);
                selectMarker(marker, isSelect);
                mClusterListener.onClickField(field, isSelect);
            } else {
                mClusterListener.onClickCluster(marker, cluster);
            }
            return true;
        }
        return false;
    }


    @Override
    public void onMapLoaded() {
        mCurrentZoomLevel = mAMap.getCameraPosition().zoom;
    }
}
