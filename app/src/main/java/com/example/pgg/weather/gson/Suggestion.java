package com.example.pgg.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PGG on 2017/2/27.
 */

public class Suggestion {

    public Comfort comfort;
    public CarWash carWash;
    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;
    }

    public class CarWash {

        @SerializedName("txt")
        public String info;
    }

    public class Sport {

        @SerializedName("txt")
        public String info;
    }
}
