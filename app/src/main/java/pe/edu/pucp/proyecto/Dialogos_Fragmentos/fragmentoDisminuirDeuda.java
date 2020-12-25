package pe.edu.pucp.proyecto.Dialogos_Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pe.edu.pucp.proyecto.R;


public class fragmentoDisminuirDeuda extends DialogFragment {



    public fragmentoDisminuirDeuda() {

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogo();
    }

    private AlertDialog crearDialogo(){
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v= inflater.inflate(R.layout.fragment_fragmento_disminuir_deuda, null);
        builder.setView(v);

        Button btnAceptar=v.findViewById(R.id.btnFragAceptarPago);
        Button btnCancelar=v.findViewById(R.id.btnFragCancelarPago);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return builder.create();
    }


}