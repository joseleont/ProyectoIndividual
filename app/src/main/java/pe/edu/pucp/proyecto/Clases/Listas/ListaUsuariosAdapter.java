package pe.edu.pucp.proyecto.Clases.Listas;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.PantallaListaDeudasDelCliente;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.InfoUsuarioViewHolder> {

    private InfoUsuario[] data;
    private Context contexto;

    public ListaUsuariosAdapter(InfoUsuario[] data,Context contexto){
        this.data=data;
        this.contexto=contexto;
    }

    @NonNull
    @Override
    public InfoUsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.rv_item_usuario,parent,false);
        InfoUsuarioViewHolder infoUsuarioViewHolder = new InfoUsuarioViewHolder(itemView);
        return infoUsuarioViewHolder;
    }

    int color=0;

    @Override
    public void onBindViewHolder(@NonNull InfoUsuarioViewHolder holder, int position) {

        //MODIFICAR EL VIEW
    String nombre=data[position].getNombre();
    String apellido=data[position].getApellido();

    String montoTotal=data[position].getMontoTotal();

    if(color==0){
        holder.rvImage.setBackgroundColor(Color.parseColor("#5820c1"));
        holder.mLayout.setBackgroundColor(Color.parseColor("#03A9F4"));
        color=1;
    }else{
        holder.rvImage.setBackgroundColor(Color.parseColor("#EA2268"));
        holder.mLayout.setBackgroundColor(Color.parseColor("#60CF18"));
        color=0;
    }

    holder.rvNombre.setText(nombre+" "+apellido);
    holder.rvMontoTotal.setText("S/."+montoTotal);
    holder.mLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //ABRE LA PANTALLA ´PARA VER TODAS LAS DEUDAS DEL USUARIO
            //SE ABRE PRESIONANDO UN USUARIO DEL RECYCLERVIEW DE LA PANTALLA PRINCIPAL DEL VENDEDOR
            Intent intent = new Intent(contexto, PantallaListaDeudasDelCliente.class);
            intent.putExtra("usuario", data[position].getUsuario());
            intent.putExtra("nombre", data[position].getNombre());
            intent.putExtra("apellido", data[position].getApellido());
            intent.putExtra("montoTotal", data[position].getMontoTotal());
            contexto.startActivity(intent);

        }
    });



    }

    @Override
    public int getItemCount() {
        return data.length;
    }




    public static class InfoUsuarioViewHolder extends RecyclerView.ViewHolder {


        public TextView rvNombre;
        public TextView rvMontoTotal;
        public ImageView rvImage;
        public ConstraintLayout mLayout;

        public InfoUsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rvNombre=itemView.findViewById(R.id.rvNombre);
            this.rvMontoTotal=itemView.findViewById(R.id.rvMontoTotal);
            this.rvImage=itemView.findViewById(R.id.rvImage);
            this.mLayout= itemView.findViewById(R.id.mLayoutUsuario);

        }
    }
}
