package com.ponny.radiomobile.controlador.persistencia.archivo;

import android.content.Context;
import android.util.Log;

import com.ponny.radiomobile.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by daniel on 21/06/2016.
 */
public class escribirJSON {
    private Context mContext;
    private File file;
    private boolean nuevo;

    public escribirJSON(Context mContext) {
        this.mContext = mContext;
        file = new File(mContext.getFilesDir(), mContext.getString(R.string.antennas_json));
        nuevo = file.exists();
        iniciarArchivo();
    }

    private void iniciarArchivo() {
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.println(Log.ASSERT, "FILES", e.toString());
                e.printStackTrace();
            }
    }

}
