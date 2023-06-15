package com.example.terraceoapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Arrays;
import java.util.List;



public class MeteoAPIClient {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/";

    private final MeteoAPI service;

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
                .build();

        service = retrofit.create(MeteoAPI.class);
    }

    public void getForecast(double latitude, double longitude) {
        String endpoint = "forecast";
        String queryParams = String.format("latitude=%f&longitude=%f&daily=temperature_2m_max,precipitation_sum,et0_fao_evapotranspiration&timezone=auto", latitude, longitude);

        Call<ForecastResponse> call = service.getForecast(endpoint, queryParams);
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful()) {
                    ForecastResponse forecastResponse = response.body();
                    if (forecastResponse != null) {
                        temperatureMaxArray = forecastResponse.getDaily().getTemperatureMaxArray();
                        precipitationArray = forecastResponse.getDaily().getPrecipitationArray();
                        evapotranspirationArray = forecastResponse.getDaily().getEvapotranspirationArray();

                        // Hacer lo que necesites con los datos guardados en los arrays
                        // Por ejemplo, imprimirlos en la consola
                        System.out.println("Temperature Max Array: " + Arrays.toString(temperatureMaxArray));
                        System.out.println("Precipitation Array: " + Arrays.toString(precipitationArray));
                        System.out.println("Evapotranspiration Array: " + Arrays.toString(evapotranspirationArray));
                    }
                } else {
                    System.out.println("Error en la solicitud: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public double[] getTemperatureMaxArray() {
        return temperatureMaxArray;
    }

    public double[] getPrecipitationArray() {
        return precipitationArray;
    }

    public double[] getEvapotranspirationArray() {
        return evapotranspirationArray;
    }
}

