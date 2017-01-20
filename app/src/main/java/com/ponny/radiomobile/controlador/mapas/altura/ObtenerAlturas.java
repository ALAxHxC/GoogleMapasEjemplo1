package com.ponny.radiomobile.controlador.mapas.altura;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ponny.radiomobile.R;
import com.ponny.radiomobile.controlador.persistencia.conexion.Internet;
import com.ponny.radiomobile.modelo.ubicacion.PosicionGeografica;
import com.ponny.radiomobile.modelo.ubicacion.Punto;
import com.ponny.radiomobile.modelo.ubicacion.PuntoMedio;
import com.ponny.radiomobile.vistas.listas.AdaptadorPuntos;
import com.ponny.radiomobile.vistas.mensajes.Mensajes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 23/06/2016.
 */
public class ObtenerAlturas {
    private List<Punto> puntosposibles;
    private List<PuntoMedio> puntosmedios;
    private List<PosicionGeografica> posicionGeograficas;
    private int intervalo;
    private Context mContext;
    private ProgressDialog dialog;
    private Mensajes mensajes;
    private Marker origin;
    private int contador = 0;
    private ExpandableListView lista;
    private TextView ideal;

    public ObtenerAlturas(List<Punto> puntosposibles, Marker origin, int intervalo, Context mContext, ExpandableListView lista, TextView ideal) {

        this.puntosposibles = puntosposibles;
        Log.println(Log.ASSERT, "ALTURA", "Puntos totales:" + puntosposibles.size() + "");
        this.intervalo = intervalo;
        mensajes = new Mensajes(mContext);
        this.mContext = mContext;
        this.origin = origin;
        dialog = new ProgressDialog(mContext);
        posicionGeograficas = new ArrayList<>();
        this.lista = lista;
        this.ideal = ideal;
    }

    public void obtenerPuntos() {
        for (Punto punto : puntosposibles) {
            puntosmedios = alturas(puntosMedios(punto.getMarker().getPosition(), origin.getPosition()));
            posicionGeograficas.add(new PosicionGeografica(punto, puntosmedios));
            descargaraltura();
        }
        lista.setAdapter(new AdaptadorPuntos(posicionGeograficas, mContext));


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
                esperar();
                j++;
            }
        } else {
            mensajes.Toast("NO hay conexion a internet imposible revisar altura");
        }
    }

    private List<LatLng> puntosMedios(LatLng a, LatLng b) {
        Double distancia = SphericalUtil.computeDistanceBetween(a, b);
        List<LatLng> listapuntos = new ArrayList<>();
        double contadorDis = 0;
        while (contadorDis <= (distancia + intervalo)) {
            listapuntos.add(SphericalUtil.interpolate(a, b, (contadorDis / distancia) >= 1 ? 0.99 : (contadorDis / distancia)));
            contadorDis += intervalo;
        }
        return listapuntos;
    }

    public List<PuntoMedio> alturas(List<LatLng> lista) {
        List<PuntoMedio> alturas = new ArrayList<>();
        for (LatLng punto : lista) {
            PuntoMedio antenna = new PuntoMedio(punto);
         antenna.setDistanciaA(SphericalUtil.computeDistanceBetween(origin.getPosition(), punto));
            alturas.add(antenna);
        }
        return alturas;
    }

    private void comparar() {
        for (PosicionGeografica posicionGeografica : posicionGeograficas) {
            try {
                if (posiblePunto(posicionGeografica)) {
                    ideal.setText(posicionGeografica.getPunto().getMarker().getTitle());
                    return;
                }
            } catch (Exception ex) {
                Log.println(Log.ASSERT, "ALTURAS", ex.toString());
                ex.printStackTrace();

            }
        }
        ideal.setText(mContext.getString(R.string.no_pos_ideal));

    }

    private boolean posiblePunto(PosicionGeografica posicionGeografica) {
        PuntoMedio a = posicionGeografica.getPuntoMedio().get(0); //punto uno
        PuntoMedio b = posicionGeografica.getPuntoMedio().get(posicionGeografica.getPuntoMedio().size() - 1);
        for (int i = 1; i < (posicionGeografica.getPuntoMedio().size() - 1); i++) {
            if ((posicionGeografica.getPuntoMedio().get(i).getAltura() > a.getAltura()) ||
                    (posicionGeografica.getPuntoMedio().get(i).getAltura() > b.getAltura()))
                return false;
        }
        return true;
    }


    private final Handler mHandlerWS = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:

                    contador = msg.getData().getInt("result");
                    dialog.setProgress(contador);
                    if (contador >= puntosmedios.size() - 1) {
                        dialog.dismiss();
                        comparar();
                    }
                    //     Log.println(Log.ASSERT, "ALTURAS", "!TERMINO");
                    break;
            }
        }
    };

    public void esperar() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.println(Log.ASSERT, "ALTURA", "ESPERANDO");
            }
        }, 400);
    }

}
