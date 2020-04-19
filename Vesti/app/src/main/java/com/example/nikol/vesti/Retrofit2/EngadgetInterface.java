package com.example.nikol.vesti.Retrofit2;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by nikol on 27-Mar-17.
 */

public interface EngadgetInterface {
    @GET("articles?source=techradar&sortBy=top&apiKey=aacda514ba6e4a4489bfcfcd0b3f2008")
    Call<NewsResponse> getTechRadar();
}
