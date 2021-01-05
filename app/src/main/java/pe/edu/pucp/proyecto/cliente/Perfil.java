package pe.edu.pucp.proyecto.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.NuevaDeuda;

public class Perfil extends AppCompatActivity {

    String uid;
    String nombre;
    String montoTotal;

    ListenerFb listenerFb;

    InfoUsuario infoUsuario;

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference= FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");
        nombre=intent.getStringExtra("nombre");
        montoTotal=intent.getStringExtra("montoTotal");


        listenerFb = new ListenerFb();
        infoUsuario=new InfoUsuario();
        databaseReference.child("Usuarios").child(uid).child("Informacion").addValueEventListener(listenerFb);


        TextView textNombre=findViewById(R.id.textNombrePerfil);
        TextView textMonto=findViewById(R.id.textMontoTotalPerfil);
        textNombre.setText(nombre);
        if(montoTotal.equals("0")){
            textMonto.setText("No debe dinero");
        }else{
            textMonto.setText("Monto Total: S/."+montoTotal);
        }


        TextView textCelular=findViewById(R.id.textCelularPerfil);
        textCelular.setVisibility(View.GONE);
        TextView textDireccion=findViewById(R.id.textDIreccionPerfil);
        textDireccion.setVisibility(View.GONE);


        StorageReference fileRef=storageReference.child("FotosPerfil/"+uid+"/fotoPerfil");
        fileRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                colocarFoto();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // no hay foto de perfil
            }
        });
    }

    public void colocarFoto(){
        StorageReference referenceGlide = FirebaseStorage.getInstance().getReference().child("FotosPerfil/"+uid+"/fotoPerfil");

        ImageView imagen=findViewById(R.id.imageViewPerfil);

        referenceGlide.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(Perfil.this)
                            .load(task.getResult())
                            .apply(RequestOptions.circleCropTransform())
                            .into(imagen);

                } else {

                    Toast.makeText(Perfil.this,"Error en obtener la foto de perfil",Toast.LENGTH_SHORT).show();
                }

            }
        });


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
            infoUsuario=new InfoUsuario();
            databaseReference.child("Usuarios").child(uid).child("Informacion").addValueEventListener(listenerFb);
        }
    }

    private class ListenerFb implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue()!=null){

               infoUsuario= snapshot.getValue(InfoUsuario.class);


               TextView textCelular=findViewById(R.id.textCelularPerfil);
               TextView textDireccion=findViewById(R.id.textDIreccionPerfil);


               if((infoUsuario.getNumeroTelefonico()+"vacio").equals("vacio")){
                   textCelular.setVisibility(View.GONE);
               }else{
                   if((infoUsuario.getNumeroTelefonico()+"vacio").equals("nullvacio")){
                       textCelular.setVisibility(View.GONE);
                   }
                   else{
                       textCelular.setVisibility(View.VISIBLE);
                       textCelular.setText("+51 "+infoUsuario.getNumeroTelefonico());

                   }
               }

                if((infoUsuario.getDireccion()+"vacio").equals("vacio")){
                    textDireccion.setVisibility(View.GONE);
                }else{
                    if((infoUsuario.getDireccion()+"vacio").equals("nullvacio")){
                        textDireccion.setVisibility(View.GONE);
                    }
                    else{
                        textDireccion.setVisibility(View.VISIBLE);
                        textDireccion.setText(infoUsuario.getDireccion());

                    }
                }

                Log.d("InfoAppABC",infoUsuario.getDireccion()+"  "+infoUsuario.getNumeroTelefonico());


            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
