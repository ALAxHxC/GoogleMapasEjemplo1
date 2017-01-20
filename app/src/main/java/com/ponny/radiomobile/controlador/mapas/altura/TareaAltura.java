package com.ponny.radiomobile.controlador.mapas.altura;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ponny.radiomobile.R;
import com.ponny.radiomobile.controlador.persistencia.archivo.InterpreteJSON;
import com.ponny.radiomobile.controlador.persistencia.conexion.UrlServicio;
import com.ponny.radiomobile.modelo.ubicacion.PuntoMedio;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by daniel on 29/06/2016.
 */
public class TareaAltura extends AsyncTask<Void, Void, Void> {
    private PuntoMedio puntoMedio;
    private Context mContext;
    private InterpreteJSON json;
    private Handler mHandler;
    private int i;
    private UrlServicio conexion;
    private Message msg;
    private Bundle bundle;

    public TareaAltura(PuntoMedio puntoMedio, Context mContext, Handler mHandler, int i)

    {
        this.puntoMedio = puntoMedio;
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.i = i;
        json = new InterpreteJSON(mContext);
        conexion = new UrlServicio(generarUrl(puntoMedio.getPosition()));
    }


    private String generarUrl(LatLng latLng) {
        return mContext.getString(R.string.jsonURL)
                + latLng.latitude + "," + latLng.longitude + "&"
                + mContext.getString(R.string.jsonkey);
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            conexion.getJSON();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void strings) {

        String salida = conexion.getRespuesta();
        actualizarPunto(salida);
        msg = mHandler.obtainMessage(2);
        bundle = new Bundle();
        bundle.putInt("result", i);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

    private void actualizarPunto(String salida) {
        puntoMedio.setAltura(Double.parseDouble(json.altura(salida)));
    }



}
