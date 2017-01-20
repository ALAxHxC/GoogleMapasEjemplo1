package com.ponny.radiomobile.controlador.mapas.camino;

import android.content.Context;

import com.ponny.radiomobile.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

/**
 * Created by daniel on 30/06/2016.
 */
public class TrazarRuta {
    private GoogleMap mapa;
    private Context mContext;
    private LatLng origin, dest;
    private Polyline polyline;

    public TrazarRuta(GoogleMap mapa, LatLng origin, LatLng dest, Context mContext, Polyline polyline) {
        this.polyline = polyline;
        this.mapa = mapa;
        this.origin = origin;
        this.dest = dest;
        this.mContext = mContext;
    }

    private String GenerarUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;


        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";
        String waypoints = "";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;


        // Building the url to the web service
        String url = mContext.getString(R.string.jsonRuta) + "?" + parameters;

        return url;
    }

    public void GenerarCamino() {
        TareaConexionCamino camino = new TareaConexionCamino(GenerarUrl(origin, dest), mapa,polyline);
        camino.execute();
    }

}
