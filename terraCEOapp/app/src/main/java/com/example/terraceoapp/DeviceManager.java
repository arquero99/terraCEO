public class DeviceManager
{
    List <Device> relatedDevices= new ArrayList<>();

    private String username_TB;
    private String password_TB;
    private String jwt_TB;
    private String refresh_jwt_TB;

    /*
     * //Al iniciar:
     *  1. Comprobar si hay JWT guardado en FireBase
     *  Si no lo hay, obtener JWT mediante:
     *      POST /api/auth/login Login method to get user JWT token data
     *  Si lo hay, comprobar cambios.  
     *  
     * 2. Obtener dispositivos relacionados con user
     * GET /api/device/types Get Device Types (getDeviceTypes)
     * Y RELLENAR LISTA relatedDevices con todos aquellos cuyo entityType=Device
     * indicando su type;
     *
     * 3. Recorrer lista de relatedDevices y segun su type, incluirlo en lista de WSN, Spike o Meteo.
     * 
     * 4. Para cada una de las listas, llamar a método updateList, que a su vez llame 
     * a updateDevice en donde se implemeta el retrofit+GSON.
     * GET /api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
     * /
    
    

}