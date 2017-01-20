package com.ponny.radiomobile.modelo.ubicacion;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by daniel on 29/06/2016.
 */
public class PuntoMedio {
    private LatLng position;
    private double altura;
    private double distanciaA;


    public PuntoMedio(LatLng position) {
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getDistanciaA() {
        return distanciaA;
    }

    public void setDistanciaA(double distanciaA) {
        this.distanciaA = distanciaA;
    }
}
