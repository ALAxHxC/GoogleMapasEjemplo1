package com.dcs.googlempasejemplo1.controlador.mapas.altura;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dcs.googlempasejemplo1.controlador.persistencia.conexion.Internet;
import com.dcs.googlempasejemplo1.modelo.ubicacion.PuntoMedio;
import com.dcs.googlempasejemplo1.vistas.mensajes.Mensajes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 23/06/2016.
 */
public class ObtenerAlturas {
    private HashMap<Marker, Polyline> puntosposibles;
    private List<PuntoMedio> puntosmedios;
    private GoogleMap map;
    private int intervalo;
    private Context mContext;
    private ProgressDialog dialog;
    private int contador = 0;
    private Mensajes mensajes;

    public ObtenerAlturas(HashMap<Marker, Polyline> puntosposibles, GoogleMap map, int intervalo, Context mContext) {
        this.map = map;
        this.puntosposibles = puntosposibles;
        this.intervalo = intervalo;
        mensajes = new Mensajes(mContext);
        this.mContext = mContext;
        dialog = new ProgressDialog(mContext);
    }

    public void obtenerPuntos() {
        Iterator it = puntosposibles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            Polyline linea = (Polyline) e.getValue();
            puntosmedios = alturas(puntosMedios(linea.getPoints().get(0), linea.getPoints().get(1)));
            Log.println(Log.ASSERT, "NUMERO DE POS", puntosmedios.size() + "");
            //SphericalUtil
        }
    }

    public void descargaraltura() {
        int j = 0;
        if (Internet.SalidaInternet(mContext)) {
            dialog.setMax(puntosmedios.size());
            dialog.setTitle("Descargando alturas puntos medios");
            dialog.setIndeterminate(false);
            dialog.show();
            for (PuntoMedio punto : puntosmedios) {
                TareaAltura tarea = new TareaAltura(punto, mContext, mHandlerWS, j);
                tarea.execute();
                j++;
            }
        } else {
            mensajes.Toast("NO hay conexion a internet imposible revisar altura");
        }
    }

    private List<LatLng> puntosMedios(LatLng a, LatLng b) {
        Double distancia = SphericalUtil.computeDistanceBetween(a, b);
        List<LatLng> listapuntos = new ArrayList<>();
        double contador = intervalo;
        while (contador <= distancia) {
            listapuntos.add(SphericalUtil.interpolate(a, b, (contador / distancia)));
            contador += intervalo;
        }
        return listapuntos;
    }

    public List<PuntoMedio> alturas(List<LatLng> lista) {
        List<PuntoMedio> alturas = new ArrayList<>();
        for (LatLng punto : lista) {
            PuntoMedio antenna = new PuntoMedio(punto);
            alturas.add(antenna);
        }
        return alturas;
    }


    //JSON
    private final Handler mHandlerWS = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:

                    contador = msg.getData().getInt("result");
                    dialog.setProgress(contador);
                    if (contador == puntosmedios.size() - 1) {
                        dialog.dismiss();
                    }
                    Log.println(Log.ASSERT, "ALTURAS", "!TERMINO");
                    break;
            }
        }
    };

}
