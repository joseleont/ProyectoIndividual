package pe.edu.pucp.proyecto.Dialogos_Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import pe.edu.pucp.proyecto.R;


public class DialogoModificarImagenPerfil extends DialogFragment {



    public DialogoModificarImagenPerfil() {
        // Required empty public constructor
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return CrearDialogo();
    }

    private Dialog CrearDialogo() {


        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_modificar_imagen_perfil,null);
        builder.setView(v);

        Button btnCamara =v.findViewById(R.id.btnCamaraPerfil);

        Button btnGaleria=v.findViewById(R.id.btnGaleriaPerfil);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                cambioImagenPerfil.opcionEscogida("camara");


            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cambioImagenPerfil.opcionEscogida("galeria");

                dismiss();
            }
        });



        return builder.create();
    }


    CambioImagenPerfil cambioImagenPerfil;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        cambioImagenPerfil= (CambioImagenPerfil) context;
        //SE OBTIENE EL context de la actividad que lo abrio
        //SE OBTIENE CUANDO SE ABRIO EL FRAGMENTO
    }


    //SE CREA UNA INTERFAZ PARA CREAR EL METODO QUE SE IMPLEMENTA EN LA ACTIVIDAD NuevaDeuda
    //DE ESTA MANERA, SE LE PUEDE DAR LOS DATOS OBTENIDOS DEL FRAGMENTO Y MANDARSELO AL ACTIVITY QUE LO ABRIO
    public interface CambioImagenPerfil{

        void opcionEscogida(String app);
    }




}









