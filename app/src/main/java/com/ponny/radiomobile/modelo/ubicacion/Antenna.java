package com.ponny.radiomobile.modelo.ubicacion;

/**
 * Created by daniel on 20/06/2016.
 */
public class Antenna {
    private double Longitud, Latitud, Altura;
    private String Descripccion, Tipo, Municipio;

    public Antenna(double longitud, double latitud) {
        Longitud = longitud;
        Latitud = latitud;
    }

    public Antenna(double longitud, double latitud, double altura) {
        Longitud = longitud;
        Latitud = latitud;
        Altura = altura;
    }

    public Antenna(double longitud, double latitud, double altura, String descripccion, String tipo, String municipio) {
        Longitud = longitud;
        Latitud = latitud;
        Altura = altura;
        Descripccion = descripccion;
        Tipo = tipo;
        Municipio = municipio;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getAltura() {
        return Altura;
    }

    public void setAltura(double altura) {
        Altura = altura;
    }

    public String getDescripccion() {
        return Descripccion;
    }

    public void setDescripccion(String descripccion) {
        Descripccion = descripccion;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        Municipio = municipio;
    }
}
