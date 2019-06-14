package com.app.foodster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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
import com.app.foodster.Persona.Documentos;
import com.app.foodster.Persona.HiloPedidos;
import com.app.foodster.Persona.RegistroPersona;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Login extends AppCompatActivity{

    String TAG = this.getClass().getName();
    boolean salir = false;

    int idUsuario;
    boolean usuarioValido;

    GlobalState gs;

    EditText etUsuario;
    EditText etPassword;

    Switch cbRecordar;
    Switch cbSession;

    Button btnIngresar;
    Button btnRegistro;

    ProgressDialog progress;

    String usuario;
    String password;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gs = (GlobalState)getApplication();
        request = Volley.newRequestQueue(getApplicationContext());

        usuario = getIntent().getExtras().getString("usuario");
        password = getIntent().getExtras().getString("password");
        String recordar = getIntent().getExtras().getString("recordar");

        progress = new ProgressDialog(Login.this);
        progress.setMessage(getString(R.string.text_carga));
        progress.setCanceledOnTouchOutside(false);

        etUsuario = findViewById(R.id.etNumero);
        etPassword = findViewById(R.id.etPassword);

        etUsuario.setText(usuario);
        etPassword.setText(password);

        cbRecordar = findViewById(R.id.cbRecordar);

        cbSession = findViewById(R.id.cbSession);

        if(recordar.compareTo("") != 0){
            cbRecordar.setChecked(true);
        }

        btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                validarCampos();
            }
        });

        btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPersona();
            }
        });
    }

    public void ingresar(){
        Intent ingreso = new Intent(Login.this, Principal.class);

        ingreso.addFlags(ingreso.FLAG_ACTIVITY_CLEAR_TOP | ingreso.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(ingreso);
        progress.hide();
    }

    public void registrarPersona(){


        Intent registro = new Intent(this, RegistroPersona.class);
        startActivity(registro);
    }

    private void validarCampos(){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

        usuario = etUsuario.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if(usuario.compareTo("") != 0 && password.compareTo("") != 0) {
            usuarioValido = false;
            progress.show();
            consultarPersona(usuario);
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.text_completar_campos), Toast.LENGTH_SHORT).show();
        }
    }

    public void consultarPersona(String usuario) {

            /*try {
                String passwdMd5 = this.toMd5(password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/

        String url = "http://" + gs.getIp() + "/Usuario/consultar_persona.php?email=" + usuario;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("persona");
                JSONObject jsonObject = null;
                try {

                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaPersona(jsonObject);
                        verificarPreferencias();
                        consultarPedidos();
                    }
                    else{
                        consultarUsuario();
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



    public void consultarUsuario() {

            /*try {
                String passwdMd5 = this.toMd5(password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/

        progress.show();
        String url = "http://" + gs.getIp() + "/Usuario/consultar_usuario.php?usuario=" + usuario;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("usuario");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaUsuario(jsonObject);
                    }
                    else{
                        progress.hide();
                        Toast.makeText(getApplicationContext(),getString(R.string.text_usuario_erroneo), Toast.LENGTH_SHORT).show();
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


    private void consultarPedidos() {

        String url = "http://" + gs.getIp() + "/Persona/consultar_pedidos.php?idPersona="+gs.getIdPersona();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("pedido");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        gs.setExistePedidos(true);
                        HiloPedidos hiloPedidos = new HiloPedidos(getApplicationContext(), gs);
                        hiloPedidos.execute();
                        gs.setHiloPedidos(hiloPedidos);
                    }
                    else{
                        gs.setActualizaEmpresas(true);
                    }
                    GenerarDatos generarDatos = new GenerarDatos(gs, request);
                    gs.setActualizaEmpresas(false);

                    ingresar();
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

    private void verificarPreferencias(){

        SharedPreferences preferencesCuenta = getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorCuenta = preferencesCuenta.edit();

        if(cbRecordar.isChecked() || cbSession.isChecked()){
            editorCuenta.putString("usuario", etUsuario.getText().toString().trim());
            editorCuenta.putString("password", etPassword.getText().toString().trim());
        }

        if(cbRecordar.isChecked()){
            editorCuenta.putString("recordar", "si");
        }
        else{
            editorCuenta.putString("usuario","");
            editorCuenta.putString("password", "");
            editorCuenta.putString("recordar", "");
            etPassword.setText("");
            etUsuario.setText("");
        }

        if(cbSession.isChecked()){
            editorCuenta.putString("session", "si");
        }
        else{
            editorCuenta.putString("session", "");
        }
        editorCuenta.apply();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(gs.getIdPersona() != 0){
            cbSession.setChecked(false);
        }
    }

    /*@Override
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        boolean existeUsuario = true;
        boolean existePedidos = false;

        try {
            jsonObject = datos.getJSONObject(0);

            if(jsonObject.optString("id").compareTo("0") != 0) {
                switch (consulta){

                    case "persona":
                                    break;

                    case "usuario": consultaUsuario(jsonObject);
                                    existeUsuario = true;
                                    break;

                    case "datos": consultaDatos(jsonObject); break;

                    case "pedido": existePedidos = true; break;
                }
            }
            else{
                if(consulta.compareTo("persona") == 0){
                    existeUsuario = false;
                    consultarUsuario(usuario);
                }
                if(consulta.compareTo("usuario") == 0){
                    existeUsuario = false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(existeUsuario){
            accionConsulta(existePedidos);
        }
        else{
            progress.hide();
            Toast.makeText(getApplicationContext(),getString(R.string.text_usuario_erroneo), Toast.LENGTH_SHORT).show();
        }
    }*/

    /*private void accionConsulta(boolean existePedidos){
        switch(consulta){
            case "persona" : if(usuarioValido){
                verificarPreferencias();
                consultarPedidos();
            }
                break;

            case "pedido" : if(existePedidos){
                gs.setExistePedidos(true);
                HiloPedidos hiloPedidos = new HiloPedidos(getApplicationContext(), gs);
                hiloPedidos.execute();
                gs.setHiloPedidos(hiloPedidos);
            }
            else{
                gs.setActualizaEmpresas(true);
            }
            GenerarDatos generarDatos = new GenerarDatos(gs, request);
            gs.setActualizaEmpresas(false);
            ingresar();
            break;
        }
    }*/

    private void consultaPersona(JSONObject jsonObject){
        idUsuario = jsonObject.optInt("id");
        if(idUsuario != 0){
            String pass = jsonObject.optString("password");

            if(pass.compareTo(password) == 0){
                gs.setIdPersona(idUsuario);
                gs.setEmail(jsonObject.optString("email"));
                gs.setPassword(pass);

                gs.setNombre(jsonObject.optString("nombre"));
                gs.setTelefono(jsonObject.optString("telefono"));
                gs.setDepartamento(jsonObject.optString("departamento"));
                gs.setCiudad(jsonObject.optString("ciudad"));

                int tipo = jsonObject.optInt("tipo_documento");
                String numero = jsonObject.optString("numero_documento");

                ArrayList<Documentos> datos = gs.getDocumentos();
                for(int i=0;i<datos.size();i++){
                    if(datos.get(i).getId() == tipo){
                        gs.setDocumento(datos.get(i).getAbreviacion()+" "+numero);
                        break;
                    }
                }
                usuarioValido = true;
            }
            else{
                usuarioValido = false;
                progress.hide();
                Toast.makeText(getApplicationContext(), getString(R.string.text_pass_erronea), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            progress.hide();
            Toast.makeText(getApplicationContext(),getString(R.string.text_usuario_erroneo), Toast.LENGTH_SHORT).show();
        }
    }

    private void consultaUsuario(JSONObject jsonObject){
        idUsuario = jsonObject.optInt("id");
        if(idUsuario != 0){
            String pass = jsonObject.optString("password");


            if(pass.compareTo(password) == 0){
                gs.setIdUsuario(idUsuario);
                gs.setUsuario(jsonObject.optString("usuario"));
                gs.setPassword(pass);
                usuarioValido = true;
            }
            else{
                usuarioValido = false;
                progress.hide();
                Toast.makeText(getApplicationContext(), getString(R.string.text_pass_erronea), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            progress.hide();
            Toast.makeText(getApplicationContext(),getString(R.string.text_usuario_erroneo), Toast.LENGTH_SHORT).show();
        }
    }

    /*private void consultaDatos(JSONObject jsonObject){
        gs.setIdPersona(jsonObject.optInt("id"));
        gs.setNombre(jsonObject.optString("nombre"));
        gs.setTelefono(jsonObject.optString("telefono"));
        gs.setEmail(jsonObject.optString("email"));
        gs.setDepartamento(jsonObject.optString("departamento"));
        gs.setCiudad(jsonObject.optString("ciudad"));
    }*/

    /*@Override
    public void onErrorResponse(VolleyError error) {
        if(error.toString().compareTo("com.android.volley.TimeoutError") == 0){
            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Error de login "+ error.toString(), Toast.LENGTH_SHORT).show();
        }
        progress.hide();
        Log.i("ERROR", error.toString());
    }*/

    @Override
    public void onBackPressed() {

        Log.d(TAG, "click");

        if(salir){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
        salir = true;
        Toast.makeText(Login.this, getString(R.string.text_pulsar_salir), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                salir = false;
            }
        },3000);
    }
}
