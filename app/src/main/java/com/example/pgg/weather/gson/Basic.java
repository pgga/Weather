package com.example.pgg.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PGG on 2017/2/27.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Updata updata;

    public class Updata {

        @SerializedName("loc")
        public String updataTime;
    }
}
