package com.app.foodster.Producto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorListaCartas extends RecyclerView.Adapter<AdaptadorListaCartas.MyViewHolder> {
    Context context;
    int idEmpresa;

    ArrayList<ListaCartas> carta;

    ArrayList<ListaProductos> producto;
    ArrayList<ListaProductos> productosCarta;
    AdaptadorListaProductos adaptadorListaProductos;

    public AdaptadorListaCartas(Context context, ArrayList<ListaCartas> carta, ArrayList<ListaProductos> producto, int idEmpresa) {
        this.context = context;
        this.carta = carta;
        this.producto = producto;
        this.idEmpresa = idEmpresa;
    }

    @NonNull
    @Override
    public AdaptadorListaCartas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_carta,viewGroup,false);
        final AdaptadorListaCartas.MyViewHolder holder = new AdaptadorListaCartas.MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaCartas.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvCarta.setText(carta.get(i).getCarta());

        productosCarta = new ArrayList<>();
        int id;

        for(int j=0;j<producto.size();j++){
            id = carta.get(i).getId();
            if(producto.get(j).getIdCarta() == id) {
                if (producto.get(j).getPromocion() != 0) {
                    productosCarta.add(new ListaProductos(idEmpresa, id, producto.get(j).getId(), producto.get(j).getNombre(),
                            producto.get(j).getDescripcion(), producto.get(j).getPrecio(), producto.get(j).getPromocion(), producto.get(j).getDescPromocion(),
                            producto.get(j).getDescuento(), producto.get(j).getFecha()));
                }
                else {
                    productosCarta.add(new ListaProductos(idEmpresa, id, producto.get(j).getId(), producto.get(j).getNombre(),
                            producto.get(j).getDescripcion(), producto.get(j).getPrecio(), producto.get(j).getPromocion()));

                }
                if(producto.get(j).getFoto1() != null){
                    productosCarta.get(productosCarta.size()-1).setFoto1(producto.get(j).getFoto1());
                }
                if(producto.get(j).getFoto2() != null){
                    productosCarta.get(productosCarta.size()-1).setFoto2(producto.get(j).getFoto2());
                }
                if(producto.get(j).getFoto3() != null){
                    productosCarta.get(productosCarta.size()-1).setFoto3(producto.get(j).getFoto3());
                }
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adaptadorListaProductos = new AdaptadorListaProductos(context, productosCarta);
        myViewHolder.rvProductos.setLayoutManager(layoutManager);
        myViewHolder.rvProductos.setAdapter(adaptadorListaProductos);
    }

    @Override
    public int getItemCount() {
        return carta.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCarta;
        private RecyclerView rvProductos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCarta = itemView.findViewById(R.id.tvCarta);
            rvProductos = itemView.findViewById(R.id.rvProductos);
        }
    }
}
