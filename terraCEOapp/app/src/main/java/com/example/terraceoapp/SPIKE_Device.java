package com.example.terraceoapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto Spike
public class SPIKE_Device extends Device {
    private double soilHumidity;
    private double airTemperature;
    private double luminosity;
    private double airHumidity;
    public SPIKE_Device(String value) {
        super(value);
    }

    public SPIKE_Device(String valueId, DeviceTypes valueType) {
        super(valueId, valueType);
    }

    public SPIKE_Device(String valueId, DeviceTypes valueType, String valueName) {
        super(valueId, valueType, valueName);
    }
    public double getSoilHumidity() {
        return soilHumidity;
    }

    public void setSoilHumidity(double soilHumidity) {
        this.soilHumidity = soilHumidity;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(double luminosity) {
        this.luminosity = luminosity;
    }

    public double getAirHumidity() {
        return airHumidity;
    }

    public void setAirHumidity(double airHumidity) {
        this.airHumidity = airHumidity;
    }

    @Override
    public void updateDevice() {
        String url = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/" + this.getId() + "/values/timeseries?useStrictDataTypes=false";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + getJwt())
                .build();

        this.updateDevice();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);

                        JSONArray latitudeArray = json.getJSONArray("latitude");
                        if (latitudeArray.length() > 0) {
                            JSONObject latitudeObject = latitudeArray.getJSONObject(0);
                            double latitudeValue = latitudeObject.getDouble("value");
                            getPosition().setLatitude(latitudeValue);
                        }

                        JSONArray longitudeArray = json.getJSONArray("longitude");
                        if (longitudeArray.length() > 0) {
                            JSONObject longitudeObject = longitudeArray.getJSONObject(0);
                            double longitudeValue = longitudeObject.getDouble("value");
                            getPosition().setLatitude(longitudeValue);
                        }

                        // Actualiza las demás variables según el formato del JSON

                        JSONArray soilHumidityArray = json.getJSONArray("soilHumidity");
                        if (soilHumidityArray.length() > 0) {
                            JSONObject soilHumidityObject = soilHumidityArray.getJSONObject(0);
                            double soilHumidityValue = soilHumidityObject.getDouble("value");
                            setSoilHumidity(soilHumidityValue);
                        }

                        JSONArray airTemperatureArray = json.getJSONArray("airTemperature");
                        if (airTemperatureArray.length() > 0) {
                            JSONObject airTemperatureObject = airTemperatureArray.getJSONObject(0);
                            double airTemperatureValue = airTemperatureObject.getDouble("value");
                            setAirTemperature(airTemperatureValue);
                        }

                        JSONArray luminosityArray = json.getJSONArray("luminosity");
                        if (luminosityArray.length() > 0) {
                            JSONObject luminosityObject = luminosityArray.getJSONObject(0);
                            double luminosityValue = luminosityObject.getDouble("value");
                            setLuminosity(luminosityValue);
                        }

                        JSONArray airHumidityArray = json.getJSONArray("airHumidity");
                        if (airHumidityArray.length() > 0) {
                            JSONObject airHumidityObject = airHumidityArray.getJSONObject(0);
                            double airHumidityValue = airHumidityObject.getDouble("value");
                            setAirHumidity(airHumidityValue);
                        }

                        // Resto del procesamiento de los datos del JSON

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

}