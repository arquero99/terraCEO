package com.example.terraceoapp;//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto METEO

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class METEO_Device extends Device implements Serializable {

    private double baromPressure;
    private double lightLux;
    private boolean raining;
    private int wind_KPH;

    public METEO_Device(String valueId, DeviceTypes valueType, String valueName) {
        super(valueId, valueType);
        setName(valueName);
        setDescription("Meteorological Station. Gives information about current weather an climate status.");
        getPosition().setName(valueName);
        setType(DeviceTypes.METEO);
    }
    public String getSensorName1()
    {
        return "Barometric Pressure";
    }
    public String getSensorName2()
    {
        return "Ambient Light";
    }
    public String getSensorName3()
    {
        return "Currently Raining";
    }
    public String getSensorName4()
    {
        return "Wind velocity";
    }
    public String getSensorValue1()
    {
        return (decimalFormat.format(getBaromPressure())+" bar");
    }
    public String getSensorValue2()
    {
        return (decimalFormat.format(getLightLux()));
    }
    public String getSensorValue3()
    {
        if (isRaining())
        {
            return "Yes";
        }
        else return "No";
    }
    public String getSensorValue4()
    {
        return (getWind_KPH()+ " kph");
    }
    public double getBaromPressure() {
        return baromPressure;
    }

    public void setBaromPressure(double baromPressure) {
        this.baromPressure = baromPressure;
    }

    public double getLightLux() {
        return lightLux;
    }

    public void setLightLux(double lightLux) {
        this.lightLux = lightLux;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public int getWind_KPH() {
        return wind_KPH;
    }

    public void setWind_KPH(int wind_KPH) {
        this.wind_KPH = wind_KPH;
    }


    @Override
    public void updateDevice() {
        String url = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/" + this.getId() + "/values/timeseries?useStrictDataTypes=false";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + getJwt())
                .build();

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

                        JSONArray baromPreassureArray = json.getJSONArray("baromPreassure");
                        if (baromPreassureArray.length() > 0) {
                            JSONObject baromPreassureObject = baromPreassureArray.getJSONObject(0);
                            double baromPreassureValue = baromPreassureObject.getDouble("value");
                            setBaromPressure(baromPreassureValue);
                        }

                        JSONArray lightLuxArray = json.getJSONArray("lightLux");
                        if (lightLuxArray.length() > 0) {
                            JSONObject lightLuxObject = lightLuxArray.getJSONObject(0);
                            double lightLuxValue = lightLuxObject.getDouble("value");
                            setLightLux(lightLuxValue);
                        }

                        JSONArray rainingArray = json.getJSONArray("raining");
                        if (rainingArray.length() > 0) {
                            JSONObject rainingObject = rainingArray.getJSONObject(0);
                            boolean rainingValue = rainingObject.getBoolean("value");
                            setRaining(rainingValue);
                        }

                        JSONArray wind_KPHArray = json.getJSONArray("wind_KPH");
                        if (wind_KPHArray.length() > 0) {
                            JSONObject wind_KPHObject = wind_KPHArray.getJSONObject(0);
                            int wind_KPHValue = wind_KPHObject.getInt("value");
                            setWind_KPH(wind_KPHValue);
                        }

                        // Resto del procesamiento de los datos del JSON

                    } catch (JSONException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
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
