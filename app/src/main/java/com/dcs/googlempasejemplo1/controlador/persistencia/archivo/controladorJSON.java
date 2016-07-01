package com.dcs.googlempasejemplo1.controlador.persistencia.archivo;

import android.content.Context;
import android.util.JsonWriter;

import com.dcs.googlempasejemplo1.R;
import com.dcs.googlempasejemplo1.modelo.ubicacion.Antenna;

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
    public void Editar(Antenna antenna)
    {
    //    controlador.setIndent();

    }
}
