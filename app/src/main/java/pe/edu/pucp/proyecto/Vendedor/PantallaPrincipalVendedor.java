package pe.edu.pucp.proyecto.Vendedor;
import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.Clases.Listas.ListaUsuariosAdapter;
import pe.edu.pucp.proyecto.Dialogos_Fragmentos.MenuAyuda;
import pe.edu.pucp.proyecto.MainActivity;
import pe.edu.pucp.proyecto.R;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PantallaPrincipalVendedor extends AppCompatActivity {

    //PARA LEER Y GUARDAR USUARIOS
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    //LISTA DE USUARIOS
    ArrayList<InfoUsuario> arrayUsuarios=new ArrayList<>();;


    ListenerFb listenerFb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_vendedor);

        listenerFb = new ListenerFb();
        databaseReference.child("Usuarios").addChildEventListener(listenerFb);


    }



    ListaUsuariosAdapter adapter;
    InfoUsuario[] arregloUsuarios;
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
        Log.d("infoApp","onRESUME");
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

            case R.id.menuModificarInformacion:

                break;


            case R.id.menuSalirVendedor:

                SharedPreferences sharedPreferences=getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor edit=sharedPreferences.edit();

                edit.putString("Login",""); //SE VACIA EL ARCHIVO PREFERENCE
                edit.apply();

                Intent intentMenuSalirCliente = new Intent( this , MainActivity.class);
                startActivity(intentMenuSalirCliente);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //LISTENER
    class ListenerFb implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            if (snapshot.getValue() != null) {
                InfoUsuario infoUsuario2 = snapshot.getValue(InfoUsuario.class);

                arrayUsuarios.add(infoUsuario2);
                //SE ALMACENA LA LISTA DE USUARIOS
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

                        if(arregloUsuarios[a].getUsuario().equals(infoUsuario2.getUsuario())){

                            arregloUsuarios[a].setNombre(infoUsuario2.getNombre());
                            arregloUsuarios[a].setApellido(infoUsuario2.getApellido());
                            arregloUsuarios[a].setMontoTotal(infoUsuario2.getMontoTotal());
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