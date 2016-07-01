package com.dcs.googlempasejemplo1.vistas.mensajes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.dcs.googlempasejemplo1.controlador.persistencia.preferencias.Preferencias;

/**
 * Created by daniel on 15/06/2016.
 */
public class Mensajes {
    private Context mContext;
    private Toast toast;
    private boolean registrar;

    public Mensajes(Context mContext) {
        this.mContext = mContext;
    }

    public void Toast(String mensaje) {
        toast = Toast.makeText(mContext, mensaje, Toast.LENGTH_SHORT);
        toast.show();

    }

    public void cancelToast() {
        toast.cancel();
    }

    public void SetRadio(String titulo, String cuerpo, final Preferencias preferencias) {

        final EditText entrada = new EditText(mContext);
        entrada.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(titulo);
        builder.setMessage(cuerpo);
        builder.setView(entrada);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    preferencias.setRadio(Integer.parseInt(entrada.getText().toString()));
                } catch (Exception ex) {
                    Toast("Imposible guardar cambios");
                    Log.println(Log.ASSERT, "pref", ex.toString());
                    ex.printStackTrace();
                }

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();

    }

    public void SetIntervalo(String titulo, String cuerpo, final Preferencias preferencias) {

        final EditText entrada = new EditText(mContext);
        entrada.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(titulo);
        builder.setMessage(cuerpo);
        builder.setView(entrada);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    preferencias.setInvervalo(Integer.parseInt(entrada.getText().toString()));
                } catch (Exception ex) {
                    Toast("Imposible guardar cambios");
                    Log.println(Log.ASSERT, "pref", ex.toString());
                    ex.printStackTrace();
                }

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();

    }
}
