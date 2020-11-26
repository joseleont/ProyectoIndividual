package pe.edu.pucp.proyecto.Vendedor;

import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.cliente.PantallaPrincipalCliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PantallaListaDeudasDelCliente extends AppCompatActivity {

    String usuario;
    String nombre;
    String apellido;
    int montoTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_deudas_del_cliente);

        Intent intent=getIntent();
        usuario=intent.getStringExtra("usuario");
        nombre=intent.getStringExtra("nombre");
        apellido=intent.getStringExtra("apellido");
        montoTotal=intent.getIntExtra("montoTotal",0);

        TextView info=findViewById(R.id.textInfoCliente);
        info.setText(nombre+" "+apellido);
    }

    public void agregarProducto(View view){

    }

    public void agregarDeuda(View view){
        Intent intent=new Intent(this,NuevaDeuda.class);
        intent.putExtra("usuario",usuario);
        intent.putExtra("nombre",nombre);
        startActivity(intent);




    }

}