package com.ponny.radiomobile.controlador.persistencia.archivo;

import android.content.Context;
import android.util.JsonWriter;

import com.ponny.radiomobile.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by daniel on 21/06/2016.
 */
public class controladorJSON {
    private JsonWriter controlador;
    private Context mContext;

    public controladorJSON(Context mContext) throws FileNotFoundException, UnsupportedEncodingException {
        this.mContext = mContext;
        FileOutputStream fos = new FileOutputStream(mContext.getString(R.string.path_assets) + "/" + mContext.getString(R.string.antennas_json));
        controlador = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
    }

}
