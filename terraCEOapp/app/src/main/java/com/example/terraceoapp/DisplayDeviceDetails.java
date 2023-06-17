package com.example.terraceoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class DisplayDeviceDetails extends AppCompatActivity {
    public Device targetDevice;
    private Handler handler;
    private Runnable runnable;

    /// Detalles del dispositivo en la vista
    TextView deviceName;
    TextView sensor1Name;
    TextView sensor2Name;
    TextView sensor3Name;
    TextView sensor4Name;
    TextView sensor1Data;
    TextView sensor2Data;
    TextView sensor3Data;
    TextView sensor4Data;

    //Forecast
    TextView[] days = new TextView[7];
    TextView[] precProb = new TextView[7];
    TextView[] precSum = new TextView[7];
    TextView[] temps = new TextView[7];
    TextView[] et0s = new TextView[7];

    public DisplayDeviceDetails(Device target) {
        this.targetDevice = target;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Nombre del dipositivo
        deviceName = findViewById(R.id.deviceName);
        deviceName.setText(targetDevice.getName());
        //Nombres de Sensores. No se actualizan
        sensor1Name = findViewById(R.id.sensorName1);
        sensor1Name.setText(targetDevice.getSensorName1());
        sensor2Name = findViewById(R.id.sensorName2);
        sensor2Name.setText(targetDevice.getSensorName2());
        sensor3Name = findViewById(R.id.sensorName3);
        sensor3Name.setText(targetDevice.getSensorName3());
        sensor4Name = findViewById(R.id.sensorName4);
        sensor4Name.setText(targetDevice.getSensorName4());
        //Forecast. No se actualiza
        displayForecast();
        // Iniciar el Handler
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Valores de los sensores. Se actualizan
                sensor1Data = findViewById(R.id.sensorData1);
                sensor1Data.setText(targetDevice.getSensorValue1());

                sensor2Data = findViewById(R.id.sensorData2);
                sensor2Data.setText(targetDevice.getSensorValue2());

                sensor3Data = findViewById(R.id.sensorData3);
                sensor3Data.setText(targetDevice.getSensorValue3());

                sensor4Data = findViewById(R.id.sensorData4);
                sensor4Data.setText(targetDevice.getSensorValue4());

                // Volver a ejecutar el Runnable después de 10 segundos
                handler.postDelayed(this, 10000);
            }
        };
    }
            @Override
            protected void onResume() {
                super.onResume();
                // Iniciar la actualización periódica al entrar en la actividad
                handler.postDelayed(runnable, 5000);
            }

            @Override
            protected void onPause() {
                super.onPause();
                // Detener la actualización periódica al salir de la actividad
                handler.removeCallbacks(runnable);
            }

            @Override
            protected void onDestroy() {
                super.onDestroy();
                //Vuelve al main activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

            public void displayForecast() {
                days[0] = findViewById(R.id.dia1);
                days[0].setText(targetDevice.getDeviceForecast().getDaily().getTime(0));
                precProb[0] = findViewById(R.id.prob1);
                precProb[0].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitation_probability_max(0));
                precSum[0] = findViewById(R.id.sum1);
                precSum[0].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitationSumArray(0));
                temps[0] = findViewById(R.id.temp1);
                temps[0].setText(targetDevice.getDeviceForecast().getDaily().getTemperatureMaxArray(0));
                et0s[0] = findViewById(R.id.et1);
                et0s[0].setText(targetDevice.getDeviceForecast().getDaily().getEvapotranspirationArray(0));

                days[1] = findViewById(R.id.dia2);
                days[1].setText(targetDevice.getDeviceForecast().getDaily().getTime(1));
                precProb[1] = findViewById(R.id.prob2);
                precProb[1].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitation_probability_max(1));
                precSum[1] = findViewById(R.id.sum2);
                precSum[1].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitationSumArray(1));
                temps[1] = findViewById(R.id.temp2);
                temps[1].setText(targetDevice.getDeviceForecast().getDaily().getTemperatureMaxArray(1));
                et0s[1] = findViewById(R.id.et2);
                et0s[1].setText(targetDevice.getDeviceForecast().getDaily().getEvapotranspirationArray(1));

                days[2] = findViewById(R.id.dia3);
                days[2].setText(targetDevice.getDeviceForecast().getDaily().getTime(2));
                precProb[2] = findViewById(R.id.prob3);
                precProb[2].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitation_probability_max(2));
                precSum[2] = findViewById(R.id.sum3);
                precSum[2].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitationSumArray(2));
                temps[2] = findViewById(R.id.temp3);
                temps[2].setText(targetDevice.getDeviceForecast().getDaily().getTemperatureMaxArray(2));
                et0s[2] = findViewById(R.id.et3);
                et0s[2].setText(targetDevice.getDeviceForecast().getDaily().getEvapotranspirationArray(2));

                days[3] = findViewById(R.id.dia4);
                days[3].setText(targetDevice.getDeviceForecast().getDaily().getTime(3));
                precProb[3] = findViewById(R.id.prob4);
                precProb[3].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitation_probability_max(3));
                precSum[3] = findViewById(R.id.sum4);
                precSum[3].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitationSumArray(3));
                temps[3] = findViewById(R.id.temp4);
                temps[3].setText(targetDevice.getDeviceForecast().getDaily().getTemperatureMaxArray(3));
                et0s[3] = findViewById(R.id.et4);
                et0s[3].setText(targetDevice.getDeviceForecast().getDaily().getEvapotranspirationArray(3));

                days[4] = findViewById(R.id.dia5);
                days[4].setText(targetDevice.getDeviceForecast().getDaily().getTime(4));
                precProb[4] = findViewById(R.id.prob5);
                precProb[4].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitation_probability_max(4));
                precSum[4] = findViewById(R.id.sum5);
                precSum[4].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitationSumArray(4));
                temps[4] = findViewById(R.id.temp5);
                temps[4].setText(targetDevice.getDeviceForecast().getDaily().getTemperatureMaxArray(4));
                et0s[4] = findViewById(R.id.et5);
                et0s[4].setText(targetDevice.getDeviceForecast().getDaily().getEvapotranspirationArray(4));

                days[5] = findViewById(R.id.dia6);
                days[5].setText(targetDevice.getDeviceForecast().getDaily().getTime(5));
                precProb[5] = findViewById(R.id.prob6);
                precProb[5].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitation_probability_max(5));
                precSum[5] = findViewById(R.id.sum6);
                precSum[5].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitationSumArray(5));
                temps[5] = findViewById(R.id.temp6);
                temps[5].setText(targetDevice.getDeviceForecast().getDaily().getTemperatureMaxArray(5));
                et0s[5] = findViewById(R.id.et6);
                et0s[5].setText(targetDevice.getDeviceForecast().getDaily().getEvapotranspirationArray(5));

                days[6] = findViewById(R.id.dia7);
                days[6].setText(targetDevice.getDeviceForecast().getDaily().getTime(6));
                precProb[6] = findViewById(R.id.prob7);
                precProb[6].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitation_probability_max(6));
                precSum[6] = findViewById(R.id.sum7);
                precSum[6].setText(targetDevice.getDeviceForecast().getDaily().getPrecipitationSumArray(6));
                temps[6] = findViewById(R.id.temp7);
                temps[6].setText(targetDevice.getDeviceForecast().getDaily().getTemperatureMaxArray(6));
                et0s[6] = findViewById(R.id.et7);
                et0s[6].setText(targetDevice.getDeviceForecast().getDaily().getEvapotranspirationArray(6));
            }
        }
