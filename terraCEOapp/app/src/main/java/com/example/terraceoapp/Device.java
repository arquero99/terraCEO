//Clase abstracta con método updateDevice por implementar. 
//Este metodo realiza la llamada a la api
//GET /api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public abstract class Device
{
    private String id;
    private String name;
    private String type;

    private Location position;

    private String description;

    Device(String value){
        this.id=value;
    }

    Device(String valueId, String valueType)
    {
        this.Device(valueId);
        this.type=valueType;
    }

    Device(String valueId, String valueType, String valueName)
    {
        this.Device(valueId, valueType);
        this.name=valueName;
    }

    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String value)
    {
        this.name=value;
    }

    public void setId(String value){this.id=value;}
    public void getInitInfo(String token) {
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
            JsonElement jsonElement = JsonParser.parseString(responseBody);
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

            if(jsonObject.get("description").getAsJsonArray().get(0)!=NULL)
            {
                JsonElement descriptionElement;
                String descriptionValue=gson.fromJson(descriptionElement.getAsJsonOnject.get("value"), String class));
                this.description=descriptionValue;
            }

            this.name = nameValue;
            this.position = new Location(Double.parseDouble(latValue), Double.parseDouble(longValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    public abstract void updateDevice();
}