package pe.edu.pucp.proyecto.Dialogos_Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.Toast;

import pe.edu.pucp.proyecto.Clases.Deuda;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.NuevaDeuda;


public class AgregarProducto extends DialogFragment {

    private ProductoDialogListener productoDialogListener;

    public AgregarProducto() {
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
        View v = inflater.inflate(R.layout.fragment_agregar_producto,null);
        builder.setView(v);

        Button btnAceptar=v.findViewById(R.id.btnFragAceptar);
        Button btnCancelar=v.findViewById(R.id.btnFragCancelar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int error=0;

                //OBTENER LA INFORMACION DE LOS VIEWS
                EditText editTextProducto=v.findViewById(R.id.editTextProducto);
                EditText editTextCantidad=v.findViewById(R.id.editTextCantidad);
                EditText editTextPrecio=v.findViewById(R.id.editTextPrecio);
                EditText editTextDescripcion=v.findViewById(R.id.editTextDescripcion);

                //transformar la informacion y validaciones
                String producto=editTextProducto.getText().toString();
                if(producto.isEmpty()){
                    error=1;
                    editTextProducto.setError("Debe ingresar un producto");
                }

                //VALIDACION DE cantidad
                int cantidad=0;
                try{
                    cantidad=Integer.parseInt(editTextCantidad.getText().toString());
                }catch (NumberFormatException e){
                    error=1;
                    editTextCantidad.setError("Debe ingresar un número entero");
                }

                //Validacion de precio
                float precio=0;
                try{
                   precio =Float.valueOf(editTextPrecio.getText().toString());
                }catch (NumberFormatException e){
                    error=1;
                    editTextPrecio.setError("Debe ingresar un número");
                }


                //validacion de descripcion
                String descripcion=editTextDescripcion.getText().toString();
                if(descripcion.isEmpty()){
                    descripcion="";
                    //LA DESCRIPCION ES OPCIONAL
                }


                if (error==0){
                    //productoDialogListener que contiene el contexto que abrio este fragmento
                    //SE OBTIENE EN EL onAttach y se consigue cuando se crea el fragmento
                    productoDialogListener.pasarProducto(producto,cantidad,precio,descripcion);

                    dismiss(); //CIERRA EL DIALOGFRAGMENT
                }

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); //CIERRA EL DIALOGFRAGMENT
            }
        });


        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        productoDialogListener= (ProductoDialogListener) context;
        //SE OBTIENE EL context de la actividad que lo abrio
        //SE OBTIENE CUANDO SE ABRIO EL FRAGMENTO
    }


    //SE CREA UNA INTERFAZ PARA CREAR EL METODO QUE SE IMPLEMENTA EN LA ACTIVIDAD NuevaDeuda
    //DE ESTA MANERA, SE LE PUEDE DAR LOS DATOS OBTENIDOS DEL FRAGMENTO Y MANDARSELO AL ACTIVITY QUE LO ABRIO
    public interface ProductoDialogListener{

        void pasarProducto(String producto,int cantidad,float precio, String descripcion);
    }


}