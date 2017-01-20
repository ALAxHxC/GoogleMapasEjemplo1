package com.ponny.radiomobile.vistas.mensajes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.ponny.radiomobile.MapsActivity;
import com.ponny.radiomobile.R;
import com.ponny.radiomobile.controlador.persistencia.preferencias.Preferencias;

/**
 * Created by daniel on 15/06/2016.
 */
public class Mensajes {
    private Context mContext;
    private Toast toast;

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

    public void SetRadio(String titulo, String cuerpo, final Preferencias preferencias, final MapsActivity activity) {
        final EditText entrada = getEntrada();
        final AlertDialog.Builder builder = iniciarDialogoAlerta(titulo, cuerpo, entrada);

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    preferencias.setRadio(Integer.parseInt(entrada.getText().toString()));
                    if (activity.getPoscentral() != null)
                        activity.AgregarPositionCentral(activity.getPoscentral());
                } catch (Exception ex) {
                    Toast(mContext.getString(R.string.imposible_guardar));
                    Log.println(Log.ASSERT, "pref", ex.toString());
                    ex.printStackTrace();
                }

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("NO", getNegativeButton());
        builder.create();
        builder.show();

    }

    public void SetIntervalo(String titulo, String cuerpo, final Preferencias preferencias) {
        final EditText entrada = getEntrada();
        final AlertDialog.Builder builder = iniciarDialogoAlerta(titulo, cuerpo, entrada);

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    preferencias.setInvervalo(Integer.parseInt(entrada.getEditableText().toString()));
                } catch (Exception ex) {
                    Toast("Imposible guardar cambios");
                    Log.println(Log.ASSERT, "pref", ex.toString());
                    ex.printStackTrace();
                }

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("NO", getNegativeButton());
        builder.create();
        builder.show();

    }

    public void RegistrarPosCentral(final Dialog dialogo, final MapsActivity activity) {
        final EditText latitud = (EditText) dialogo.findViewById(R.id.txtLatitud);
        final EditText longitud = (EditText) dialogo.findViewById(R.id.txtLongitud);
        latitud.setHint(mContext.getResources().getString(R.string.longitud));
        longitud.setHint(mContext.getResources().getString(R.string.latitud));
        Button registrar = (Button) dialogo.findViewById(R.id.btnRegistrar);
        Button cancelar = (Button) dialogo.findViewById(R.id.btnCancelar);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.isPuntocentral()) {
                    activity.setPuntocentral(false);
                    activity.setPoscentral(new LatLng(Double.parseDouble(latitud.getText().toString()), Double.parseDouble(String.valueOf(longitud.getText().toString()))));
                    activity.AgregarPositionCentral(activity.getPoscentral());
                    Log.println(Log.ASSERT, "MAPS", "punto central");
                    dialogo.dismiss();
                    return;
                }

            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();

            }
        });
        dialogo.show();
    }

    public void opccionRegistroAntena(final String titulo, final String mensje, final MapsActivity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(titulo);
        dialog.setPositiveButton(mContext.getString(R.string.registrar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegistrarAntena(generarDialogoNuevaAntena(titulo, mensje), activity);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(mContext.getString(R.string.mapa_ubicar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.setAntenna(true);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void RegistrarAntena(final Dialog dialogo, final MapsActivity activity) {
        final EditText latitud = (EditText) dialogo.findViewById(R.id.txtLatitud);
        final EditText longitud = (EditText) dialogo.findViewById(R.id.txtLongitud);
        final EditText titulo = (EditText) dialogo.findViewById(R.id.editTextTitulo);
        latitud.setHint(mContext.getResources().getString(R.string.longitud));
        longitud.setHint(mContext.getResources().getString(R.string.latitud));
        titulo.setHint(mContext.getResources().getString(R.string.nueva_antena_name));
        Button registrar = (Button) dialogo.findViewById(R.id.btnRegistrar);
        Button cancelar = (Button) dialogo.findViewById(R.id.btnCancelar);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.println(Log.ASSERT, "MAPS", "punto central");
                if (validarEntradaAntena(titulo, latitud, longitud)) {
                    activity.registrarAntena(Double.parseDouble(latitud.getText().toString()), Double.parseDouble(String.valueOf(longitud.getText().toString())), titulo.getEditableText().toString());
                    dialogo.dismiss();
                }

                return;


            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();

            }
        });
        dialogo.show();
    }

    private AlertDialog.Builder iniciarDialogoAlerta(String titulo, String cuerpo, EditText entrada) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(titulo);
        builder.setMessage(cuerpo);
        builder.setView(entrada);
        return builder;
    }

    private EditText getEntrada() {
        final EditText entrada = new EditText(mContext);
        entrada.setInputType(InputType.TYPE_CLASS_NUMBER);
        return entrada;

    }

    public DialogInterface.OnClickListener getNegativeButton() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

    }

    private boolean validarEntradaAntena(EditText titulo, EditText latitud, EditText longitud) {
        if (titulo.getEditableText().toString().isEmpty() || titulo.getEditableText().toString().length() <= 2) {
            Toast(mContext.getString(R.string.titulo_invalido));
            return false;
        }
        try {
            if (Double.parseDouble(latitud.getEditableText().toString()) < -90 || Double.parseDouble(latitud.getEditableText().toString()) > 90) {
                Toast(mContext.getString(R.string.latitud_invalidad));
                return false;
            }
            if (Double.parseDouble(longitud.getEditableText().toString()) < -90 || Double.parseDouble(longitud.getEditableText().toString()) > 90) {
                Toast(mContext.getString(R.string.latitud_invalidad));
                return false;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast(mContext.getString(R.string.formato_mal));
            return false;
        }
        return true;
    }

    public Dialog generarDialogo(String titulo, String mensaje) {
        Dialog dialogo = new Dialog(mContext);
        dialogo.setContentView(R.layout.atenna_cordenaas);
        dialogo.setTitle(titulo);
        TextView men = (TextView) dialogo.findViewById(R.id.textViewMensaje);
        men.setText(mensaje);
        return dialogo;
    }

    public Dialog generarDialogoNuevaAntena(String titulo, String mensaje) {
        Dialog dialogo = new Dialog(mContext);
        dialogo.setContentView(R.layout.nueva_atenna_sistema);
        dialogo.setTitle(titulo);
        TextView men = (TextView) dialogo.findViewById(R.id.textViewMensaje);
        men.setText(mensaje);
        return dialogo;
    }
}


