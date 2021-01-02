package pe.edu.pucp.proyecto.cliente;

import pe.edu.pucp.proyecto.Dialogos_Fragmentos.DialogoModificarImagenPerfil;
import pe.edu.pucp.proyecto.R;


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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    }

    //metodo de la interfaz del dialogo donde mustra dos opciones
    // una la galetia y otra la camara
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void opcionEscogida(String app) {

        if(app.equals("camara")){
            fotoCamara();
        }else{
            //opcion galeria
            fotoGaleria();
        }

    }

    int a=0;
    int REQUEST_IMAGE_CAPTURE=1;
    Bitmap imageBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ImageView imagePerfil = findViewById(R.id.imagePerfil);

            imageBitmap = (Bitmap) data.getExtras().get("data");
            imagePerfil.setImageBitmap(imageBitmap);


            }
    }



    int REQUEST_PERMISO_CAMARA=200;

    //@RequiresApi(api = Build.VERSION_CODES.M)
    public void fotoCamara() {
            Log.d("infoAppAS","FotodCamara");
           // if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //no tiene los permisos
            //    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISO_CAMARA);
           // }else{
                //TIENE LOS PERMISOS DE LA CAMARA
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 1);
           // }

        }


    public void fotoGaleria(){

    }






    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

            if(requestCode==REQUEST_PERMISO_CAMARA){
                    fotoCamara();
            }
            else{
                if(requestCode==2){
                    fotoGaleria();
                }
            }

        }

    }// fjn onRequestPermissionResult

    //GUARDAR EN EL FIREBASE
    public void guardar(View view){

        Button btnGuardar=findViewById(R.id.btnGuardarModificarPerfil);
        Button btnCancelar=findViewById(R.id.btnCancelarNuevaDeuda);

        btnCancelar.setVisibility(View.INVISIBLE);
        btnGuardar.setVisibility(View.INVISIBLE);

        TextView textCargando=findViewById(R.id.textCargandoModificarPerfil);
        textCargando.setVisibility(View.VISIBLE);

        //Transformar la foto en una rreglo de bytes
        ByteArrayOutputStream fotoJPG = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fotoJPG); //formato JPEG
        byte cadenabytes[] = fotoJPG.toByteArray();

        String nombreArchivo = "fotoPerfil";

        StorageReference referenceStorage = storageReference.child("FotosPerfil/" + nombreArchivo);
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
                                Log.d("infoAppAS","b");
                                String miURL = task.getResult().toString();
                                DatabaseReference referenceDataBase = FirebaseDatabase.getInstance().getReference();
                                referenceDataBase.child("Usuarios").child(currentUser.getUid()).child("Informacion").child("urlFoto").setValue(miURL);


                                textCargando.setText("Perfil Actualizado");
                                finish();
                            }
                        });

                    }
                });
    }


    public void cancelar(View view){
        finish();
    }
}