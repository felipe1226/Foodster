package com.app.foodster.Persona;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Perfil extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{

    GlobalState gs;

    LinearLayout layout_datos;
    TextView tvUsuario;
    TextView tvNombre;
    TextView tvTelefono;
    TextView tvDireccion;
    TextView tvEmail;
    TextView tvLocalidad;

    LinearLayout layout_campos;
    EditText etNombre;
    EditText etTelefono;
    EditText etEmail;

    Button btnActualizar;
    Button btnNuevaDireccion;

    RecyclerView rvDirecciones;

    ProgressDialog progressEliminar;
    AlertDialog dialogDireccion;

    int posicion;
    String consulta;

    private ArrayList<ListaDireccion> listaDireccion;
    private AdaptadorListaDireccion adaptadorListaDireccion;

    Perfil.AdaptadorListaDireccion.MyViewHolder auxHolder = null;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_perfil, container, false);

        layout_datos = v.findViewById(R.id.layout_datos);
        tvUsuario = v.findViewById(R.id.tvUsuario);
        tvNombre = v.findViewById(R.id.tvNombre);
        tvTelefono = v.findViewById(R.id.tvTelefono);
        tvDireccion = v.findViewById(R.id.tvDireccion);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvLocalidad = v.findViewById(R.id.tvLocalidad);

        layout_campos = v.findViewById(R.id.layout_campos);
        etNombre = v.findViewById(R.id.etNombre);
        etTelefono = v.findViewById(R.id.etTelefono);
        etEmail = v.findViewById(R.id.etEmail);

        rvDirecciones = v.findViewById(R.id.rvDirecciones);

        btnActualizar = v.findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnNuevaDireccion = v.findViewById(R.id.btnNuevaDireccion);
        btnNuevaDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDireccion();
            }
        });

        obtenerDatos();

        if(!gs.isActualizaDirecciones()){
            if(gs.getDatosDireccion().size() > 0){
                listaDireccion = gs.getDatosDireccion();
                generarDirecciones();
            }
        }
        else{
            listarDirecciones();
        }

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState)getActivity().getApplication();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());

    }

    private void obtenerDatos(){
        tvUsuario.setText(gs.getUsuario());
        tvNombre.setText(gs.getNombre());
        etNombre.setText(gs.getNombre());
        tvTelefono.setText(gs.getTelefono());
        etTelefono.setText(gs.getTelefono());
        tvDireccion.setText("Direccion");
        tvEmail.setText(gs.getEmail());
        etEmail.setText(gs.getEmail());

        int ciudad = gs.getidCiudad();
        tvLocalidad.setText(""+ciudad);
    }

    private void generarDirecciones(){
        adaptadorListaDireccion = new Perfil.AdaptadorListaDireccion(getContext(), listaDireccion);
        rvDirecciones.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvDirecciones.setAdapter(adaptadorListaDireccion);
    }

    private void dialogDireccion(){
        AlertDialog.Builder buider = new AlertDialog.Builder(getContext());
        buider.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_direccion, null);

        EditText etTitulo = view.findViewById(R.id.etTitulo);
        EditText etDireccion = view.findViewById(R.id.etDireccion);

        final LinearLayout layout_mapa = view.findViewById(R.id.layout_mapa);

        Button btnUbicacion = view.findViewById(R.id.btnUbicacion);
        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_mapa.setVisibility(View.VISIBLE);
            }
        });

        MapView mapView = view.findViewById(R.id.mapView);
        MapsInitializer.initialize(getContext());

        final Button btnCancelar = view.findViewById(R.id.btnCancelar);
        final Button btnConfirmar = view.findViewById(R.id.btnConfirmar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDireccion.cancel();
            }
        });


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Confirmar", Toast.LENGTH_SHORT).show();

            }
        });


        buider.setView(view);
        dialogDireccion = buider.create();

        mapView.onCreate(dialogDireccion.onSaveInstanceState());
        mapView.onResume();// needed to get the map to display immediately
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                Toast.makeText(getContext(), "Mapa", Toast.LENGTH_SHORT).show();
            }
        });

        dialogDireccion.show();
    }

    private void listarDirecciones() {

        consulta = "direccion";
        String url = "http://" + gs.getIp() + "/Persona/listar_direcciones.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void actualizarPredeterminada(int idDireccion) {

        consulta = "predeterminada";
        String url = "http://" + gs.getIp() + "/Persona/establecer_predeterminada.php?idPersona="+gs.getIdPersona()+"&idDireccion="+idDireccion;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void eliminarDireccion(int idDireccion){
        consulta = "eliminar_direccion";
        String url = "http://" + gs.getIp() + "/Persona/eliminar_direccion.php?idDireccion="+idDireccion;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        boolean actualizaPredeterminada = false;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject != null){
                if(jsonObject.optInt("id") != 0) {

                    if(consulta.compareTo("direccion") == 0){

                        listaDireccion = new ArrayList<>();
                        for(int i=0; i<datos.length();i++) {
                            jsonObject = datos.getJSONObject(i);

                            int id = jsonObject.optInt("id");
                            String titulo = jsonObject.optString("titulo");
                            String direccion = jsonObject.optString("direccion");
                            String ubicacion = jsonObject.optString("ubicacion");
                            int predeterminada = jsonObject.optInt("predeterminada");

                            listaDireccion.add(new ListaDireccion(id, titulo, direccion, ubicacion, predeterminada));
                        }

                        generarDirecciones();

                        gs.setDatosDireccion(listaDireccion);
                        gs.setActualizaDirecciones(false);
                    }

                    if(consulta.compareTo("predeterminada") == 0){
                        actualizaPredeterminada = true;

                        for(int k=0;k<listaDireccion.size();k++){
                            if(posicion == k){
                                listaDireccion.get(k).setPredeterminada(1);
                            }
                            else{
                                listaDireccion.get(k).setPredeterminada(0);
                            }
                        }

                        gs.setDatosDireccion(listaDireccion);

                        adaptadorListaDireccion.direccion = listaDireccion;
                    }


                    if(consulta.compareTo("eliminar_direccion") == 0){
                        progressEliminar.cancel();
                        listaDireccion.remove(posicion);
                        gs.setDatosDireccion(listaDireccion);

                        if(listaDireccion.size() > 0){
                            generarDirecciones();
                        }
                    }
                }
                else{
                    if(consulta.compareTo("eliminar") == 0 || consulta.compareTo("eliminar") == 0){
                        Toast.makeText(getContext(), "Error en la acción",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                Toast.makeText(getContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(consulta.compareTo("predeterminada") == 0){
            if(actualizaPredeterminada){
                generarDirecciones();
            }
        }
    }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error de consulta "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }

    public class AdaptadorListaDireccion extends RecyclerView.Adapter<AdaptadorListaDireccion.MyViewHolder> implements OnMapReadyCallback {
        Context context;
        ArrayList<ListaDireccion> direccion;
        int ind;
        int cont = 0;
        GoogleMap googleMap;

        public AdaptadorListaDireccion(Context context, ArrayList<ListaDireccion> direccion) {
            this.context = context;
            this.direccion = direccion;
        }


        @NonNull
        @Override
        public AdaptadorListaDireccion.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_direccion,viewGroup,false);
            final AdaptadorListaDireccion.MyViewHolder holder = new AdaptadorListaDireccion.MyViewHolder(v);

            holder.btnPredeterminada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    posicion = holder.getAdapterPosition();
                    actualizarPredeterminada(direccion.get(posicion).getId());
                }
            });


            holder.btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Editar", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(direccion.get(holder.getAdapterPosition()).getPredeterminada() == 0){
                        dialog_borrar(holder);
                    }
                    else{
                        Toast.makeText(context, "No se puede eliminar una direccion predeterminada", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            return holder;
        }

        private void dialog_borrar(final Perfil.AdaptadorListaDireccion.MyViewHolder holder){
            android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);
            dialogo1.setTitle("");
            dialogo1.setMessage("¿Eliminar el producto del carrito?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    posicion = holder.getAdapterPosition();
                    progressEliminar.show();
                    eliminarDireccion(direccion.get(holder.getAdapterPosition()).getId());
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
        public void onMapReady(GoogleMap googleMap) {
            //MapsInitializer.initialize(context);
            this.googleMap = googleMap;

            /*for(int i = 0;i<direccion.size();i++){
                    if(i == cont){
                        LatLng coordenadas = null;

                        String ubicacion[] = direccion.get(i).getUbicacion().split(",");
                        coordenadas = new LatLng(Double.parseDouble(ubicacion[0]), Double.parseDouble(ubicacion[1]));
                        this.googleMap.addMarker(new MarkerOptions().position(coordenadas));

                        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
                        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
                    }
                }
                cont++;*/
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorListaDireccion.MyViewHolder myViewHolder, int i)  {
            myViewHolder.tvTitulo.setText(direccion.get(i).getTitulo());
            myViewHolder.tvDireccion.setText(direccion.get(i).getDireccion());

            String u = direccion.get(i).getUbicacion();

            if(u.compareTo("") != 0){
                myViewHolder.mapView.setVisibility(View.VISIBLE);
            }
            else{
                myViewHolder.mapView.setVisibility(View.GONE);
            }

            if(direccion.get(i).getPredeterminada() == 1){
                myViewHolder.btnPredeterminada.setText("predeterminada");
                myViewHolder.btnPredeterminada.setEnabled(false);
            }
            else{
                myViewHolder.btnPredeterminada.setText("establecer predeterminada");
            }
        }


        public int getItemCount() {
            return direccion.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder  {

            private TextView tvTitulo;
            private TextView tvDireccion;
            private MapView mapView;
            private ImageButton btnEditar;
            private ImageButton btnBorrar;
            private Button btnPredeterminada;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitulo = itemView.findViewById(R.id.tvTitulo);
                tvDireccion = itemView.findViewById(R.id.tvDireccion);
                mapView = itemView.findViewById(R.id.mapView);
                btnEditar = itemView.findViewById(R.id.btnEditar);
                btnBorrar = itemView.findViewById(R.id.btnBorrar);
                btnPredeterminada = itemView.findViewById(R.id.btnPredeterminada);


                /*if (mapView != null)
                {
                    mapView.onCreate(null);
                    mapView.onResume();
                    mapView.getMapAsync(AdaptadorListaDireccion.this);
                }*/
            }
        }
    }
}
