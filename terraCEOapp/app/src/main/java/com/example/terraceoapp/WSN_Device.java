//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto WSN

public class WSN_Device extends Device {
    private double waterPreassure;
    private int flowRate;
    private double ph;
    private int TDS;

    public WSN_Device(String value) {
        super(value);
    }

    public WSN_Device(String valueId, String valueType) {
        super(valueId, valueType);
    }

    public double getWaterPressure() {
        return waterPressure;
    }

    public void setWaterPressure(double waterPressure) {
        this.waterPressure = waterPressure;
    }

    public int getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(int flowRate) {
        this.flowRate = flowRate;
    }

    public double getPh() {
        return ph;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }

    public int getTDS() {
        return TDS;
    }

    public void setTDS(int TDS) {
        this.TDS = TDS;
    }

    public WSN_Device(String valueId, String valueType, String valueName) {
        super(valueId, valueType, valueName);
    }

    @Override
    public void updateDevice() {
        String url = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/" + this.getId() + "/values/timeseries?useStrictDataTypes=false";
        String token = //Obtener de DeviceManager;

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

                        JSONArray waterPressureArray = json.getJSONArray("waterPreassure");
                        if (waterPressureArray.length() > 0) {
                            JSONObject waterPressureObject = waterPressureArray.getJSONObject(0);
                            double waterPressureValue = waterPressureObject.getDouble("value");
                            setWaterPressure(waterPressureValue);
                        }

                        JSONArray flowRateArray = json.getJSONArray("flowrate");
                        if (flowRateArray.length() > 0) {
                            JSONObject flowRateObject = flowRateArray.getJSONObject(0);
                            int flowRateValue = flowRateObject.getInt("value");
                            setFlowRate(flowRateValue);
                        }

                        JSONArray pHArray = json.getJSONArray("pH");
                        if (pHArray.length() > 0) {
                            JSONObject pHObject = pHArray.getJSONObject(0);
                            double pHValue = pHObject.getDouble("value");
                            setPh(pHValue);
                        }

                        JSONArray TDSArray = json.getJSONArray("TDS");
                        if (TDSArray.length() > 0) {
                            JSONObject TDSObject = TDSArray.getJSONObject(0);
                            int TDSValue = TDSObject.getInt("value");
                            setTDS(TDSValue);
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