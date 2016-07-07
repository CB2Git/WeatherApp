package com.jay.bean.weather;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WeatherBean {

    @SerializedName("HeWeather data service 3.0")
    private List<Heweather_data_service> Heweather_data_service;
    public void setHeweather_data_service(List<Heweather_data_service> Heweather_data_service) {
         this.Heweather_data_service = Heweather_data_service;
     }
     public List<Heweather_data_service> getHeweather_data_service() {
         return Heweather_data_service;
     }

}