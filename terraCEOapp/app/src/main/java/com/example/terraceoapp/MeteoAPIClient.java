package com.example.terraceoapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Collections;

public class MeteoAPIClient {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/";
    private static MeteoApiService service;
    private double[] temperatureMaxArray;
    private double[] precipitationArray;
    private double[] evapotranspirationArray;

    public MeteoAPIClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        service = retrofit.create(MeteoApiService.class);
    }

    public void obtainForecast(double latitude, double longitude) {
        String requiredDailyData="temperature_2m_max,precipitation_sum,precipitation_probability_max,et0_fao_evapotranspiration";
        service.getForecast(latitude,longitude,requiredDailyData,"Europe/Berlin")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(forecastData -> {
                    // Realiza alguna transformación o procesamiento en cada objeto ForecastData aquí si es necesario
                    return Collections.singletonList(forecastData);
                });
    }

    public double[] getTemperatureMaxArray() {
        return this.temperatureMaxArray;
    }

    public double[] getPrecipitationArray() {
        return this.precipitationArray;
    }

    public double[] getEvapotranspirationArray() {
        return this.evapotranspirationArray;
    }
}

//////// INTERFAZ API /////////

