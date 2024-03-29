package com.hahaxueche.util;

import java.text.DecimalFormat;

/**
 * 距离处理
 * Created by Administrator on 2016/5/24.
 */
public class DistanceUtil {
    private static final double EARTH_RADIUS = 6378137.0;

    // 返回单位是米
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    //返回km
    public static String getDistanceKm(double longitude1, double latitude1,
                                       double longitude2, double latitude2) {
        DecimalFormat dfInt = new DecimalFormat("#####.#");
        double s = getDistance(longitude1, latitude1, longitude2, latitude2);
        if (Double.compare(s / 1000, 50) > 0) {
            return "50+";
        } else {
            return dfInt.format(s / 1000);
        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
