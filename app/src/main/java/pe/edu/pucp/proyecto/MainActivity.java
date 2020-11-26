package pe.edu.pucp.proyecto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.cliente.PantallaPrincipalCliente;
import pe.edu.pucp.proyecto.cliente.RegistroNewUsuario;

public class MainActivity extends AppCompatActivity {

    //PARA LEER Y GUARDAR USUARIOS
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    ArrayList<InfoUsuario> arrayUsuarios=new ArrayList<>(); //LISTA DE USUARIOS
    ListenerFb listenerFb;

    String login;
    public void leerPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        login=sharedPreferences.getString("Login","");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        leerPreferences(); //LEER EL Preferences
        if(!login.equals("")){
            Intent intent = new Intent( this , PantallaPrincipalCliente.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(listenerFb);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listenerFb=new ListenerFb();
        databaseReference.child("Clientes").addChildEventListener(listenerFb);

        leerPreferences(); //LEER EL Preferences
        if(!login.equals("")){
            Intent intent = new Intent( this , PantallaPrincipalCliente.class);
            startActivity(intent);
            finish();
        }

    }

    public void contraseñaVisibilidad(View view){

        EditText idContraseña= findViewById(R.id.idContraseña);
        ImageView imagenEye =findViewById(R.id.idEye);

        //145 Texto sin Ocultar
        //129 Texto Oculto
        int n = idContraseña.getInputType();
        if (n == 145) {
            idContraseña.setInputType(129);
            imagenEye.setImageResource(R.drawable.ic_action_visibilityoff);

        } else {
            idContraseña.setInputType(145);
            imagenEye.setImageResource(R.drawable.ic_action_visibility);

        }

    }//FIN DEL METODO DE LA contraseñaVisibilidad


    public void btnIngresar(View view){
    int error = 0;

        EditText idUsuario= findViewById(R.id.idUsuario);
        String usuario = idUsuario.getText().toString();

        //VALIDACIÓN DEL USUARIO Y CONTRASEÑA
        if (usuario.isEmpty()){
            idUsuario.setError("Debe ingresar un usuario");
            error=1;
        }


        EditText idContraseña= findViewById(R.id.idContraseña);
        String contraseña = idContraseña.getText().toString();

        if (contraseña.isEmpty()){
            idContraseña.setError("Debe ingresar su contraseña");
            error=1;
        }

        if (error ==0){

            databaseReference.child("Clientes").addChildEventListener(listenerFb);

            InfoUsuario[] arregloUsuarios=new InfoUsuario[arrayUsuarios.size()];
            arregloUsuarios=arrayUsuarios.toArray(arregloUsuarios);

            for(int pos=0;pos<arrayUsuarios.size();pos++){
                if((arregloUsuarios[pos].getUsuario().equals(usuario))||(arregloUsuarios[pos].getContraseña().equals(contraseña))){

                    //CONFIGURACION PARA PERMANECER LA CUENTA LOGEADA
                    SharedPreferences sharedPreferences=getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPreferences.edit();

                    edit.putString("Login",usuario);
                    edit.apply();

                    Intent intent = new Intent( this , PantallaPrincipalCliente.class);
                    intent.putExtra("usuario", arregloUsuarios[pos].getUsuario());
                    intent.putExtra("nombre", arregloUsuarios[pos].getNombre());
                    intent.putExtra("apellido", arregloUsuarios[pos].getApellido());
                    intent.putExtra("montoTotal", arregloUsuarios[pos].getMontoTotal());
                    startActivity(intent);
                    finish();
                    break;
                }
            }
            idUsuario.setError("Esta cuenta no existe");


        }//FIN DEL IF (SIN ERROR)

    } // FIN DEL METODO DEL BOTON DE INGRESAR


    public void btnRegistro(View view){
        Intent intent = new Intent( this , RegistroNewUsuario.class);
        startActivity(intent);
      //ABRIR EL ACTIVITY RegistroNewUsuario
    }





    public boolean tengoInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network networks = connectivityManager.getActiveNetwork();
            if (networks == null)
                return false;

            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(networks);


            if (networkCapabilities == null) // no tiene salida de internet
                return false;

            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) // esto es que tiene wifi
                return true;
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                return true;
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                return true;
            return false;
        } else {
            //versiones antiguas
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null)
                return false;
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET)
                return true;
            return false;

        }
    } //FIN DEL METODO DE tengoInternet


    private class ListenerFb implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            if(snapshot.getValue()!=null){
                InfoUsuario usuarios= snapshot.getValue(InfoUsuario.class);
                arrayUsuarios.add(usuarios);
                //SE ALMACENA LA LISTA DE USUARIOS
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
    } //FIN DE LA CLASE INTERNA

}