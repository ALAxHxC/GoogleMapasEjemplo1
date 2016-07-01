package com.dcs.googlempasejemplo1.modelo.ubicacion;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by daniel on 29/06/2016.
 */
public class PuntoMedio {
    private LatLng position;
    private double altura;

    public PuntoMedio(LatLng position) {
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }
}
