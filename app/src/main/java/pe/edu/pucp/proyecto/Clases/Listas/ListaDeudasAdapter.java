package pe.edu.pucp.proyecto.Clases.Listas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import pe.edu.pucp.proyecto.Clases.Deuda;
import pe.edu.pucp.proyecto.Dialogos_Fragmentos.VisualizarProducto;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.PantallaRegistrarPagos;


public class ListaDeudasAdapter extends RecyclerView.Adapter<ListaDeudasAdapter.DeudaViewHolder>  {

    private Deuda[] data;
    private Context contexto;
    private String actividad;

    public ListaDeudasAdapter(Deuda[] data, Context contexto,String actividad){
        this.data=data;
        this.contexto=contexto;
        this.actividad=actividad;
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

        holder.rvProducto.setText(producto);
        holder.rvCantidad.setText("Cant: "+cantidad);
        holder.rvPrecio.setText("S/."+precio+" c/u");
        float temp=(float)Math.round(cantidad*precio* 100) / 100; //limitar los decimales
        holder.rvPrecioTotal.setText("S/."+temp);



        if(actividad.equals("NuevaDeuda")){
            //la actividad NuevaDeuda la ha llamado


        }else{
            //la actividad PantallaRegistroPagos la ha llamado

            //AL PRESIONAR UN ELEMENTO DEL RECYCLER VIEW DEBE SALIR UN FRAGMENTO
            //CON LA INFORMACION DEL ELEMENTO PRESIONADO
           holder.mLayoutDeuda.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   //ABRIR DIALOGO
                   PantallaRegistrarPagos myActivity = (PantallaRegistrarPagos) contexto;

                   VisualizarProducto visualizarProducto = new VisualizarProducto();

                   Bundle args = new Bundle();
                   args.putString("producto", producto);
                   args.putString("cantidad", String.valueOf(cantidad));
                   args.putString("precio", String.valueOf(precio));
                   args.putString("descripcion", descripcion);
                   args.putString("vista",actividad);

                   visualizarProducto.setArguments(args);

                   visualizarProducto.show(myActivity.getSupportFragmentManager(), "ver producto");


               }
           });

        }


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
