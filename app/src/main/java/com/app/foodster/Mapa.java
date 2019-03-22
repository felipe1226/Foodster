package com.app.foodster;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Empresa.InformacionEmpresa;
import com.app.foodster.Empresa.fragInformacionEmpresa;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Mapa extends Fragment implements OnMapReadyCallback {

    GlobalState gs;

    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 2;

    MapView mapView;
    private GoogleMap googleMap;

    double distance;
    private Location instLoc = new Location("punto1");

    Button btnFiltrar;
    AlertDialog dialogFiltro;
    View viewFiltro;

    ArrayList<DatosEmpresa> datosEmpresa;
    ArrayList<String> filtroCategorias;

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

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        inicializarMapa();
    }

    private void inicializarMapa() {

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        generarMarcadores();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

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
                if (datosEmpresa.get(i).getCategoria().compareTo(gs.filtroCategorias.get(j)) == 0) {
                    ubicacion = datosEmpresa.get(i).getUbicacion().split(",");
                    coordenadas = new LatLng(Double.parseDouble(ubicacion[0]), Double.parseDouble(ubicacion[1]));

                    googleMap.addMarker(new MarkerOptions().position(coordenadas).title(datosEmpresa.get(i).getNombre()).snippet(datosEmpresa.get(i).getDireccion()));
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

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
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudad, 14));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1500, null);

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

        instLoc.setLatitude(marker.getPosition().latitude);
        instLoc.setLongitude(marker.getPosition().longitude);

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

    private void verEmpresa(String nombre){
        int id = 0;

        for(int i=0;i<datosEmpresa.size();i++){
            if(datosEmpresa.get(i).getNombre().compareTo(nombre) == 0){
                id = datosEmpresa.get(i).getId();
            }
        }

        Bundle args = new Bundle();
        args.putInt("idEmpresa", id);

        AppCompatActivity activity = (AppCompatActivity) getContext();

        fragInformacionEmpresa fragment = new fragInformacionEmpresa();
        gs.setFragmentEmpresas(fragment);
        gs.setFragment(fragment);
        gs.setFragmentActual("InformacionEmpresas");
        fragment.setArguments(args);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment, fragment.getClass().toString()) // add and tag the new fragment
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
        viewFiltro = getLayoutInflater().inflate(R.layout.dialog_filtro_mapa, null);

        RecyclerView rvCategorias = viewFiltro.findViewById(R.id.rvCategorias);

        filtroCategorias = new ArrayList<>();

        AdaptadorCategorias adaptadorCategorias = new AdaptadorCategorias(getContext(), gs.getCategorias());
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

    private class AdaptadorCategorias extends RecyclerView.Adapter<AdaptadorCategorias.MyViewHolder> {
            Context context;
            ArrayList<String> categorias;

    public AdaptadorCategorias(Context context, ArrayList<String> categorias) {
                this.context = context;
                this.categorias = categorias;
            }

        @NonNull
        @Override
        public AdaptadorCategorias.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_filtro_categoria,viewGroup,false);
            final AdaptadorCategorias.MyViewHolder holder = new AdaptadorCategorias.MyViewHolder(v);

            holder.cbCategoria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String categoria = categorias.get(holder.getAdapterPosition());

                    if(filtroCategorias.size() == 0){
                        filtroCategorias.add(categoria);
                    }
                    else{
                        if(holder.cbCategoria.isChecked()){
                            filtroCategorias.add(categoria);

                        }
                        else{
                            for(int i=0;i<filtroCategorias.size();i++){
                                if(filtroCategorias.get(i).compareTo(categoria) == 0){
                                    filtroCategorias.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

            boolean marca = false;

            for(int j=0;j<gs.filtroCategorias.size();j++){
                if(gs.filtroCategorias.get(j).compareTo(categorias.get(i)) == 0){
                    marca = true;
                }
            }
            myViewHolder.cbCategoria.setChecked(marca);
            myViewHolder.cbCategoria.setText(categorias.get(i));
        }

        @Override
        public int getItemCount() {
            return categorias.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout item_categoria;
            private CheckBox cbCategoria;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                item_categoria = itemView.findViewById(R.id.item_categoria);
                cbCategoria = itemView.findViewById(R.id.cbCategoria);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_mapa, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filtrar:
                dialogFiltroCategorias();
                break;

            default:break;
        }
        return false;
    }
}
