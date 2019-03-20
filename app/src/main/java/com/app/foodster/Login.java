package com.app.foodster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.Persona.RegistroPersona;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    String TAG = this.getClass().getName();
    boolean salir = false;

    int idUsuario;
    boolean usuarioValido;

    GlobalState gs;

    EditText teclado;
    EditText etUsuario;
    EditText etPassword;

    CheckBox cbRecordar;
    CheckBox cbSession;

    Button btnIngresar;
    Button btnRegistro;

    ProgressDialog progress;

    String usuario;
    String password;

    String consulta;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gs = (GlobalState)getApplication();

        request = Volley.newRequestQueue(getApplicationContext());

        progress = new ProgressDialog(Login.this);
        progress.setMessage(getString(R.string.text_carga));
        progress.setCanceledOnTouchOutside(false);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);

        cbRecordar = findViewById(R.id.cbRecordar);
        cbSession = findViewById(R.id.cbSession);

        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

                etUsuario = findViewById(R.id.etUsuario);
                usuario = etUsuario.getText().toString().trim();

                etPassword = findViewById(R.id.etPassword);
                password = etPassword.getText().toString().trim();

                if(usuario.compareTo("") != 0 && password.compareTo("") != 0) {
                    usuarioValido = false;
                    consultarUsuario();
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.text_completar_campos), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarPersona();
            }
        });

        SharedPreferences preferencesCuenta = getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        usuario = preferencesCuenta.getString("usuario", "");
        password = preferencesCuenta.getString("password", "");
        String recordar = preferencesCuenta.getString("recordar", "");
        String session = preferencesCuenta.getString("session", "");

        if(recordar.compareTo("") != 0){
            etUsuario.setText(usuario);
            etPassword.setText(password);
            cbRecordar.setChecked(true);
        }

        if(session.compareTo("") != 0){
            cbSession.setChecked(true);
            consultarUsuario();
        }
    }

    public void Ingresar(){
        Intent ingreso = new Intent(Login.this, Principal.class);

        ingreso.addFlags(ingreso.FLAG_ACTIVITY_CLEAR_TOP | ingreso.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(ingreso);
        progress.hide();
    }

    public void RegistrarPersona(){

        Intent registro = new Intent(this, RegistroPersona.class);
        registro.addFlags(registro.FLAG_ACTIVITY_CLEAR_TOP | registro.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(registro);
    }

    private void consultarUsuario() {

            /*try {
                String passwdMd5 = this.toMd5(password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/

        consulta = "usuario";
        progress.show();
        String url = "http://" + gs.getIp() + "/Usuario/consultar_usuario.php?usuario=" + usuario;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void obtenerDatos() {

        consulta = "persona";
        String url = "http://" + gs.getIp() + "/Persona/consultar_persona.php?idUsuario="+idUsuario;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
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
        editorCuenta.commit();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(gs.getIdPersona() != 0){
            cbSession.setChecked(false);
        }
    }



    @Override
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject != null){
                if(consulta.compareTo("usuario") == 0){
                    idUsuario = jsonObject.optInt("id");
                    if(idUsuario != 0){
                        String pass = jsonObject.optString("password");


                        if(pass.compareTo(password) == 0){
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
                if(consulta.compareTo("persona") == 0){
                    gs.setIdPersona(jsonObject.optInt("id"));
                    gs.setNombre(jsonObject.optString("nombre"));
                    gs.setTelefono(jsonObject.optString("telefono"));
                    gs.setEmail(jsonObject.optString("email"));
                    gs.setidCiudad(jsonObject.optInt("id_ciudad"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gs.setActualizaEmpresas(true);
        if(consulta.compareTo("usuario") == 0 && usuarioValido){
            obtenerDatos();
        }
        else{
            if(consulta.compareTo("persona") == 0){
                verificarPreferencias();
                Ingresar();
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.toString().compareTo("com.android.volley.TimeoutError") == 0){
            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Error de login "+ error.toString(), Toast.LENGTH_SHORT).show();
        }
        progress.hide();
        Log.i("ERROR", error.toString());
    }

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
