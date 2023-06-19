package com.example.terraceoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements Marker.OnMarkerClickListener {
    private MapView map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    LocationManager mLocationManager;
    List<Location> locationsList = new ArrayList<>();

    DeviceManager dManager;

    //List<GeoPoint> puntosRuta = new ArrayList<>();
    //pwdmanager testConfig=new pwdmanager();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Realizar el log out aquí
        FirebaseAuth.getInstance().signOut();
        // Otros pasos necesarios para cerrar sesión en tu aplicación
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer el diseño de la actividad principal
        setContentView(R.layout.activity_main);

        //Obtener valores de email y contraseña
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");


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
        //locationsList.add(new Location(40.38999, -3.65518));        // Crear una lista de ubicaciones (incluyendo el punto de inicio y la ubicación actual)
        locationsList.add(locActual);

        dManager = new DeviceManager(email, password);
        if (!dManager.obtainTokenFromTB_API()) //Mostrar Mensaje cuenta no corresponde con TB account. Volver al login
        {
            Toast.makeText(MainActivity.this, "La cuenta no corresponde con TB account", Toast.LENGTH_SHORT).show(); //Mostrar el mensaje en pantalla
            FirebaseAuth.getInstance().signOut();   //Logout FireBase
            Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);  // Redirigir al inicio de sesión
            startActivity(intent2);
            finish(); // Opcionalmente, finalizar la actividad actual para que no se pueda volver atrás
        }
        else
        {
            dManager.obtainDevicesFromTB_API();
            if (dManager.relatedDevices.isEmpty())
            {
                Toast.makeText(MainActivity.this, "No se han encontrado dispositivos", Toast.LENGTH_SHORT).show(); // Mostrar mensaje de "No se han encontrado dispositivos"
                Button buttonRefresh = findViewById(R.id.button_refresh); //Botón de refresco
                buttonRefresh.setVisibility(View.VISIBLE);
                buttonRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dManager.obtainDevicesFromTB_API();
                        buttonRefresh.setVisibility(View.INVISIBLE);
                    }
                });

            }
            else //Lista de dispositivos relacionados no vacía
            {
                MeteoAPIClient meteoClient = new MeteoAPIClient();    //Creamos API Meteo
                for (Device dev : dManager.relatedDevices) {
                    Location devLocation = dev.getPosition();
                    meteoClient.obtainForecast(dev); //Levar al onClick.
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
        }
        // Agregar marcadores para cada ubicación en la lista
        for (Location loc : locationsList) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            map.getOverlays().add(m);
            //puntosRuta.add(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// Permiso de ubicación otorgado, puedes obtener la ubicación
                getLastKnownLocation();
            } else {
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
    //CLASE SUGERIDA POR CHATGPT. NO SE SI ES LA MANERA DE SOLUCIONAR. LUISO?
        @Override
        protected Void doInBackground(Void... params) {
            // Aquí debes mover el código que realiza las operaciones de red
            // por ejemplo, llamadas a la API de TB o cualquier otra operación de red
            dManager.obtainDevicesFromTB_API();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Aquí puedes realizar cualquier acción que necesites después de completar las operaciones de red
            // Por ejemplo, actualizar la interfaz de usuario con los datos obtenidos
            if (dManager.relatedDevices.isEmpty()) {
                // ...
            } else {
                // ...
            }
        }
    }
}