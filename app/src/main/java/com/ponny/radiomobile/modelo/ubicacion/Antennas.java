package com.ponny.radiomobile.modelo.ubicacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 20/06/2016.
 */
public class Antennas {
    private List<Antenna> listado;

    public Antennas() {
        listado = new ArrayList<>();
    }

    public List<Antenna> getListado() {
        return listado;
    }


    public void setElementi(Antenna antenna) {
        listado.add(antenna);
    }
}
