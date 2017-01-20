package com.ponny.radiomobile.controlador.persistencia.archivo;

import android.content.Context;
import android.util.Log;

import com.ponny.radiomobile.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daniel on 20/06/2016.
 */
public class InterpreteJSON {

    private Context mContext;

    public InterpreteJSON(Context mContext) {

        this.mContext = mContext;
    }

    public String altura(String respuesta) {
       // Log.println(Log.ASSERT, "json", respuesta);
        try {
            JSONObject json = new JSONObject(respuesta).getJSONArray(mContext.getString(R.string.results))
                    .getJSONObject(Integer.parseInt(mContext.getString(R.string.altura_0)));
            return json.getString(mContext.getString(R.string.elevation));
        } catch (JSONException e) {
            Log.println(Log.ASSERT, "JSON/ERROR CASTEP", respuesta);

            return "0";
        }

    }

}
