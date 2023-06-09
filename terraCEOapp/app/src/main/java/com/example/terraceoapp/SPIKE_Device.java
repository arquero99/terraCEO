//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto Spike
public class SPIKE_Device extends Device {
    private double soilHumidity;
    private double airTemperature;
    private double soilTemperature;
    private double luminosity;
    private double airHumidity;
    public SPIKE_Device(String value) {
        super(value);
    }

    public SPIKE_Device(String valueId, String valueType) {
        super(valueId, valueType);
    }

    public SPIKE_Device(String valueId, String valueType, String valueName) {
        super(valueId, valueType, valueName);
    }
    public double getSoilHumidity() {
        return soilHumidity;
    }

    public void setSoilHumidity(double soilHumidity) {
        this.soilHumidity = soilHumidity;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getSoilTemperature() {
        return soilTemperature;
    }

    public void setSoilTemperature(double soilTemperature) {
        this.soilTemperature = soilTemperature;
    }

    public double getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(double luminosity) {
        this.luminosity = luminosity;
    }

    public double getAirHumidity() {
        return airHumidity;
    }

    public void setAirHumidity(double airHumidity) {
        this.airHumidity = airHumidity;
    }

    @Override
    public void updateDevice() {
        // Implementación específica para SPIKE_Device
        // Actualizar dispositivo SPIKE
    }
}