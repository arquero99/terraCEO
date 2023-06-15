package com.example.terraceoapp;

import java.util.List;

public class ForecastResponse {
    private double latitude;
    private double longitude;
    private double generationtime_ms;
    private int utc_offset_seconds;
    private String timezone;
    private String timezone_abbreviation;
    private double elevation;
    private DailyData daily;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getGenerationtime_ms() {
        return generationtime_ms;
    }

    public int getUtc_offset_seconds() {
        return utc_offset_seconds;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getTimezone_abbreviation() {
        return timezone_abbreviation;
    }

    public double getElevation() {
        return elevation;
    }

    public DailyData getDaily() {
        return daily;
    }

    public static class DailyData {
        private List<String> time;
        private List<Double> temperature_2m_max;
        private List<Double> precipitation_sum;
        private List<Double> et0_fao_evapotranspiration;

        public List<String> getTime() {
            return time;
        }

        public double[] getTemperatureMaxArray() {
            return listToArray(temperature_2m_max);
        }

        public double[] getPrecipitationArray() {
            return listToArray(precipitation_sum);
        }

        public double[] getEvapotranspirationArray() {
            return listToArray(et0_fao_evapotranspiration);
        }

        private double[] listToArray(List<Double> list) {
            double[] array = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            return array;
        }
    }
}
