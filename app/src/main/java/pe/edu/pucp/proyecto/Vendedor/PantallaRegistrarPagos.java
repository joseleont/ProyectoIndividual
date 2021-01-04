package pe.edu.pucp.proyecto.Vendedor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pe.edu.pucp.proyecto.Clases.Deuda;
import pe.edu.pucp.proyecto.Clases.DeudaGeneral;
import pe.edu.pucp.proyecto.Clases.Listas.ListaDeudasAdapter;
import pe.edu.pucp.proyecto.Clases.Listas.ListaDeudasSinPagarAdapter;
import pe.edu.pucp.proyecto.Dialogos_Fragmentos.FragmentoDisminuirDeuda;
import pe.edu.pucp.proyecto.R;



public class PantallaRegistrarPagos extends AppCompatActivity implements FragmentoDisminuirDeuda.DisminuirDeuda {

    String uid;
    String identificador;
    String monto;
    String fecha;
    String tipo; // PARA SABER SI SE ABRIO DESDE UNA DEUDA PENDIENTE O UNA DEUDA SALDADA

    String cliente; //para saber si el cliente esta utilizando la aplicacion
    //PARA LEER Y GUARDAR USUARIOS
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    //LISTA DE Productos
    ArrayList<Deuda> arrayDeudas=new ArrayList<>();

    //LISTA DE Productos
    ArrayList<DeudaGeneral> arrayDeudasGeneral=new ArrayList<>();

    ListenerFb listenerFb;
    Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_registrar_pagos);

        Intent intent=getIntent();
        identificador=intent.getStringExtra("identificador");
        //el identificador sirve para ubicar que deuda se está leyendo
        fecha=intent.getStringExtra("fecha");
        monto=intent.getStringExtra("monto");
        tipo=intent.getStringExtra("tipo")+"";
        //tipo puede ser Pagados o Pendientes
        Log.d("infoAB",tipo);
        if((tipo).equals("Pagados")){
            TextView textView=findViewById(R.id.textDeudaPendienteSaldada);
            textView.setText("Deuda Saldada");
            Button btn =findViewById(R.id.btnDisminuirDeuda);
            btn.setVisibility(View.INVISIBLE);
        }
        
        cliente=intent.getStringExtra("cliente")+"";
        if(cliente.equals("cliente")){
            Button btn =findViewById(R.id.btnDisminuirDeuda);
            btn.setVisibility(View.INVISIBLE);
        }

        //OBTENER VALOR DEL uid
        SharedPreferences sharedPreferences=getSharedPreferences("uidUsuario",MODE_PRIVATE);
        uid=sharedPreferences.getString("uid","vacio");

        listenerFb=new ListenerFb();
        databaseReference.child("Usuarios").child(uid).child("Deuda").child("Deuda"+identificador).child("Producto").addChildEventListener(listenerFb);

        listener=new Listener();


        actualizarTextoMonto(monto);

        Log.d("infoAppD",identificador+" "+fecha+" "+ monto+" "+uid);


    }

    public void disminuirDeuda(View view){
        //ABRIR EL FRAGMENTO AGREGAR PRODUCTOS
        FragmentoDisminuirDeuda fragmentoDisminuirDeuda=new FragmentoDisminuirDeuda();

        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("identificador", identificador);

        TextView textMonto=findViewById(R.id.textRPmontoDeuda);
        monto=textMonto.getText().toString();
        Log.d("infoAppD",monto);
        args.putString("monto", monto);

        fragmentoDisminuirDeuda.setArguments(args);
        fragmentoDisminuirDeuda.show(getSupportFragmentManager(),"fragmentoDisminuirDeuda");

    }

    public void actualizarTextoMonto(String monto){
        //colocar el monto en el texto de la activity
        TextView textMonto=findViewById(R.id.textRPmontoDeuda);

        if(Float.parseFloat(monto)==0){
            textMonto.setText("Se ha pagado la deuda");
            Button disminuirDeuda= findViewById(R.id.btnDisminuirDeuda);
            disminuirDeuda.setVisibility(View.INVISIBLE);

            TextView texto=findViewById(R.id.textDeudaPendienteSaldada);
            texto.setVisibility(View.INVISIBLE);

        }else{
            textMonto.setText("S/. "+monto);
        }

    }


    public void iniciarRecyclerViewProductos(){

        //SE CREA UN ARREGLO apartir DEL arrayDeuda(aqui se almacena toda la info)
        Deuda[] arregloDeudas=new Deuda[arrayDeudas.size()];
        arregloDeudas=arrayDeudas.toArray(arregloDeudas);

        //COLOCAR UNA ARRELGO DE arregloDeuda AHI
        ListaDeudasAdapter adapter=new ListaDeudasAdapter(arregloDeudas,PantallaRegistrarPagos.this,"PantallaRegistroPagos");
        RecyclerView recyclerView=findViewById(R.id.rvPantallaRegistrarPagos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PantallaRegistrarPagos.this));

    }


    public void iniciarRecyclerViewHistorial(){

        //SE CREA UN ARREGLO apartir DEL arrayDeudaGeneral(aqui se almacena toda la info)
        DeudaGeneral[] arregloDeudasGeneral=new DeudaGeneral[arrayDeudasGeneral.size()];
        arregloDeudasGeneral=arrayDeudasGeneral.toArray(arregloDeudasGeneral);

        //COLOCAR UNA ARRELGO DE arregloDeuda AHI
        ListaDeudasSinPagarAdapter adapter=new ListaDeudasSinPagarAdapter(arregloDeudasGeneral,PantallaRegistrarPagos.this,"Visualizar","cliente");
        RecyclerView recyclerView=findViewById(R.id.rvPantallaRegistrarPagos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PantallaRegistrarPagos.this));

    }


    public void verHistorialPagos(View view){

        databaseReference.removeEventListener(listenerFb);
        arrayDeudas=new ArrayList<>();
        iniciarRecyclerViewProductos();
        databaseReference.child("Usuarios").child(uid).child("Pagos").child("Pagos"+identificador).child("infoPago").addChildEventListener(listener);

        TextView texto=findViewById(R.id.textPRPProductosHistorial);
        texto.setText("Historial de Pagos");
        Button btn =findViewById(R.id.btnDisminuirDeuda);
        btn.setVisibility(View.INVISIBLE);

        Button btn2=findViewById(R.id.btnPRPVerListaProductos);
        btn2.setVisibility(View.VISIBLE);
        Button btn3=findViewById(R.id.btnPRPVerHistorial);
        btn3.setVisibility(View.INVISIBLE);

        b=0;
    }

    public void verListaProductos(View view){
        databaseReference.removeEventListener(listener);
        arrayDeudasGeneral=new ArrayList<>();
        iniciarRecyclerViewHistorial();
        databaseReference.child("Usuarios").child(uid).child("Deuda").child("Deuda"+identificador).child("Producto").addChildEventListener(listenerFb);


        TextView texto=findViewById(R.id.textPRPProductosHistorial);
        texto.setText("Lista de Productos");
        if(!tipo.equals("Pagados")){
            Button btn =findViewById(R.id.btnDisminuirDeuda);
            btn.setVisibility(View.VISIBLE);
        }


        Button btn2=findViewById(R.id.btnPRPVerListaProductos);
        btn2.setVisibility(View.INVISIBLE);
        Button btn3=findViewById(R.id.btnPRPVerHistorial);
        btn3.setVisibility(View.VISIBLE);


        if(cliente.equals("cliente")){
            Button btn =findViewById(R.id.btnDisminuirDeuda);
            btn.setVisibility(View.INVISIBLE);
        }

        b=1;
    }


    @Override
    protected void onPause() {
        super.onPause();

        databaseReference.removeEventListener(listenerFb); //PRODUCTOS
        databaseReference.removeEventListener(listener); //HISTORIAL
        a=1;
    }
    int a=0;
    int b=0;

    @Override
    protected void onResume() {
        super.onResume();
        if(a==1){
            if(b==0){
                listenerFb=new ListenerFb();
                arrayDeudas=new ArrayList<>();
                databaseReference.removeEventListener(listenerFb);
            }

            else{
                listenerFb=new ListenerFb();
                arrayDeudasGeneral=new ArrayList<>();
                databaseReference.removeEventListener(listener);
            }
            a=0;
        }
    }

    //LISTENER para leer los productos
    class ListenerFb implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            if (snapshot.getValue() != null) {
                Deuda deuda = snapshot.getValue(Deuda.class);
                if(!(deuda.getProducto().equals("null"))){
                    arrayDeudas.add(deuda);
                    iniciarRecyclerViewProductos();
                }

            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    //LISTENER para leer el historial de pagos
    class Listener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            if (snapshot.getValue() != null) {
                DeudaGeneral deudaGeneral = snapshot.getValue(DeudaGeneral.class);
                if(!((deudaGeneral.getMontoPagado()+"").equals("null"))){
                    arrayDeudasGeneral.add(deudaGeneral);
                    Log.d("infoAppW",deudaGeneral.getFecha());
                    iniciarRecyclerViewHistorial();
                }

            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }



    //METODO DEL INTERFAZ DEL FragmentoDisminuirDeuda
    @Override
    public void actualizarMontoTextView(String monto) {
        actualizarTextoMonto(monto);
    }


}