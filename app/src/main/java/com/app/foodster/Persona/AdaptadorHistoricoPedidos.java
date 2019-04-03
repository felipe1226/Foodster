package com.app.foodster.Persona;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.foodster.GlobalState;
import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorHistoricoPedidos extends RecyclerView.Adapter<AdaptadorHistoricoPedidos.MyViewHolder> {

    Context context;

    GlobalState gs;
    ArrayList<ListaHistoricoPedidos> pedido;

    ArrayList<ListaProductosHistorico> productosPedido;
    ArrayList<ListaProductosHistorico> productos;
    AdaptadorProductosHistorico adaptadorProductoHistorico;

    public AdaptadorHistoricoPedidos(Context context, ArrayList<ListaHistoricoPedidos> pedido, ArrayList<ListaProductosHistorico> productosPedido) {
        this.context = context;
        this.pedido = pedido;
        this.productosPedido = productosPedido;

        gs = (GlobalState)context.getApplicationContext();
    }


    @NonNull
    @Override
    public AdaptadorHistoricoPedidos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_historico_pedido,viewGroup,false);
        final AdaptadorHistoricoPedidos.MyViewHolder holder = new AdaptadorHistoricoPedidos.MyViewHolder(v);

        holder.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.rvProductos.getVisibility() == View.GONE){
                    holder.rvProductos.setVisibility(View.VISIBLE);
                    holder.btnVer.setImageResource(R.drawable.ic_expand_less);
                }
                else{
                    holder.rvProductos.setVisibility(View.GONE);
                    holder.btnVer.setImageResource(R.drawable.ic_expand_more);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorHistoricoPedidos.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvEmpresa.setText(pedido.get(i).getEmpresa());
        myViewHolder.tvFecha.setText(pedido.get(i).getFecha());
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
        adaptadorProductoHistorico = new AdaptadorProductosHistorico(context, productos);
        myViewHolder.rvProductos.setLayoutManager(layoutManager);
        myViewHolder.rvProductos.setAdapter(adaptadorProductoHistorico);
    }

    public int getItemCount() {
        return pedido.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvEmpresa;
        private TextView tvFecha;
        private ImageButton btnVer;
        private RecyclerView rvProductos;
        private TextView tvPago;
        private TextView tvTotal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            btnVer = itemView.findViewById(R.id.btnVer);
            rvProductos = itemView.findViewById(R.id.rvProductos);
            tvPago = itemView.findViewById(R.id.tvPago);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
