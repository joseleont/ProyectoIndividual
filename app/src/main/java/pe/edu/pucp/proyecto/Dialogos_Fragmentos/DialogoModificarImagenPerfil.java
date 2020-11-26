package pe.edu.pucp.proyecto.Dialogos_Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
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

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();


                int REQUEST_IMAGE_CAPTURE = 1;
                //ABRE LA APLICACION DE FOTOS Y TOMA UNA FOTO
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    Log.d("infoApp","SE TOMO UNA FOTO YEAH");
                }


            }
        });
        return builder.create();
    }


}









