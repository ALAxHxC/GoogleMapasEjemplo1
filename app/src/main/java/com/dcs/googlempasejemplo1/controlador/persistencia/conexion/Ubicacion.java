package com.dcs.googlempasejemplo1.controlador.persistencia.conexion;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dcs.googlempasejemplo1.R;
import com.dcs.googlempasejemplo1.controlador.mapas.altura.TareaConexionAltura;
import com.dcs.googlempasejemplo1.controlador.mapas.camino.TrazarRuta;
import com.dcs.googlempasejemplo1.controlador.persistencia.archivo.InterpreteJSON;
import com.dcs.googlempasejemplo1.controlador.persistencia.preferencias.Preferencias;
import com.dcs.googlempasejemplo1.modelo.ubicacion.Antenna;
import com.dcs.googlempasejemplo1.vistas.mensajes.Mensajes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 17/06/2016.
 */
public class Ubicacion {
    private ProgressDialog pDialog;
    private Context mContext;
    private Mensajes mensajes;
    private InterpreteJSON json;
    private Preferencias preferencias;
    private GoogleMap mapa;
    private HashMap<Marker, Polyline> posiblespuntos;
    private HashMap<Marker, Polyline> posiblesrutas;

    public Ubicacion(Context mContext, GoogleMap mMap) {
        this.mContext = mContext;
        mensajes = new Mensajes(mContext);
        json = new InterpreteJSON(mContext);
        preferencias = new Preferencias(mContext);
        this.mapa = mMap;
        posiblespuntos = new HashMap<>();
        posiblesrutas = new HashMap<>();
    }

    public void obtenerAlturapunto(Antenna antenna, Marker marca) {
        TareaConexionAltura conexion = new TareaConexionAltura(mContext, mHandlerWS, pDialog, generarUrl(antenna), antenna, marca, json);
        Log.println(Log.ASSERT, "JSONURL", generarUrl(antenna));
        conexion.execute();
    }

    public void obtenerAlturapuntoJSON(Antenna antenna, Marker marca) {
        TareaConexionAltura conexion = new TareaConexionAltura(mContext, mHandlerWS, generarUrl(antenna), antenna, marca, json);
        conexion.execute();
    }

    private String generarUrl(Antenna antenna) {
        return mContext.getString(R.string.jsonURL)
                + antenna.getLatitud() + "," + antenna.getLongitud() + "&"
                + mContext.getString(R.string.jsonkey) + mContext.getString(R.string.api_key);
    }

    //JSON
    private final Handler mHandlerWS = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:

                    String respuesta = msg.getData().getString("result");
                    if (respuesta.startsWith("ERROR")) {
                        mensajes.Toast(mContext.getResources().getString(R.string.fallo_internet));
                    } else {
                        //    Log.println(Log.ASSERT, "json", "Altura Dada");
                    }
                    break;
            }
        }
    };

    public void encontrarPuntos(Marker marker, List<Marker> lista) {
        List<Marker> marcas = new ArrayList<>();
        for (Marker marca : lista) {
            if (SphericalUtil.computeDistanceBetween(marker.getPosition(), marca.getPosition()) <= preferencias.getRadio()) {

                marcas.add(marca);
            }
        }
        crearLineas(marcas, marker);
        crearRutas(marcas, marker);
    }

    public void crearLineas(List<Marker> marcas, Marker puntocentral) {
        for (Marker marca : marcas) {
            posiblespuntos.put(marca, CrearLinea(marca, puntocentral));
        }

    }

    public void crearRutas(List<Marker> marcas, Marker puntocentral) {
        for (Marker marca : marcas) {
            Polyline polyline = mapa.addPolyline(new PolylineOptions());
            posiblesrutas.put(marca, polyline);
            TrazarRuta ruta = new TrazarRuta(mapa, puntocentral.getPosition(), marca.getPosition(), mContext, polyline);
            ruta.GenerarCamino();
        }
        Log.println(Log.ASSERT, "RUTAS", marcas.size() + "");
    }


    public Polyline CrearLinea(Marker marca, Marker central) {
        try {
            Polyline linea = mapa.addPolyline(new PolylineOptions().geodesic(true));
            linea.setClickable(true);
            linea.setWidth((float) 0.8);
            linea.setColor(Color.BLUE);
            linea.setPoints(Arrays.asList(marca.getPosition(), central.getPosition()));
            linea.setVisible(true);
            return linea;
        } catch (Exception ex) {
            Log.println(Log.ASSERT, "POLYLINE", ex.toString());
            ex.printStackTrace();
            return null;
        }
    }

    public void limpiarpuntos() {
        Iterator it = posiblespuntos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            Polyline linea = (Polyline) e.getValue();
            linea.remove();
        }
        Iterator  it2 = posiblesrutas.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry e1 = (Map.Entry) it2.next();
            try {
                Polyline linea = (Polyline) e1.getValue();
                linea.setVisible(false);
                linea.remove();
            } catch (NullPointerException ex) {
                Log.println(Log.ASSERT, "Camini nulo", "");
            }
        }
        posiblespuntos = new HashMap<>();
        posiblesrutas = new HashMap<>();


    }

    public HashMap<Marker, Polyline> getPosiblespuntos() {
        return posiblespuntos;
    }
}
