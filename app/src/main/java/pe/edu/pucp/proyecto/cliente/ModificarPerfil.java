package pe.edu.pucp.proyecto.cliente;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.Dialogos_Fragmentos.DialogoModificarImagenPerfil;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.NuevaDeuda;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ModificarPerfil extends AppCompatActivity implements DialogoModificarImagenPerfil.CambioImagenPerfil{


    StorageReference storageReference=FirebaseStorage.getInstance().getReference();

    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();


    ListenerFb listenerFb;

    int repeticion=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_perfil);


        ImageButton btnCamara=findViewById(R.id.btnCamara);
        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogoModificarImagenPerfil dialogoModificarImagenPerfil=new DialogoModificarImagenPerfil();
                dialogoModificarImagenPerfil.show(getSupportFragmentManager(),"camara_galeria");
            }
        });


        if(repeticion==0){
            StorageReference fileRef=storageReference.child("FotosPerfil/"+currentUser.getUid()+"/fotoPerfil");
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
            repeticion=repeticion+1;
        }


        listenerFb= new ListenerFb();
        databaseReference.child("Usuarios").child(currentUser.getUid()).child("Informacion").addValueEventListener(listenerFb);


    }


    public void colocarFoto(){
            StorageReference referenceGlide = FirebaseStorage.getInstance().getReference().child("FotosPerfil/"+currentUser.getUid()+"/fotoPerfil");

            //en el child se debe poner el nombre del archivo
            ImageView imagen=findViewById(R.id.imagePerfil);

         //   Glide.with(this).load(referenceGlide).into(imagen);
            //load DESCARGA LA IMAGEN
            //into la coloca en el imageview

        referenceGlide.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(ModificarPerfil.this)
                            .load(task.getResult())
                            .apply(RequestOptions.circleCropTransform())
                            .into(imagen);

                } else {

                    Toast.makeText(ModificarPerfil.this,"Error en obtener la foto de perfil",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    int eleccion=0;

    //metodo de la interfaz del dialogo donde mustra dos opciones
    // una la galetia y otra la camara
    @Override
    public void opcionEscogida(String app) {

        if(app.equals("camara")){
            fotoCamara();
            eleccion=1;
        }else{
            //opcion galeria
            fotoGaleria();
            eleccion=2;
        }

    }

    int a=0;
    int REQUEST_IMAGE_CAPTURE=1;
    int REQUEST_GALERIA=2;
    Bitmap imageBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        ImageView imagePerfil = findViewById(R.id.imagePerfil);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            imageBitmap = (Bitmap) data.getExtras().get("data");
            imagePerfil.setImageBitmap(imageBitmap);

            }else{
            if (requestCode == REQUEST_GALERIA && resultCode == RESULT_OK) {
                Uri direccionImagen = data.getData();
                try{
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),direccionImagen);

                    imagePerfil.setImageBitmap(imageBitmap);
                    imagePerfil.invalidate();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    int REQUEST_PERMISO_GALERIA=300;


    public void fotoCamara() {

            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                //TIENE LOS PERMISOS DE LA CAMARA
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, REQUEST_IMAGE_CAPTURE);

            }else{
                Toast.makeText(this,"Su celular debe contar con una camara para acceder a esta opcion",Toast.LENGTH_SHORT).show();
            }

        }


    public void fotoGaleria(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //PEDIR PERMISO PARA ACCEDER AL ALMACENAMIENTO INTERNO
            String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permisos,1);

        }else{
            //SE TIENE LOS PERSMISOS
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALERIA);
        }

    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                if(requestCode==REQUEST_PERMISO_GALERIA){
                    //GALERIA
                    fotoGaleria();
                }
        }

    }// fjn onRequestPermissionResult

    //GUARDAR EN EL FIREBASE
    public void guardar(View view){

        if(eleccion!=0){
            guardarFoto(); //no se actualizo el perfil con ninguna foto
            guardarInformacion();
        }else{
            guardarInformacion();
            finish();
        }

    }

    public void guardarInformacion() {

        databaseReference.removeEventListener(listenerFb);

        EditText editTextCelular = findViewById(R.id.editTextCelularModificarPerfil);
        EditText editTextDireccion = findViewById(R.id.editTextDirecionModificarPerfil);

        String celular = editTextCelular.getText().toString() + "";
        String direccion = editTextDireccion.getText().toString() + "";

        DatabaseReference referenceDataBase = FirebaseDatabase.getInstance().getReference();
        referenceDataBase.child("Usuarios").child(currentUser.getUid()).child("Informacion").child("numeroTelefonico").setValue(celular);
        referenceDataBase.child("Usuarios").child(currentUser.getUid()).child("Informacion").child("direccion").setValue(direccion);



    }


    public void guardarFoto(){
        Button btnGuardar=findViewById(R.id.btnGuardarModificarPerfil);
        Button btnCancelar=findViewById(R.id.btnCancelarModificarPerfil);

        btnCancelar.setVisibility(View.INVISIBLE);
        btnGuardar.setVisibility(View.INVISIBLE);

        TextView textCargando=findViewById(R.id.textCargandoModificarPerfil);
        textCargando.setVisibility(View.VISIBLE);

        //Transformar la foto en una rreglo de bytes
        ByteArrayOutputStream fotoJPG = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fotoJPG); //formato JPEG
        byte cadenabytes[] = fotoJPG.toByteArray();

        String nombreArchivo = "fotoPerfil";
        String ruta="FotosPerfil/"+currentUser.getUid()+"/";

        StorageReference referenceStorage = storageReference.child(ruta + nombreArchivo);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        StorageMetadata metadata= new StorageMetadata.Builder().setCustomMetadata("perfil",currentUser.getDisplayName()).build();


        referenceStorage.putBytes(cadenabytes,metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Foto de perfil subida exitosamente", Toast.LENGTH_SHORT).show();
                        referenceStorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                textCargando.setText("Perfil Actualizado");
                                String miURL = task.getResult().toString();
                                DatabaseReference referenceDataBase = FirebaseDatabase.getInstance().getReference();
                                referenceDataBase.child("Usuarios").child(currentUser.getUid()).child("Informacion").child("urlFoto").setValue(miURL);


                                finish();
                            }
                        });

                    }
                });
    }


    public void cancelar(View view){
        databaseReference.removeEventListener(listenerFb);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(listenerFb);
    }

    //Leer la informacion
    private class ListenerFb implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue()!=null){

                InfoUsuario infoUsuario=new InfoUsuario();
                infoUsuario=snapshot.getValue(InfoUsuario.class);

                EditText editTextCelular = findViewById(R.id.editTextCelularModificarPerfil);
                EditText editTextDireccion = findViewById(R.id.editTextDirecionModificarPerfil);
                Log.d("infoAoo",infoUsuario.getNumeroTelefonico()+"dd");
                editTextCelular.setText("");
                editTextDireccion.setText("");

                if(!(infoUsuario.getNumeroTelefonico()+"").equals("null")){
                    editTextCelular.setText(infoUsuario.getNumeroTelefonico());
                }

                if(!(infoUsuario.getDireccion()+"").equals("null")){

                    editTextDireccion.setText(infoUsuario.getDireccion());
                }

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}