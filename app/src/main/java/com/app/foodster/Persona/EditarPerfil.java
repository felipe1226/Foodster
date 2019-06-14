package com.app.foodster.Persona;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

public class EditarPerfil extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    GlobalState gs;
    ProgressDialog carga;

    ArrayList<Documentos> documentos = new ArrayList<>();
    ArrayList<Localidad> localidad = new ArrayList<>();
    DatosLocalidad datosLocalidad;

    ImageButton btnConfirmar;
    ImageButton btnCancelar;


    EditText etNumero;
    EditText etNombre;
    EditText etMovil;
    EditText etEmail;

    Spinner spDocumentos;
    Spinner spDepartamento;
    Spinner spCiudad;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        gs = (GlobalState) getApplication();
        request = Volley.newRequestQueue(getApplicationContext());

        carga = new ProgressDialog(this);
        carga.setMessage("Actualizando datos...");
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

        spDocumentos = findViewById(R.id.spDocumentos);

        etNumero = findViewById(R.id.etNumero);
        etNombre = findViewById(R.id.etNombre);
        etMovil = findViewById(R.id.etMovil);
        etEmail = findViewById(R.id.etEmail);

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

        obtenerDatos();
    }

    private void obtenerDatos(){

        String documento[] = gs.getDocumento().split(" ");
        etNumero.setText(documento[1]);
        etNombre.setText(gs.getNombre());
        etMovil.setText(gs.getTelefono());
        etEmail.setText(gs.getEmail());

        cargarDocumentos();
        cargarDepartamentos();
    }

    public int obtenerPosicionItem(Spinner spinner, String item){

        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                posicion = i;
            }
        }
        return posicion;
    }

    private void validarDatos(){
        String documento = spDocumentos.getSelectedItem().toString();
        String numero = etNumero.getText().toString().trim();

        String nombre = etNombre.getText().toString().trim();
        String movil = etMovil.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ciudad = spCiudad.getSelectedItem().toString();

        if(numero. compareTo("") != 0 && nombre.compareTo("") != 0 && movil.compareTo("") != 0 &&  email.compareTo("") != 0){
            registrar(documento, numero, nombre, movil, email, ciudad);
        }
        else{
            Toast.makeText(this, "Complete los campos, por favor", Toast.LENGTH_SHORT).show();
        }
    }

    private void registrar(String documento, String numero, String nombre, String movil, String email, String ciudad){
        String url = "http://" + gs.getIp() + "/Persona/actualizar_perfil.php?idPersona="+gs.getIdPersona()
                +"&documento="+documento+"&numero="+numero+"&nombre="+nombre+"&movil="+movil+"&email="+email+"&ciudad="+ciudad;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void cargarDocumentos() {

        documentos = gs.getDocumentos();

        ArrayList<String> documento = new ArrayList<>();

        String abrev[] = gs.getDocumento().split(" ");
        String item = "";

        for(int i=0;i<documentos.size();i++){
            documento.add(documentos.get(i).getTipo());
            if(documentos.get(i).getAbreviacion().compareTo(abrev[0]) == 0){
                item = documentos.get(i).getTipo();
            }
        }

        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                documento);

        spDocumentos.setAdapter(adaptador);

        spDocumentos.setSelection(obtenerPosicionItem(spDocumentos, item));
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

    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray("perfil");
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {

                gs.setNombre(etNombre.getText().toString());
                gs.setTelefono(etMovil.getText().toString());
                gs.setEmail(etEmail.getText().toString());
                gs.setDepartamento(spDepartamento.getSelectedItem().toString());
                gs.setCiudad(spCiudad.getSelectedItem().toString());

                ArrayList<Documentos> documentos = gs.getDocumentos();

                String tipo = spDocumentos.getSelectedItem().toString();
                String numero = etNumero.getText().toString();
                for(int i=0;i<documentos.size();i++){
                    if(documentos.get(i).getTipo().compareTo(tipo) == 0){
                        gs.setDocumento(documentos.get(i).getAbreviacion()+" "+numero);
                        break;
                    }
                }

                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_LONG).show();
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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
    }
}
