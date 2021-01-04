package pe.edu.pucp.proyecto;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.Vendedor.PantallaListaDeudasDelCliente;
import pe.edu.pucp.proyecto.Vendedor.PantallaPrincipalVendedor;

public class MainActivity extends AppCompatActivity {


    InfoUsuario infoUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoUsuario=new InfoUsuario();
        validarUsuario();
    }

    public void ingresar(View view){
        List<AuthUI.IdpConfig> provedores= Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),new AuthUI.IdpConfig.GoogleBuilder().build());
        // el primero del Arrays.asList= Email = Ingresar con usuario y contraseña
        // el segundo es para ingresar con google

        AuthUI instance = AuthUI.getInstance();
        Intent intent = instance.createSignInIntentBuilder().setAvailableProviders(provedores).build();
        //createSignInIntentBuilder(): CREAR EL CONJUNTO DE VENTANAS PARA PODER LOGEARSE.
        //setAvailableProviders(): Se debe ingresar dentro la lista de soportes que tendrá las ventanas
        // es decir, si puede logearse con google o con facebook

        startActivityForResult(intent,1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1){
             validarUsuario();
        }
    }



    public void validarUsuario(){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                goPaginaPrincipalVC(currentUser);
            } else {
                currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (currentUser.isEmailVerified()) {
                            goPaginaPrincipalVC(currentUser);
                        } else {
                            Toast.makeText(MainActivity.this, "Se le ha enviado un correo para verificar su cuenta", Toast.LENGTH_SHORT).show();
                            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("infoApp", "correo enviado");
                                }
                            });
                        }
                    }
                });
            }
        }

    }



    public void goPaginaPrincipalVC(FirebaseUser currentUser){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        if(currentUser.getEmail().equals("jose.leont@pucp.pe")){
            infoUsuario.setTipo("vendedor");
        }
        else{
            infoUsuario.setTipo("cliente");
            infoUsuario.setUid(currentUser.getUid());

        }
        infoUsuario.setNombre(currentUser.getDisplayName());

        //Guardar la información en el firebase

        reference.child("Usuarios").child(currentUser.getUid()).child("nombre").setValue(infoUsuario.getNombre());
        reference.child("Usuarios").child(currentUser.getUid()).child("tipo").setValue(infoUsuario.getTipo());
        reference.child("Usuarios").child(currentUser.getUid()).child("uid").setValue(infoUsuario.getUid());


        if(infoUsuario.getTipo().equals("vendedor")){
            startActivity(new Intent(MainActivity.this, PantallaPrincipalVendedor.class));
        }else{


            Intent intent = new Intent(this,PantallaListaDeudasDelCliente.class);

            intent.putExtra("uid", infoUsuario.getUid());
            intent.putExtra("nombre", infoUsuario.getNombre());
            intent.putExtra("cliente","cliente");

            startActivity(intent);
        }
        finish();
    }


    int a=0;

    @Override
    protected void onPause() {
        super.onPause();
        a=1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(a!=0){
            validarUsuario();
        }
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



}