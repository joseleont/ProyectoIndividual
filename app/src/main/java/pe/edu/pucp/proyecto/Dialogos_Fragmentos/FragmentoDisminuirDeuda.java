package pe.edu.pucp.proyecto.Dialogos_Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import pe.edu.pucp.proyecto.R;


public class FragmentoDisminuirDeuda extends DialogFragment {

   Listener listener;
   ListenerFb listenerFb;
   DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

   String montoTotal; //DEUDA TOTAL DEL CLIENTE
    //SE RESTARA CON EL PAGO REALIZADO Y LUEGO SE GUARDARA EN EL FIREBASE

    String montoDeudaPagada="0";//Monto pagado de la deuda seleccionada
    //el max valor será igual a el monto de la deuda seleccionada

    public FragmentoDisminuirDeuda() {

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

        String uid = getArguments() != null ? getArguments().getString("uid") : "uid";
        String identificador = getArguments() != null ? getArguments().getString("identificador") : "identificador";
        String M1 = getArguments() != null ? getArguments().getString("monto") : "monto";

        String[] parts = M1.split(". ");
        String dinero = parts[0];
        String monto = parts[1];


        Button btnAceptar=v.findViewById(R.id.btnFragAceptarPago);
        Button btnCancelar=v.findViewById(R.id.btnFragCancelarPago);
        EditText editText=v.findViewById(R.id.editTextDD);

        //Leer el monto total de las deudas
        listener=new Listener();
        databaseReference.child("Usuarios").child(uid).child("montoTotal").addValueEventListener(listener);


        //Leer el monto total pagado de la deuda seleccionada- se obtiene con el identificador
        listenerFb=new ListenerFb();
        databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("montoPagado").addValueEventListener(listenerFb);


        //Obteer fecha
        Calendar calendar = Calendar.getInstance();
        // El objeto calendar nos permite tenr la fecha y hora actual
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        String fecha = day+"-"+month+"-"+year;


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.removeEventListener(listener);
                databaseReference.removeEventListener(listenerFb);


                String montoPagado= editText.getText().toString();
                float montoRestante=Float.parseFloat(monto)-Float.parseFloat(montoPagado);
                if(montoRestante>=0){
                    //Guardar el monto de la DEUDA RESTANTE y el PAGO en la lista de pagos
                    DatabaseReference ruta=databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("infoPago").push();
                    ruta.child("montoPagado").setValue(montoPagado);

                    databaseReference.child("Usuarios").child(uid).child("Deuda").child("Deuda"+identificador).child("MontoDeuda").setValue(String.valueOf(montoRestante));
                    if(montoRestante==0){
                        databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("estado").setValue("completo");
                    }else{
                        databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("estado").setValue("incompleto");
                    }



                    //Guardar fecha de cuando se realizó el pago
                    ruta.child("Fecha").setValue(fecha);

                    //DENTRO DE PAGO->PAGO#
                    //GENERAL
                    //*********************************
                    //Guardar la fecha del ultimo pago
                    databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("Fecha").setValue(fecha);
                    //Guarfar el identificador
                    databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("identificador").setValue(identificador);

                    //Guarfar el montoPagado hasta ahora
                   String montoPagadoActual=String.valueOf(Float.parseFloat(montoDeudaPagada)+Float.parseFloat(montoPagado));

                    databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("montoPagado").setValue(montoPagadoActual);
                    //***************************************


                    //Actualizar el montoTotal de todas las deudas del cliente
                    float nuevoMontoTotal=Float.parseFloat(montoTotal)-Float.parseFloat(montoPagado);
                    nuevoMontoTotal=(float)Math.round(nuevoMontoTotal* 100) / 100; //reducir los decimales
                    databaseReference.child("Usuarios").child(uid).child("montoTotal").setValue(String.valueOf(nuevoMontoTotal));



                    //actualizar el textView de la actividad PantallaRegistroPagos
                    //METODO DE LA INTEFAZ
                    disminuirDeuda.actualizarMontoTextView(String.valueOf(montoRestante));

                    dismiss();
                }
                else{
                    //el monto pagado es mayor que el monto a deber
                    editText.setError("El monto pagado es mayor a la deuda pendiente");
                }

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.removeEventListener(listener);
                databaseReference.removeEventListener(listenerFb);
                dismiss();
            }
        });


        return builder.create();
    }


    private DisminuirDeuda disminuirDeuda;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        disminuirDeuda= (DisminuirDeuda) context;
    }

    //ACTUALIZA EN TEXTVIEW de la activity PantallaRegistrarPago
    public interface DisminuirDeuda{

        void actualizarMontoTextView(String monto);
    }


    //Leer el monto total de TODAS LAS deudas
    class Listener implements ValueEventListener{

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue()!=null){
                montoTotal= snapshot.getValue().toString();
                //MONTO TOTAL DE TODAS LAS DEUDA
                //ESTE VALOR SE RESTARA CON EL VALOR PAGADO, Y SE SUBIRA EN EL FIREBASE
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    //Leer el monto total pagado de la deuda seleccionada
    class ListenerFb implements ValueEventListener{

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue()!=null){
                montoDeudaPagada= snapshot.getValue().toString();
                if((""+montoDeudaPagada).equals("null")){
                    montoDeudaPagada="0";
                }
                Log.d("infoAppE",montoDeudaPagada);

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

}