package com.app.foodster.Empresa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorListaHorarios extends RecyclerView.Adapter<AdaptadorListaHorarios.MyViewHolder> {

    Context context;
    ArrayList<ListaHorarios> horario;

    public AdaptadorListaHorarios(Context context, ArrayList<ListaHorarios> horario) {
        this.context = context;
        this.horario = horario;
    }

    @NonNull
    @Override
    public AdaptadorListaHorarios.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_horario,viewGroup,false);
        final AdaptadorListaHorarios.MyViewHolder holder = new AdaptadorListaHorarios.MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaHorarios.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvDia.setText(horario.get(i).getDia());

        if(horario.get(i).getEstado() == 1){
            myViewHolder.tvHoras.setText(horario.get(i).getApertura()+" - "+horario.get(i).getCierre());
        }
        else{
            myViewHolder.tvHoras.setVisibility(View.GONE);
            myViewHolder.tvEstado.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return horario.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout item_horario;
        private TextView tvDia;
        private TextView tvHoras;
        private TextView tvEstado;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_horario = itemView.findViewById(R.id.item_horario);
            tvDia = itemView.findViewById(R.id.tvDia);
            tvHoras = itemView.findViewById(R.id.tvHoras);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
