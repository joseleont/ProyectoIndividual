package pe.edu.pucp.proyecto.Clases.Listas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import pe.edu.pucp.proyecto.Clases.DeudaGeneral;
import pe.edu.pucp.proyecto.R;
import pe.edu.pucp.proyecto.Vendedor.PantallaListaDeudasDelCliente;
import pe.edu.pucp.proyecto.Vendedor.PantallaRegistrarPagos;

public class ListaDeudasSinPagarAdapter extends RecyclerView.Adapter<ListaDeudasSinPagarAdapter.DeudaSinPagarViewHolder>  {

    private DeudaGeneral[] data;
    private Context contexto;
    private String tipoLista;
    private String cliente; //identifica si es un cliente quien ingreso

    private int color=0;

    public ListaDeudasSinPagarAdapter(DeudaGeneral[] data, Context contexto, String tipoLista,String cliente){
        this.data=data;
        this.contexto=contexto;
        this.tipoLista=tipoLista;
        this.cliente=cliente;
    }

    @NonNull
    @Override
    public DeudaSinPagarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.rv_item_lista_deudas,parent,false);
        DeudaSinPagarViewHolder deudaSinPagarViewHolder = new DeudaSinPagarViewHolder(itemView);
        return deudaSinPagarViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeudaSinPagarViewHolder holder, int position) {
        //MODIFICAR EL VIEW
        Log.d("infoAppH","Se llego al onBindViewHolder");
        String fecha= data[position].getFecha();
        String identificador=data[position].getIdentificador();

        String monto;
        if(tipoLista.equals("Pendientes")){
            monto= data[position].getMontoDeuda();
        }else{
            monto= data[position].getMontoPagado();
        }


        holder.rvFechaLD.setText(fecha);
        holder.rvMontoDeudaLD.setText("S/."+monto);

        if(color==0){
            holder.rvLayoutLD.setBackgroundColor(Color.parseColor("#7AE633"));
            color=1;
        }else{
            holder.rvLayoutLD.setBackgroundColor(Color.parseColor("#03A9F4"));
            color=0;
        }



        if(tipoLista.equals("Pendientes")){
            //AQUI SE ABRIRA la pantalla para registrar los pagos.
            holder.rvLayoutLD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(contexto, PantallaRegistrarPagos.class);
                    intent.putExtra("identificador", identificador);
                    intent.putExtra("fecha", fecha);
                    intent.putExtra("monto", monto);
                    intent.putExtra("tipo","Pendientes");
                    intent.putExtra("cliente",cliente);

                    contexto.startActivity(intent);

                }
            });

        }else{
            Log.d("mensajeA",tipoLista);
            if(tipoLista.equals("Pagados")){
                Log.d("mensajeA",tipoLista+"Aqui");
                //como el pago ya se realizó se abria la misma pantalla pero solo se podrá ver la información
                //mas no agregar PAGOS
                holder.rvLayoutLD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("infoAppHA","Pagados");
                        Intent intent = new Intent(contexto, PantallaRegistrarPagos.class);
                        intent.putExtra("identificador", identificador);
                        intent.putExtra("fecha", fecha);
                        intent.putExtra("monto", monto);
                        intent.putExtra("tipo","Pagados");
                        intent.putExtra("cliente",cliente);

                        contexto.startActivity(intent);

                    }
                });

            }

            //TIENE LA FUNCION DE SOLO MOSTRAR LA FECHA Y EL MONTO PAGADO
            //SIRVE PARA VER EL HISTORIAL DE PAGOS. ESTE PAGO NO SE HA FINALIZADO
            if(tipoLista.equals("Visualizar")){


            }
        }


    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class DeudaSinPagarViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout rvLayoutLD;
        public TextView rvMontoDeudaLD;
        public TextView rvFechaLD;

        public DeudaSinPagarViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rvLayoutLD=itemView.findViewById(R.id.rvLayoutLD);
            this.rvMontoDeudaLD=itemView.findViewById(R.id.rvMontoDeudaLD);
            this.rvFechaLD=itemView.findViewById(R.id.rvFechaLD);


        }
    }
}
