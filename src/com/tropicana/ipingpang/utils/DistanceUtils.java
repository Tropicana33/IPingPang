package com.tropicana.ipingpang.utils;

public class DistanceUtils {

	
	public static double getDistance(double lat1,double longt1 , double lat2,double longt2
            ) {
        double PI = 3.14159265358979323; // 圆周率
        double R = 6371229; // 地球的半径

        double x, y, distance;
        x = (longt2 - longt1) * PI * R
                * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
        y = (lat2 - lat1) * PI * R / 180;
        distance = Math.hypot(x, y);

        return distance;
    }
	
}
