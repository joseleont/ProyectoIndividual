package pe.edu.pucp.proyecto.Vendedor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pe.edu.pucp.proyecto.Clases.Deuda;
import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.Clases.Listas.ListaDeudasAdapter;
import pe.edu.pucp.proyecto.Clases.Listas.ListaUsuariosAdapter;

import pe.edu.pucp.proyecto.Dialogos_Fragmentos.AgregarProducto;
import pe.edu.pucp.proyecto.Dialogos_Fragmentos.FragmentoPreDefinido.DatePickerFragment;
import pe.edu.pucp.proyecto.Dialogos_Fragmentos.MenuAyuda;
import pe.edu.pucp.proyecto.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class NuevaDeuda extends AppCompatActivity implements AgregarProducto.ProductoDialogListener {


    ArrayList<Deuda> arrayDeuda=new ArrayList<>();   //se llena en la funcion Pasar productos

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReferenceCantidadDeDeudas= FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReferenceGuardarCantidadDeDeudas= FirebaseDatabase.getInstance().getReference();

    DatabaseReference databaseReferenceFecha= FirebaseDatabase.getInstance().getReference(); //GUARDAR LA FECHA

    DatabaseReference databaseReferenceMontoTotal= FirebaseDatabase.getInstance().getReference();



    ListenerFb listenerFb = new ListenerFb();
    Listener listener = new Listener();


    String uid;
    String nombre;

    String fecha;

    int cantidadDeudas; //Cantidad de deudas
    //SIRVE PARA ALMACENAR LA INFORMACION

    float montoDeuda;

    float montoTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_deuda);

        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");
        nombre=intent.getStringExtra("nombre");




        Log.d("infoApp",uid);
        //OBTENER EL MONTO TOTAL
        databaseReferenceMontoTotal.child("Usuarios").child(uid).child("montoTotal").addValueEventListener(listener);

        Log.d("infoApp","hola");
        //OBTENER LA CANTIDAD DE DEUDAS
        databaseReferenceCantidadDeDeudas.child("Usuarios").child(uid).child("CantidadDeDeudas").addValueEventListener(listenerFb);



        TextView textViewFecha =findViewById(R.id.textViewFecha);
        Calendar calendar = Calendar.getInstance();
        
        // El objeto calendar nos permite tenr la fecha y hora actual
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;

        String mes;
        if(month<10){
            mes="0"+month;
        }else{
            mes= String.valueOf(month);
        }

        String dia;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        if(day<10){
            dia="0"+day;
        }else{
            dia= String.valueOf(day);
        }

        fecha = dia+"-"+mes+"-"+year;

        textViewFecha.setText(fecha);

        Button btnCancelar =findViewById(R.id.btnCancelarNuevaDeuda);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReferenceCantidadDeDeudas.removeEventListener(listenerFb);
            }
        });



    }



    public void agregarProducto(View view){
        //ABRIR EL FRAGMENTO AGREGAR PRODUCTOS
        AgregarProducto agregarProducto=new AgregarProducto();
        agregarProducto.show(getSupportFragmentManager(),"producto");


    }

    //metodo de la interfaz de AgregarProducto
    @Override
    public void pasarProducto(String producto, int cantidad, float precio, String descripcion) {
      //RELLENA EL array de deudas
        Deuda deuda=new Deuda(producto,cantidad,precio,descripcion);
        arrayDeuda.add(deuda);
        iniciarRecyclerView();

    }



    public void mostrarFecha(View view){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        //SE INSTANCIA EL FRAGMENTO
        datePickerFragment.show(getSupportFragmentManager(),"fecha"); //SE MUESTRA EL FRAGMENTO
        //getSupportFragmentManager(): metodo que se encarga de la gestion del fragmento
        // el segundo parametro es una etiqueta

    }

    //ESTA FUNCION ES LLAMDA DESDE EL FRAGMENTO DEL TIMEPICKER
    public void respuestaDatePicker(int year, int month,int day){
        TextView textViewFecha =findViewById(R.id.textViewFecha);
        fecha=day+"-"+(month+1)+"-"+year;
        textViewFecha.setText(fecha);
    }


    public void guardar(View view){

        databaseReferenceMontoTotal.removeEventListener(listener);

        //GUARDAR EN LA BASE DE DATOS

        Deuda[] arregloDeuda=new Deuda[arrayDeuda.size()];
        arregloDeuda=arrayDeuda.toArray(arregloDeuda);

        for(int pos=0;pos<arregloDeuda.length;pos++){
            arregloDeuda[pos].setPrecio(Math.round(arregloDeuda[pos].getPrecio()* 100) / 100f); //LIMITAR LOS DECIMALES
            montoDeuda=montoDeuda+arregloDeuda[pos].getPrecio()*arregloDeuda[pos].getCantidad();
            databaseReference.child("Usuarios").child(uid).child("Deuda").child("Deuda"+(cantidadDeudas+1)).child("Producto").child("Producto"+(pos+1)).setValue(arregloDeuda[pos]);
            databaseReference.child("Usuarios").child(uid).child("Deuda").child("Deuda"+(cantidadDeudas+1)).child("identificador").setValue(String.valueOf(cantidadDeudas+1));
        }

        //GUARDAR FECHA
        databaseReferenceFecha.child("Usuarios").child(uid).child("Deuda").child("Deuda"+(cantidadDeudas+1)).child("Fecha").setValue(fecha);

        databaseReferenceFecha.child("Usuarios").child(uid).child("Deuda").child("Deuda"+(cantidadDeudas+1)).child("MontoDeuda").setValue(String.valueOf(Math.round(montoDeuda * 100) / 100f));

        actualizarMontoTotal();

        guardarCantidadDeDeudas();
        databaseReferenceCantidadDeDeudas.removeEventListener(listenerFb);
        finish();

    }

    public void guardarCantidadDeDeudas(){
        String a= String.valueOf(cantidadDeudas+1);

       databaseReferenceGuardarCantidadDeDeudas.child("Usuarios").child(uid).child("CantidadDeDeudas").setValue(a);
    }

    public void actualizarMontoTotal(){

        float a=montoDeuda+montoTotal;
        float c= Math.round(a * 100) / 100f;
        Log.d("SUMA",montoDeuda +"+"+montoTotal +"="+(montoTotal+montoDeuda));
        String b= String.valueOf(c);
        //ACTUALIZAR EL MONTO TOTAL
        databaseReferenceMontoTotal.child("Usuarios").child(uid).child("montoTotal").setValue(b);

    }


    public void iniciarRecyclerView(){

        //SE CREA UN ARREGLO apartir DEL arrayDeuda(aqui se almacena toda la info)
        Deuda[] arregloDeuda=new Deuda[arrayDeuda.size()];
        arregloDeuda=arrayDeuda.toArray(arregloDeuda);

        //COLOCAR UNA ARRELGO DE arregloDeuda AHI
        ListaDeudasAdapter adapter=new ListaDeudasAdapter(arregloDeuda,NuevaDeuda.this,"NuevaDeuda");
        RecyclerView recyclerView=findViewById(R.id.rvDeuda);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(NuevaDeuda.this));

    }



    //se obtiene la cantidad de deudas
    private class ListenerFb implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue()!=null){

                if((snapshot.getValue().toString()+"").equals("null")){
                    //EN CASO NO SE TENGA NINGUNA cantidad, se considera como 0
                    cantidadDeudas=0;
                }
                else{
                    cantidadDeudas= Integer.parseInt(String.valueOf(snapshot.getValue()));
                    //SE TRANSFORMA DE UN OBJETO A STRING
                    //SE TRANSOFRMA DE UN STRING A INT
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }


    //se obtiene la montoTotal
    private class Listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue()!=null){


                if((snapshot.getValue().toString()+"").equals("null")){
                    //EN CASO NO SE TENGA NINGUN monto, se considera como 0
                    montoTotal=0;
                }
                else{
                    montoTotal= Float.parseFloat(String.valueOf(snapshot.getValue()));
                    Log.d("infoApp",""+montoTotal);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }


}