package com.example.terraceoapp;

import java.text.DecimalFormat;
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


    //////////////////CLASE DAILY DATA////////////////////////////
    public static class DailyData {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        private List<String> time;
        private List<Double> temperature_2m_max;
        private List<Double> precipitation_sum;
        private List<Double>precipitation_probability_max;
        private List<Double> et0_fao_evapotranspiration;

        public String getTime(int index)
        {
            return time.get(index);
        }

        public String getTemperatureMaxArray(int index)
        {
            return decimalFormat.format(precipitation_probability_max.get(index));
        }

        public String getPrecipitationSumArray(int index)
        {
            return decimalFormat.format(precipitation_sum.get(index));
        }

        public String getEvapotranspirationArray(int index)
        {
            return decimalFormat.format(et0_fao_evapotranspiration.get(index));
        }
        public String getPrecipitation_probability_max(int index)
        {
            return decimalFormat.format(precipitation_probability_max.get(index));
        }
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
        public double[] getPrecipitation_probability_max() {
            return listToArray(precipitation_probability_max);
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
