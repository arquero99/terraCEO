//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto METEO

public class METEO_Device extends Device {

    private double baromPreassure;
    private double lightLux;
    private bool raining;

    public METEO_Device(String value) {
        super(value);
    }

    public METEO_Device(String valueId, String valueType) {
        super(valueId, valueType);
    }
    public double getBaromPressure() {
        return baromPressure;
    }

    public void setBaromPressure(double baromPressure) {
        this.baromPressure = baromPressure;
    }

    public double getLightLux() {
        return lightLux;
    }

    public void setLightLux(double lightLux) {
        this.lightLux = lightLux;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }
    public METEO_Device(String valueId, String valueType, String valueName) {
        super(valueId, valueType, valueName);
    }

    @Override
    public void updateDevice() {
        // Implementación específica para METEO_Device
        // Actualizar dispositivo METEO
    }
}