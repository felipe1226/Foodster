package com.app.foodster.Persona;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorProductoPedido extends RecyclerView.Adapter<AdaptadorProductoPedido.MyViewHolder> {

    Context context;
    ArrayList<ListaProductoPedido> producto;

    public AdaptadorProductoPedido(Context context, ArrayList<ListaProductoPedido> producto) {
        this.context = context;
        this.producto = producto;
    }

    @NonNull
    @Override
    public AdaptadorProductoPedido.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_producto_pedido,viewGroup,false);
        final AdaptadorProductoPedido.MyViewHolder holder = new AdaptadorProductoPedido.MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorProductoPedido.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvNombre.setText(producto.get(i).getNombre());
        myViewHolder.tvPrecio.setText(String.valueOf(producto.get(i).getPrecio()));
        myViewHolder.tvDetalles.setText(producto.get(i).getDetalles());
    }

    public int getItemCount() {
        return producto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNombre;
        private TextView tvPrecio;
        private TextView tvDetalles;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvDetalles = itemView.findViewById(R.id.tvDetalles);
        }
    }
}
