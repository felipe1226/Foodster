package com.app.foodster.Empresa;

import android.os.Bundle;
import android.content.Context;
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

import java.util.ArrayList;

public class AdaptadorListaEmpresas extends RecyclerView.Adapter<AdaptadorListaEmpresas.MyViewHolder> {

    Context context;
    ArrayList<ListaEmpresas> empresa;

    public AdaptadorListaEmpresas(Context context, ArrayList<ListaEmpresas> empresa) {
        this.context = context;
        this.empresa = empresa;
    }

    public void setFilter(ArrayList<ListaEmpresas> lista){
        empresa = new ArrayList<>();
        empresa.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdaptadorListaEmpresas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_empresa,viewGroup,false);
        final AdaptadorListaEmpresas.MyViewHolder holder = new AdaptadorListaEmpresas.MyViewHolder(v);

        holder.item_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = empresa.get(holder.getAdapterPosition()).getId();
                int ind = holder.getAdapterPosition();

                Bundle args = new Bundle();
                args.putInt("idEmpresa", id);

                GlobalState gs = (GlobalState)context.getApplicationContext();

                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                fragInformacionEmpresa fragment = new fragInformacionEmpresa();
                gs.setFragmentEmpresas(fragment);
                gs.setFragment(fragment);
                gs.setFragmentActual("InformacionEmpresas");
                fragment.setArguments(args);
                FragmentManager fm = activity.getSupportFragmentManager();

                activity.getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container, fragment, fragment.getClass().toString()) // add and tag the new fragment
                            .commit();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaEmpresas.MyViewHolder myViewHolder, int i) {
        //myViewHolder.ivFoto.setImageBitmap(empresa.get(i).getFoto());
        myViewHolder.tvNombre.setText(empresa.get(i).getNombre());
    }

    public int getItemCount() {
        return empresa.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout item_empresa;
        private ImageView ivFoto;
        private TextView tvNombre;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_empresa = itemView.findViewById(R.id.item_empresa);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
        }
    }
}
