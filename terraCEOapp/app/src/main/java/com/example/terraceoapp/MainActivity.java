package com.example.terraceoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

//
import com.google.firebase.auth.FirebaseAuth;

import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements Marker.OnMarkerClickListener {
    private MapView map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    LocationManager mLocationManager;
    List<Location> locationsList = new ArrayList<>();
    DeviceManager dManager;
    NetworkTask obtainDevices;
    private Handler handler;
    private Runnable periodicTask;
    private ProgressBar spinner;
    private Timer timer;


    //List<GeoPoint> puntosRuta = new ArrayList<>();
    Credentials creds=new Credentials();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Realizar el log out aquí
        FirebaseAuth.getInstance().signOut();
        handler.removeCallbacks(periodicTask);
        // Otros pasos necesarios para cerrar sesión en tu aplicación
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener valores de email y contraseña
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");

        dManager=new DeviceManager(creds.email, creds.pwd);
        dManager.obtainTokenFromTB_API();

        // Establecer el diseño de la actividad principal
        setContentView(R.layout.activity_main);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        // Configurar el agente de usuario de la biblioteca OSMDroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Obtener la referencia al mapa desde el diseño
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setBuiltInZoomControls(true);

        // Establecer el punto de inicio (coordenadas de Madrid) y el nivel de zoom
        GeoPoint startPoint = new GeoPoint(40.416775, -3.703790);
        map.getController().setZoom(15);
        map.getController().setCenter(startPoint);

        // Crear un marcador en el punto de inicio
        Marker marker = new Marker(map);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);

        // Obtener la ubicación actual del dispositivo
        Location locActual = null;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)       // Solicitar permiso de ubicación si aún no se ha otorgado
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            locActual = getLastKnownLocation();     // Los permisos ya están otorgados, puedes obtener la ubicación
        }
        locationsList.add(new Location(40.38999, -3.65518));        // Crear una lista de ubicaciones (incluyendo el punto de inicio y la ubicación actual)
        locationsList.add(locActual);

        boolean continueLoop=dManager.getConnectedStatus()!=ConnStatus.Connected;
        boolean displayMsg=true;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkConnectionStatus();
                    }
                });
            }
        }, 0, 1000);
        /*
        while(continueLoop)
        {
            if(spinner.getVisibility()!=View.VISIBLE)
            {
                spinner.setVisibility(View.VISIBLE);
            }
            if(displayMsg)
            {
                Toast.makeText(MainActivity.this,"Conectando",Toast.LENGTH_SHORT).show();
                displayMsg=false;
            }
            if(dManager.getConnectedStatus()==ConnStatus.Refused)
            {
                spinner.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "La cuenta no corresponde con TB account", Toast.LENGTH_SHORT).show(); //Mostrar el mensaje en pantalla
                FirebaseAuth.getInstance().signOut();   //Logout FireBase
                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);  // Redirigir al inicio de sesión
                startActivity(intent2);
                continueLoop=false;
                finish(); // Opcionalmente, finalizar la actividad actual para que no se pueda volver atrás
            }
            else continueLoop=(dManager.getConnectedStatus()!=ConnStatus.Connected);
        }
         */
        spinner.setVisibility(View.GONE);
        obtainDevices=new NetworkTask();
        //obtainDevices.execute();
        Toast.makeText(MainActivity.this, "Conectado con la cuenta de Thingsboard. Obteniendo dispositivos", Toast.LENGTH_SHORT).show();

        // Agregar marcadores para cada ubicación en la lista
        if(!locationsList.isEmpty()) {
            for (Location loc : locationsList) {
                if (loc != null)
                {
                    Marker m = new Marker(map);
                    m.setPosition(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                    map.getOverlays().add(m);
                    //puntosRuta.add(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                }
            }
        }
        handler = new Handler(Looper.getMainLooper());
        periodicTask = new Runnable() {
            @Override
            public void run() {
                // Actualiza lista de dispositivos cada minuto
                obtainDevices.execute();
                // Programar la próxima ejecución después de 1 minuto
                handler.postDelayed(this, 60 * 1000); // 60 segundos * 1000 ms
            }
        };
        handler.post(periodicTask);
    }
    public void updateMap()
    {
        map.getOverlays().clear();
        for (Location loc : locationsList)
        {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            map.getOverlays().add(m);
        }
        for(Device dev : dManager.relatedDevices)
        {
            Location devLocation = dev.getPosition();
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(devLocation.getLatitude(), devLocation.getLongitude()));
            m.setTitle(dev.getName());
            if(dev.getId()!=null)m.setSnippet(dev.getId());
            if(dev.getDescription()!=null)m.setSubDescription(dev.getDescription());
            switch (dev.getType())
            {
                case WSN:
                    m.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.wsn_icon, null));
                    break;
                case METEO:
                    m.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.meteo_icon,null));
                    break;
                case SPIKE:
                    m.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.spike_icon,null));
                default:
                    break;
            }
            map.getOverlays().add(m);
        }
    }

    public void searchDevices()
    {
        Toast.makeText(MainActivity.this, "No se han encontrado dispositivos", Toast.LENGTH_SHORT).show(); // Mostrar mensaje de "No se han encontrado dispositivos"
        Button buttonRefresh = findViewById(R.id.button_refresh); //Botón de refresco
        buttonRefresh.setVisibility(View.VISIBLE);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainDevices.execute();
                buttonRefresh.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación otorgado, puedes obtener la ubicación
                getLastKnownLocation();
            } else
            {
                // Permiso de ubicación denegado, debes manejar este caso según tus necesidades
            }
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        android.location.Location bestLocation = null;
        Location myLocation = null;
        // Obtener la última ubicación conocida
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                android.location.Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
                if (bestLocation != null) {
                    myLocation = new Location(bestLocation.getLatitude(),
                            bestLocation.getLongitude());
                }
            }
        }
        return myLocation;
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        Device targetDevice = dManager.searchDeviceByName(marker.getTitle());
        Button buttonTelemetries = findViewById(R.id.getDeviceTelems);
        buttonTelemetries.setText("Telemetries");
        buttonTelemetries.setVisibility(View.VISIBLE);
        buttonTelemetries.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonTelemetries.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), DisplayDeviceDetails.class);
                        intent.putExtra("device",targetDevice);
                        startActivity(intent);
                        finish();
                    }
                });
        return false;
    }

    private class NetworkTask extends AsyncTask<Void, Void, Void> {
    //NO SE SI ES LA MANERA DE SOLUCIONAR. LUISO?
        @Override
        protected Void doInBackground(Void... params) {
            dManager.obtainDevicesFromTB_API();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dManager.relatedDevices.isEmpty()) {
                searchDevices();
                // ...
            } else {
                updateMap();
                // ...
            }
        }
    }

    private void checkConnectionStatus() {
        if (dManager.getConnectedStatus() == ConnStatus.Refused) {
            spinner.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "La cuenta no corresponde con TB account", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent2);
            finish();
        } else if (dManager.getConnectedStatus() == ConnStatus.Connected) {
            // Si la conexión está establecida, detén el temporizador y continúa con el flujo normal
            timer.cancel();
            obtainDevices.execute();
            Toast.makeText(MainActivity.this, "Conectado con la cuenta de Thingsboard. Obteniendo dispositivos", Toast.LENGTH_SHORT).show();
        }
    }
}