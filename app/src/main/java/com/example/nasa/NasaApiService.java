package com.example.nasa;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NasaApiService {
    @GET("planetary/apod")
    Call<NasaPhoto> getPictureOfTheDay(@Query("api_key") String apiKey, @Query("date") String date);
}