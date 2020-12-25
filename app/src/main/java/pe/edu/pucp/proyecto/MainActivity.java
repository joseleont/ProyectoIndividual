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
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.Vendedor.PantallaPrincipalVendedor;
import pe.edu.pucp.proyecto.cliente.PantallaPrincipalCliente;
import pe.edu.pucp.proyecto.cliente.RegistroNewUsuario;

public class MainActivity extends AppCompatActivity {

    //PARA LEER Y GUARDAR USUARIOS
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    ArrayList<InfoUsuario> arrayUsuarios=new ArrayList<>(); //LISTA DE USUARIOS
    ListenerFb listenerFb;

    String login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<AuthUI.IdpConfig> provedores= Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),new AuthUI.IdpConfig.GoogleBuilder().build());
        // el primero del Arrays.asList= Email = Ingresar con usuario y contraseña
        // el segundo es para ingresar con google

        AuthUI instance = AuthUI.getInstance();
        Intent intent = instance.createSignInIntentBuilder().setAvailableProviders(provedores).build();
        //createSignInIntentBuilder(): CREAR EL CONJUNTO DE VENTANAS PARA PODER LOGEARSE.
        //setAvailableProviders(): Se debe ingresar dentro la lista de soportes que tendrá las ventanas
        // es decir, si puede logearse con google o con facebook

        startActivityForResult(intent,1);

        listenerFb=new ListenerFb();
        databaseReference.child("Usuarios").addChildEventListener(listenerFb);
       // abrirActivity();
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1){
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            validarUsuario();
        }
    }

    public void validarUsuario(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //CON VERIFICACION CON CORREO
        if(currentUser!=null){
            if(currentUser.isEmailVerified()) {
                startActivity(new Intent(this,PantallaPrincipalCliente.class));
                finish();
            }else{
                currentUser.reload();
                Toast.makeText(this,"Se le ha enviado un correo para verificar su cuenta",Toast.LENGTH_SHORT).show();
                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //CON ESTO PODEMOS VER QUE SE MANDO EL CORREO CORRECTAMENT
                        Log.d("infoApp","Se mandó correctamente el correo");
                    }
                });
            }

        }

    }

    public void leerPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        login=sharedPreferences.getString("Login","");
    }

    public void abrirActivity(){
        leerPreferences(); //LEER EL Preferences

        if(!login.equals("")){
            if(login.equals("vendedor_jose")){
                Intent intent = new Intent( this , PantallaPrincipalVendedor.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent( this , PantallaPrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        }

    }





    int a=0;
    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(listenerFb);

        a=1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(a==1){
            listenerFb=new ListenerFb();
            databaseReference.child("Usuarios").addChildEventListener(listenerFb);
            arrayUsuarios=new ArrayList<>();
            a=0;
            //abrirActivity();
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
        } //validar si el espacio de texto de la contraseña no está vacia


        if (error ==0){

            databaseReference.child("Usuarios").addChildEventListener(listenerFb);

            InfoUsuario[] arregloUsuarios=new InfoUsuario[arrayUsuarios.size()];
            arregloUsuarios=arrayUsuarios.toArray(arregloUsuarios);

            for(int pos=0;pos<arrayUsuarios.size();pos++){
                Log.d("infoApp",arregloUsuarios[pos].getUsuario()+"-"+arregloUsuarios[pos].getContraseña());
                if((arregloUsuarios[pos].getUsuario().equals(usuario))&&(arregloUsuarios[pos].getContraseña().equals(contraseña))){

                    //CONFIGURACION PARA PERMANECER LA CUENTA LOGEADA
                    SharedPreferences sharedPreferences=getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPreferences.edit();

                    //GUARDAR EN EL PREFERENCIA EL USUARIO_NOMBRE
                    edit.putString("Login",arregloUsuarios[pos].getUsuario()+"_"+arregloUsuarios[pos].getNombre());
                    edit.apply();

                    Intent intent = new Intent( this , PantallaPrincipalCliente.class);
                    intent.putExtra("usuario", arregloUsuarios[pos].getUsuario());
                    intent.putExtra("nombre", arregloUsuarios[pos].getNombre());
                    intent.putExtra("apellido", arregloUsuarios[pos].getApellido());
                    intent.putExtra("montoTotal", arregloUsuarios[pos].getMontoTotal());
                    startActivity(intent);
                    finish();
                    break;
                } else{
                    idUsuario.setError("Esta cuenta no existe");
                }
            }

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


    class ListenerFb implements ChildEventListener {
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