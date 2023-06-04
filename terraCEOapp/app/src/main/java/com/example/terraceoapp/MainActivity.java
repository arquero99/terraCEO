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
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    LocationManager mLocationManager;

    List<Location> listaLocalizaciones = new ArrayList<>();
    List<GeoPoint> puntosRuta = new ArrayList<>();

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
        setContentView(R.layout.activity_main);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setBuiltInZoomControls(true);
        GeoPoint startPoint = new GeoPoint(40.416775, -3.703790); // coordenadas de Madrid
        map.getController().setZoom(15); // nivel de zoom
        map.getController().setCenter(startPoint);
        Marker marker = new Marker(map);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        Location locActual = null;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
// Los permisos ya están otorgados, puedes obtener la ubicación
//locActual = getLocation();
            locActual = getLastKnownLocation();
        }
        listaLocalizaciones.add(new Location("Parque Azorín", "Parque con amplias zonas verdes en Metro Buenos aires",40.38999, -3.65518));
        listaLocalizaciones.add(locActual);
        for (Location loc : listaLocalizaciones) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            m.setTitle(loc.getName());
            m.setSnippet(loc.getDescription());
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
                    myLocation = new Location("Luiso", "Su ultima posición", bestLocation.getLatitude(),
                            bestLocation.getLongitude());
                }
            }
        }
        return myLocation;
                }
}