package com.app.foodster.Persona;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorProductosPedido extends RecyclerView.Adapter<AdaptadorProductosPedido.MyViewHolder> {

    Context context;
    ArrayList<ListaProductosPedido> producto;

    public AdaptadorProductosPedido(Context context, ArrayList<ListaProductosPedido> producto) {
        this.context = context;
        this.producto = producto;
    }

    @NonNull
    @Override
    public AdaptadorProductosPedido.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_producto_pedido,viewGroup,false);
        final AdaptadorProductosPedido.MyViewHolder holder = new AdaptadorProductosPedido.MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorProductosPedido.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvNombre.setText(producto.get(i).getNombre());
        if(producto.get(i).getPromocion() == 0){
            myViewHolder.tvPrecio.setText(String.valueOf(producto.get(i).getPrecio()));
        }
        else{
            myViewHolder.tvPrecio.setText(String.valueOf(producto.get(i).getPromocion()));
        }

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

            tvNombre = itemView.findViewById(R.id.etNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvDetalles = itemView.findViewById(R.id.tvDetalles);
        }
    }
}
