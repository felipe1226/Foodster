package com.app.foodster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.Empresa.AdaptadorCategorias;
import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Empresa.InformacionEmpresa;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Mapa extends Fragment implements OnMapReadyCallback {

    GlobalState gs;

    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 2;

    MapView mapView;
    private GoogleMap googleMap;

    private FloatingActionButton fbNavigation;
    LatLng origen;

    String empDestino;
    String empAntDestino;
    LatLng destino;

    ArrayList<LatLng> markerPoints;

    private Location instLoc = new Location("punto1");

    Button btnFiltrar;
    AlertDialog dialogFiltro;
    View viewFiltro;

    ArrayList<DatosEmpresa> datosEmpresa;
    ArrayList<String> filtroCategorias;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mapa, container, false);

        btnFiltrar = v.findViewById(R.id.btnFiltrar);
        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFiltroCategorias();
            }
        });

        mapView = v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        fbNavigation = v.findViewById(R.id.fbNavigation);
        fbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocation()) {
                    if(empAntDestino != empDestino){

                        empAntDestino = empDestino;

                        double latitud = googleMap.getMyLocation().getLatitude();
                        double longitud = googleMap.getMyLocation().getLongitude();

                        origen =  new LatLng(latitud, longitud);

                        String url = getDirectionsUrl(origen, destino);

                        generarRuta(url);
                    }
                }
            }
        });

        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
        gs.setFragment(this);
        request = Volley.newRequestQueue(getActivity().getApplicationContext());

        markerPoints = new ArrayList<LatLng>();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMinZoomPreference(5);
        googleMap.setMaxZoomPreference(16);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        this.googleMap.setMyLocationEnabled(true);

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onMapClick(LatLng point) {
                fbNavigation.setVisibility(View.GONE);

            }
        });

        inicializarMapa();
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String origen = "origin="+origin.latitude+","+origin.longitude;

        String destino = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";

        String parametros = origen+"&"+destino;

        String key = "&key=AIzaSyCT-UX_0m_B97OdEYcCc_axktdQs2Ipkks";

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parametros + key;

        return url;
    }

    private void generarRuta(String url){
        final JSONObject[] jso = {null};
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jso[0] = new JSONObject(response);
                    trazarRuta(jso[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    private void trazarRuta(JSONObject jso){
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jso.getJSONArray("routes");

            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");


                    for(int k=0;k<jSteps.length();k++){
                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");

                        List<LatLng> list = PolyUtil.decode(polyline);
                        googleMap.addPolyline(new PolylineOptions().addAll(list).color(Color.RED).width(6));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
    }

    private void inicializarMapa() {

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        generarMarcadores();

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Toast.makeText(getContext(),"Distancia=" + polyline.getWidth(), Toast.LENGTH_LONG).show();
            }
        });

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if (!checkLocation()) {
                    return false;
                }
                return false;
            }
        });
    }

    private void generarMarcadores() {
        googleMap.clear();

        datosEmpresa = gs.getDatosEmpresa();

        LatLng coordenadas;

        String ubicacion[];

        for (int i = 0; i < datosEmpresa.size(); i++) {
            for (int j = 0; j < gs.filtroCategorias.size(); j++) {

                String categoria = gs.filtroCategorias.get(j);
                if (datosEmpresa.get(i).getCategoria().compareTo(categoria) == 0) {
                    ubicacion = datosEmpresa.get(i).getUbicacion().split(",");

                    double latitud = Double.parseDouble(ubicacion[0]);
                    double longitud = Double.parseDouble(ubicacion[1]);

                    coordenadas = new LatLng(latitud, longitud );

                    String empresa = datosEmpresa.get(i).getNombre();
                    String direccion = datosEmpresa.get(i).getDireccion();

                    googleMap.addMarker(new MarkerOptions().position(coordenadas).title(empresa).snippet(direccion));
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            fbNavigation.setVisibility(View.VISIBLE);

                            destino = marker.getPosition();
                            empDestino = marker.getTitle();

                            if (isLocationEnabled()) {
                                calcularDistancia(marker);
                            }
                            return false;
                        }
                    });

                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            verEmpresa(marker.getTitle());
                        }
                    });
                }
            }
        }

        LatLng ciudad = new LatLng(4.0864458, -76.1971384);

        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudad, 14));

        googleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(getContext(), "Ubicación actual", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularDistancia(Marker marker){
        double distancia;
        String dist = "";
        Location loc = googleMap.getMyLocation();

        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;

        instLoc.setLatitude(lat);
        instLoc.setLongitude(lon);

        distancia = loc.distanceTo(instLoc);

        DecimalFormat df1 = new DecimalFormat("#");
        DecimalFormat df2 = new DecimalFormat("#.#");
        if(distancia < 1000){
            dist = df1.format(distancia)+" Mts";
        }
        else{
            dist = df2.format(distancia / 1000) + " Kms";
        }
        if(!marker.getSnippet().contains("/")){
            marker.setSnippet(marker.getSnippet()+" / "+dist);
        }
    }

    public void generarVisita(int idEmpresa){
        String url = "http://" + gs.getIp() + "/Empresa/generar_visita.php?idEmpresa="
                + idEmpresa + "&idPersona="+gs.getIdPersona()+"&busqueda=";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("visita");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
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

    private void verEmpresa(String nombre){
        int id = 0;

        for(int i=0;i<datosEmpresa.size();i++){
            if(datosEmpresa.get(i).getNombre().compareTo(nombre) == 0){
                id = datosEmpresa.get(i).getId();
            }
        }

        generarVisita(id);

        Bundle args = new Bundle();
        args.putInt("idEmpresa", id);

        AppCompatActivity activity = (AppCompatActivity) getContext();

        InformacionEmpresa fragment = new InformacionEmpresa();
        gs.setFragment(fragment);
        gs.setFragmentActual("InformacionEmpresas");
        fragment.setArguments(args);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment, fragment.getClass().toString())
                .commit();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
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

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void dialogFiltroCategorias(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        viewFiltro = getLayoutInflater().inflate(R.layout.dialog_filtro_categorias, null);

        RecyclerView rvCategorias = viewFiltro.findViewById(R.id.rvCategorias);

        filtroCategorias = new ArrayList<>();

        AdaptadorCategorias adaptadorCategorias = new AdaptadorCategorias(getContext(), gs.getCategorias(), filtroCategorias, gs);
        rvCategorias.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvCategorias.setAdapter(adaptadorCategorias);

        Button btnAplicar = viewFiltro.findViewById(R.id.btnAplicar);
        Button btnCancelar = viewFiltro.findViewById(R.id.btnCancelar);

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gs.setFiltroCategorias(filtroCategorias);
                generarMarcadores();
                dialogFiltro.hide();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFiltro.hide();
            }
        });

        builder.setView(viewFiltro);
        builder.setCancelable(false);
        dialogFiltro = builder.create();
        dialogFiltro.show();
    }

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
