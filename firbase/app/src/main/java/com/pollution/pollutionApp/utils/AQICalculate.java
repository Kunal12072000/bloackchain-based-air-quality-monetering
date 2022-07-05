package com.pollution.pollutionApp.utils;

import androidx.annotation.NonNull;

public class AQICalculate {
    public static String aqi = "85";

    @NonNull
    @Override
    public String toString() {
        return "AQI " + aqi;
    }
    public static int getRange(int aqi){
        if (aqi<=50){
            return 0;
        }else if (aqi<=100){
            return 1;
        }else if (aqi <= 150){
            return 2;
        }else if (aqi <= 200){
            return 3;
        }else if (aqi <= 300){
            return 4;
        }else /*if (aqi> 300)*/{
            return 5;
        }
        //return 1;
    }

    /*
    0-50-good,0
    51-100-moderate, 1
    101-150-unhealthy for sensitive group, 2
    151-200-unhealthy, 3
    201-300-very unhealthy, 4
    300+-hazardous, 5
*/
}
