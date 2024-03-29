package com.hahaxueche.ui.dialog.findCoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yellowlgx on 2015/8/12.
 */
public class MapDialog extends AlertDialog implements AMap.OnMarkerClickListener {
    private Context mContext;
    private MapView mMapView;
    private TextView infoTextView;
    private AMap aMap;
    private MapView mapView;
    private int contentWidth, contentHeight;
    private View contentView;
    private Field mField;
    private View mView;

    private MapDialogDismissListener mListener;

    public interface MapDialogDismissListener {
        boolean dialogDismiss();
    }

    public MapDialog(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MapDialog(Context context, int theme, Field field, View view, MapDialogDismissListener listener) {
        super(context, theme);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        mField = field;
        mView = view;
        mListener = listener;
        this.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        contentView = inflater.inflate(R.layout.dialog_field_map, null);
        setContentView(contentView);
        mapView = (MapView) contentView.findViewById(R.id.dialog_field_map);
        this.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        init();
        initMap();
        setDialogPosition();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        contentWidth = Utils.instence(mContext).getDm().widthPixels;
        contentHeight = Utils.instence(mContext).dip2px(159);
    }

    private void initMap() {
        if (mField != null) {
            ArrayList<MarkerOptions> markerOptionlst = new ArrayList<>();
            MarkerOptions markerOption = new MarkerOptions();
            LatLng x = new LatLng(mField.lat, mField.lng);
            markerOption.position(x);
            markerOption.title(mField.name).snippet(mField.street);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(mContext.getResources(),
                            R.drawable.ic_map_local_choseon)));
            markerOptionlst.add(markerOption);
            List<Marker> markerList = aMap.addMarkers(markerOptionlst, true);
            //设定初始可视区域
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mField.lat, mField.lng), 12));
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }

    private void setDialogPosition() {
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        win.setGravity(Gravity.LEFT | Gravity.TOP);
//        lp.width = Util.instence(mContext).dip2px(208);
//        lp.height = Util.instence(mContext).dip2px(153);

        int[] location = new int[2];
        mView.getLocationOnScreen(location);

        int stateBarH = Utils.instence(mContext).getStatusBarHeight(mContext);

        int x = location[0] + mView.getWidth() / 2 - contentWidth / 2;
        int y = location[1] + mView.getHeight() - stateBarH;
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display display = m.getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        if ((y + contentHeight) > rect.height()) {
            y = location[1] - contentHeight - stateBarH - mView.getHeight();
            contentView.setBackgroundResource(R.drawable.address_pop_up_bk);
        } else {
            contentView.setBackgroundResource(R.drawable.address_pop_down_bk);
        }

        lp.x = x;//设置x坐标
        lp.y = y;//设置y坐标
        win.setAttributes(lp);
    }

    private SpannableString getBaseInfo(String s, int color) {
        SpannableString spannableString = new SpannableString(s);
        if (s.contains("\n")) {
            int start = s.indexOf("\n");
            spannableString.setSpan(new ForegroundColorSpan(color), start, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.7f), start, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        if (mListener != null) {
            mListener.dialogDismiss();
        }
        super.dismiss();
    }
}
