package com.app.foodster.Empresa;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.Producto.AdaptadorListaCartas;
import com.app.foodster.Producto.ListaCartas;
import com.app.foodster.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformacionEmpresa extends Fragment implements OnMapReadyCallback{

    GlobalState gs;

    AlertDialog dialogSeguir;

    int ind;
    int idEmpresa;
    ArrayList<DatosEmpresa> datosEmpresa;

    ArrayList<ListaHorarios> datosHorarios;

    private ArrayList<ListaHorarios> listaHorarios;
    private AdaptadorHorarios adaptadorHorarios;
    boolean suscripcion;
    boolean notificacion;

    private ArrayList<ListaEventos> listaEventos;
    private AdaptadorPromocion adaptadorPromocion;

    private AdaptadorListaCartas adaptadorListaCartas;


    MapView mapView;
    private GoogleMap googleMap;

    ImageButton btnRegresar;

    TextView tvTitulo;

    ProgressBar progressBar;

    ScrollView layout_informacion;

    TextView tvTipo;
    TextView tvCategoria;
    TextView tvDescripcion;
    TextView tvDireccion;
    TextView tvTelefonos;
    TextView tvHorarios;

    Button btnSeguir;

    RecyclerView rvHorarios;
    RecyclerView rvPromociones;
    RecyclerView rvCartas;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_informacion_empresa, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRegresar = (ImageButton) v.findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        datosEmpresa = gs.getDatosEmpresa();
        datosHorarios = gs.getDatosHorarios();

        progressBar = v.findViewById(R.id.progressBar);

        layout_informacion = v.findViewById(R.id.layout_informacion);

        tvTitulo = v.findViewById(R.id.tvTitulo);
        tvTipo = v.findViewById(R.id.tvTipo);
        tvCategoria = v.findViewById(R.id.tvCategoria);
        tvDescripcion = v.findViewById(R.id.tvDescProducto);
        tvDireccion = v.findViewById(R.id.tvDireccion);
        tvTelefonos = v.findViewById(R.id.tvTelefonos);

        tvHorarios = v.findViewById(R.id.tvHorarios);

        rvPromociones = v.findViewById(R.id.rvPromociones);

        btnSeguir = v.findViewById(R.id.btnSeguir);
        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(btnSeguir.getText().toString().compareTo("Seguir") == 0){
                    dialogNotificacion();
                }
                else{
                    dialogSeguir();
                }
            }
        });

        rvHorarios = v.findViewById(R.id.rvHorarios);

        rvCartas = v.findViewById(R.id.rvCartas);
        suscripcion = false;
        notificacion=false;

        mostrarInformacion();
        mostrarHorarios();
        consultarSuscripcion();
        consultarPromociones();

        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
        request = Volley.newRequestQueue(getActivity().getApplicationContext());

        idEmpresa = getArguments().getInt("idEmpresa");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void dialogNotificacion(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("")
                .setMessage("¿Activar notificaciones?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        notificacion = true;
                        crearSuscripcion("true");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        notificacion = false;
                        crearSuscripcion("false");
                    }
                });
        dialog.show();
    }

    private void dialogSeguir(){
        AlertDialog.Builder buider = new AlertDialog.Builder(getContext());
        buider.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.dialog_seguir_empresa, null);

        TextView tvEmpresa = view.findViewById(R.id.tvEmpresa);
        tvEmpresa.setText(tvTitulo.getText().toString());

        final ImageView ivNotificacion = view.findViewById(R.id.ivNotificacion);
        final TextView tvNotificacion = view.findViewById(R.id.tvNotificacion);

        if(notificacion){
            ivNotificacion.setImageResource(R.drawable.ic_notifications_off);
            tvNotificacion.setText("Desactivar notificaciones");
        }
        else{
            ivNotificacion.setImageResource(R.drawable.ic_notifications_active);
            tvNotificacion.setText("Activar notificaciones");
        }

        LinearLayout layout_notificacion = view.findViewById(R.id.layout_notificacion);
        layout_notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notificacion){
                    notificacion("false");
                    ivNotificacion.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_notifications_active));
                    tvNotificacion.setText("Activar notificaciones");
                }
                else{
                    notificacion("true");
                    ivNotificacion.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_notifications_off));
                    tvNotificacion.setText("Desactivar notificaciones");
                }
            }
        });

        Button btnSeguir = view.findViewById(R.id.btnSeguir);
        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    eliminarSuscripcion();
                    dialogSeguir.cancel();
            }
        });


        buider.setView(view);
        dialogSeguir = buider.create();
        dialogSeguir.show();
    }

    private void mostrarInformacion() {
        ArrayList<ListaCartas> cartas = new ArrayList<>();

        for(int i=0;i<datosEmpresa.size();i++){
            if(datosEmpresa.get(i).getId() == idEmpresa){
                ind = i;
                tvTitulo.setText(datosEmpresa.get(i).getNombre());

                tvTipo.setText(datosEmpresa.get(i).getTipo());
                tvCategoria.setText(datosEmpresa.get(i).getCategoria());
                tvDescripcion.setText(datosEmpresa.get(i).getDescripcion());
                tvDireccion.setText(datosEmpresa.get(i).getDireccion());
                tvTelefonos.setText(datosEmpresa.get(i).getTelefono()+"-"+datosEmpresa.get(ind).getMovil());

                for(int j=0;j<gs.getDatosCartas().size();j++){
                    if(gs.getDatosCartas().get(j).getIdEmpresa() == idEmpresa){
                        cartas.add(gs.getDatosCartas().get(j));
                    }
                }

                break;
            }
        }

        adaptadorListaCartas = new AdaptadorListaCartas(getContext(), cartas, gs.getDatosProducto(), idEmpresa);
        rvCartas.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvCartas.setAdapter(adaptadorListaCartas);
    }

    private void mostrarHorarios() {
        boolean existeHorarios = false;
        listaHorarios = new ArrayList<>();
        for(int i=0;i<datosHorarios.size();i++){
            if(datosHorarios.get(i).getIdEmpresa() == idEmpresa){
                listaHorarios.add(datosHorarios.get(i));
                existeHorarios = true;
            }
        }

        if(!existeHorarios){
            consultarHorarios();
        }
        else{
            generarHorarios();
        }
    }

    private void generarHorarios(){
        tvHorarios.setVisibility(View.VISIBLE);

        adaptadorHorarios = new AdaptadorHorarios(getContext(), listaHorarios);
        rvHorarios.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvHorarios.setAdapter(adaptadorHorarios);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        String ubicacion[];

        ubicacion = datosEmpresa.get(ind).getUbicacion().split(",");
        LatLng coordenadas = new LatLng(Double.parseDouble(ubicacion[0]), Double.parseDouble(ubicacion[1]));

        googleMap.addMarker(new MarkerOptions().position(coordenadas));

        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
    }

    private void consultarSuscripcion() {

        String url = "http://" + gs.getIp() + "/Empresa/consultar_suscripcion.php?idEmpresa="+idEmpresa+"&idPersona="+gs.getIdPersona();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("suscripcion");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaSuscripcion(jsonObject, datos);
                    }
                    else{
                        if(!suscripcion){
                            btnSeguir.setText("Seguir");
                        }
                        else{
                            Toast.makeText(getContext(), "Error en la acción",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void consultarHorarios() {

        String url = "http://" + gs.getIp() + "/Empresa/consultar_horarios.php?idEmpresa="+idEmpresa;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("horarios");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaHorarios(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void consultarPromociones() {

        String url = "http://" + gs.getIp() + "/Empresa/listar_promociones.php?idEmpresa="+idEmpresa;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("promocion");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaPromocion(jsonObject, datos);
                    }
                    progressBar.setVisibility(View.GONE);
                    layout_informacion.setVisibility(View.VISIBLE);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void crearSuscripcion(String notificacion) {

        String url = "http://" + gs.getIp() + "/Empresa/crear_suscripcion.php?idEmpresa="+idEmpresa+"&idPersona="+gs.getIdPersona()+"&notificacion="+notificacion;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("crear");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaSuscripcion(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void notificacion(String notificacion) {

        String url = "http://" + gs.getIp() + "/Empresa/notificacion.php?idEmpresa="+idEmpresa+"&idPersona="+gs.getIdPersona()+"&notificacion="+notificacion;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("notificacion");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaNotificacion();
                    }
                    else{
                        Toast.makeText(getContext(), "Error en la acción",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void eliminarSuscripcion() {

        String url = "http://" + gs.getIp() + "/Empresa/eliminar_suscripcion.php?idEmpresa="+idEmpresa+"&idPersona="+gs.getIdPersona();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("eliminar");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        suscripcion = false;
                        notificacion = false;
                        btnSeguir.setText("Seguir");
                    }
                    else{
                        Toast.makeText(getContext(), "Error en la acción",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject != null){
                if(jsonObject.optInt("id") != 0) {

                    if(consulta.compareTo("horarios") == 0){
                        consultaHorarios(jsonObject, datos);
                    }
                    if(consulta.compareTo("suscripcion") == 0 || consulta.compareTo("crear") == 0){
                        consultaSuscripcion(jsonObject, datos);
                    }
                    if(consulta.compareTo("promocion") == 0) {
                        consultaPromocion(jsonObject, datos);
                    }

                    if(consulta.compareTo("eliminar") == 0){
                        suscripcion = false;
                        notificacion = false;
                        btnSeguir.setText("Seguir");
                    }
                    if(consulta.compareTo("notificacion") == 0){
                        notificacion = !notificacion;
                    }
                }
                else{
                    if(consulta.compareTo("suscripcion") == 0){
                        if(!suscripcion){
                            btnSeguir.setText("Seguir");
                        }
                        else{
                            Toast.makeText(getContext(), "Error en la acción",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(consulta.compareTo("eliminar") == 0 || consulta.compareTo("notificacion") == 0){
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

        accionConsulta();
    }*/

    /*private void accionConsulta(){

        switch (consulta){
            case "horarios" : consultarSuscripcion(); break;
            case "suscripcion" : consultarPromociones();
                break;
            case "promocion":   progressBar.setVisibility(View.GONE);
                layout_informacion.setVisibility(View.VISIBLE);
                break;
        }
    }*/

    private void consultaHorarios(JSONObject jsonObject, JSONArray datos) throws JSONException {
        listaHorarios = new ArrayList<>();
        for(int i=0; i<datos.length();i++) {
            jsonObject = datos.getJSONObject(i);

            String dia = (jsonObject.optString("dia"));
            String apertura = (jsonObject.optString("hora_apertura"));
            String cierre = (jsonObject.optString("hora_cierre"));
            int estado = (jsonObject.optInt("estado"));

            listaHorarios.add(new ListaHorarios(idEmpresa, dia, apertura, cierre, estado));
        }
        gs.addDatosHorarios(listaHorarios);

        generarHorarios();
    }

    private void consultaSuscripcion(JSONObject jsonObject, JSONArray datos) throws JSONException {
        jsonObject = datos.getJSONObject(0);

        int not = jsonObject.optInt("notificacion");
        if(not == 1){
            notificacion = true;
        }
        else{
            notificacion = false;
        }
        suscripcion = true;
        btnSeguir.setText("No seguir");
    }

    private void consultaNotificacion(){
        notificacion = !notificacion;
    }

    private void consultaPromocion(JSONObject jsonObject, JSONArray datos) throws JSONException {

        listaEventos = new ArrayList<>();

        for (int i = 0; i < datos.length(); i++) {

            jsonObject = datos.getJSONObject(i);

            listaEventos.add(new ListaEventos(jsonObject.optInt("id"),
                    jsonObject.optString("imagen"),
                    jsonObject.optString("empresa"),
                    jsonObject.optString("nombre"),
                    jsonObject.optString("fecha")));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        adaptadorPromocion = new AdaptadorPromocion(getContext(), listaEventos);
        rvPromociones.setLayoutManager(layoutManager);
        rvPromociones.setAdapter(adaptadorPromocion);
    }

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error de consulta "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }*/

    private void detectarError(VolleyError error){
        if (error instanceof AuthFailureError){
            Log.e("VOLLEY", "Se ha producido un fallo con las credenciales. " + error.getMessage() );
        } else if (error instanceof NetworkError) {
            Log.e("VOLLEY", "Se ha producido un fallo en la red. "+ error.getMessage());
        } else if (error instanceof NoConnectionError) {
            Log.e("VOLLEY", "Se ha producido un fallo en la conexión. "+ error.getMessage());
        } else if (error instanceof TimeoutError) {
            Log.e("VOLLEY", "Fallo en tiempo de espera. "+ error.getMessage());
        }
    }
}
