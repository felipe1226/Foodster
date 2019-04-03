package com.app.foodster.Empresa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorHorarios extends RecyclerView.Adapter<AdaptadorHorarios.MyViewHolder> {

    Context context;
    ArrayList<ListaHorarios> horario;

    public AdaptadorHorarios(Context context, ArrayList<ListaHorarios> horario) {
        this.context = context;
        this.horario = horario;
    }

    @NonNull
    @Override
    public AdaptadorHorarios.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_horario,viewGroup,false);
        final AdaptadorHorarios.MyViewHolder holder = new AdaptadorHorarios.MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorHorarios.MyViewHolder myViewHolder, int i) {
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
        private TextView tvDia;
        private TextView tvHoras;
        private TextView tvEstado;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tvDia);
            tvHoras = itemView.findViewById(R.id.tvHoras);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
