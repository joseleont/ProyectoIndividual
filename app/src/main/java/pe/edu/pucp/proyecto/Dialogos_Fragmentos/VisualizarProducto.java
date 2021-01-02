package pe.edu.pucp.proyecto.Dialogos_Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import pe.edu.pucp.proyecto.R;


public class VisualizarProducto extends DialogFragment {



    public VisualizarProducto() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogo();
    }

    private AlertDialog crearDialogo() {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_visualizar_producto,null);
        builder.setView(v);


        String producto = getArguments() != null ? getArguments().getString("producto") : "producto";
        String cantidad = getArguments() != null ? getArguments().getString("cantidad") : "cantidad";
        String precio = getArguments() != null ? getArguments().getString("precio") : "precio";
        String descripcion = getArguments() != null ? getArguments().getString("descripcion") : "descripcion";
        String vista = getArguments() != null ? getArguments().getString("vista") : "vista";


        TextView textProducto=v.findViewById(R.id.textProductoVP);
        textProducto.setText(producto);

        TextView textCantidad=v.findViewById(R.id.textCantidadVP);
        textCantidad.setText(cantidad);

        TextView textPrecio=v.findViewById(R.id.textoPrecioVP);
        textPrecio.setText("S/. "+precio);

        TextView textDescripcion=v.findViewById(R.id.textDescripcionVP);
        textDescripcion.setText(descripcion);

        if(descripcion.isEmpty()){
            ScrollView scrollView=v.findViewById(R.id.svVPDescripcion);
            scrollView.setVisibility(View.GONE);
        }


        TextView textMonto=v.findViewById(R.id.textMontoVP);
        textMonto.setText("S/. "+Float.parseFloat(precio)*Float.parseFloat(cantidad));

        return builder.create();
    }






}