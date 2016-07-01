package com.dcs.googlempasejemplo1.controlador.persistencia.preferencias;

import android.content.Context;
import android.content.SharedPreferences;

import com.dcs.googlempasejemplo1.R;

/**
 * Created by daniel on 29/06/2016.
 */
public class Preferencias {
    private Context mContext;
    private SharedPreferences preferences;
    private int radio;
    private int invervalo;
    private SharedPreferences.Editor editor;


    public Preferencias(Context mContext) {
        this.mContext = mContext;
        preferences = mContext.getSharedPreferences(mContext.getString(R.string.preferencias), mContext.MODE_PRIVATE);
        editor = preferences.edit();
        radio = preferences.getInt(mContext.getString(R.string.radio), Integer.parseInt(mContext.getString(R.string.radio_default)));
        invervalo = preferences.getInt(mContext.getString(R.string.invetervalo), Integer.parseInt(mContext.getString(R.string.intervalo_default)));
    }

    public void setRadio(int radio) {
        this.radio = radio*1000;
        editor.putInt(mContext.getString(R.string.radio), radio*1000);
        editor.commit();
    }

    public int getRadio() {
        return radio;
    }

    public int getInvervalo() {
        return invervalo;
    }

    public void setInvervalo(int invervalo) {
        this.invervalo = invervalo;
        editor.putInt(mContext.getString(R.string.invetervalo), invervalo);
        editor.commit();
    }
}
