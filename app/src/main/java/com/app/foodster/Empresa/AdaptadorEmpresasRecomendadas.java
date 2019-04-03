package com.app.foodster.Empresa;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.foodster.GlobalState;
import com.app.foodster.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class AdaptadorEmpresasRecomendadas extends RecyclerView.Adapter<AdaptadorEmpresasRecomendadas.MyViewHolder> {

    Context context;
    Empresas frag;
    ArrayList<ListaEmpresasRecomendadas> empresa;

    public AdaptadorEmpresasRecomendadas(Empresas frag, Context context, ArrayList<ListaEmpresasRecomendadas> empresa) {
        this.frag = frag;
        this.context = context;
        this.empresa = empresa;
    }


    @NonNull
    @Override
    public AdaptadorEmpresasRecomendadas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_empresa_recomendada, viewGroup, false);
        final AdaptadorEmpresasRecomendadas.MyViewHolder holder = new AdaptadorEmpresasRecomendadas.MyViewHolder(v);

        holder.item_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = empresa.get(holder.getAdapterPosition()).getIdEmpresa();
                verEmpresa(id, v);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorEmpresasRecomendadas.MyViewHolder myViewHolder, int i) {

        String nombre = empresa.get(i).getNombre();
        myViewHolder.tvNombre.setText(nombre);
        myViewHolder.tvCategoria.setText(empresa.get(i).getCategoria());

        String url = empresa.get(i).getFoto();
        if (url != null) {
            Picasso.with(context)
                    .load("http://foodster.com.co/back-end/empresa/" + nombre + "/" + url)
                    .error(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(myViewHolder.ivFoto);
            empresa.get(i).setbFoto(myViewHolder.ivFoto.getDrawingCache());
        }
    }

    public int getItemCount() {
        return empresa.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout item_empresa;
        private ImageView ivFoto;
        private TextView tvNombre;
        private TextView tvCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_empresa = itemView.findViewById(R.id.item_empresa);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
        }
    }


    public void verEmpresa(int id, View v) {
        frag.generarVisita(id);

        Bundle args = new Bundle();
        args.putInt("idEmpresa", id);

        GlobalState gs = (GlobalState) context.getApplicationContext();

        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        fragInformacionEmpresa fragment = new fragInformacionEmpresa();
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