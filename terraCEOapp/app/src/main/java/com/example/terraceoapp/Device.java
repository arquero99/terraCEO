//Clase abstracta con método updateDevice por implementar. 
//Este metodo realiza la llamada a la api
//GET /api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)

public class Device
{
    private String id;
    private String name;
    private String type;

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

    
}