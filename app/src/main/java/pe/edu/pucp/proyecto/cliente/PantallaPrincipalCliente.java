package pe.edu.pucp.proyecto.cliente;

import pe.edu.pucp.proyecto.MainActivity;
import pe.edu.pucp.proyecto.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PantallaPrincipalCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_cliente);


        Intent intent=getIntent();
        String usuario=intent.getStringExtra("usuario");
        String nombre=intent.getStringExtra("nombre");
        String apellido=intent.getStringExtra("apellido");
        int montoTotal=intent.getIntExtra("montoTotal",0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pantalla_principal_cliente,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.menuSalirCliente:
                //CONFIGURACION PARA DESLOGEAR
                SharedPreferences sharedPreferences=getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor edit=sharedPreferences.edit();

                edit.putString("Login",""); //SE VACIA EL ARCHIVO PREFERENCE
                edit.apply();

                Intent intentMenuSalirCliente = new Intent( this , MainActivity.class);
                startActivity(intentMenuSalirCliente);
                finish();

                return true;
            case R.id.menuModificarPerfil:
                Intent intentMenuModificarPerfil = new Intent( this , ModificarPerfil.class);
                startActivity(intentMenuModificarPerfil);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}