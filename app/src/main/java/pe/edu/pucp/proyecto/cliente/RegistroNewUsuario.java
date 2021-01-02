package pe.edu.pucp.proyecto.cliente;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;

import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.PantallaPrincipalVendedor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class RegistroNewUsuario extends AppCompatActivity {

    //PARA LEER Y GUARDAR USUARIOS
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    //LISTA DE USUARIOS
    ArrayList<InfoUsuario> arrayUsuarios=new ArrayList<>();

    int error=0;

    ListenerFbLectura listenerFbLectura;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registron_new_usuario);


        //OBTENER TODOS LOS USUARIOS
        listenerFbLectura=new ListenerFbLectura();
        databaseReference.child("Usuarios").addChildEventListener(listenerFbLectura);



    }

    int a=0;

    @Override
    protected void onPause() {
        super.onPause();

        databaseReference.removeEventListener(listenerFbLectura);
        a=1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(a==1){
            listenerFbLectura=new ListenerFbLectura();
            databaseReference.child("Usuarios").addChildEventListener(listenerFbLectura);
            a=0;
        }
    }


    public void btnCancelar(View view){
        finish();
    }


    public void btnCrearCuenta(View view){


        EditText usuario=findViewById(R.id.editTextUsuarioRegistro);
        EditText nombre=findViewById(R.id.editTextNombreRegistro);
        EditText apellido=findViewById(R.id.editTextApellidoRegistro);
        EditText contraseña=findViewById(R.id.editTextContraseñaRegistro);


        if (usuario.getText().toString().isEmpty()){
            usuario.setError("Debe ingresar un usuario");
            error=1;
        }
        // Recorremos el texto de usuario para verificar si no hay un espacio en blanco
        for (int i = 0; i < usuario.length(); i++) {
            if (usuario.getText().toString().charAt(i) == ' '){
                error=1;
                usuario.setError("El usario no debe contener espacios vacios");
                break;
            }
        }

        if (nombre.getText().toString().isEmpty()){
            nombre.setError("Debe ingresar un nombre");
            error=1;
        }
        if (apellido.getText().toString().isEmpty()){
            apellido.setError("Debe ingresar un apellido");
            error=1;
        }
        if (contraseña.getText().toString().isEmpty()){
            contraseña.setError("Debe ingresar un contraseña");
            error=1;
        }


        //VERIFICAR SI EL USUARIO INGRESADO YA EXISTE
        for(int i=0;i<arrayUsuarios.size();i++){
            if(usuario.getText().toString().equals(arrayUsuarios.get(i).getUsuario())){
                error=2;
                usuario.setError("Este usuario ya existe, debe elegir uno nuevo");
                break;
            }
        }

        if (error==0)
        {
            //GUARDAR INFORMACION
            InfoUsuario usuarioClase =new InfoUsuario();

            usuarioClase.setUsuario(usuario.getText().toString());
            usuarioClase.setNombre(nombre.getText().toString());

            usuarioClase.setContraseña(contraseña.getText().toString());
            usuarioClase.setTipo("cliente");


            databaseReference.child("Usuarios").child(usuario.getText().toString()+"_"+nombre.getText().toString()).setValue(usuarioClase).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    //CONFIGURACION PARA PERMANECER LA CUENTA LOGEADA
                    SharedPreferences sharedPreferences=getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPreferences.edit();

                    edit.putString("Login",usuario.getText().toString());
                    edit.apply();
                    Toast.makeText(RegistroNewUsuario.this,"Bienvenido "+nombre.getText().toString(),Toast.LENGTH_SHORT).show();

                    finish(); //CERRAR LA PANTALLA
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistroNewUsuario.this,"Fallo en crear la cuenta",Toast.LENGTH_SHORT).show();

                }
            });

           databaseReference.child("Usuarios").child(usuario.getText().toString()+"_"+nombre.getText().toString()).child("Deuda").child("CantidadDeDeudas").setValue("0");
        }


    }


    private class ListenerFbLectura implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getValue()!=null){
                InfoUsuario infoUsuario2= snapshot.getValue(InfoUsuario.class);
                Log.d("infoApp","Nombre: "+infoUsuario2.getNombre());

                arrayUsuarios.add(infoUsuario2);
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
    } //FIN DE LA CLASE INTERNA DE LECTURA



}