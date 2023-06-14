//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto METEO

public class METEO_Device extends Device {

    private double baromPreassure;
    private double lightLux;
    private boolean raining;
    private int wind_KPH;

    public METEO_Device(String value) {
        super(value);
    }

    public METEO_Device(String valueId, String valueType) {
        super(valueId, valueType);
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

    public METEO_Device(String valueId, String valueType, String valueName) {
        super(valueId, valueType, valueName);
    }


    @Override
    public void updateDevice() {
        String url = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/" + this.getId() + "/values/timeseries?useStrictDataTypes=false";
        String token = ;//Obetener de device manager creado con info de login.

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
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
                            setLatitude(latitudeValue);
                        }

                        JSONArray longitudeArray = json.getJSONArray("longitude");
                        if (longitudeArray.length() > 0) {
                            JSONObject longitudeObject = longitudeArray.getJSONObject(0);
                            double longitudeValue = longitudeObject.getDouble("value");
                            setLongitude(longitudeValue);
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
