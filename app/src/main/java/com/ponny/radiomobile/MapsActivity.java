package com.ponny.radiomobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ponny.radiomobile.controlador.graficas.Graficador;
import com.ponny.radiomobile.controlador.mapas.altura.ObtenerAlturas;
import com.ponny.radiomobile.controlador.mapas.ubicacion.MiUbicacion;
import com.ponny.radiomobile.controlador.mapas.ubicacion.Ubicacion;
import com.ponny.radiomobile.controlador.persistencia.archivo.LeerJson;
import com.ponny.radiomobile.controlador.persistencia.conexion.Internet;
import com.ponny.radiomobile.controlador.persistencia.preferencias.Preferencias;
import com.ponny.radiomobile.modelo.ubicacion.Antenna;
import com.ponny.radiomobile.modelo.ubicacion.Antennas;
import com.ponny.radiomobile.modelo.ubicacion.PosicionGeografica;
import com.ponny.radiomobile.vistas.listas.AdaptadorDistancias;
import com.ponny.radiomobile.vistas.mensajes.Mensajes;

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
    private ImageButton buttonMiUbicacion;
    private ImageButton buttonRadio;
    private Marker markerInicial;
    private boolean puntocentral;
    private boolean antenna;
    private Mensajes mensajes;
    private Antennas antennas;
    private Preferencias preferencias;
    //lista
    private AdaptadorDistancias adaptadorDistancias;
    private GridView listaAntennas;
    //graficos

    private Circle circle;
    private Ubicacion ubicacion;
    private List<Marker> marcas;
    //antenas existentes
    private LeerJson jsonAtenas;
    private TabHost tabshost;
    private TabHost.TabSpec mapa, annteas, alturas, grafica;
    private ExpandableListView listaAlturas;
    private TextView puntoideal;
    //chart
    private LineChart linechart;
    //mi ubicacion
    MiUbicacion miubicacion;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //orientacion
    private void orientacion() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientacion();
        Log.println(Log.ASSERT, "NUEVA COFIG", "NEUVA");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orientacion();
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        cargarvistas();
        inicializar();
        preferencias = new Preferencias(MapsActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.radio_nuevo:
                mensajes.SetRadio(getString(R.string.radio_nuevo_text), getString(R.string.radio_actual) + " " + preferencias.getRadio() + " m", preferencias, this);
                return true;
            case R.id.intervalo_nuevo:
                mensajes.SetIntervalo(getString(R.string.intervalo_nuevo_text), getString(R.string.intervalo_nuevo_text) + preferencias.getInvervalo() + " m", preferencias);
                return true;
            case R.id.recargarAlturas:
                if (Internet.SalidaInternet(MapsActivity.this)) {
                    cargarPuntoIdeal();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ubicacion = new Ubicacion(this, mMap);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        cargarAssets();
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


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.println(Log.ASSERT, "CONT", connectionResult.getErrorMessage());
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (antenna) {
            antenna = false;
            registrarAntena(latLng.latitude, latLng.longitude, getString(R.string.atenna));
            return;
        }
        if (puntocentral) {
            puntocentral = false;
            poscentral = latLng;
            AgregarPositionCentral(poscentral);
            return;

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

    }

    @Override
    public void onClick(View v) {
        if (v == buttonCurrent) {

        }
    }

    //mover a ubicacion actual
    private void getCurrentLocation() {
        miubicacion = new MiUbicacion(mMap, markerInicial, this, googleApiClient, MapsActivity.this);
        miubicacion.buscarMiUbicacion();
    }


    public void AgregarPositionCentral(LatLng latLng) {
        if (markerInicial != null) {
            markerInicial.remove();
            ubicacion.limpiarpuntos();
            markerInicial = null;
        }
        if (circle != null) {
            circle.remove();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 8.0f));
        markerInicial = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(getResources().getString(R.string.mi_ubicacion)
        ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        crearCircunferencia(preferencias.getRadio());
        ubicacion.encontrarPuntos(markerInicial, marcas);
        markerInicial.showInfoWindow();

    }

    private void inicializar() {
        //Initializing googleapi client
        marcas = new ArrayList<>();
        antennas = new Antennas();
        mensajes = new Mensajes(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        puntocentral();
        antena();
        miUbicacion();
        cargarCircunferencia();

    }

    private void cargarvistas() {
        buttonSave = (ImageButton) findViewById(R.id.buttonSave);
        buttonCurrent = (ImageButton) findViewById(R.id.buttonCurrent);
        buttonView = (ImageButton) findViewById(R.id.buttonView);
        buttonMiUbicacion = (ImageButton) findViewById(R.id.imageButtonMiUbicacion);
        buttonAntena = (ImageButton) findViewById(R.id.imageButtonAtenna);
        listaAntennas = (GridView) findViewById(R.id.gridViewLista);
        listaAlturas = (ExpandableListView) findViewById(R.id.expandableListViewListaAlturas);
        puntoideal = (TextView) findViewById(R.id.textViewTextViewPuntoFactible);
        linechart = (LineChart) findViewById(R.id.chart);
        CargarHost();
        configurarHost();
    }

    private void CargarHost() {
        tabshost = (TabHost) findViewById(R.id.tabHost);
        tabshost.setup();
        mapa = tabshost.newTabSpec(getString(R.string.mapa_host));
        mapa.setContent(R.id.mapa);
        mapa.setIndicator(getString(R.string.mapa_host));
        tabshost.addTab(mapa);
        annteas = tabshost.newTabSpec(getString(R.string.atennas_host));
        annteas.setContent(R.id.lista);
        annteas.setIndicator(getString(R.string.atennas_host));
        tabshost.addTab(annteas);
        alturas = tabshost.newTabSpec(getString(R.string.alturas_hots));
        alturas.setContent(R.id.altura);
        alturas.setIndicator(getString(R.string.alturas_hots));
        tabshost.addTab(alturas);
        grafica = tabshost.newTabSpec(getString(R.string.graficas));
        grafica.setContent(R.id.graficaChart);
        grafica.setIndicator(getString(R.string.graficas));
        tabshost.addTab(grafica);
    }

    private void configurarHost() {
        tabshost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equalsIgnoreCase(annteas.getTag())) {
                    cargarLista();
                }
                if (tabId.equalsIgnoreCase(alturas.getTag())) {
                    if (Internet.SalidaInternet(MapsActivity.this)) {
                        if (listaAlturas.getAdapter() == null)
                            cargarPuntoIdeal();
                    } else {
                        mensajes.Toast(getString(R.string.no_alturas));
                    }
                }
            }
        });
    }

    private void cargarLista() {
        if (ubicacion != null) {
            if (ubicacion.getPuntos().size() <= 0) {
                mensajes.Toast(getString(R.string.no_antenas_pos));
            }
            adaptadorDistancias = new AdaptadorDistancias(ubicacion.getPuntos(), MapsActivity.this);
            listaAntennas.setAdapter(adaptadorDistancias);

        } else {
            mensajes.Toast(getString(R.string.no_atennas));
        }
    }

    private void cargarPuntoIdeal() {
        listaAlturas.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                PosicionGeografica posicionGeografica = (PosicionGeografica) parent.getExpandableListAdapter().getGroup(groupPosition);
                graficar(posicionGeografica);
                return false;
            }
        });
        ObtenerAlturas alturas = new ObtenerAlturas(ubicacion.getPuntos(), markerInicial, preferencias.getInvervalo(), MapsActivity.this, listaAlturas, puntoideal);
        alturas.obtenerPuntos();


    }

    private void graficar(PosicionGeografica posicionGeografica) {
        Graficador graficador = new Graficador(MapsActivity.this, posicionGeografica, linechart);
    }

    private void dialogoPosCentral() {
        mensajes.RegistrarPosCentral(mensajes.generarDialogo(getString(R.string.punto_central), getString(R.string.n_coordenadas)), this);
    }


    private void antena() {
        buttonAntena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensajes.opccionRegistroAntena(getString(R.string.agregar_antena), getString(R.string.n_coordenadas), MapsActivity.this);

            }
        });
    }


    private void miUbicacion() {
        Log.println(Log.ASSERT, "del", "entro");
        buttonMiUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.println(Log.ASSERT, "UBI", "CLICKUB");
                getCurrentLocation();
            }
        });
    }

    private void puntocentral() {
        puntocentral = false;
        buttonCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(getString(R.string.punto_central));
                builder.setTitle(getString(R.string.opccion));
                builder.setNegativeButton(getString(R.string.mapa_ubicar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajes.Toast(getResources().getString(R.string.mi_ubicacion));
                        puntocentral = true;
                    }
                });
                builder.setPositiveButton(getString(R.string.mapa_digitar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogoPosCentral();
                        puntocentral = true;
                    }
                });
                builder.show();
            }
        });

    }

    public void registrarAntena(double latitude, double longitude, String titulo) {
        Log.println(Log.ASSERT, "ti", titulo);
        Marker marca = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(titulo));
        Antenna antenna = new Antenna(marca.getPosition().latitude, marca.getPosition().longitude, 0.0);
        antenna.setMunicipio(titulo);
        marca.showInfoWindow();
        marcas.add(marca);
        antennas.setElementi(antenna);
    }

    private void cargarCircunferencia() {
        buttonMiUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    //circunferencia
    private void crearCircunferencia(int radio) {
        if (circle != null)
            circle.remove();
        circle = mMap.addCircle(new CircleOptions()
                        .center(poscentral)
                        .radius(radio)
                        .strokeColor(Color.CYAN)
                        .fillColor(Color.TRANSPARENT)
        );
    }

    public void iniciarDescarga(Antenna antenna, Marker marca) {
        ubicacion.obtenerAlturapunto(antenna, marca);
    }

    public void cargarAssets() {
        try {
            jsonAtenas = new LeerJson(getApplicationContext());
            jsonAtenas.AgregarAMapa(mMap, marcas, antennas.getListado());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public LatLng getPoscentral() {
        return poscentral;
    }

    public void setPoscentral(LatLng poscentral) {
        this.poscentral = poscentral;
    }

    public boolean isPuntocentral() {
        return puntocentral;
    }

    public void setPuntocentral(boolean puntocentral) {
        this.puntocentral = puntocentral;
    }

    public boolean isAntenna() {
        return antenna;
    }

    public void setAntenna(boolean antenna) {
        this.antenna = antenna;
    }
}
