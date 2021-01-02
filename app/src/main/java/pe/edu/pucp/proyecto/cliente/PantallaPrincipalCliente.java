package pe.edu.pucp.proyecto.cliente;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.MainActivity;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.PantallaPrincipalVendedor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PantallaPrincipalCliente extends AppCompatActivity {
    DatabaseReference reference;
    ListenerFb listenerFb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_cliente);

        listenerFb=new ListenerFb();
        reference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("Usuarios").child(currentUser.getUid()).child("montoTotal").addValueEventListener(listenerFb);

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
                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(PantallaPrincipalCliente.this,MainActivity.class));
                        finish();
                    }
                });

                return true;
            case R.id.menuModificarPerfil:
                Intent intentMenuModificarPerfil = new Intent( this , ModificarPerfil.class);
                startActivity(intentMenuModificarPerfil);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(listenerFb);
    }

    String montoTotal;
    class ListenerFb implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if((snapshot.getValue()!=null)){


                String monto= snapshot.getValue().toString();

                if((monto+"").equals("null")){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    reference.child("Usuarios").child(currentUser.getUid()).child("montoTotal").setValue("0");
                    reference.child("Usuarios").child(currentUser.getUid()).child("Deuda").child("CantidadDeDeudas").setValue("0");
                }
                else{
                    montoTotal=monto;

                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d("infoApp","Error");
        }
    }
}