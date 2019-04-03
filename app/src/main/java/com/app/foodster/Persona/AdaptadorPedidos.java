package com.app.foodster.Persona;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.foodster.GlobalState;
import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.MyViewHolder> {

    Context context;
    Pedido fragPedido;

    GlobalState gs;
    ArrayList<ListaPedido> pedido;

    ArrayList<ListaProductosPedido> productosPedido;
    ArrayList<ListaProductosPedido> productos;
    AdaptadorProductosPedido adaptadorProductosPedido;

    public AdaptadorPedidos(Pedido fragPedido, Context context, ArrayList<ListaPedido> pedido, ArrayList<ListaProductosPedido> productosPedido) {
        this.fragPedido = fragPedido;
        this.context = context;
        this.pedido = pedido;
        this.productosPedido = productosPedido;

        gs = (GlobalState)context.getApplicationContext();
    }

    public void actualizar(ArrayList<ListaPedido> lista){
        pedido = new ArrayList<>();
        pedido.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdaptadorPedidos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_pedido,viewGroup,false);
        final AdaptadorPedidos.MyViewHolder holder = new AdaptadorPedidos.MyViewHolder(v);

        holder.btnRecibido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = pedido.get(holder.getAdapterPosition()).getId();

                dialog_recibido(id);
            }
        });

        return holder;
    }

    private void dialog_recibido(final int idPedido){
        android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);
        dialogo1.setTitle("");
        dialogo1.setMessage("Confirma que recibió el pedido?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(R.string.text_confirmar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                fragPedido.confirmarRecibido(idPedido);
            }
        });
        dialogo1.setNegativeButton(R.string.text_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.cancel();
            }
        });
        dialogo1.show();
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPedidos.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvEmpresa.setText(pedido.get(i).getEmpresa());
        String estado = pedido.get(i).getEstado();

        switch (estado){
            case "Enviado" : myViewHolder.ivEstado.setImageResource(R.drawable.ic_estado_visto);
                break;
            case "Visto" : myViewHolder.ivEstado.setImageResource(R.drawable.ic_estado_enviado);
                break;
            case "Preparacion" : myViewHolder.ivEstado.setImageResource(R.drawable.ic_estado_preparacion);
                break;
            case "Despachado" : myViewHolder.ivEstado.setImageResource(R.drawable.ic_estado_despachado);
                                myViewHolder.btnRecibido.setVisibility(View.VISIBLE);
                break;
        }
        myViewHolder.tvEstado.setText(estado);

        myViewHolder.tvCola.setText(pedido.get(i).getCola());
        myViewHolder.tvPago.setText(pedido.get(i).getPago());
        myViewHolder.tvTotal.setText(String.valueOf(pedido.get(i).getTotal()));

        productos = new ArrayList<>();

        int id = pedido.get(i).getId();

        for(int k=0;k<productosPedido.size();k++){
            if(id == productosPedido.get(k).getIdPedido()){
                productos.add(productosPedido.get(k));
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        adaptadorProductosPedido = new AdaptadorProductosPedido(context, productos);
        myViewHolder.rvProductos.setLayoutManager(layoutManager);
        myViewHolder.rvProductos.setAdapter(adaptadorProductosPedido);
    }

    public int getItemCount() {
        return pedido.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvEmpresa;
        private ImageView ivEstado;
        private TextView tvEstado;
        private TextView tvCola;
        private RecyclerView rvProductos;
        private TextView tvPago;
        private TextView tvTotal;
        private Button btnRecibido;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            ivEstado = itemView.findViewById(R.id.ivEstado);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvCola = itemView.findViewById(R.id.tvCola);
            rvProductos = itemView.findViewById(R.id.rvProductos);
            tvPago = itemView.findViewById(R.id.tvPago);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnRecibido = itemView.findViewById(R.id.btnRecibido);
        }
    }
}
