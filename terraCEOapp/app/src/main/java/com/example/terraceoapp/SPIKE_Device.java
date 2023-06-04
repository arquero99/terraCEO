//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto Spike

public class Spike
{
    private double soilHumidity;
    private double airTemperature;
    private double soilTemperature;
    private double luminosity;
    private double airHumidity;
}