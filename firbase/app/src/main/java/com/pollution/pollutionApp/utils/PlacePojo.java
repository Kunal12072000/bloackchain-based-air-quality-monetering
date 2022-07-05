package com.pollution.pollutionApp.utils;

import com.google.android.gms.maps.model.LatLng;
import com.pollution.pollutionApp.pojo.WSN_Data;

public class PlacePojo {
    public static LatLng otherplaceLatlong = new LatLng(22.211188, 84.862007);
    public static LatLng placeLatlong = new LatLng(22.253497, 84.901301);
    public static String placeAdd = "National Institute of Technology, Sector 1, Rourkela, Odisha 769008";
    public static String placeName = "Rourkela, Odisha";
    public static String otherplaceName = "Rourkela Steel Plant";

    public static WSN_Data getWSNData() {
        return WSNData;
    }

    public static void setWSNData(WSN_Data WSNData) {
        PlacePojo.WSNData = WSNData;
    }

    public static WSN_Data WSNData;
}
