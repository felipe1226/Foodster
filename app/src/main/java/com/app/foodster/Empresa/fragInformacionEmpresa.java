package com.app.foodster.Empresa;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.Producto.AdaptadorListaCartas;
import com.app.foodster.Producto.AdaptadorListaProductos;
import com.app.foodster.Producto.ListaCartas;
import com.app.foodster.Producto.ListaProductos;
import com.app.foodster.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragInformacionEmpresa extends Fragment implements OnMapReadyCallback, Response.Listener<JSONObject>, Response.ErrorListener{

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_frag_informacion_empresa, container, false);

        btnRegresar = (ImageButton) v.findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        datosEmpresa = gs.getDatosEmpresa();

        progressBar = v.findViewById(R.id.progressBar);

        layout_informacion = v.findViewById(R.id.layout_informacion);

        tvTitulo = v.findViewById(R.id.tvTitulo);
        tvTipo = v.findViewById(R.id.tvTipo);
        tvCategoria = v.findViewById(R.id.tvCategoria);
        tvDescripcion = v.findViewById(R.id.tvDescProducto);
        tvDireccion = v.findViewById(R.id.tvDireccion);
        tvTelefonos = v.findViewById(R.id.tvTelefonos);

        tvHorarios = v.findViewById(R.id.tvHorarios);

        btnSeguir = v.findViewById(R.id.btnSeguir);
        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
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

        btnFotos = v.findViewById(R.id.btnFotos);
        btnFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        rvHorarios = v.findViewById(R.id.rvHorarios);

        rvCartas = v.findViewById(R.id.rvCartas);



        mostrarInformacion();

        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        suscripcion = false;
        consultarSuscripcion();
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
        for(int i=0;i<datosEmpresa.size();i++){
            if(datosEmpresa.get(i).getId() == idEmpresa){
                ind = i;
                tvTitulo.setText(datosEmpresa.get(i).getNombre());

                tvTipo.setText(datosEmpresa.get(i).getTipo());
                tvCategoria.setText(datosEmpresa.get(i).getCategoria());
                tvDescripcion.setText(datosEmpresa.get(i).getDescripcion());
                tvDireccion.setText(datosEmpresa.get(i).getDireccion());
                tvTelefonos.setText(datosEmpresa.get(i).getTelefono()+"-"+datosEmpresa.get(ind).getMovil());
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng coordenadas = null;

        String ubicacion[];

        ubicacion = datosEmpresa.get(ind).getUbicacion().split(",");
        coordenadas = new LatLng(Double.parseDouble(ubicacion[0]), Double.parseDouble(ubicacion[1]));

        googleMap.addMarker(new MarkerOptions().position(coordenadas));

        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
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

                        adaptadorListaHorarios = new AdaptadorListaHorarios(getContext(), listaHorarios);
                        rvHorarios.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        rvHorarios.setAdapter(adaptadorListaHorarios);
                    }
                    if(consulta.compareTo("carta") == 0) {

                        listaCartas = new ArrayList<>();
                        listaProductos = new ArrayList<>();
                        boolean existeCarta;
                        int idAnterior = 0;
                        int cont = 0;
                        for (int i = 0; i < datos.length(); i++) {
                            existeCarta = false;
                            jsonObject = datos.getJSONObject(i);

                            int idProducto = jsonObject.optInt("idProducto");
                            String imagen = jsonObject.optString("imagen");
                            if(idProducto != idAnterior){
                                idAnterior = idProducto;
                                cont = 0;

                                int id = jsonObject.optInt("id");
                                String carta = jsonObject.optString("carta");

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
                                    promocion = (int)(precio - ( precio * (descuento/100)));
                                    String fecha = jsonObject.optString("fecha_inicio") + " - " + jsonObject.optString("fecha_fin");
                                    listaProductos.add(new ListaProductos(idEmpresa, id, carta, idProducto, producto, descripcion, precio,
                                            promocion, descPromocion, descuento, fecha));
                                }
                                else{
                                    listaProductos.add(new ListaProductos(idEmpresa, id, carta, idProducto, producto, descripcion, precio, promocion));

                                }
                                if(imagen != null){
                                    listaProductos.get(listaProductos.size()-1).setFoto1(imagen);
                                }
                            }
                            else{
                                if(imagen != null){
                                    if(cont == 1){
                                        listaProductos.get(listaProductos.size()-1).setFoto2(imagen);
                                    }
                                    else{
                                        listaProductos.get(listaProductos.size()-1).setFoto3(imagen);
                                    }
                                }
                            }
                            cont++;
                        }

                        adaptadorListaCartas = new AdaptadorListaCartas(getContext(), listaCartas, listaProductos, idEmpresa);
                        rvCartas.setLayoutManager(new GridLayoutManager(getContext(), 1));
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
                            Toast.makeText(getContext(), "Error en la acción",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(consulta.compareTo("eliminar") == 0){
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error de consulta "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }
}
