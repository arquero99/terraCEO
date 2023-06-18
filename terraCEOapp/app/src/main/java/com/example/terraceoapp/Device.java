package com.example.terraceoapp;//Clase abstracta con método updateDevice por implementar.
//Este metodo realiza la llamada a la api
//GET /api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)

import android.graphics.drawable.Drawable;

import com.example.terraceoapp.Location;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.Serializable;
import java.text.DecimalFormat;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public abstract class Device implements Serializable
{
    private String id;
    private String name;
    private DeviceTypes type;
    private String jwt;
    private Location position;
    private String description;
    private ForecastResponse deviceForecast;
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    Device(){this.id="NOID";}
    Device(String value){
        this.id=value;
    }

    Device(String valueId, DeviceTypes valueType)
    {
        this(valueId);
        this.type=valueType;
    }

    Device(String valueId, DeviceTypes valueType, String valueJWT)
    {
        this(valueId, valueType);
        setJwt(valueJWT);
    }

    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getJwt(){return this.jwt;}

    public void setName(String value)
    {
        this.name=value;
    }

    public void setId(String value){this.id=value;}

    public void setJwt(String value){this.jwt=value;}

    public void updateDevice(String token) {
        String url = "https://thingsboard.cloud/api/plugins/telemetry/DEVICE/" + this.id + "/values/timeseries?useStrictDataTypes=false";
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Parsear la respuesta JSON
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(responseBody);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Obtener los valores correspondientes
            JsonElement nameElement = jsonObject.get("name").getAsJsonArray().get(0);
            JsonElement posXElement = jsonObject.get("latitude").getAsJsonArray().get(0);
            JsonElement posYElement = jsonObject.get("longitude").getAsJsonArray().get(0);

            // Actualizar los campos de la instancia
            Gson gson = new Gson();
            String nameValue = gson.fromJson(nameElement.getAsJsonObject().get("value"), String.class);
            String latValue = gson.fromJson(posXElement.getAsJsonObject().get("value"), String.class);
            String longValue = gson.fromJson(posYElement.getAsJsonObject().get("value"), String.class);

            if (jsonObject.get("description").getAsJsonArray().size() > 0) {
                JsonArray descriptionArray = jsonObject.get("description").getAsJsonArray();
                JsonObject descriptionObject = descriptionArray.get(0).getAsJsonObject();
                JsonElement descriptionValueElement = descriptionObject.get("value");
                if (!descriptionValueElement.isJsonNull()) {
                    String descriptionValue = descriptionValueElement.getAsString();
                    this.description = descriptionValue;
                }
            }

            this.name = nameValue;
            this.position = new Location(Double.parseDouble(latValue), Double.parseDouble(longValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract void updateDevice();
    public ForecastResponse getDeviceForecast() {
        return deviceForecast;
    }
    public void setDeviceForecast(ForecastResponse deviceForecast) {
        this.deviceForecast = deviceForecast;
    }
    public abstract String getSensorName1();
    public abstract String getSensorName2();
    public abstract String getSensorName3();
    public abstract String getSensorName4();
    public abstract String getSensorValue1();
    public abstract String getSensorValue2();
    public abstract String getSensorValue3();
    public abstract String getSensorValue4();

    public DeviceTypes getType() {
        return type;
    }

    public void setType(DeviceTypes type) {
        this.type = type;
    }
}