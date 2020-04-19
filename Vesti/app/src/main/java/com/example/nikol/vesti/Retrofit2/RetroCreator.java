package com.example.nikol.vesti.Retrofit2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nikol on 27-Mar-17.
 */

public class RetroCreator {
    static final String BASE_URL = "https://newsapi.org/v1/";

    public static Retrofit getService() {

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
