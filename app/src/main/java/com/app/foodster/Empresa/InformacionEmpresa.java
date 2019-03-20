package com.app.foodster.Empresa;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.Producto.AdaptadorListaCartas;
import com.app.foodster.Producto.ListaCartas;
import com.app.foodster.Producto.ListaProductos;
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

public class InformacionEmpresa extends AppCompatActivity implements OnMapReadyCallback, Response.Listener<JSONObject>, Response.ErrorListener{


    GlobalState gs;
    String consulta;

    int ind;
    int idEmpresa;
    ArrayList<DatosEmpresa> datosEmpresa;

    private ArrayList<ListaHorarios> listaHorarios;
    private AdaptadorListaHorarios adaptadorListaHorarios;
    boolean suscripcion;

    private ArrayList<ListaCartas> listaCartas;
    private AdaptadorListaCartas adaptadorListaCartas;

    private ArrayList<ListaProductos> listaProductos;

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
    Button btnFotos;

    RecyclerView rvHorarios;
    RecyclerView rvCartas;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_empresa);

        gs = (GlobalState)getApplication();

        request = Volley.newRequestQueue(getApplicationContext());

        Bundle datos = this.getIntent().getExtras();
        idEmpresa = datos.getInt("id");
        ind = datos.getInt("ind");

        btnRegresar = (ImageButton) findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        datosEmpresa = gs.getDatosEmpresa();

        progressBar = findViewById(R.id.progressBar);

        layout_informacion = findViewById(R.id.layout_informacion);

        tvTitulo = findViewById(R.id.tvTitulo);
        tvTipo = findViewById(R.id.tvTipo);
        tvCategoria = findViewById(R.id.tvCategoria);
        tvDescripcion = findViewById(R.id.tvDescProducto);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvTelefonos = findViewById(R.id.tvTelefonos);

        tvHorarios = findViewById(R.id.tvHorarios);

        btnSeguir = findViewById(R.id.btnSeguir);
        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnSeguir.getText().toString().compareTo("Seguir") == 0){
                    dialogNotificacion();
                }
                else{
                    eliminarSuscripcion();
                }
            }
        });

        btnFotos = findViewById(R.id.btnFotos);
        btnFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        rvHorarios = findViewById(R.id.rvHorarios);

        rvCartas = findViewById(R.id.rvCartas);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        mostrarInformacion();

        suscripcion = false;
        consultarSuscripcion();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng coordenadas = null;

        String ubicacion[];

        for (int i = 0; i < datosEmpresa.size(); i++) {
            if(idEmpresa == datosEmpresa.get(i).getId()){
                ubicacion = datosEmpresa.get(i).getUbicacion().split(",");
                coordenadas = new LatLng(Double.parseDouble(ubicacion[0]), Double.parseDouble(ubicacion[1]));

                googleMap.addMarker(new MarkerOptions().position(coordenadas));
            }
        }
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
    }

    private void dialogNotificacion(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("")
                .setMessage("¿Activar notificaciones?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        crearSuscripcion("true");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        crearSuscripcion("false");
                    }
                });
        dialog.show();
    }

    private void mostrarInformacion() {

        tvTitulo.setText(datosEmpresa.get(ind).getNombre());

        tvTipo.setText(datosEmpresa.get(ind).getTipo());
        tvCategoria.setText(datosEmpresa.get(ind).getCategoria());
        tvDescripcion.setText(datosEmpresa.get(ind).getDescripcion());
        tvDireccion.setText(datosEmpresa.get(ind).getDireccion());
        tvTelefonos.setText(datosEmpresa.get(ind).getTelefono()+"-"+datosEmpresa.get(ind).getMovil());
    }

    private void consultarSuscripcion() {

        consulta = "suscripcion";
        String url = "http://" + gs.getIp() + "/Empresa/consultar_suscripcion.php?idEmpresa="+idEmpresa+"&idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void crearSuscripcion(String notificacion) {

        consulta = "crear";
        String url = "http://" + gs.getIp() + "/Empresa/crear_suscripcion.php?idEmpresa="+idEmpresa+"&idPersona="+gs.getIdPersona()+"&notificacion="+notificacion;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void eliminarSuscripcion() {

        consulta = "eliminar";
        String url = "http://" + gs.getIp() + "/Empresa/eliminar_suscripcion.php?idEmpresa="+idEmpresa+"&idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void consultarHorarios() {

        consulta = "horarios";
        String url = "http://" + gs.getIp() + "/Empresa/consultar_horarios.php?idEmpresa="+idEmpresa;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void consultarCartas(){

        consulta = "carta";
        String url = "http://" + gs.getIp() + "/Empresa/consultar_cartas.php?idEmpresa="+idEmpresa;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }



    @Override
    public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject != null){
                if(jsonObject.optInt("id") != 0) {

                    if(consulta.compareTo("horarios") == 0){

                        listaHorarios = new ArrayList<>();
                        for(int i=0; i<datos.length();i++) {
                            jsonObject = datos.getJSONObject(i);

                            String dia = (jsonObject.optString("dia"));
                            String apertura = (jsonObject.optString("hora_apertura"));
                            String cierre = (jsonObject.optString("hora_cierre"));
                            int estado = (jsonObject.optInt("estado"));

                            listaHorarios.add(new ListaHorarios(dia, apertura, cierre, estado));
                        }

                        tvHorarios.setVisibility(View.VISIBLE);

                        adaptadorListaHorarios = new AdaptadorListaHorarios(this, listaHorarios);
                        rvHorarios.setLayoutManager(new GridLayoutManager(this, 1));
                        rvHorarios.setAdapter(adaptadorListaHorarios);
                    }

                    if(consulta.compareTo("carta") == 0) {

                        listaCartas = new ArrayList<>();
                        listaProductos = new ArrayList<>();
                        boolean existeCarta;
                        for (int i = 0; i < datos.length(); i++) {
                            existeCarta = false;
                            jsonObject = datos.getJSONObject(i);

                            int id = jsonObject.optInt("id");
                            String carta = jsonObject.optString("carta");

                            int idProducto = jsonObject.optInt("idProducto");
                            String producto = jsonObject.optString("producto");
                            String descripcion = jsonObject.optString("descProducto");
                            int precio = jsonObject.optInt("precio");
                            int promocion = jsonObject.optInt("id_promocion");

                            if(i == 0){
                                listaCartas.add(new ListaCartas(id, carta));
                            }
                            else{
                                for(int j=0;j<listaCartas.size();j++){
                                    if(listaCartas.get(j).getId() == id){
                                        existeCarta = true;
                                    }
                                }
                                if(!existeCarta){
                                    listaCartas.add(new ListaCartas(id, carta));
                                }
                            }

                            if(promocion != 0){
                                String descPromocion = (jsonObject.optString("descripcion"));
                                int descuento = (jsonObject.optInt("descuento"));
                                String fecha = jsonObject.optString("fecha_inicio") + " - " + jsonObject.optString("fecha_fin");
                                listaProductos.add(new ListaProductos(idEmpresa, id, carta, idProducto, producto, descripcion, precio,
                                                                        promocion, descPromocion, descuento, fecha));
                            }
                            else{
                                listaProductos.add(new ListaProductos(idEmpresa, id, carta, idProducto, producto, descripcion, precio, promocion));

                            }
                            gs.setDatosProducto(listaProductos);
                        }
                        adaptadorListaCartas = new AdaptadorListaCartas(this, listaCartas, listaProductos, idEmpresa);
                        rvCartas.setLayoutManager(new GridLayoutManager(this, 1));
                        rvCartas.setAdapter(adaptadorListaCartas);
                    }
                    if(consulta.compareTo("suscripcion") == 0 || consulta.compareTo("crear") == 0){
                        suscripcion = true;
                        btnSeguir.setText("No seguir");
                    }
                    if(consulta.compareTo("eliminar") == 0){
                        suscripcion = false;
                        btnSeguir.setText("Seguir");
                    }
                }
                else{
                    if(consulta.compareTo("suscripcion") == 0){
                        if(!suscripcion){
                            btnSeguir.setText("Seguir");
                        }
                        else{
                            Toast.makeText(this, "Error en la acción",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(consulta.compareTo("eliminar") == 0 || consulta.compareTo("eliminar") == 0){
                        Toast.makeText(this, "Error en la acción",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(consulta.compareTo("empresa") == 0){
            consultarHorarios();
        }
        else{
            if(consulta.compareTo("suscripcion") == 0){
                consultarCartas();
            }
            else{
                if(consulta.compareTo("carta") == 0){
                    consultarHorarios();
                }
                else{
                    if(consulta.compareTo("horarios") == 0){
                        progressBar.setVisibility(View.GONE);
                        layout_informacion.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error de consulta "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }
}
