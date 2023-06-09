//Clase que hereda de device e implemeta el metodo updatedevice que realiza
//lamada a la api GET/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries{?keys,useStrictDataTypes} Get latest time-series value (getLatestTimeseries)
//Realizando la conversión propia a objeto WSN

public class WSN_Device extends Device {
    private double waterPreassure;
    private int flowRate;
    private double ph;
    private int TDS;

    public WSN_Device(String value) {
        super(value);
    }

    public WSN_Device(String valueId, String valueType) {
        super(valueId, valueType);
    }

    public double getWaterPressure() {
        return waterPressure;
    }

    public void setWaterPressure(double waterPressure) {
        this.waterPressure = waterPressure;
    }

    public int getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(int flowRate) {
        this.flowRate = flowRate;
    }

    public double getPh() {
        return ph;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }

    public int getTDS() {
        return TDS;
    }

    public void setTDS(int TDS) {
        this.TDS = TDS;
    }

    public WSN_Device(String valueId, String valueType, String valueName) {
        super(valueId, valueType, valueName);
    }

    @Override
    public void updateDevice() {
        // Implementación específica para WSN_Device
        // Actualizar dispositivo WSN
    }
}