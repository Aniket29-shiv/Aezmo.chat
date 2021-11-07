package com.aniket.aezmodate.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://jsonkeeper.com/b/";
//    https://aezmo-date-test-api-5mz94.ondigitalocean.app/
    public static Retrofit retrofit = null;

    public static Retrofit getApiClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit;

    }
}
