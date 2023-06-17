package com.example.terraceoapp;


import retrofit2.http.GET;
import io.reactivex.Observable;
import retrofit2.http.Query;

public interface MeteoApiService{
    @GET("forecast")
    Observable<ForecastResponse> getForecast(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("daily") String daily,
            @Query("timezone") String timezone
    );

}
