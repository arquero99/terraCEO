package com.example.terraceoapp;

import android.annotation.SuppressLint;

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
    private double[] precipitationProbArray;

    private double[] precipitationSumArray;
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

        service = retrofit.create(MeteoApiService.class); //Crea una instancia de la interfaz MeteoApiService utilizando el objeto retrofit para realizar llamadas a la API.
    }

    @SuppressLint("CheckResult")
    public void obtainForecast(Device targetDev) {

        double latitude=targetDev.getPosition().getLatitude();
        double longitude=targetDev.getPosition().getLongitude();
        String requiredDailyData="temperature_2m_max,precipitation_sum,precipitation_probability_max,et0_fao_evapotranspiration";

        // Realiza una llamada a la API utilizando el método getForecast de la instancia service y pasa los parámetros de latitud, longitud,
        // indicador de datos diarios requeridos y zona horaria.
        service.getForecast(latitude,longitude,requiredDailyData,"Europe/Berlin")
                .subscribeOn(Schedulers.io()) // Especifica el hilo en el que se ejecutará la operación de suscripción. En este caso, se utilizará el hilo de E/S (io) para realizar la llamada a la API.
                .observeOn(AndroidSchedulers.mainThread()) // Especifica el hilo en el que se recibirán los resultados de la operación de suscripción. En este caso, se utilizará el hilo principal (mainThread) para recibir los resultados y actualizar la interfaz de usuario.
                .doOnNext(
                        targetDev::setDeviceForecast
                )
                .subscribe();
    }

    public double[] getTemperatureMaxArray() {
        return this.temperatureMaxArray;
    }

    public double[] getPrecipitationProbArray() {
        return this.precipitationProbArray;
    }

    public double[] getEvapotranspirationArray() {
        return this.evapotranspirationArray;
    }

    public double[] getPrecipitationSumArray() {
        return this.precipitationSumArray;
    }

}

//////// INTERFAZ API /////////

