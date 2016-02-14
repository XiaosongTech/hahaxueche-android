package com.hahaxueche.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 工具类
 * Created by gibxin on 2016/1/23.
 */
public class Util {
    private static Util mUtil;

    private static DisplayMetrics dm;

    public static Util instence(Context context) {
        if (mUtil == null)
            mUtil = new Util();
        if (dm == null)
            dm = context.getResources().getDisplayMetrics();
        return mUtil;
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public DisplayMetrics getDm() {
        return dm;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = dm.density;
        return (int) (dpValue * scale + 0.5f);//最后的0.5f只是为了四舍五入
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = dm.density;
        return (int) (pxValue / scale + 0.5f);//最后的0.5f只是为了四舍五入
    }


    /**
     * findViewById方法，取消强制转换步骤
     */
    public <T extends View> T $(Context context, int id) {
        return (T) ((Activity) context).findViewById(id);
    }

    /**
     * findViewById方法，取消强制转换步骤
     */
    public <T extends View> T $(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    public boolean isApkInstall(Context context, String pkg) {
        PackageManager mPackageManager = context.getApplicationContext().getPackageManager();
        PackageInfo intent;
        try {
            intent = mPackageManager.getPackageInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (intent == null)
            return false;
        else
            return true;
    }

    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable() && info.isConnected()) {
            return true;
        } else
            return false;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int px2sp(float pxValue) {
        final float fontScale = dm.density;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int sp2px(float spValue) {
        final float fontScale = dm.density;
        return (int) (spValue * fontScale + 0.5f);
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * API<17时，生成viewid
     *
     * @return
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public void loadImgToView(Context context, String url, int width, int height, ImageView view) {
        if (!TextUtils.isEmpty(url)) {
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(context).cancelRequest(view);
                Picasso.with(context).load(Uri.parse(url)).resize(width, height).into(view);
            }
        }
    }
}
