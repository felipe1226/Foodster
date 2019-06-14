package com.app.foodster.Empresa;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.app.foodster.R;
import com.app.foodster.Ubicacion.DatosLocalidad;
import com.app.foodster.Ubicacion.Localidad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SugerirEmpresa extends AppCompatActivity {

    GlobalState gs;
    ProgressDialog carga;

    ArrayList<Localidad> localidad = new ArrayList<>();
    DatosLocalidad datosLocalidad;

    ImageButton btnConfirmar;
    ImageButton btnCancelar;

    EditText etNombre;
    EditText etDireccion;

    Spinner spDepartamento;
    Spinner spCiudad;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerir_empresa);


        gs = (GlobalState) getApplication();
        request = Volley.newRequestQueue(getApplicationContext());

        carga = new ProgressDialog(this);
        carga.setMessage("Registrando datos...");
        carga.setCanceledOnTouchOutside(false);

        btnConfirmar = findViewById(R.id.btnAplicar);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
        btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etMensaje);

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

        cargarDepartamentos();
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

    private void validarDatos(){
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String departamento = spDepartamento.getSelectedItem().toString();
        String ciudad = spCiudad.getSelectedItem().toString();

        if(nombre.compareTo("") != 0 && direccion.compareTo("") != 0){
            registrar(nombre, direccion, departamento, ciudad);
        }
        else{
            Toast.makeText(this, "Complete los campos, por favor", Toast.LENGTH_SHORT).show();
        }
    }

    private void registrar(String nombre, String direccion, String departamento, String ciudad){
        String url = "http://" + gs.getIp() + "/Persona/sugerir_empresa.php?idPersona="+gs.getIdPersona()
                +"&nombre="+nombre+"&direccion="+direccion+"&departamento="+departamento+"&ciudad="+ciudad;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("registro");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        Toast.makeText(getApplicationContext(), "Muchas gracias por tu sugerencia!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error al registrar los datos", Toast.LENGTH_SHORT).show();
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
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray("registro");
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                Toast.makeText(this, "Muchas gracias por tu sugerencia!", Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                Toast.makeText(this, "Error al registrar los datos", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        carga.cancel();
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
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
