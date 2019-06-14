package com.app.foodster.Empresa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.app.foodster.GlobalState;
import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorCategorias extends RecyclerView.Adapter<AdaptadorCategorias.MyViewHolder> {
    Context context;
    ArrayList<String> categorias;
    ArrayList<String> filtroCategorias;
    GlobalState gs;

    public AdaptadorCategorias(Context context, ArrayList<String> categorias
                                , ArrayList<String> filtroCategorias, GlobalState gs) {
        this.context = context;
        this.categorias = categorias;
        this.filtroCategorias = filtroCategorias;
        this.gs = gs;
    }

    @NonNull
    @Override
    public AdaptadorCategorias.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_filtro_categoria,viewGroup,false);
        final AdaptadorCategorias.MyViewHolder holder = new AdaptadorCategorias.MyViewHolder(v);

        holder.cbCategoria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String categoria = categorias.get(holder.getAdapterPosition());

                if(filtroCategorias.size() == 0){
                    filtroCategorias.add(categoria);
                }
                else{
                    if(holder.cbCategoria.isChecked()){
                        filtroCategorias.add(categoria);
                    }
                    else{
                        for(int i=0;i<filtroCategorias.size();i++){
                            if(filtroCategorias.get(i).compareTo(categoria) == 0){
                                filtroCategorias.remove(i);
                                break;
                            }
                        }
                    }
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCategorias.MyViewHolder myViewHolder, int i) {

        boolean marca = false;

        for(int j=0;j<gs.filtroCategorias.size();j++){
            if(gs.filtroCategorias.get(j).compareTo(categorias.get(i)) == 0){
                marca = true;
            }
        }
        myViewHolder.cbCategoria.setChecked(marca);
        myViewHolder.cbCategoria.setText(categorias.get(i));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item_categoria;
        private CheckBox cbCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_categoria = itemView.findViewById(R.id.item_categoria);
            cbCategoria = itemView.findViewById(R.id.cbCategoria);
        }
    }
}