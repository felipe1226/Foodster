package com.app.foodster.Persona;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MisDirecciones extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    GlobalState gs;

    boolean actualizarUbicacion;

    ProgressBar progress;
    MapView mapView;
    LinearLayout layout_mapa;
    double latitud;
    double longitud;

    EditText etTitulo;
    EditText etDireccion;
    String ubicacion;


    ProgressDialog progressEliminar;
    AlertDialog dialogDireccion;

    RecyclerView rvDirecciones;
    Button btnNuevaDireccion;

    int posicion;
    String consulta;

    private ArrayList<ListaDireccion> listaDireccion;
    private MisDirecciones.AdaptadorListaDireccion adaptadorListaDireccion;

    MisDirecciones.AdaptadorListaDireccion.MyViewHolder auxHolder = null;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState) getActivity().getApplication();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mis_direcciones, container, false);

        progressEliminar = new ProgressDialog(getContext());
        progressEliminar.setMessage(getString(R.string.text_eliminando));
        progressEliminar.setCanceledOnTouchOutside(false);

        rvDirecciones = v.findViewById(R.id.rvDirecciones);
        btnNuevaDireccion = v.findViewById(R.id.btnNuevaDireccion);
        btnNuevaDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDireccion();
            }
        });
        ubicacion = "";
        verificarDirecciones();

        return v;
    }

    private void verificarDirecciones() {
        if (!gs.isActualizaDirecciones()) {
            if (gs.getDatosDireccion().size() > 0) {
                listaDireccion = gs.getDatosDireccion();
                generarDirecciones();
            }
        } else {
            listarDirecciones();
        }
    }

    private void generarDirecciones() {
        adaptadorListaDireccion = new MisDirecciones.AdaptadorListaDireccion(getContext(), listaDireccion);
        rvDirecciones.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvDirecciones.setAdapter(adaptadorListaDireccion);
    }

    private void dialogDireccion() {
        AlertDialog.Builder buider = new AlertDialog.Builder(getContext());
        buider.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_direccion, null);

        etTitulo = view.findViewById(R.id.etNombre);
        etDireccion = view.findViewById(R.id.etMensaje);

        progress = view.findViewById(R.id.progressBar);

        layout_mapa = view.findViewById(R.id.layout_mapa);

        Button btnUbicacion = view.findViewById(R.id.btnUbicacion);
        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                } else {
                    actualizarUbicacion = true;
                    progress.setVisibility(View.VISIBLE);
                    locationStart();
                }
            }
        });

        mapView = view.findViewById(R.id.mapView);
        MapsInitializer.initialize(getContext());

        final Button btnCancelar = view.findViewById(R.id.btnCancelar);
        final Button btnConfirmar = view.findViewById(R.id.btnAplicar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDireccion.cancel();
            }
        });


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCampos();
            }
        });


        buider.setView(view);
        dialogDireccion = buider.create();

        dialogDireccion.show();
    }

    private void validarCampos(){
        String titulo = etTitulo.getText().toString();
        String direccion = etDireccion.getText().toString();

        if(titulo.compareTo("") != 0 && direccion.compareTo("") != 0){
            registrarDireccion(titulo, direccion);
        }
        else{
            Toast.makeText(getContext(), "Complete los campos, por favor", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarMapa(){
        progress.setVisibility(View.GONE);
        layout_mapa.setVisibility(View.VISIBLE);
        mapView.onCreate(dialogDireccion.onSaveInstanceState());
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.clear();

                googleMap.setMinZoomPreference(5);
                googleMap.setMaxZoomPreference(16);

                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);

                Marker marcador;
                final LatLng coordenadas = new LatLng(latitud, longitud);
                marcador = googleMap.addMarker(new MarkerOptions().position(coordenadas));
                marcador.setDraggable(true);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 16));

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        latitud = marker.getPosition().latitude;
                        longitud = marker.getPosition().longitude;

                        ubicacion = latitud +","+longitud;
                    }
                });
            }
        });
    }

    private void listarDirecciones() {

        consulta = "direccion";
        String url = "http://" + gs.getIp() + "/Persona/listar_direcciones.php?idPersona=" + gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void actualizarPredeterminada(int idDireccion) {

        consulta = "predeterminada";
        String url = "http://" + gs.getIp() + "/Persona/establecer_predeterminada.php?idPersona=" + gs.getIdPersona() + "&idDireccion=" + idDireccion;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void registrarDireccion(String titulo, String direccion){
        consulta = "registro";
        String url = "http://" + gs.getIp() + "/Persona/registrar_direccion.php?idPersona=" + gs.getIdPersona()+"&titulo="+titulo
                + "&direccion="+direccion +"&ubicacion="+ubicacion;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void eliminarDireccion(int idDireccion) {
        consulta = "eliminar_direccion";
        String url = "http://" + gs.getIp() + "/Persona/eliminar_direccion.php?idDireccion=" + idDireccion;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void accionConsulta(boolean actualizaPredeterminada) {

        switch (consulta) {
            case "predeterminada":
                    if (actualizaPredeterminada) {
                        adaptadorListaDireccion.actualizar(listaDireccion);
                    }
                    break;
            case "registro":
                    if(gs.isActualizaDirecciones()){
                        verificarDirecciones();
                    }
                    break;
        }
    }

    public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        boolean actualizaPredeterminada = false;

        try {
            jsonObject = datos.getJSONObject(0);
            if (jsonObject != null) {
                if (jsonObject.optInt("id") != 0) {

                    if (consulta.compareTo("direccion") == 0) {

                        listaDireccion = new ArrayList<>();
                        for (int i = 0; i < datos.length(); i++) {
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

                    if (consulta.compareTo("predeterminada") == 0) {
                        actualizaPredeterminada = true;

                        for (int k = 0; k < listaDireccion.size(); k++) {
                            if (posicion == k) {
                                listaDireccion.get(k).setPredeterminada(1);
                            } else {
                                listaDireccion.get(k).setPredeterminada(0);
                            }
                        }

                        gs.setDatosDireccion(listaDireccion);
                    }
                    if (consulta.compareTo("registro") == 0) {
                        dialogDireccion.cancel();
                        Toast.makeText(getContext(), "Direccion registrada", Toast.LENGTH_SHORT).show();
                        gs.setActualizaDirecciones(true);
                    }

                    if (consulta.compareTo("eliminar_direccion") == 0) {
                        progressEliminar.cancel();
                        listaDireccion.remove(posicion);
                        gs.setDatosDireccion(listaDireccion);

                        if (listaDireccion.size() > 0) {
                            generarDirecciones();
                        }
                    }
                } else {
                    if (consulta.compareTo("registro") == 0) {
                        Toast.makeText(getContext(), "Error en el registro", Toast.LENGTH_SHORT).show();
                    }

                    if (consulta.compareTo("eliminar") == 0 || consulta.compareTo("eliminar") == 0) {
                        Toast.makeText(getContext(), "Error en la acción", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        accionConsulta(actualizaPredeterminada);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error de consulta " + error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }

    public class AdaptadorListaDireccion extends RecyclerView.Adapter<MisDirecciones.AdaptadorListaDireccion.MyViewHolder> implements OnMapReadyCallback {
        Context context;
        ArrayList<ListaDireccion> direccion;
        GoogleMap googleMap;

        public AdaptadorListaDireccion(Context context, ArrayList<ListaDireccion> direccion) {
            this.context = context;
            this.direccion = direccion;
        }

        public void actualizar(ArrayList<ListaDireccion> lista) {
            direccion = new ArrayList<>();
            direccion.addAll(lista);
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public MisDirecciones.AdaptadorListaDireccion.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_direccion, viewGroup, false);
            final MisDirecciones.AdaptadorListaDireccion.MyViewHolder holder = new MisDirecciones.AdaptadorListaDireccion.MyViewHolder(v);

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
                    if (direccion.get(holder.getAdapterPosition()).getPredeterminada() == 0) {
                        dialog_borrar(holder);
                    } else {
                        Toast.makeText(context, "No se puede eliminar una direccion predeterminada", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            return holder;
        }

        private void dialog_borrar(final MisDirecciones.AdaptadorListaDireccion.MyViewHolder holder) {
            android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);
            dialogo1.setTitle("");
            dialogo1.setMessage("¿Eliminar esta dirección?");
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
        public void onBindViewHolder(@NonNull MisDirecciones.AdaptadorListaDireccion.MyViewHolder myViewHolder, int i) {
            myViewHolder.tvTitulo.setText(direccion.get(i).getTitulo());
            myViewHolder.tvDireccion.setText(direccion.get(i).getDireccion());

            String u = direccion.get(i).getUbicacion();

            if (u.compareTo("") != 0) {
                myViewHolder.mapView.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.mapView.setVisibility(View.GONE);
            }

            if (direccion.get(i).getPredeterminada() == 1) {
                myViewHolder.btnPredeterminada.setText("predeterminada");
                myViewHolder.btnPredeterminada.setEnabled(false);
            } else {
                myViewHolder.btnPredeterminada.setText("establecer predeterminada");
                myViewHolder.btnPredeterminada.setEnabled(true);
            }
        }


        public int getItemCount() {
            return direccion.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

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


    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            showAlert();
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        //tvMensaje1.setText("Localización agregada");
        //tvMensaje2.setText("");
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Habilitar GPS")
                .setMessage("Su GPS esta desactivado.")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //tvMensaje2.setText("Mi direccion es: \n"+ DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Localizacion implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if(actualizarUbicacion){
                actualizarUbicacion = false;

                latitud = loc.getLatitude();
                longitud = loc.getLongitude();

                ubicacion = latitud +","+longitud;
                mostrarMapa();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //tvMensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //tvMensaje1.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
}
