package com.dcs.googlempasejemplo1.controlador.mapas.altura;

/**
 * Created by daniel on 20/06/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dcs.googlempasejemplo1.R;
import com.dcs.googlempasejemplo1.controlador.persistencia.conexion.UrlServicio;
import com.dcs.googlempasejemplo1.vistas.mensajes.Mensajes;
import com.dcs.googlempasejemplo1.modelo.ubicacion.Antenna;
import com.dcs.googlempasejemplo1.controlador.persistencia.archivo.InterpreteJSON;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by daniel on 09/06/2016.
 */
public class TareaConexionAltura extends AsyncTask<Void, Void, String> {
    private UrlServicio conexion;
    private Context mContext;
    private ProgressDialog dialog = null;
    private Handler mHandler;
    private Message msg;
    private Bundle bundle;
    private Antenna antenna;
    private Marker marca;
    private InterpreteJSON json;
    private Mensajes mensaje;

    public TareaConexionAltura(Context context, Handler mHandler, ProgressDialog dialog, String url, Antenna antenna, Marker marca, InterpreteJSON json) {
        mContext = context;
        this.mHandler = mHandler;
        this.dialog = dialog;
        this.antenna = antenna;
        this.json = json;
        this.marca = marca;
        conexion = new UrlServicio(url);
        mensaje = new Mensajes(mContext);
    }

    public TareaConexionAltura(Context context, Handler mHandler, String url, Antenna antenna, Marker marca, InterpreteJSON json) {
        mContext = context;
        this.mHandler = mHandler;
        this.antenna = antenna;
        this.json = json;
        this.marca = marca;
        mensaje = new Mensajes(mContext);
        conexion = new UrlServicio(url);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        msg = mHandler.obtainMessage(1);
        bundle = new Bundle();
        bundle.putString("result", "Envio peticion");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        iniciarDialogo();
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            conexion.getJSON();
        } catch (Exception e) {
            mensaje.Toast("Revise su conexion a internet");
            cancelarDialogo();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String strings) {
        cancelarDialogo();
        String salida = conexion.getRespuesta();
        actualizarAntena(salida);
        msg = mHandler.obtainMessage(2);
        bundle = new Bundle();
        bundle.putString("result", mContext.getString(R.string.termino));
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }


    private void iniciarDialogo() {
        if (dialog != null) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage(mContext.getResources().getString(R.string.Descargando));
            dialog.setCancelable(false);
            dialog.show();
        } else {
            mensaje.Toast(mContext.getResources().getString(R.string.Descargando));
        }
    }

    private void cancelarDialogo() {
        if (dialog != null)
            if (dialog.isShowing()) {
                dialog.dismiss();
            } else mensaje.cancelToast();

    }

    private void actualizarAntena(String salida) {
        antenna.setAltura(Double.parseDouble(json.altura(salida)));
        marca.setTitle(antenna.getMunicipio());
    }


}
