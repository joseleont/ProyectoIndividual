package pe.edu.pucp.proyecto.cliente;

import pe.edu.pucp.proyecto.Dialogos_Fragmentos.DialogoModificarImagenPerfil;
import pe.edu.pucp.proyecto.R;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ModificarPerfil extends AppCompatActivity {

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


    int REQUEST_IMAGE_CAPTURE=1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ImageView imagePerfil = findViewById(R.id.imagePerfil);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagePerfil.setImageBitmap(imageBitmap);
        }
    }



}