import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

    /*
     * //Al iniciar:
     *  1. Comprobar si hay JWT guardado en FireBase    -> public bool getTokenFromFB();
     *
     *  2.Si no lo hay, obtener JWT mediante:           -> getTokenFromTB_API();
     *      POST /api/auth/login Login method to get user JWT token data
     *  Si lo hay, comprobar cambios.
     *
     * 3. Obtener dispositivos relacionados con user    -> getDeviceTypesFromTB_API();
     * GET /api/device/types Get Device Types (getDeviceTypes)
     * Y RELLENAR LISTA relatedDevices con todos aquellos cuyo entityType=Device ->filterDeviceTypes
     * indicando su type;
     *
     * 4. Recorrer lista de relatedDevices y segun su type, incluirlo en lista de WSN, Spike o Meteo.
     *
     * 5. Para cada una de las listas, llamar a método updateList, que a su vez llame
     * a updateDevice en donde se implemeta el retrofit+GSON.
     * GET /api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
     * /

     */

import java.util.List;
import java.util.stream.Collectors;
public class DeviceManager
{
    private static final String BASE_URL = "https://thingsboard.cloud/api/";
    private static final String AUTH_HEADER = "Authorization";

    List <Device> relatedDevices= new ArrayList<>();
    private String username_TB;
    private String password_TB;
    private String jwt_TB;
    private String refresh_jwt_TB;

    public String getUsername_TB() {
        return username_TB;
    }

    public void setUsername_TB(String username_TB) {
        this.username_TB = username_TB;
    }

    public String getPassword_TB() {
        return password_TB;
    }

    public void setPassword_TB(String password_TB) {
        this.password_TB = password_TB;
    }

    public String getJwt_TB() {
        return jwt_TB;
    }

    public void setJwt_TB(String jwt_TB) {
        this.jwt_TB = jwt_TB;
    }

    public String getRefresh_jwt_TB() {
        return refresh_jwt_TB;
    }

    public void setRefresh_jwt_TB(String refresh_jwt_TB) {
        this.refresh_jwt_TB = refresh_jwt_TB;
    }

    /** Comprobar si hay JWT guardado en FireBase
        REQUISITOS: Disponer del ID del usuario autenticado en Firebase
                    Disponer de una instancia de FirebaseFirestore
     **/
    public boolean getTokenFromFB(FirebaseAuth auth, FirebaseToken token, Firestore firestore) {

        String userId = token.getUid();

        // Consulta el registro "token" asociado al usuario en Firestore
        DocumentSnapshot document;
        try {
            ApiFuture<DocumentSnapshot> future = firestore.collection("users").document(userId).get();
            document = future.get();
        } catch (Exception e) {
            // Maneja la excepción en caso de error al consultar Firestore
            return false;
        }

        // Verifica si el registro "token" existe y guarda su valor en jwt_TB
        if (document.exists()) {
            String tokenValue = document.getString("token");
            setJwt_TB(tokenValue);
            return true;
        } else {
            // Maneja el caso de que el registro "token" no exista
            return false;
        }
    }
    public boolean getTokenFromTB_API(String user, String pwd) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("text/plain");
        String requestBody = "{\"username\": \"" + user + "\", \"password\": \"" + pwd + "\"}";

        RequestBody body = RequestBody.create(mediaType, requestBody);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ApiResponse> call = apiService.getToken(body);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        setJwt_TB(apiResponse.getToken());
                        setRefresh_jwt_TB(apiResponse.getRefreshToken());
                        getDeviceTypesFromTB_API();
                    }
                } else {
                    // Maneja el caso de respuesta no exitosa
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Maneja la falla en la solicitud
            }
        });

        return true;
    }

    public List<DeviceTypeResponse> getDeviceTypesFromTB_API() {
        OkHttpClient client = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        String authHeaderValue = "Bearer " + getJwt_TB();
        Call<List<DeviceTypeResponse>> call = apiService.getDeviceTypes(authHeaderValue);

        call.enqueue(new Callback<List<DeviceTypeResponse>>() {
            @Override
            public void onResponse(Call<List<DeviceTypeResponse>> call, Response<List<DeviceTypeResponse>> response) {
                if (response.isSuccessful()) {
                    List<DeviceTypeResponse> deviceTypes = response.body();
                    if (deviceTypes != null) {
                        List<DeviceTypeResponse> filteredDeviceTypes = deviceTypes.stream()
                                .filter(deviceType -> "DEVICE".equals(deviceType.getEntityType()))
                                .collect(Collectors.toList());
                        return filteredDeviceTypes;
                    }
                } else {
                    // Maneja el caso de respuesta no exitosa
                    return Collections.emptyList();
                }
            }

            @Override
            public void onFailure(Call<List<DeviceTypeResponse>> call, Throwable t) {
                // Maneja la falla en la solicitud
            }
        });
    }

    /**
     * Crea instancias de la clase Device a partir de los elementos de la lista filteredDeviceTypes,
     * transfiriendo únicamente los campos "id" y "type". Las instancias creadas se devuelven en una lista.
     *
     * @param filteredDeviceTypes Lista de elementos DeviceTypeResponse filtrados.
     * @return Lista de instancias de la clase Device con los campos "id" y "type" transferidos.
     */
    public List<Device> createDeviceInstances(List<DeviceTypeResponse> filteredDeviceTypes) {
        List<Device> deviceInstances = new ArrayList<>();

        for (DeviceTypeResponse deviceType : filteredDeviceTypes) {
            Device device = new Device();
            device.setId(deviceType.getId());
            device.setType(deviceType.getType());
            deviceInstances.add(device);
        }

        return deviceInstances;
    }

    //////////CLASES E INTERFACES AUXILIARES
    public interface ApiService {
        @Headers("Content-Type: text/plain")
        @POST("auth/login")
        Call<ApiResponse> getToken(@Body RequestBody requestBody);

        @GET("device/types")
        Call<List<DeviceTypeResponse>> getDeviceTypes(@Header(AUTH_HEADER) String authorizationHeader);
    }

    public class ApiResponse {
        @SerializedName("token")
        private String token;

        @SerializedName("refreshToken")
        private String refreshToken;

        public String getToken() {
            return token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }

    /**
     * Clase que representa la respuesta de la API de ThingsBoard para obtener los tipos de dispositivos.
     * Contiene los campos "tenantId", "entityType", "type" y "id" asociados a cada tipo de dispositivo.
     */
    public class DeviceTypeResponse {
        @SerializedName("tenantId")
        private TenantId tenantId;

        @SerializedName("entityType")
        private String entityType;

        @SerializedName("type")
        private String type;

        @SerializedName("id")
        private String id;

        public TenantId getTenantId() {
            return tenantId;
        }

        public String getEntityType() {
            return entityType;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }

    public class TenantId {
        @SerializedName("entityType")
        private String entityType;

        @SerializedName("id")
        private String id;

        public String getEntityType() {
            return entityType;
        }

        public String getId() {
            return id;
        }
    }
}

    
    

}