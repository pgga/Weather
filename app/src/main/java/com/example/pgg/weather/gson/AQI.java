package com.example.pgg.weather.gson;

/**
 * Created by PGG on 2017/2/27.
 */

public class AQI {

    public AQICity city;

    public class AQICity {

        public String aqi;
        public String pm25;
    }
}
