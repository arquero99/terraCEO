package com.example.terraceoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.LocationManager;
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



public class MainActivity extends AppCompatActivity {

    private MapView map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    LocationManager mLocationManager;

    List<Location> locationsList = new ArrayList<>();
    List<GeoPoint> puntosRuta = new ArrayList<>();

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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso de ubicación si aún no se ha otorgado
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Los permisos ya están otorgados, puedes obtener la ubicación
            locActual = getLastKnownLocation();
        }

        // Crear una lista de ubicaciones (incluyendo el punto de inicio y la ubicación actual)
        locationsList.add(new Location(40.38999, -3.65518));
        locationsList.add(locActual);

        //Hay que probar esta:
        DeviceManager dManager=new DeviceManager(email,password);
        if(!dManager.obtainTokenFromTB_API())
        {
            //Mostrar Mensaje cuenta no corresponde con TB account. Volver al login
            // Mostrar el mensaje en pantalla
            Toast.makeText(MainActivity.this, "La cuenta no corresponde con TB account", Toast.LENGTH_SHORT).show();
            // Logout de Firebase
            FirebaseAuth.getInstance().signOut();
            // Redirigir al inicio de sesión
            Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent2);
            finish(); // Opcionalmente, finalizar la actividad actual para que no se pueda volver atrás
        }
        else
        {
            dManager.obtainDevicesFromTB_API();
            if(dManager.relatedDevices.isEmpty())
            {
                //Mostrar Mensaje no se han encontrado dispositivos y poner boton de
                // refresco asociado con dManager.obtainDevicesFromTB_API();

                // Mostrar mensaje de "No se han encontrado dispositivos"
                Toast.makeText(MainActivity.this, "No se han encontrado dispositivos", Toast.LENGTH_SHORT).show();
                // Botón
                Button buttonRefresh = findViewById(R.id.button_refresh);
                buttonRefresh.setVisibility(View.VISIBLE);

                buttonRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dManager.obtainDevicesFromTB_API();
                    }
                });

            }
            else
            {

                MeteoAPIClient meteoClient=new MeteoAPIClient();    //Creamos API Meteo
                for (Device dev : dManager.relatedDevices)
                {
                    Location devLocation=dev.getPosition();
                    locationsList.add(devLocation);
                    meteoClient.obtainForecast(devLocation.getLatitude(), devLocation.getLongitude());
                    Marker m = new Marker(map);
                    m.setPosition(new GeoPoint(devLocation.getLatitude(), devLocation.getLongitude()));
                    m.setTitle(dev.getName());
                    //Añadir metodo abstracto de devide getDataString, que devuelva un string con los datos captado para poner como descripcion.

                }
            }
        }
        // Agregar marcadores y puntos de ruta para cada ubicación en la lista
        for (Location loc : locationsList) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            //m.setTitle(loc.getName());
            //m.setSnippet(loc.getDescription());
            map.getOverlays().add(m);
            puntosRuta.add(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
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
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
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
                if (bestLocation != null){
                    myLocation = new Location(bestLocation.getLatitude(),
                            bestLocation.getLongitude());
                }
            }
        }
        return myLocation;
                }
}