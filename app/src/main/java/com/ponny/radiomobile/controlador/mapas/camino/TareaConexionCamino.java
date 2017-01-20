package com.ponny.radiomobile.controlador.mapas.camino;

import android.os.AsyncTask;

import com.ponny.radiomobile.controlador.persistencia.conexion.UrlServicio;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

/**
 * Created by daniel on 30/06/2016.
 */
public class TareaConexionCamino extends AsyncTask<String, Void, String> {

    private GoogleMap mapa;
    private UrlServicio servicio;
    private AgregarCamino agregarCamino;
private Polyline polyline;
    public TareaConexionCamino(String url, GoogleMap map,Polyline polyline) {
        mapa = map;
        servicio = new UrlServicio(url);
        this.polyline=polyline;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            servicio.getJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return servicio.getRespuesta();
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        agregarCamino = new AgregarCamino(json, mapa,polyline);

    }
}
