package com.ponny.radiomobile.controlador.persistencia.preferencias;

import android.content.Context;
import android.content.SharedPreferences;

import com.ponny.radiomobile.R;

/**
 * Created by daniel on 29/06/2016.
 */
public class Preferencias {
    private Context mContext;
    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;


    public Preferencias(Context mContext) {
        this.mContext = mContext;
        preferences = mContext.getSharedPreferences(mContext.getString(R.string.preferencias), mContext.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setRadio(int radio) {
        editor.putInt(mContext.getString(R.string.radio), radio);
        editor.commit();
    }

    public int getRadio() {
        return
                preferences.getInt(mContext.getString(R.string.radio), Integer.parseInt(mContext.getString(R.string.radio_default)));
    }

    public int getInvervalo() {
        return preferences.getInt(mContext.getString(R.string.invetervalo), Integer.parseInt(mContext.getString(R.string.intervalo_default)));
    }

    public void setInvervalo(int invervalo) {
        editor.putInt(mContext.getString(R.string.invetervalo), invervalo);
        editor.commit();
    }
}
