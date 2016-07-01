package com.dcs.googlempasejemplo1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.dcs.googlempasejemplo1.controlador.mapas.camino.TrazarRuta;
import com.dcs.googlempasejemplo1.controlador.persistencia.archivo.LeerJson;
import com.dcs.googlempasejemplo1.controlador.persistencia.conexion.Ubicacion;
import com.dcs.googlempasejemplo1.controlador.persistencia.preferencias.Preferencias;
import com.dcs.googlempasejemplo1.modelo.ubicacion.Antennas;
import com.dcs.googlempasejemplo1.vistas.mensajes.Mensajes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {

    private GoogleMap mMap;

    //Google ApiClient
    private GoogleApiClient googleApiClient;
    private LatLng poscentral;
    private ImageButton buttonSave;
    private ImageButton buttonCurrent;
    private ImageButton buttonView;
    private ImageButton buttonAntena;
    private ImageButton buttonRadio;
    private Marker markerInicial;
    private boolean puntocentral;
    private Mensajes mensajes;
    private Antennas antennas;
    //graficos

    private Circle circle;
    private Ubicacion ubicacion;
    private List<Marker> marcas;
    //antenas existentes
    private LeerJson jsonAtenas;

    //preferencias
    private Preferencias preferencias;

    //lineas


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        cargarvistas();
        inicializar();

    }


    private void inicializar() {
        //Initializing googleapi client

        marcas = new ArrayList<>();
        antennas = new Antennas();
        mensajes = new Mensajes(this);
        preferencias = new Preferencias(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.radio_nuevo:
                mensajes.SetRadio(getString(R.string.radio_nuevo_text),
                        getString(R.string.radio_actual) + (preferencias.getRadio() / 1000), preferencias);

                return true;
            case R.id.intervalo_nuevo:
                mensajes.SetIntervalo(getString(R.string.intervalo_nuevo_text),
                        getString(R.string.intervalo_actual) + preferencias.getInvervalo(), preferencias);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ubicacion = new Ubicacion(this, mMap);
        getCurrentLocation();
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);

    }

    //instanciadores
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if (puntocentral) {
            puntocentral = false;
            poscentral = latLng;
            AgregarPositionCentral(poscentral);
            return;

        } else {
            if (poscentral != null) {
                TrazarRuta ruta = new TrazarRuta(mMap, poscentral, latLng, MapsActivity.this, mMap.addPolyline(new PolylineOptions()));
                ruta.GenerarCamino();
            }
        }
        return;


    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        //  latitude = marker.getPosition().latitude;
        // longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonCurrent) {
            getCurrentLocation();
            moveMap();
        }
    }

    //mover a ubicacion actual
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            poscentral = new LatLng(0, 0);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            poscentral = new LatLng(location.getLongitude(), location.getLatitude());
            //moving the map to location
            moveMap();

        }
    }

    //Function to move the map
    private void moveMap() {
        //String to display current latitude and longitude
        //   String msg = latitude + ", " + longitude;
        //Creating a LatLng Object to store Coordinates
        LatLng latLng = poscentral;
        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location"))
                .showInfoWindow();//Adding a title
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void AgregarPositionCentral(LatLng latLng) {
        if (markerInicial != null) {
            markerInicial.remove();
            markerInicial = null;
        }
        if (circle != null) {
            circle.remove();
            ubicacion.limpiarpuntos();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 8.0f));
        markerInicial = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(getResources().getString(R.string.mi_ubicacion)
        ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        ubicacion.encontrarPuntos(markerInicial, marcas);
        markerInicial.showInfoWindow();

    }


    private void cargarvistas() {
        buttonSave = (ImageButton) findViewById(R.id.buttonSave);
        buttonCurrent = (ImageButton) findViewById(R.id.buttonCurrent);
        buttonView = (ImageButton) findViewById(R.id.buttonView);
        buttonAntena = (ImageButton) findViewById(R.id.imageButtonAtenna);
        buttonRadio = (ImageButton) findViewById(R.id.imageButtonRadio);
    }


}
