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
import com.amap.api.maps.model.animation.Animation;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshirui on 2017/5/17.
 */

public class ClusterOverlay implements AMap.OnCameraChangeListener,
        AMap.OnMarkerClickListener {
    private AMap mAMap;
    private Context mContext;
    private List<FieldItem> mFieldItems;
    private List<Cluster> mClusters;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private List<Marker> mAddMarkers = new ArrayList<>();
    private float maxZoomLevel = 12;
    private Field mSelectField;

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param amap
     * @param fieldItems
     * @param context
     */
    public ClusterOverlay(AMap amap, List<FieldItem> fieldItems, Context context) {
        if (fieldItems != null) {
            mFieldItems = fieldItems;
        } else {
            mFieldItems = new ArrayList<>();
        }
        mContext = context;
        mClusters = new ArrayList<>();
        this.mAMap = amap;
        amap.clear();
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        assignClusters();
    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
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
        assignClusters();
    }

    //点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mClusterClickListener == null) {
            return true;
        }
        Cluster cluster = (Cluster) marker.getObject();
        if (cluster != null) {
            if (cluster.isFieldPoint()) {
                Field field = cluster.getFieldItems().get(0).getField();
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(field.lat, field.lng), 14));
                if (mSelectField != null && mSelectField.id.equals(field.id)) {
                    mSelectField = null;
                } else {
                    mSelectField = field;
                }
                boolean isSelect = mSelectField != null && mSelectField.id.equals(field.id);
                selectMarker(marker, isSelect);
                mClusterClickListener.onClickField(field, isSelect);
            } else {
                mClusterClickListener.onClickCluster(marker, cluster);
            }
            return true;
        }
        return false;
    }

    /**
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {
        //1.先删掉之前的marker点
        ArrayList<Marker> removeMarkers = new ArrayList<>();
        removeMarkers.addAll(mAddMarkers);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }
        //2.再添加新的marker
        for (Cluster cluster : clusters) {
            addSingleClusterToMap(cluster);
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
        final Marker marker = mAMap.addMarker(markerOptions);
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
            marker.showInfoWindow();
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(mContext.getResources(),
                            R.drawable.ic_map_local_choseon)));
            marker.hideInfoWindow();
        }
    }

    private void calculateClusters() {
        mClusters.clear();
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        for (FieldItem fieldItem : mFieldItems) {
            LatLng latlng = fieldItem.getPosition();
            if (visibleBounds.contains(latlng)) {
                Cluster cluster;
                if (mAMap.getCameraPosition().zoom > maxZoomLevel) {
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
        }

        //复制一份数据，规避同步
        addClusterToMap(mClusters);
    }

    /**
     * 对点进行聚合
     */
    private void assignClusters() {
        calculateClusters();
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
        textView.setText(zoneName + "\n" + num + "个");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.haha_white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        if (mClusterRender != null && mClusterRender.getDrawAble(num, zoneName, cluster.isFieldPoint()) != null) {
            textView.setBackgroundDrawable(mClusterRender.getDrawAble(num, zoneName, cluster.isFieldPoint()));
        }
        return BitmapDescriptorFactory.fromView(textView);
    }

    //-----------------------辅助内部类用---------------------------------------------

    /**
     * marker渐变动画，动画结束后将Marker删除
     */
    class MyAnimationListener implements Animation.AnimationListener {
        private List<Marker> mRemoveMarkers;

        MyAnimationListener(List<Marker> removeMarkers) {
            mRemoveMarkers = removeMarkers;
        }

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {
            for (Marker marker : mRemoveMarkers) {
                marker.remove();
            }
            mRemoveMarkers.clear();
        }
    }
}
