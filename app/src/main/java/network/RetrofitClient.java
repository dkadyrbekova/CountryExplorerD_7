package com.example.countryexplorerd.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://b9c1c3d2-1f21-4dcc-b6d6-4ea7dcfaa0c8.mock.pstmn.io/";
    private static RetrofitClient mInstance;
    private static Retrofit retrofit = null;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public com.example.countryexplorerd.network.ApiService getApi() {
        return retrofit.create(com.example.countryexplorerd.network.ApiService.class);
    }

    // Оставляем для совместимости, если используется в других местах
    public static com.example.countryexplorerd.network.ApiService getApiService() {
        if (retrofit == null) {
            new RetrofitClient();
        }
        return retrofit.create(com.example.countryexplorerd.network.ApiService.class);
    }
}