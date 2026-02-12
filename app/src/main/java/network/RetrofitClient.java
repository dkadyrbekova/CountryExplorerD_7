package com.example.countryexplorerd.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Твой реальный URL Mock Server из Postman
    private static final String BASE_URL = "https://b93078c3-83ff-44eb-8298-46c2514460fb.mock.pstmn.io/";
    private static Retrofit retrofit = null;

    public static com.example.countryexplorerd.network.ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(com.example.countryexplorerd.network.ApiService.class);
    }
}