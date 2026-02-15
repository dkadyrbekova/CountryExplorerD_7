package com.example.countryexplorerd.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://b93078c3-83ff-44eb-8298-46c2514460fb.mock.pstmn.io/";
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