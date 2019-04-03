package com.app.foodster.Producto;

import android.content.Context;
import android.graphics.Paint;
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

public class AdaptadorListaProductos extends RecyclerView.Adapter<AdaptadorListaProductos.MyViewHolder> {
    Context context;
    ArrayList<ListaProductos> producto;
    GlobalState gs;

    public AdaptadorListaProductos(Context context, ArrayList<ListaProductos> producto) {
        this.context = context;
        this.producto = producto;

        gs = (GlobalState)context.getApplicationContext();
        verificarProductos();

    }

    private void verificarProductos(){
        ArrayList<ListaProductos> lista = gs.getDatosProducto();

        boolean existe = false;
        if(lista != null){
            for(int i=0;i<lista.size();i++){
                for(int j=0;j<producto.size();j++){
                    if(lista.get(i).getId() == producto.get(j).getId()){
                        existe = true;
                        break;
                    }
                }
            }
        }

        if(!existe){
            gs.addDatosProducto(producto);
        }
    }

    @NonNull
    @Override
    public AdaptadorListaProductos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_producto,viewGroup,false);
        final AdaptadorListaProductos.MyViewHolder holder = new AdaptadorListaProductos.MyViewHolder(v);

        holder.item_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idEmpresa = producto.get(holder.getAdapterPosition()).getIdEmpresa();
                int idProducto = producto.get(holder.getAdapterPosition()).getId();

                /*Intent intent = new Intent(context, InformacionProducto.class);

                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id", id);
                context.startActivity(intent);*/

                Bundle args = new Bundle();
                args.putInt("idEmpresa", idEmpresa);
                args.putInt("idProducto", idProducto);

                GlobalState gs = (GlobalState)context.getApplicationContext();

                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                fragInformacionProducto fragment = new fragInformacionProducto();
                gs.setFragment(fragment);
                gs.setFragmentActual("InformacionProducto");
                fragment.setArguments(args);
                FragmentManager fm = activity.getSupportFragmentManager();

                activity.getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container, fragment, fragment.getClass().toString()).addToBackStack(null) // add and tag the new fragment
                            .commit();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaProductos.MyViewHolder myViewHolder, int i) {

        myViewHolder.tvNombre.setText(producto.get(i).getNombre());
        myViewHolder.tvPrecio.setText("$"+ producto.get(i).getPrecio());
        if(producto.get(i).getPromocion() != 0){
            myViewHolder.tvPrecio.setPaintFlags( myViewHolder.tvPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            myViewHolder.tvPromocion.setText("$"+producto.get(i).getPromocion());
            myViewHolder.tvPromocion.setVisibility(View.VISIBLE);
        }
        String url = producto.get(i).getFoto1();
        if(url != null){
            Picasso.with(context)
                    .load("http://foodster.com.co/back-end/"+url)
                    .error(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(myViewHolder.ivFoto);
            producto.get(i).setbFoto1(myViewHolder.ivFoto.getDrawingCache());

        }
    }

    @Override
    public int getItemCount() {
        return producto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout item_producto;
        private ImageView ivFoto;
        private TextView tvNombre;
        private TextView tvPrecio;
        private TextView tvPromocion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_producto = itemView.findViewById(R.id.item_producto);
            ivFoto= itemView.findViewById(R.id.ivFoto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvPromocion = itemView.findViewById(R.id.tvPromocion);
        }
    }
}
