package com.app.foodster.Empresa;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.foodster.GlobalState;
import com.app.foodster.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorEventos extends RecyclerView.Adapter<AdaptadorEventos.MyViewHolder> {

    Context context;
    ArrayList<ListaEventos> evento;

    public AdaptadorEventos(Context context, ArrayList<ListaEventos> evento) {
        this.context = context;
        this.evento = evento;
    }

    public void setFilter(ArrayList<ListaEventos> lista) {
        evento = new ArrayList<>();
        evento.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdaptadorEventos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_evento, viewGroup, false);
        final AdaptadorEventos.MyViewHolder holder = new AdaptadorEventos.MyViewHolder(v);

        holder.item_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = evento.get(holder.getAdapterPosition()).getId();
                verEvento(id, v);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorEventos.MyViewHolder myViewHolder, int i) {

        String nombre = evento.get(i).getNombre();
        myViewHolder.tvNombre.setText(nombre);
        myViewHolder.tvEmpresa.setText(evento.get(i).getEmpresa());
        myViewHolder.tvFecha.setText(evento.get(i).getFecha());

        String url = evento.get(i).getFoto();
        if (url != "null" && url != null) {
            Picasso.with(context)
                    .load("http://foodster.com.co/back-end/" + url)
                    .error(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(myViewHolder.ivImagen);
            evento.get(i).setbFoto(myViewHolder.ivImagen.getDrawingCache());
        }
        else{
            myViewHolder.ivImagen.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        return evento.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_evento;
        private ImageView ivImagen;
        private TextView tvNombre;
        private TextView tvEmpresa;
        private TextView tvFecha;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_evento = itemView.findViewById(R.id.item_evento);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            tvNombre = itemView.findViewById(R.id.etNombre);
            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            tvFecha = itemView.findViewById(R.id.etMes);
        }
    }


    public void verEvento(int id, View v) {

        Bundle args = new Bundle();
        args.putInt("idEvento", id);

        GlobalState gs = (GlobalState) context.getApplicationContext();

        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        InformacionEmpresa fragment = new InformacionEmpresa();
        gs.setFragment(fragment);
        gs.setFragmentActual("InformacionEmpresas");
        fragment.setArguments(args);
        FragmentManager fm = activity.getSupportFragmentManager();

        activity.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment, fragment.getClass().toString()).addToBackStack(null) // add and tag the new fragment
                .commit();


    }
}