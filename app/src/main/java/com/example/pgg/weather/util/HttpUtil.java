package com.example.pgg.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by PGG on 2017/2/26.
 */

public class HttpUtil {

    public static void sendOKHttpReuset(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
