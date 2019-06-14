package com.app.foodster.Persona;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.foodster.GlobalState;
import com.app.foodster.R;

import java.util.ArrayList;

public class AdaptadorTarjetas extends RecyclerView.Adapter<AdaptadorTarjetas.MyViewHolder> {Context context;

    GlobalState gs;
    ArrayList<ListaTarjetas> tarjeta;
    MisTarjetas fragment;

    public AdaptadorTarjetas(MisTarjetas fragment, Context context, ArrayList<ListaTarjetas> tarjeta) {
        this.fragment = fragment;
        this.context = context;
        this.tarjeta = tarjeta;

        gs = (GlobalState)context.getApplicationContext();
    }

    public void actualizar(ArrayList<ListaTarjetas> lista){
        tarjeta = new ArrayList<>();
        tarjeta.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdaptadorTarjetas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_tarjeta,viewGroup,false);
        final AdaptadorTarjetas.MyViewHolder holder = new AdaptadorTarjetas.MyViewHolder(v);

        holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = tarjeta.get(holder.getAdapterPosition()).getId();

                dialog_borrar(id, holder.getAdapterPosition());
            }
        });

        return holder;
    }

    private void dialog_borrar(final int id, final int posicion){
        android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);
        dialogo1.setTitle("");
        dialogo1.setMessage("Desea borrar esta tarjeta?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(R.string.text_confirmar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                fragment.borrarTarjeta(id, posicion);
            }
        });
        dialogo1.setNegativeButton(R.string.text_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.cancel();
            }
        });
        dialogo1.show();
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorTarjetas.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvNombre.setText(tarjeta.get(i).getNombre());
        myViewHolder.tvTipo.setText(tarjeta.get(i).getTipo());
        myViewHolder.tvNumero.setText(tarjeta.get(i).getNumero());
        myViewHolder.tvFecha.setText(tarjeta.get(i).getFecha());

    }

    public int getItemCount() {
        return tarjeta.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNombre;
        private TextView tvTipo;
        private TextView tvNumero;
        private TextView tvFecha;
        private ImageButton btnBorrar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.etNombre);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvNumero = itemView.findViewById(R.id.tvNumero);
            tvFecha = itemView.findViewById(R.id.etMes);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }
    }
}