package pe.edu.pucp.proyecto.Vendedor;

import pe.edu.pucp.proyecto.Clases.Deuda;
import pe.edu.pucp.proyecto.Clases.DeudaGeneral;
import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.Clases.Listas.ListaDeudasSinPagarAdapter;
import pe.edu.pucp.proyecto.Clases.Listas.ListaUsuariosAdapter;
import pe.edu.pucp.proyecto.MainActivity;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.cliente.ModificarPerfil;
import pe.edu.pucp.proyecto.cliente.PantallaPrincipalCliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class PantallaListaDeudasDelCliente extends AppCompatActivity {

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    ArrayList<DeudaGeneral> arrayDeudas=new ArrayList<>();


    String usuario;
    String nombre;
    String uid;

    int montoTotal;

    String cliente;

    ListenerFb listenerFb;
    Listener listener;

    String lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_deudas_del_cliente);

        Intent intent=getIntent();
        usuario=intent.getStringExtra("usuario");
        uid=intent.getStringExtra("uid");
        nombre=intent.getStringExtra("nombre");
        montoTotal=intent.getIntExtra("montoTotal",0);
        cliente=intent.getStringExtra("cliente")+"";


        //ES LA PAGINA PRINCIPAL DEL CLIENTE
        if(cliente.equals("cliente")){
            Button btn=findViewById(R.id.btnPLDAgregarDeuda);
            btn.setVisibility(View.INVISIBLE);
        }


        //GUARDAR EN EL SHAREDPREFERENCE el uid del usuario para identificarlo
        SharedPreferences sharedPreferences=getSharedPreferences("uidUsuario",MODE_PRIVATE);
        SharedPreferences.Editor edit =sharedPreferences.edit();
        edit.putString("uid",uid);
        edit.apply();

        TextView info=findViewById(R.id.textInfoCliente);
        info.setText(nombre);

        listenerFb = new ListenerFb();
        databaseReference.child("Usuarios").child(uid).child("Deuda").addChildEventListener(listenerFb);

        listener = new Listener();



    }

  //  public void agregarProducto(View view){

   // }

    public void agregarDeuda(View view){
        Intent intent=new Intent(this,NuevaDeuda.class);
        intent.putExtra("uid",uid);
        intent.putExtra("nombre",nombre);
        startActivity(intent);

    }

    public void ordenarPorFecha(DeudaGeneral[] arreglo){

        int[] suma=new int[arreglo.length];
        Log.d("infoAppA",""+arreglo.length);

        try{
        for(int a=0;a<arreglo.length;a++){

                String[] parts = arreglo[a].getFecha().split("-");
                int dia= Integer.parseInt(parts[0]);
                int mes= Integer.parseInt(parts[1])*30;
                int year= Integer.parseInt(parts[2])*365;
                suma[a]=dia+mes+year;
                if(suma[a]==737687){
                    Log.d("infoAppC",""+arreglo[a].getFecha());
                }

        }

            int[] sinRep;
        if(suma.length==0){
            sinRep=suma;
        }else{
            sinRep=sinRepetir(suma);
        }


        for(int papa=0;papa<sinRep.length;papa++){
            Log.d("infoAppB",""+sinRep[papa]);
        }
            Log.d("infoAppB","********************");

        int pos=0;
        for(int r=0;r<sinRep.length;r++){
            for(int Ra=0;Ra<arreglo.length;Ra++){

                String[] parts = arreglo[Ra].getFecha().split("-");
                int dia= Integer.parseInt(parts[0]);
                int mes= Integer.parseInt(parts[1])*30;
                int year= Integer.parseInt(parts[2])*365;
                int tempo=dia+mes+year;
                Log.d("infoAppB",""+tempo);
                if(tempo==sinRep[r]){
                    arregloDeudasGeneralesOrdenado[pos]=arreglo[Ra];
                    Log.d("infoAppB",arregloDeudasGenerales[pos].getFecha());
                    pos=pos+1;
                }
            }
        }
            Log.d("infoAppB","$$$$$$$$$$$$$$$$$$$$");
        }catch (NullPointerException e){
            Log.d("infoAppA","Error");
        }

    }

    public static int[] sinRepetir(int[]a){
        Arrays.sort(a);
        int len = a.length;
        int j = 0;
        for(int i=0;i<len-1;i++){
            if(a[i]!=a[i+1]){
                a[j++] = a[i];
            }
        }
        a[j++] = a[len-1];
        int [] c = new int[j];
        for(int k = 0;k<j;k++){
            c[k] = a[k];
        }
        return c;
    }


    ListaDeudasSinPagarAdapter adapter;
    DeudaGeneral[] arregloDeudasGenerales;
    DeudaGeneral[] arregloDeudasGeneralesOrdenado;

    public void iniciarRecyclerView(int i) {
        //COLOCAR UN ARREGLO DE DEUDAS
        arregloDeudasGenerales = new DeudaGeneral[arrayDeudas.size()];
        arregloDeudasGenerales = arrayDeudas.toArray(arregloDeudasGenerales);

        //AQUI DISTINGUE DONDE SE CREO EL RECYCLERVIEW
        // si es 1 entonces se abrio desde una deuda pendiente
        //si es 2, entonces se abrio desde una deuda saldada
        if(i==1){
            arregloDeudasGeneralesOrdenado = new DeudaGeneral[arrayDeudas.size()];
            arregloDeudasGeneralesOrdenado = arrayDeudas.toArray(arregloDeudasGeneralesOrdenado);
            ordenarPorFecha(arregloDeudasGenerales);

            adapter=new ListaDeudasSinPagarAdapter(arregloDeudasGeneralesOrdenado,PantallaListaDeudasDelCliente.this,"Pendientes",cliente);


        }else{
            Log.d("mensaje","Se envio pagados");
            adapter=new ListaDeudasSinPagarAdapter(arregloDeudasGenerales,PantallaListaDeudasDelCliente.this,"Pagados",cliente);
        }

        RecyclerView recyclerView=findViewById(R.id.rvListaDeudas); //id del recylcer view a utilizar
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PantallaListaDeudasDelCliente.this));

    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(listenerFb);
        databaseReference.removeEventListener(listener);
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
                databaseReference.child("Usuarios").child(uid).child("Deuda").addChildEventListener(listenerFb);
                arrayDeudas=new ArrayList<>();
            }

            else{
                listener=new Listener();
                databaseReference.child("Usuarios").child(uid).child("Pagos").addChildEventListener(listener);
                arrayDeudas=new ArrayList<>();
            }
            a=0;
        }

    }

    public void verDeudasPendientes(View view){
    //PRIMER BOTON PARA SER PRESIONADO
        //SE ELIMINA EL LISTENER de

        databaseReference.removeEventListener(listener);
        arrayDeudas=new ArrayList<>();

        databaseReference.child("Usuarios").child(uid).child("Deuda").addChildEventListener(listenerFb);
        Button btnSaldado = findViewById(R.id.btnPLDCDeudasSaldadas);
        Button btnPendiente = findViewById(R.id.btnPLDCDeudasPendientes);
        btnSaldado.setVisibility(View.VISIBLE);
        btnPendiente.setVisibility(View.INVISIBLE);
        b=0;

        TextView textIdentificacionLista=findViewById(R.id.textIdentificacionLista);
        textIdentificacionLista.setText("Lista de Deudas Pendientes");

        if(!cliente.equals("cliente")){
            Button btn=findViewById(R.id.btnPLDAgregarDeuda);
            btn.setVisibility(View.VISIBLE);
        }

    }

    public void verDeudasSaldadas(View view){
        databaseReference.removeEventListener(listenerFb);
        arrayDeudas=new ArrayList<>();
        iniciarRecyclerView(1);
        databaseReference.child("Usuarios").child(uid).child("Pagos").addChildEventListener(listener);

        Button btnSaldado = findViewById(R.id.btnPLDCDeudasSaldadas);
        Button btnPendiente = findViewById(R.id.btnPLDCDeudasPendientes);
        btnPendiente.setVisibility(View.VISIBLE);
        btnSaldado.setVisibility(View.INVISIBLE);
        b=1;

        //MODIFICAR LA PANTALLA
        TextView textIdentificacionLista=findViewById(R.id.textIdentificacionLista);
        textIdentificacionLista.setText("Lista de Deudas Saldadas");

        Button btn=findViewById(R.id.btnPLDAgregarDeuda);
        btn.setVisibility(View.INVISIBLE);
    }


    //DEUDAS PENDIENTES
    class ListenerFb implements ChildEventListener{

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (snapshot.getValue() != null) {
                DeudaGeneral deudaGeneral= snapshot.getValue(DeudaGeneral.class);

                if(!(deudaGeneral.getMontoDeuda()+"").equals("null")){
                    float tmp=Float.parseFloat(deudaGeneral.getMontoDeuda());
                    if(tmp!=0){
                        //LISTA DE DEUDAS
                        arrayDeudas.add(deudaGeneral);
                    }
                    iniciarRecyclerView(1);
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


    //DEUDAS PAGADAS
    class Listener implements ChildEventListener{

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (snapshot.getValue() != null) {
                DeudaGeneral deudaGeneral= snapshot.getValue(DeudaGeneral.class);

                if(!(deudaGeneral.getEstado()+"").equals("null")){
                    if((deudaGeneral.getEstado()).equals("completo")){
                        //LISTA DE DEUDAS COMPLETAS
                        arrayDeudas.add(deudaGeneral);
                        Log.d("infoAppF",deudaGeneral.getEstado()+"buenas0");
                    }
                    Log.d("infoAppF",deudaGeneral.getEstado());
                    iniciarRecyclerView(2);
                }
                Log.d("infoAppF",deudaGeneral.getEstado()+"buenas2");
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



    //CLIENTE
    //*******************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(cliente.equals("cliente")){
            getMenuInflater().inflate(R.menu.menu_pantalla_principal_cliente,menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.menuSalirCliente:
                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(PantallaListaDeudasDelCliente.this, MainActivity.class));
                        finish();
                    }
                });

                return true;
            case R.id.menuModificarPerfil:
                Intent intentMenuModificarPerfil = new Intent( this , ModificarPerfil.class);
                startActivity(intentMenuModificarPerfil);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}