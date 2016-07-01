package com.dcs.googlempasejemplo1.controlador.persistencia.archivo;

import android.content.Context;
import android.util.Log;

import com.dcs.googlempasejemplo1.R;
import com.dcs.googlempasejemplo1.modelo.ubicacion.Antenna;
import com.dcs.googlempasejemplo1.vistas.mensajes.Mensajes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created by daniel on 20/06/2016.
 */
public class LeerJson {
    private Context mContext;
    private String json;
    private Gson gson;
    private Mensajes mensajes;
    private JsonReader jsonReader;

    public LeerJson(Context mContext) {
        this.mContext = mContext;
        this.json = leerJson();
        gson = new Gson();
        mensajes = new Mensajes(mContext);
        Log.println(Log.ASSERT, "json", json);
    }



    private String leerJson() {
        try {
            InputStream is = mContext.getAssets().open(mContext.getString(R.string.antennas_json));
            Writer writer = new StringWriter();
            char[] buffer = new char[2048];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is
                ));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            String text = writer.toString();
            return text;

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }


    private void validarAltura(Antenna antenna, JSONObject objeto) {
        try {
            antenna.setAltura(Double.parseDouble(objeto.getString("altura")));

        } catch (JSONException e) {

            e.printStackTrace();
            return;
        }

    }

    private String render(String numero) {
        return numero.replace(",", "");
    }

    public void ConvertirJSON(List<Antenna> lista) throws JSONException {
        Antenna[] antennas = gson.fromJson(json, Antenna[].class);
        for (Antenna antenna : antennas) {
            lista.add(antenna);
        }
    }

    public void AgregarAMapa(GoogleMap map, List<Marker> marcas, List<Antenna> lista) throws JSONException {

        //   Ubicacion ubicacion = new Ubicacion(mContext, map);
        ConvertirJSON(lista);
        for (Antenna antenna : lista) {
            Marker marca = map.addMarker(new MarkerOptions().position(new LatLng(antenna.getLatitud(), -1 * antenna.getLongitud())).title(antenna.getMunicipio()));
         /*   if (Internet.SalidaInternet(mContext))
                ubicacion.obtenerAlturapuntoJSON(antenna, marca);
            else {
                mensajes.Toast("Imposible obtener altura");
            }*/
            marcas.add(marca);
        }
    }
}
