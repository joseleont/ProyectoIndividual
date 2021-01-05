package pe.edu.pucp.proyecto.Vendedor;
import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.Clases.Listas.ListaUsuariosAdapter;
import pe.edu.pucp.proyecto.Dialogos_Fragmentos.MenuAyuda;
import pe.edu.pucp.proyecto.MainActivity;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.cliente.ModificarPerfil;
import pe.edu.pucp.proyecto.cliente.RegistroNewUsuario;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class PantallaPrincipalVendedor extends AppCompatActivity {

    //PARA LEER Y GUARDAR USUARIOS
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    //LISTA DE USUARIOS
    ArrayList<InfoUsuario> arrayUsuarios=new ArrayList<>();

    //LItsa de nombreas
    ArrayList<String> arrayNombres=new ArrayList<>();

    ListenerFb listenerFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_vendedor);

        listenerFb = new ListenerFb();
        databaseReference.child("Usuarios").addChildEventListener(listenerFb);

        //SE ALMACENA LA LISTA DE USUARIOS CLIENTES


        textAutoComplete = findViewById(R.id.autoComplete);

    }
    String textoAutoCompletado="";
    AutoCompleteTextView textAutoComplete;

    public void filtrar(View view){

        textoAutoCompletado=textAutoComplete.getText().toString();

        int posi=0;
        if(!textoAutoCompletado.equals("")){

            for(int i=0;i<arrayUsuarios.size();i++){

                if(arregloUsuarios[i].getNombre().equals(textoAutoCompletado)){
                    arregloUsuariosFiltradp[0]=arregloUsuarios[i];
                    Log.d("infoAppAuto",arregloUsuariosFiltradp[0].getNombre());

                    posi=i;
                            i=arrayUsuarios.size();
                }

            }

            //COLOCAR UNA ARRELGO DE INFOUSARIOS AHI
            adapterFiltrado=new ListaUsuariosAdapter(arregloUsuariosFiltradp,PantallaPrincipalVendedor.this);
            RecyclerView recyclerView=findViewById(R.id.rvUsuarios);

            recyclerView.setAdapter(adapterFiltrado);
            recyclerView.setLayoutManager(new LinearLayoutManager(PantallaPrincipalVendedor.this));

        }else{
                iniciarRecyclerView();
        }

    }



    ListaUsuariosAdapter adapter;
    InfoUsuario[] arregloUsuarios;

    String[] nombres;
    InfoUsuario[] arregloUsuariosFiltradp=new InfoUsuario[1];
    ListaUsuariosAdapter adapterFiltrado;

    public void iniciarRecyclerView(){

            arregloUsuarios=new InfoUsuario[arrayUsuarios.size()];
            arregloUsuarios=arrayUsuarios.toArray(arregloUsuarios);

            //COLOCAR UNA ARRELGO DE INFOUSARIOS AHI
            adapter=new ListaUsuariosAdapter(arregloUsuarios,PantallaPrincipalVendedor.this);
            RecyclerView recyclerView=findViewById(R.id.rvUsuarios);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(PantallaPrincipalVendedor.this));

    }

    int a=0;

    @Override
    protected void onResume() {
        super.onResume();

        if(a==1){
            listenerFb=new ListenerFb();
            databaseReference.child("Usuarios").addChildEventListener(listenerFb);
            arrayUsuarios=new ArrayList<>();
            a=0;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("infoApp","onPAUSE");
        databaseReference.removeEventListener(listenerFb);

        a=1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pantalla_principal_vendedor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.menuAyudaVendedor:
                MenuAyuda menuAyuda=new MenuAyuda();
                menuAyuda.show(getSupportFragmentManager(),"ayuda_vendedor");

                break;

            case R.id.menuSalirVendedor:

                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(PantallaPrincipalVendedor.this,MainActivity.class));
                        finish();
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void agregarUsuario(View view){
        startActivity(new Intent(PantallaPrincipalVendedor.this, RegistroNewUsuario.class));
        databaseReference.removeEventListener(listenerFb);
        finish();
    }


    //LISTENER
    class ListenerFb implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            if (snapshot.getValue() != null) {
                InfoUsuario infoUsuario2 = snapshot.getValue(InfoUsuario.class);

                if(infoUsuario2.getTipo().equals("cliente")){
                    arrayUsuarios.add(infoUsuario2);
                    arrayNombres.add(infoUsuario2.getNombre());


                    //AUTOCOMPLETAR
                    textAutoComplete.setThreshold(1);

                    nombres=new String[arrayNombres.size()];
                    nombres=arrayNombres.toArray(nombres);

                    ArrayAdapter<String> adapterAutoComplete = new ArrayAdapter<String>(PantallaPrincipalVendedor.this,
                            android.R.layout.simple_list_item_1,nombres);
                    textAutoComplete.setAdapter(adapterAutoComplete);

                }


            }
             iniciarRecyclerView();


        }



        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            if (snapshot.getValue() != null) {
                //OBTENER EL OBJETO QUE CAMBIO
                InfoUsuario infoUsuario2 =snapshot.getValue(InfoUsuario.class);

                    int a;
                    for( a=0;a<arrayUsuarios.size();a++){

                        if(arregloUsuarios[a].getUid().equals(infoUsuario2.getUid())){

                            arregloUsuarios[a].setNombre(infoUsuario2.getNombre());
                           // arregloUsuarios[a].setApellido(infoUsuario2.getApellido());
                            arregloUsuarios[a].setMontoTotal(infoUsuario2.getMontoTotal());
                            arregloUsuarios[a].setUid(infoUsuario2.getUid());
                            break;
                        }
                    }

                    adapter=new ListaUsuariosAdapter(arregloUsuarios,PantallaPrincipalVendedor.this);

                    //adapter.notifyItemChanged(a);
                    iniciarRecyclerView();



            }


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
}