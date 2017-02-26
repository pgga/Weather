package com.example.pgg.weather.util;

import android.text.TextUtils;

import com.example.pgg.weather.db.City;
import com.example.pgg.weather.db.County;
import com.example.pgg.weather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PGG on 2017/2/26.
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponce(String responce) {
        if (!TextUtils.isEmpty(responce)) {
            try {
                JSONArray allProvince = new JSONArray(responce);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject provinceJSONObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceJSONObject.getInt("id"));
                    province.setProvinceName(provinceJSONObject.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponce(String responce,int provinceid) {
        if (!TextUtils.isEmpty(responce)) {
            try {
                JSONArray allCity = new JSONArray(responce);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject provinceJSONObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(provinceJSONObject.getInt("id"));
                    city.setCityName(provinceJSONObject.getString("name"));
                    city.setProvinceId(provinceid);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponce(String responce,int cityId) {
        if (!TextUtils.isEmpty(responce)) {
            try {
                JSONArray allCounty = new JSONArray(responce);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject countyJSONObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyJSONObject.getString("name"));
                    county.setWeatherId(countyJSONObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
}
