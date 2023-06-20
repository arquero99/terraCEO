package com.example.terraceoapp;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import retrofit2.http.Query;
enum ConnStatus{Unconnected, Refused, Connected}
public class DeviceManager
{
    private static final String BASE_URL = "https://thingsboard.cloud:443/api/";
    private static final String AUTH_HEADER = "Authorization";
    private ConnStatus connected=ConnStatus.Unconnected;
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
        setNumOfMETEOS(0);
        setNumOfSPIKES(0);
        setNumOfWSN(0);
        setConnected(ConnStatus.Unconnected);
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

    public Device searchDeviceByName(String targetName)
    {
        Device targetDevice=null;
        for (Device device:relatedDevices)
        {
            if (device.getName().equals(targetName))
            {
                targetDevice=device;
            }
        }
        return targetDevice;
    }

    public ConnStatus getConnectedStatus() {
        return connected;
    }

    public void setConnected(ConnStatus conStat) {
        this.connected = conStat;
    }

    ////        LLAMADAS A LA API REST          ////
    public void obtainTokenFromTB_API()
    {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", getUsername_TB());
            requestBody.put("password", getPassword_TB());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, requestBody.toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeviceManagerApiService deviceManagerApiService = retrofit.create(DeviceManagerApiService.class);
        Call<DeviceManagerApiResponse> call = deviceManagerApiService.getToken(body);

        call.enqueue(new Callback<DeviceManagerApiResponse>() {
            @Override
            public void onResponse(Call<DeviceManagerApiResponse> call, Response<DeviceManagerApiResponse> response) {
                if (response.isSuccessful())
                {
                    DeviceManagerApiResponse deviceManagerApiResponse = response.body();
                    if (deviceManagerApiResponse != null) {
                        setJwt_TB(deviceManagerApiResponse.getToken());
                        setRefresh_jwt_TB(deviceManagerApiResponse.getRefreshToken());
                        setConnected(ConnStatus.Connected);
                    } else {
                        setConnected(ConnStatus.Refused);
                    }
                }
                else
                {
                    setConnected(ConnStatus.Refused);
                }
            };

            @Override
            public void onFailure(Call<DeviceManagerApiResponse> call, Throwable t) {
                // Maneja la falla en la solicitud
                t.printStackTrace();
                setConnected(ConnStatus.Refused);
            }
        });
    }
    public void obtainDevicesFromTB_API()
    {
        String bearerToken=getJwt_TB();
        relatedDevices.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DeviceManagerApiService apiService = retrofit.create(DeviceManagerApiService.class);
        Call<DeviceDataResponse> call = apiService.getDevices("application/json", "Bearer " + bearerToken, 100, 0);

        try {
            Response<DeviceDataResponse> response = call.execute();
            if (response.isSuccessful()) {
                DeviceDataResponse dataResponse = response.body();
                if(dataResponse!=null) {
                    List<ObtainedDevice> devices = dataResponse.getData();
                    for (ObtainedDevice device : devices) {
                        System.out.println("ID: " + device.getDeviceId().getId());
                        System.out.println("Name: " + device.getName());
                        System.out.println("Type: " + device.getType());
                        System.out.println();
                        if (device.getName().contains("WSN") || device.getType().equals("WSN")) {
                            String wsnName = "WSN Station " + numOfWSN;
                            WSN_Device wsnDev = new WSN_Device(device.getDeviceId().getId(), DeviceTypes.WSN, wsnName);
                            wsnDev.setJwt(getJwt_TB());
                            relatedDevices.add(wsnDev);
                            setNumOfWSN(getNumOfWSN() + 1);
                        } else if (device.getName().contains("SPIKE") || device.getType().equals("SPIKE")) {
                            String spikeName = "SPIKE Sensor " + numOfSPIKES;
                            SPIKE_Device spikeDev = new SPIKE_Device(device.getDeviceId().getId(), DeviceTypes.SPIKE, spikeName);
                            spikeDev.setJwt(getJwt_TB());
                            relatedDevices.add(spikeDev);
                            setNumOfSPIKES(getNumOfSPIKES() + 1);
                        } else if (device.getName().contains("METEO") || device.getType().equals("METEO")) {
                            String meteoName = "METEO Station " + numOfMETEOS;
                            METEO_Device meteoDev = new METEO_Device(device.getDeviceId().getId(), DeviceTypes.METEO, meteoName);
                            meteoDev.setJwt(getJwt_TB());
                            relatedDevices.add(meteoDev);
                            setNumOfMETEOS(getNumOfMETEOS() + 1);
                        }
                    }
                }
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    ////        CLASES E INTERFACES AUXILIARES  ////
    public interface DeviceManagerApiService {
        @Headers("Content-Type: text/plain")
        @POST("auth/login")
        Call<DeviceManagerApiResponse> getToken(@Body RequestBody requestBody);

        @GET("user/devices")
        Call<DeviceDataResponse> getDevices(
                @Header("accept") String accept,
                @Header("Authorization") String authorization,
                @Query("pageSize") int pageSize,
                @Query("page") int page
        );

        @GET("device/types")
        Call<List<DeviceTypeResponse>> getDeviceTypes(@Header(AUTH_HEADER) String authorizationHeader);
    }
    public class DeviceManagerApiResponse {
        @SerializedName("token")
        private String token;

        @SerializedName("refreshToken")
        private String refreshToken;

        @SerializedName("scope")
        private String scope;

        public String getToken() {
            return token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getScope() {
            return scope;
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
    public class DeviceDataResponse {
        private List<ObtainedDevice> data;
        private int totalPages;
        private int totalElements;
        private boolean hasNext;

        public List<ObtainedDevice> getData() {
            return data;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public boolean hasNext() {
            return hasNext;
        }
    }
    public class DeviceId {
        private String entityType;
        private String id;

        public String getEntityType() {
            return entityType;
        }

        public String getId() {
            return id;
        }
    }
    public class ObtainedDevice {
        private DeviceId id;
        private String name;

        private String type;

        public DeviceId getDeviceId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}

