package pe.edu.pucp.proyecto.Clases.Listas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import pe.edu.pucp.proyecto.Clases.Deuda;
import pe.edu.pucp.proyecto.Clases.InfoUsuario;
import pe.edu.pucp.proyecto.R;


public class ListaDeudasAdapter extends RecyclerView.Adapter<ListaDeudasAdapter.DeudaViewHolder>  {

    private Deuda[] data;
    private Context contexto;

    public ListaDeudasAdapter(Deuda[] data, Context contexto){
        this.data=data;
        this.contexto=contexto;
    }

    @NonNull
    @Override
    public DeudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.rv_item_deuda,parent,false);
        DeudaViewHolder deudaViewHolder = new DeudaViewHolder(itemView);
        return deudaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeudaViewHolder holder, int position) {

        //MODIFICAR EL VIEW
        String producto=data[position].getProducto();
        int cantidad = data[position].getCantidad();
        float precio = data[position].getPrecio();
        String descripcion = data[position].getDescripcion();

        holder.rvProducto.setText("Producto "+producto);
        holder.rvCantidad.setText("Cant.: "+cantidad);
        holder.rvPrecio.setText("S/."+precio+" c/u");
        holder.rvPrecioTotal.setText("S/."+cantidad*precio);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }



    public static class DeudaViewHolder extends RecyclerView.ViewHolder {

        public TextView rvProducto;
        public TextView rvCantidad;
        public TextView rvPrecio;
        public TextView rvPrecioTotal;
        public ConstraintLayout mLayoutDeuda;

        public DeudaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rvProducto=itemView.findViewById(R.id.rvProducto);
            this.rvPrecio=itemView.findViewById(R.id.rvPrecio);
            this.rvCantidad=itemView.findViewById(R.id.rvCantidad);
            this.rvPrecioTotal=itemView.findViewById(R.id.rvPrecioTotal);
            this.mLayoutDeuda= itemView.findViewById(R.id.mLayoutDeuda);

        }
    }
}
