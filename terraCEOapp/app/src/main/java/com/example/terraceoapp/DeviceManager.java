package com.example.terraceoapp;

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
     * 1. Obtener JWT mediante:           -> getTokenFromTB_API();
     *      POST /api/auth/login Login method to get user JWT token data
     *
     * 2. Obtener dispositivos relacionados con user    -> getDeviceTypesFromTB_API();
     * GET /api/device/types Get Device Types (getDeviceTypes)
     * Y RELLENAR LISTA relatedDevices con todos aquellos cuyo entityType=Device ->filterDeviceTypes
     * indicando su type;
     *
     * 3. Recorrer lista de relatedDevices y segun su type, incluirlo en lista de WSN, Spike o Meteo.
     *
     * 4. Para cada una de las listas, llamar a método updateList, que a su vez llame
     * a updateDevice en donde se implemeta el retrofit+GSON.
     * GET /api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
     * /

     */

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class DeviceManager
{
    private static final String BASE_URL = "https://thingsboard.cloud/api/";
    private static final String AUTH_HEADER = "Authorization";

    public List <Device> relatedDevices= new ArrayList<>();
    private String username_TB;
    private String password_TB;
    private String jwt_TB;
    private String refresh_jwt_TB;

    private int numOfWSN;
    private int numOfSPIKES;
    private int numOfMETEOS;

    public DeviceManager(String username, String password)
    {
        setUsername_TB(username);
        setPassword_TB(password);
        numOfMETEOS=numOfSPIKES=numOfWSN=0;
    }

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

    public boolean obtainTokenFromTB_API() {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("text/plain");
        String requestBody = "{\"username\": \"" + getUsername_TB() + "\", \"password\": \"" + getPassword_TB() + "\"}";

        RequestBody body = RequestBody.create(mediaType, requestBody);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        DeviceManagerApiService deviceManagerApiService = retrofit.create(DeviceManagerApiService.class);
        Call<DeviceManagerApiResponse> call = deviceManagerApiService.getToken(body);

        call.enqueue(new Callback<DeviceManagerApiResponse>() {
            @Override
            public void onResponse(Call<DeviceManagerApiResponse> call, Response<DeviceManagerApiResponse> response) {
                if (response.isSuccessful()) {
                    DeviceManagerApiResponse deviceManagerApiResponse = response.body();
                    if (deviceManagerApiResponse != null) {
                        setJwt_TB(deviceManagerApiResponse.getToken());
                        setRefresh_jwt_TB(deviceManagerApiResponse.getRefreshToken());
                        obtainDevicesFromTB_API();
                    }
                } else {
                    // Maneja el caso de respuesta no exitosa (LANZAR MSG USUARIO/PWD PROPORCIONADOS NO CORRESPONDEN A REGISTRO EN TB)
                }
            }

            @Override
            public void onFailure(Call<DeviceManagerApiResponse> call, Throwable t) {
                // Maneja la falla en la solicitud
            }
        });

        return true;
    }

    public void obtainDevicesFromTB_API() {
        OkHttpClient client = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        DeviceManagerApiService deviceManagerApiService = retrofit.create(DeviceManagerApiService.class);
        String authHeaderValue = "Bearer " + getJwt_TB();
        Call<List<DeviceTypeResponse>> call = deviceManagerApiService.getDeviceTypes(authHeaderValue);

        call.enqueue(new Callback<List<DeviceTypeResponse>>() {
            @Override
            public void onResponse(Call<List<DeviceTypeResponse>> call, Response<List<DeviceTypeResponse>> response) {
                if (response.isSuccessful()) {
                    List<DeviceTypeResponse> deviceTypes = response.body();
                    if (deviceTypes != null) {
                        List<DeviceTypeResponse> filteredDeviceTypes = deviceTypes.stream()
                                .filter(deviceType -> "DEVICE".equals(deviceType.getEntityType()))
                                .collect(Collectors.toList());
                        for (DeviceTypeResponse filteredDevice : filteredDeviceTypes) {
                            if (filteredDevice.type.equals("WSN"))
                            {
                                String wsnName="WSN Station "+numOfWSN;
                                WSN_Device wsnDev = new WSN_Device(filteredDevice.id,DeviceTypes.WSN,wsnName);
                                wsnDev.setJwt(getJwt_TB());
                                relatedDevices.add(wsnDev);
                                numOfWSN++;
                            }
                            else if (filteredDevice.type.equals("METEO")) {
                                String meteoName="METEO Station " + numOfMETEOS;
                                METEO_Device meteoDev = new METEO_Device(filteredDevice.id, DeviceTypes.METEO, meteoName);
                                meteoDev.setJwt(getJwt_TB());
                                relatedDevices.add(meteoDev);
                                numOfMETEOS++;
                            }
                            else if(filteredDevice.type.equals("SPIKE"))
                            {
                                String spikeName="SPIKE Sensor "+ numOfSPIKES;
                                SPIKE_Device spikeDev=new SPIKE_Device(filteredDevice.id, DeviceTypes.SPIKE, spikeName);
                                spikeDev.setJwt(getJwt_TB());
                                relatedDevices.add(spikeDev);
                                numOfSPIKES++;
                            }
                        }


                    }
                } else {
                    // Maneja el caso de respuesta no exitosa
                }
            }

            @Override
            public void onFailure(Call<List<DeviceTypeResponse>> call, Throwable t) {
                // Maneja la falla en la solicitud
            }
        });
    };
    public int getNumOfWSN() {
        return numOfWSN;
    }

    public void setNumOfWSN(int numOfWSN) {
        this.numOfWSN = numOfWSN;
    }

    public int getNumOfSPIKES() {
        return numOfSPIKES;
    }

    public void setNumOfSPIKES(int numOfSPIKES) {
        this.numOfSPIKES = numOfSPIKES;
    }

    public int getNumOfMETEOS() {
        return numOfMETEOS;
    }

    public void setNumOfMETEOS(int numOfMETEOS) {
        this.numOfMETEOS = numOfMETEOS;
    }

    //////////CLASES E INTERFACES AUXILIARES
    public interface DeviceManagerApiService {
        @Headers("Content-Type: text/plain")
        @POST("auth/login")
        Call<DeviceManagerApiResponse> getToken(@Body RequestBody requestBody);

        @GET("device/types")
        Call<List<DeviceTypeResponse>> getDeviceTypes(@Header(AUTH_HEADER) String authorizationHeader);
    }

    public class DeviceManagerApiResponse {
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

