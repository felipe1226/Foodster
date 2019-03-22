package com.app.foodster.Persona;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.foodster.Producto.AdaptadorListaProductos;
import com.app.foodster.Producto.ListaProductos;
import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorListaPedidos extends RecyclerView.Adapter<AdaptadorListaPedidos.MyViewHolder> {

    Context context;
    ArrayList<ListaPedido> pedido;

    ArrayList<ListaProductoPedido> productosPedido;
    ArrayList<ListaProductoPedido> productos;
    AdaptadorProductoPedido adaptadorProductoPedido;

    public AdaptadorListaPedidos(Context context, ArrayList<ListaPedido> pedido, ArrayList<ListaProductoPedido> productosPedido) {
        this.context = context;
        this.pedido = pedido;
        this.productosPedido = productosPedido;
    }

    @NonNull
    @Override
    public AdaptadorListaPedidos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_pedido,viewGroup,false);
        final AdaptadorListaPedidos.MyViewHolder holder = new AdaptadorListaPedidos.MyViewHolder(v);

        holder.btnRecibido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = pedido.get(holder.getAdapterPosition()).getId();
                Toast.makeText(context, "Confirmado", Toast.LENGTH_SHORT).show();

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaPedidos.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvEmpresa.setText(pedido.get(i).getEmpresa());
        String estado = pedido.get(i).getEstado();

        switch (estado){
            case "Enviado" : myViewHolder.ivEstado.setImageResource(R.drawable.ic_estado_enviado);
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
        adaptadorProductoPedido = new AdaptadorProductoPedido(context, productos);
        myViewHolder.rvProductos.setLayoutManager(layoutManager);
        myViewHolder.rvProductos.setAdapter(adaptadorProductoPedido);
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
