package com.app.foodster.Persona;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.Empresa.fragInformacionEmpresa;
import com.app.foodster.GlobalState;
import com.app.foodster.Producto.fragInformacionProducto;
import com.app.foodster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductosFavoritos extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{

    GlobalState gs;

    String consulta;

    private ProductosFavoritos.AdaptadorListaProductosFavoritos adaptadorListaProductosFavoritos;
    private ArrayList<ListaProductosFavoritos> listaProductosFavoritos;

    ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder auxHolder = null;

    ProgressBar progressBar;
    ProgressDialog progressEliminar;

    TextView tvMensaje;
    RecyclerView rvFavoritos;

    int posicion;
    int total;

    String auxDetalles;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_productos_favoritos, container, false);

        progressBar = v.findViewById(R.id.progressBar);

        progressEliminar = new ProgressDialog(getContext());
        progressEliminar.setMessage("Eliminando...");
        progressEliminar.setCanceledOnTouchOutside(false);

        tvMensaje = v.findViewById(R.id.tvMensaje);

        rvFavoritos = v.findViewById(R.id.rvFavoritos);

        if(!gs.isActualizaProductosFavoritos()){
            if(gs.getDatosProductosFavoritos().size() > 0){
                listaProductosFavoritos = gs.getDatosProductosFavoritos();
                generarFavoritos();
            }
            else{
                rvFavoritos.setVisibility(View.GONE);
                tvMensaje.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState)getActivity().getApplicationContext();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generarFavoritos(){

        adaptadorListaProductosFavoritos = new ProductosFavoritos.AdaptadorListaProductosFavoritos(getContext(), listaProductosFavoritos);
        rvFavoritos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvFavoritos.setAdapter(adaptadorListaProductosFavoritos);


    }

    public void listarFavoritos(){
        consulta = "favoritos";
        String url = "http://" + gs.getIp() + "/Persona/listar_productos_favoritos.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void actualizarDetalles(int idFavorito, String detalles){
        consulta = "actualizar_detalles";
        String url = "http://" + gs.getIp() + "/Persona/actualizar_detalles.php?idFavorito="+idFavorito+"&detalles="+detalles;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void eliminarFavorito(int idFavorito){
        consulta = "eliminar_favorito";
        String url = "http://" + gs.getIp() + "/Persona/eliminar_producto_favorito.php?idFavorito="+idFavorito;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("favoritos") == 0){
                    listaProductosFavoritos = new ArrayList<>();
                    total = 0;
                    for (int i = 0; i < datos.length(); i++) {
                        jsonObject = datos.getJSONObject(i);

                        String detalles = "Detalles: " + jsonObject.optString("detalles");

                        int promocion = jsonObject.optInt("id_promocion");
                        int precio = jsonObject.optInt("precio");
                        int descuento = jsonObject.optInt("descuento");
                        if(promocion != 0){
                            promocion = (int)(precio - ( precio * ((double)descuento/100)));
                        }

                        listaProductosFavoritos.add(new ListaProductosFavoritos(jsonObject.optInt("idFavorito"),
                                jsonObject.optInt("idProducto"),
                                jsonObject.optInt("idEmpresa"),
                                jsonObject.optString("empresa"),
                                jsonObject.optString("nombre"),
                                precio,
                                promocion,
                                descuento,
                                detalles));
                    }
                    generarFavoritos();

                    gs.setDatosProductosFavoritos(listaProductosFavoritos);
                    gs.setActualizaProductosFavoritos(false);

                    progressBar.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.GONE);
                    rvFavoritos.setVisibility(View.VISIBLE);
                }
                if(consulta.compareTo("actualizar_detalles") == 0){
                    auxHolder.tvDetalles.setText("Detalles: " + auxDetalles);
                    listaProductosFavoritos.get(posicion).setDetalles("Detalles: " + auxDetalles);
                    gs.setDatosProductosFavoritos(listaProductosFavoritos);

                    adaptadorListaProductosFavoritos.favorito = listaProductosFavoritos;
                }
                if(consulta.compareTo("eliminar_favorito") == 0){
                    progressEliminar.cancel();
                    listaProductosFavoritos.remove(posicion);
                    gs.setDatosProductosFavoritos(listaProductosFavoritos);

                    if(listaProductosFavoritos.size() > 0){
                        generarFavoritos();
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        rvFavoritos.setVisibility(View.GONE);
                        tvMensaje.setVisibility(View.VISIBLE);
                    }
                }
            }
            else{
                if(consulta.compareTo("favoritos") == 0){
                    progressBar.setVisibility(View.GONE);
                    rvFavoritos.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.VISIBLE);
                }

                if(consulta.compareTo("eliminar_favorito") == 0){
                    Toast.makeText(getContext(), "No se pudo eliminar de favoritos", Toast.LENGTH_SHORT).show();
                }
                if(consulta.compareTo("actualizar_detalles") == 0){
                    Toast.makeText(getContext(), "No se pudo actualizar los detalles", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        progressEliminar.cancel();
        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }


    public class AdaptadorListaProductosFavoritos extends RecyclerView.Adapter<ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder>{

        Context context;
        ArrayList<ListaProductosFavoritos> favorito;

        public AdaptadorListaProductosFavoritos(Context context, ArrayList<ListaProductosFavoritos> favorito) {
            this.context = context;
            this.favorito = favorito;
        }

        @NonNull
        @Override
        public ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_producto_favorito,viewGroup,false);
            final ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder holder = new ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder(v);

            holder.tvEmpresa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = favorito.get(holder.getAdapterPosition()).getIdEmpresa();

                    fragInformacionEmpresa fragment = new fragInformacionEmpresa();
                    gs.setFragment(fragment);
                    gs.setFragmentActual("InformacionEmpresa");
                    verInformacion(id, 0, fragment, v);
                }
            });

            holder.tvNombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int idEmpresa = favorito.get(holder.getAdapterPosition()).getIdEmpresa();
                    int idProducto = favorito.get(holder.getAdapterPosition()).getIdProducto();

                    fragInformacionProducto fragment = new fragInformacionProducto();
                    gs.setFragment(fragment);
                    gs.setFragmentActual("InformacionProducto");
                    verInformacion(idEmpresa, idProducto, fragment, v);
                }
            });

            holder.btnDetalles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_detalles(favorito.get(holder.getAdapterPosition()).getDetalles(), holder);
                }
            });

            holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_borrar(holder);
                }
            });
            return holder;
        }

        private void verInformacion(int idEmpresa, int idProducto, android.support.v4.app.Fragment fragment, View v){
            Bundle args = new Bundle();
            args.putInt("idEmpresa", idEmpresa);
            if(idProducto != 0){
                args.putInt("idProducto", idProducto);
            }

            GlobalState gs = (GlobalState)context.getApplicationContext();

            AppCompatActivity activity = (AppCompatActivity) v.getContext();


            gs.setFragmentEmpresas(fragment);
            fragment.setArguments(args);
            FragmentManager fm = activity.getSupportFragmentManager();
            android.support.v4.app.Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
            if(!fragment.getClass().toString().equals(currentFragment.getTag()))
            {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, fragment, fragment.getClass().toString()) // add and tag the new fragment
                        .commit();
            }
        }

        private void dialog_detalles(final String detalles, final ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder holder){
            android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);

            dialogo1.setTitle("Detalles de "+ holder.tvNombre.getText().toString());
            dialogo1.setMessage("Describa los detalles de este producto para realizar pedidos");

            final EditText etDetalles = new EditText(dialogo1.getContext());
            String detalle[] = null;

            if(detalles.compareTo("Detalles: ") != 0){
                detalle = detalles.split("etalles: ");
                etDetalles.setText(detalle[1]);
            }

            dialogo1.setView(etDetalles);

            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    posicion = holder.getAdapterPosition();
                    auxDetalles = etDetalles.getText().toString().trim();
                    auxHolder = holder;
                    actualizarDetalles(favorito.get(holder.getAdapterPosition()).getIdFavorito(), auxDetalles);
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();
        }

        private void dialog_borrar(final ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder holder){
            android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);
            dialogo1.setTitle("");
            dialogo1.setMessage("¿Eliminar el producto del carrito?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    posicion = holder.getAdapterPosition();
                    progressEliminar.show();
                    eliminarFavorito(favorito.get(holder.getAdapterPosition()).getIdFavorito());
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();
        }

        @Override
        public void onBindViewHolder(@NonNull ProductosFavoritos.AdaptadorListaProductosFavoritos.MyViewHolder myViewHolder, int i) {
            myViewHolder.tvEmpresa.setText(favorito.get(i).getEmpresa());
            myViewHolder.tvNombre.setText(favorito.get(i).getNombre());

            int precio = favorito.get(i).getPrecio();
            myViewHolder.tvPrecio.setText("$"+ precio);
            if(favorito.get(i).getPromocion() != 0){
                myViewHolder.tvPrecio.setPaintFlags( myViewHolder.tvPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                myViewHolder.tvPromocion.setText("$"+favorito.get(i).getPromocion());
                myViewHolder.tvPromocion.setVisibility(View.VISIBLE);
            }

            myViewHolder.tvDetalles.setText(favorito.get(i).getDetalles());
        }

        @Override
        public int getItemCount() {
            return favorito.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            private TextView tvEmpresa;
            private TextView tvNombre;
            private TextView tvPrecio;
            private TextView tvPromocion;
            private TextView tvDetalles;
            private ImageButton btnDetalles;
            private ImageButton btnBorrar;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
                tvNombre = itemView.findViewById(R.id.tvNombre);
                tvPrecio = itemView.findViewById(R.id.tvPrecio);
                tvPromocion = itemView.findViewById(R.id.tvPromocion);
                tvDetalles = itemView.findViewById(R.id.tvDescProducto);
                btnDetalles = itemView.findViewById(R.id.btnDetalles);
                btnBorrar = itemView.findViewById(R.id.btnBorrar);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(gs.isActualizaProductosFavoritos()){
            listarFavoritos();
        }
    }
}
