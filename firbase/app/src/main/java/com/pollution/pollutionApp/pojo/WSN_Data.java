package com.pollution.pollutionApp.pojo;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

public class WSN_Data {
    public String Temperature, O2, CO2, Humidity, SO2, NH3, CH4;
    @PropertyName("Dew Point")
    public String Dew;
    @PropertyName("Time Batch")
    public String Time_Batch;

    public WSN_Data() {
    }

    public WSN_Data(String temperature, String o2, String dew, String CO2, String humidity, String SO2, String NH3, String time_Batch, String CH4) {
        Temperature = temperature;
        O2 = o2;
        this.CO2 = CO2;
        Humidity = humidity;
        this.SO2 = SO2;
        this.NH3 = NH3;
        this.CH4 = CH4;
        Dew = dew;
        Time_Batch = time_Batch;
    }
}
