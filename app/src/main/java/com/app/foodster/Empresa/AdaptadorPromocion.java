package com.app.foodster.Empresa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.foodster.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorPromocion extends RecyclerView.Adapter<AdaptadorPromocion.MyViewHolder> {

    Context context;
    ArrayList<ListaEventos> promocion;

    public AdaptadorPromocion(Context context, ArrayList<ListaEventos> promocion) {
        this.context = context;
        this.promocion = promocion;
    }

    public void setFilter(ArrayList<ListaEventos> lista) {
        promocion = new ArrayList<>();
        promocion.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdaptadorPromocion.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_promocion, viewGroup, false);
        final AdaptadorPromocion.MyViewHolder holder = new AdaptadorPromocion.MyViewHolder(v);

        holder.item_promocion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = promocion.get(holder.getAdapterPosition()).getId();
                verEvento(id, v);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPromocion.MyViewHolder myViewHolder, int i) {

        String nombre = promocion.get(i).getNombre();
        myViewHolder.tvNombre.setText(nombre);
        myViewHolder.tvFecha.setText(promocion.get(i).getFecha());

        String url = promocion.get(i).getFoto();
        if (url != "null" && url != null) {
            Picasso.with(context)
                    .load("http://foodster.com.co/back-end/" + url)
                    .error(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(myViewHolder.ivImagen);
            promocion.get(i).setbFoto(myViewHolder.ivImagen.getDrawingCache());
        } else {
            myViewHolder.ivImagen.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        return promocion.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_promocion;
        private ImageView ivImagen;
        private TextView tvNombre;
        private TextView tvFecha;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_promocion = itemView.findViewById(R.id.item_promocion);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            tvNombre = itemView.findViewById(R.id.etNombre);
            tvFecha = itemView.findViewById(R.id.etMes);
        }
    }


    public void verEvento(int id, View v) {

        Toast.makeText(context, "Promocion: "+id, Toast.LENGTH_SHORT).show();
    }
}
