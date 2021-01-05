package pe.edu.pucp.proyecto.cliente;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;

import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.PantallaPrincipalVendedor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        startActivity(new Intent(RegistroNewUsuario.this, PantallaPrincipalVendedor.class));

        finish(); //CERRAR LA PANTALLA
    }


    public void btnCrearCuenta(View view){



        EditText nombre=findViewById(R.id.editTextNombreRegistro);
        EditText apellido=findViewById(R.id.editTextApellidoRegistro);


        /*
        EditText usuario=findViewById(R.id.editTextUsuarioRegistro);
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

       */

        if (nombre.getText().toString().isEmpty()){
            nombre.setError("Debe ingresar un nombre");
            error=1;
        }
        if (apellido.getText().toString().isEmpty()){
            apellido.setError("Debe ingresar un apellido");
            error=1;
        }

        //VERIFICAR SI EL USUARIO INGRESADO YA EXISTE
        for(int i=0;i<arrayUsuarios.size();i++){
            if((nombre.getText().toString()+apellido.getText().toString()).equals(arrayUsuarios.get(i).getNombre())){
                error=2;
                nombre.setError("Ya se encuentra registrado esta persona, debe ingresar uno nuevo");
                break;
            }
        }

        if (error==0)
        {
            //GUARDAR INFORMACION
            InfoUsuario usuarioClase =new InfoUsuario();

            usuarioClase.setUsuario("creado");
            usuarioClase.setNombre(nombre.getText().toString()+ " "+apellido.getText().toString());
            usuarioClase.setMontoTotal("0");
            usuarioClase.setCantDeudas("0");
            usuarioClase.setTipo("cliente");


            String uid=databaseReference.child("Usuarios").push().getKey();

            usuarioClase.setUid(uid);

            databaseReference.child("Usuarios").child(uid).setValue(usuarioClase).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(RegistroNewUsuario.this,"Se agrego este usuario",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistroNewUsuario.this, PantallaPrincipalVendedor.class));

                    finish(); //CERRAR LA PANTALLA
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistroNewUsuario.this,"Fallo en crear la cuenta",Toast.LENGTH_SHORT).show();

                }
            });


        }


    }


    private class ListenerFbLectura implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getValue()!=null){
                InfoUsuario infoUsuario2= snapshot.getValue(InfoUsuario.class);
                Log.d("infoApp1","Nombre: "+infoUsuario2.getNombre());

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