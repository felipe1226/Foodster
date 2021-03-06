package com.app.foodster.Persona;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.R;
import com.app.foodster.Ubicacion.DatosLocalidad;
import com.app.foodster.Ubicacion.Localidad;
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
import java.util.Hashtable;
import java.util.Map;

public class RegistroPersona extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    GlobalState gs;

    ArrayList<Documentos> documento = new ArrayList<>();
    ArrayList<Localidad> localidad = new ArrayList<>();
    DatosLocalidad datosLocalidad;

    LocationManager locationManager;
    double longitud, latitud;
    String ubicacion;
    boolean registraUbicacion;

    private static final int REQUEST_LOCATION = 2;

    ProgressDialog cargaUbicacion;

    LinearLayout layout_ubicacion;
    ImageButton btnBorrar;

    EditText etNumero;
    EditText etNombre;
    EditText etTelefono;
    EditText etDireccion;
    EditText etEmail;
    EditText etPassword;

    Spinner spDocumentos;
    Spinner spDepartamento;
    Spinner spCiudad;

    ImageButton btnSalir;
    ImageButton btnUbicacion;
    Button btnRegistrar;

    ProgressDialog carga;

    String consulta;
    ArrayList<String> listaConsulta;

    boolean existeDocumento;
    boolean existeEmail;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_persona);

        gs = (GlobalState) getApplication();

        request = Volley.newRequestQueue(getApplicationContext());

        cargaUbicacion = new ProgressDialog(RegistroPersona.this);
        cargaUbicacion.setMessage("Obteniendo ubicación...");
        cargaUbicacion.setCanceledOnTouchOutside(false);

        carga = new ProgressDialog(this);
        carga.setMessage("Registrando datos...");
        carga.setCanceledOnTouchOutside(false);

        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spDocumentos = findViewById(R.id.spDocumentos);

        etNumero = findViewById(R.id.etNumero);
        etNumero.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etNumero.setHighlightColor(Color.BLUE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etNumero.getText().toString().compareTo("") != 0) {
                    existeDocumento = false;
                    verificarDocumento();
                }
            }
        });

        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etMovil);
        etDireccion = findViewById(R.id.etMensaje);

        etEmail = findViewById(R.id.etEmail);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etEmail.setHighlightColor(Color.BLUE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etEmail.getText().toString().compareTo("") != 0) {
                    existeEmail = false;
                    verificarEmail();
                }
            }
        });

        spDepartamento = findViewById(R.id.spDepartamento);
        spDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarCiudades();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCiudad = findViewById(R.id.spCiudad);

        btnUbicacion = findViewById(R.id.btnUbicacion);
        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!checkLocation()) {
                    return;
                }
                else{
                    cargaUbicacion.show();
                    obtenerUbicacion();
                }
            }
        });

        layout_ubicacion = findViewById(R.id.layout_ubicacion);

        btnBorrar = findViewById(R.id.btnBorrar);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBorrar();
            }
        });

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidarDatos();
            }
        });

        cargarDocumentos();
        cargarDepartamentos();
    }

    private void cargarDocumentos() {

        documento = gs.getDocumentos();

        ArrayList<String> documentos = new ArrayList<>();

        for(int i=0;i<documento.size();i++){
            documentos.add(documento.get(i).getTipo());
        }

        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                documentos);

        spDocumentos.setAdapter(adaptador);
    }

    private void cargarDepartamentos() {

        localidad = gs.getLocalidades();

        datosLocalidad = new DatosLocalidad(localidad);

        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                datosLocalidad.getDepartamentos());

        spDepartamento.setAdapter(adaptador);
        spDepartamento.setSelection(datosLocalidad.obtenerPosicionItem(spDepartamento, gs.getDepartamento()));
    }

    private void cargarCiudades(){

        String depto = spDepartamento.getSelectedItem().toString();

        datosLocalidad.listaCiudades(depto);

        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                datosLocalidad.getCiudades());

        spCiudad.setAdapter(adaptador);
        spCiudad.setSelection(datosLocalidad.obtenerPosicionItem(spCiudad, gs.getCiudad()));
    }

    private void ValidarDatos() {
        String documento = spDocumentos.getSelectedItem().toString();
        String numero = etNumero.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ciudad = spCiudad.getSelectedItem().toString();
        boolean emailCorrecto = true;
        boolean telefonoCorrecto = true;

        if (numero.compareTo("") != 0 && password.compareTo("") != 0 && nombre.compareTo("") != 0 && telefono.compareTo("") != 0
                && direccion.compareTo("") != 0 && email.compareTo("") != 0) {

            if (!existeDocumento  && !existeEmail) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(this, "Dirección email invalida", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                }
                else{
                    registrar(documento, numero, password, nombre, telefono, direccion, email, ciudad);
                }
            } else {
                if(existeDocumento){
                    Toast.makeText(this, "El numero de documento ya se encuentra registrado", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "El email ya se encuentra registrado", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            Toast.makeText(this, "Complete los datos, por favor", Toast.LENGTH_LONG).show();
        }
    }

    private void dialogBorrar(){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setMessage("¿Desea borrar la ubicación registrada?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                layout_ubicacion.setVisibility(View.GONE);
                registraUbicacion = false;
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void verificarDocumento() {
        consulta = "documento";

        String url = "http://" + gs.getIp() + "/Persona/consultar_documento.php?documento=" + etNumero.getText().toString();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void verificarEmail() {
        consulta = "email";

        String url = "http://" + gs.getIp() + "/Usuario/consultar_email.php?email=" + etEmail.getText().toString();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void registrar(final String documento, final String numero, final String password, final String nombre, final String telefono, final String direccion, final String email,
                              final String ciudad){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + gs.getIp() + "/Persona/registrar_persona.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.compareTo("") != 0) {
                            Toast.makeText(getApplicationContext(), "Se ha registrado satisfactoriamente", Toast.LENGTH_SHORT).show();
                            carga.cancel();
                            finish();

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Error al registrar, intente de nuevo", Toast.LENGTH_SHORT).show();
                            carga.cancel();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                params.put("documento", documento);
                params.put("numero", numero);
                params.put("password", password);
                params.put("nombre", nombre);
                params.put("telefono", telefono);
                params.put("direccion", direccion);
                params.put("ubicacion", ubicacion);
                params.put("email", email);
                params.put("ciudad", ciudad);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);

        listaConsulta = new ArrayList<>();
        ArrayAdapter<CharSequence> adaptador = null;

        JSONObject jsonObject = null;

        boolean registro = false;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject != null){
                if (consulta.compareTo("documento") == 0) {
                    int id = jsonObject.optInt("id");
                    if (id != 0) {
                        existeDocumento = true;
                        etNumero.setTextColor(Color.RED);
                        Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        existeDocumento = false;
                        etNumero.setTextColor(Color.BLACK);
                    }
                }
                if (consulta.compareTo("email") == 0) {
                    int id = jsonObject.optInt("id");
                    if (id != 0) {
                        existeEmail = true;
                        etEmail.setTextColor(Color.RED);
                        Toast.makeText(this, "El email ya está registrado", Toast.LENGTH_SHORT).show();
                    } else {
                        existeEmail = false;
                        etEmail.setTextColor(Color.BLACK);
                    }
                }
                if (jsonObject.optInt("id") != 0) {

                    if (consulta.compareTo("registro") == 0) {
                        registro = true;
                    }

                    if (consulta.compareTo("departamento") == 0 || consulta.compareTo("ciudad") == 0) {
                        for (int i = 0; i < datos.length(); i++) {
                            jsonObject = datos.getJSONObject(i);
                            listaConsulta.add(jsonObject.optString("nombre"));
                        }
                        adaptador = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaConsulta);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (registro) {
            Toast.makeText(this, "Se ha registrado satisfactoriamente", Toast.LENGTH_SHORT).show();
            carga.cancel();
            finish();
        } else {
            if (consulta.compareTo("registro") == 0) {
                Toast.makeText(this, "Error al registrar, intente de nuevo", Toast.LENGTH_SHORT).show();
                carga.cancel();
            }
        }

        if (consulta.compareTo("departamento") == 0) {
            spDepartamento.setAdapter(adaptador);
        } else {
            if (consulta.compareTo("ciudad") == 0) {
                spCiudad.setAdapter(adaptador);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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

    public void obtenerUbicacion() {

        longitud = 0;
        latitud = 0;

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(provider, 2 * 20 * 1000, 10, locationListenerBest);
        }
    }

    private void detenerEscaneo(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.removeUpdates(locationListenerBest);
    }

    private void mostrarMapa(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_mapa);
        dialog.setCanceledOnTouchOutside(false);

        MapView mMapView = dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(getApplicationContext());

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                Marker marcador;
                final LatLng coordenadas = new LatLng(latitud, longitud);
                marcador = googleMap.addMarker(new MarkerOptions().position(coordenadas));
                marcador.setDraggable(true);

                googleMap.setMinZoomPreference(5);
                googleMap.setMaxZoomPreference(16);

                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);

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

                        ubicacion = longitud + "," + latitud;

                        Toast.makeText(getApplicationContext(), latitud + "," + longitud, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button btnAceptar = dialog.findViewById(R.id.btnAplicar);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registraUbicacion = true;
                layout_ubicacion.setVisibility(View.VISIBLE);
                ubicacion = longitud + "," + latitud;
                dialog.cancel();
            }
        });

        Button btnCancelar = dialog.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registraUbicacion = false;
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {

            longitud = location.getLongitude();
            latitud = location.getLatitude();

            if(longitud != 0 && latitud !=0){
                detenerEscaneo();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cargaUbicacion.cancel();;
                    mostrarMapa();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
}
